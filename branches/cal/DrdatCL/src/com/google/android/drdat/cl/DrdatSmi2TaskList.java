package com.google.android.drdat.cl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import com.google.android.drdat.cl.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DrdatSmi2TaskList {
	private final String LOG_TAG = "DRDAT TASKLIST";
	/*
	 * example: 
	 *
    <study_id>9</study_id>
    <task>
        <task_id>9</task_id>
        <task_name>third drdat task</task_name>
        <schedule>
            <start>2009-02-28</start>
            <end>2010-11-15</end>
            <frequency>1</frequency>
            <timeofday>11:30;23:30</timeofday>
        </schedule>
    </task>
     *
	 * 
	 */

	private Context context;
	private String raw;
	private String html;
	private String email;
	private String passwordMD5;
	
	// private final String[] allTags = 
	// 	{ "tasklist","study_id","task","task_id","task_name","schedule","start","end","frequency","timeofday" };
	private String[] taskTags = 
		{ "task_id", "task_name", "start", "end", "frequency", "timeofday" };
	
	private String dateRE = "2[0-9]{3}-[0-9]{2}-[0-9]{2}";
	private String todRE = "([0-9]{1,2}:[0-9]{2}(;|$))*";

	private static final int DB_VERSION = 3;
	private static final String DB_NAME = "drdat_forms";
	private static final String DB_TASKS = "drdat_tasks";
	private static final String DB_STUDIES = "drdat_studies";
	
	private static final String DB_TASK_ROWID = "study_id=? and task_id=? and email=? and password=?";
	private static final String DB_STUDY_ROWID = "study_id=? and email=? and password=?";
	
	private static final String DB_CREATE_STUDIES = 
		"create table " + DB_STUDIES + 
		"(study_id integer, email varchar(64), password varchar(32), " + 
		"study_name varchar(255), raw text, html text, " +
		"constraint " + DB_STUDIES + "_pkey primary key (study_id, email, password))";
	
	private class Study {
		public int study_id = -1;
		public String study_name = "";
		public String raw = "";
		public String html = "";
		
		public String[] getFields() {
			return new String[] {
					"study_id",
					"study_name",
					"html"
			};
		}
		
		public String[] getKey() {
			return new String[] {
					Integer.toString(study_id),
					email,
					passwordMD5
			};
		}
		
		public ContentValues getValues() {
			ContentValues values = new ContentValues();
			values.put("study_id",study_id);
			values.put("study_name", study_name);
			values.put("email",email);
			values.put("password",passwordMD5);
			values.put("raw", raw);
			values.put("html", html);
			return values;
		}
	}
	private Study study;
	

	private static final String DB_CREATE_TASKS = 
		"create table " + DB_TASKS +
		"(study_id integer, task_id integer, email varchar(64), password varchar(32), " +
		"task_name varchar(255), start date, end date, frequency integer, timeofday text, raw text, " + 
		"constraint " + DB_TASKS + "_pkey primary key (study_id, task_id, email, password))";

	private class Task {
		public int study_id = -1;
		public int task_id = -1;
		public String task_name = "";
		public String startDate = "";
		public String endDate = "";
		public String timeofday = "";
		public int freq = 0;
		public String raw = "";

		public String[] getFields() {
			return new String[] {
					"study_id",
					"task_id",
					"task_name",
					"start",
					"end",
					"timeofday",
					"frequency"
			};
		}
		public String[] getKey() {
			return new String[] {
					Integer.toString(study_id),
					Integer.toString(task_id),
					email,
					passwordMD5
			};
		}
		
		public ContentValues getValues() {
			ContentValues values = new ContentValues();
			values.put("study_id",study_id);
			values.put("task_id",task_id);
			values.put("email",email);
			values.put("password",passwordMD5);
			values.put("task_name",task_name);
			values.put("start",startDate);
			values.put("end",endDate);
			values.put("timeofday", timeofday);
			values.put("frequency",freq);
			values.put("raw", raw);
			return values;
		}
	}
	private ArrayList<Task> tasks;

	/**
	 * from the notepad tutorial - interface to our task and scheduling db
	 * 
	 * {@linkplain http://developer.android.com/resources/tutorials/notepad/codelab/NotepadCodeLab.zip}
	 * {@linkplain http://developer.android.com/resources/tutorials/notepad/index.html
	 * 
	 * @author android 
	 *
	 */
	private static class DBHelper extends SQLiteOpenHelper {

		DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE_STUDIES);
			db.execSQL(DB_CREATE_TASKS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
	
	private DBHelper dbh;
	private SQLiteDatabase db;

	
	public DrdatSmi2TaskList(Context context, String email, String passwordMD5) {
		this.context = context;
		this.email = email;
		this.passwordMD5 = passwordMD5;
		html = ""; 
		raw = "";
		study = new Study();
		tasks = new ArrayList<Task>();
		dbh = new DBHelper(context);
		db = dbh.getWritableDatabase();		
		findTasks().toHtml().saveAll();
	}
	
	public void finalize() {
		dbh.close();
	}
	
	public DrdatSmi2TaskList saveAll() {
		Cursor c = null;
		try {
			for (Task task: tasks.toArray(new Task[tasks.size()])) {
				c = db.query(DB_TASKS, new String[] { "task_id" }, DB_TASK_ROWID, task.getKey(), null, null, null);
				if (c == null || c.getCount() == 0) {
					db.insert(DB_TASKS, null, task.getValues());
				} else {
					db.update(DB_TASKS, task.getValues(), DB_TASK_ROWID, task.getKey());
				}
				c.close();
			}
			
			c = db.query(DB_STUDIES, new String[] { "study_id" }, DB_STUDY_ROWID, study.getKey(), null, null, null);
			
			if (c == null || c.getCount() == 0) {
				db.insert(DB_STUDIES, null, study.getValues());
			} else {
				db.update(DB_STUDIES, study.getValues(), DB_STUDY_ROWID, study.getKey());
			}
			c.close();
			
		} catch (Exception e) {
			Log.e(LOG_TAG,"saveAll: "+e+": "+e.getMessage());
		}
		return this;
	}
	
	public DrdatSmi2TaskList findTasks() {
		URL url;
		
		try {
			url = new URL(
					context.getString(R.string.SmiUrl) + 
					"/tasklist.php?email="+email+"&password="+passwordMD5
				);
			Log.i(LOG_TAG,"findTasks: downloading " + url.toExternalForm());

			BufferedReader in = new BufferedReader(
					new InputStreamReader(url.openStream()));
			
			String str = new String();
			while ((str = in.readLine()) != null) {
				raw += str + " ";
			}
			in.close();
			parseTasks();

		} catch (Exception e) {
			Log.e(LOG_TAG,"findTasks: error "+e+": "+e.getMessage());
		}
		return this;
	}
	
	private void parseTasks() throws IOException {
		if (raw.length() == 0) throw new IOException("no xml data found!");
		
		int studystart = raw.indexOf("<study_id>",0);
		studystart += "<study_id>".length();
		int studyend = raw.indexOf("</study_id>",studystart);
		
		study.study_id = new Integer(raw.substring(studystart,studyend));

		if ((studystart = raw.indexOf("<study_name>",0)) != -1) {
			studystart += "<study_name>".length();
			studyend = raw.indexOf("</study_name>",studystart);
			study.study_name = raw.substring(studystart,studyend);
		}
		study.raw = raw;
		
		int taskstart = 0;
		while (taskstart != -1) {
			
			taskstart = raw.indexOf("<task>",taskstart);
			if (taskstart == -1) break;
			
			int taskend = raw.indexOf("</task>",taskstart);
			if (taskend == -1) break;
			
			String rawtask = raw.substring(taskstart, taskend).trim();
			Task task = new Task();
			task.study_id = study.study_id;
			task.raw = rawtask;
			int pos = 0;
			
			for (String tag: taskTags) {
				String starter = "<"+tag+">";
				
				pos = rawtask.indexOf(starter,pos);
				if (pos == -1) break;
				
				int end = rawtask.indexOf("</"+tag+">",pos);
				if (end == -1) break;
				
				pos += starter.length();
				String datum = rawtask.substring(pos, end).trim();

				if (tag == "task_id") {
					try {
						task.task_id = new Integer(datum);
					} catch (Exception e) {
						task.task_id = -1;
					}
				} else if (tag == "task_name") {
					task.task_name = datum;
				} else if (tag == "start" && datum.matches(dateRE)) {
					task.startDate = datum;
				} else if (tag == "end" && datum.matches(dateRE)) {
					task.endDate = datum;
				} else if (tag == "frequency") {
					task.freq = new Integer(datum);
				} else if (tag == "timeofday" && datum.matches(todRE)) {
					task.timeofday = datum;
				}
				pos = end;
			}
			tasks.add(task);
			taskstart = taskend;
		}
	}
	
	public DrdatSmi2TaskList toHtml() {
		Task[] task_ary = tasks.toArray(new Task[tasks.size()]);
		
		html = 
			"<html><head></head>" +
			"<body><center>" +
			"<form " +
			"onSubmit=\"DrdatListTasks.getTask(0+study_id.value,0+task.options[task.selectedIndex].value); " +
			"return false;\">" +
			"<input type=hidden name=study_id value=\"" + study.study_id + "\">"+
			"<select name=\"task\"><option></option>\n";
		
		for (int i = 0; i < task_ary.length; i++) {
			html += "<option value=\""+task_ary[i].task_id+"\">"+task_ary[i].task_name+"</option>\n";
		}
		
		html += "</select><p><input type=submit value=\"Open Task\"></form></center></body></html>";
		study.html = html;
		return this;
	}
	
	public Cursor getStudyCursor() {
		return db.query(DB_STUDIES, new Study().getFields(), DB_STUDY_ROWID, study.getKey(), null, null, null);
	}
	
	public Cursor getTaskListCursor() {
		return db.query(DB_TASKS, new Task().getFields(), DB_STUDY_ROWID, study.getKey(), null, null, null);
	}
}
