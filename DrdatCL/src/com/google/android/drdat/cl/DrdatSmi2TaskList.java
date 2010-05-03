package com.google.android.drdat.cl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.google.android.drdat.cl.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DrdatSmi2TaskList {
	private final String LOG_TAG = "DRDAT TASKLIST";
	private final String LOG_ALARM = "DRDAT ALARM";
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
            <daysofweek>Mon,Tues</daysofweek>
            <timesofday>11:30;23:30</timesofday>
        </schedule>
    </task>
     *
	 * 
	 */
	public static boolean REFRESHED = false;
	private Context context;
	private String raw;
	private String email;
	private String passwordMD5;
	
	// private final String[] allTags = 
	// 	{ "tasklist","study_id","task","task_id","task_name","schedule","start","end","daysofweek","timesofday" };
	private String[] taskTags = 
		{ "task_id", "task_name", "start", "end", "daysofweek", "timesofday" };
	
	private String dateRE = "2\\d{3}-\\d{2}-\\d{2}";
	private String todRE = "(\\d{1,2}:\\d{2}(,|;|$))*";
	private String dowRE = "(\\w+(,|;|$))*";

	private static final int DB_VERSION = 5;
	private static final String DB_NAME = "drdat_tasks";
	
	private Study study;
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
			db.execSQL(Study.CREATE);
			db.execSQL(Task.CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (oldVersion != newVersion) {
				db.execSQL("drop table if exists "+Study.TABLE);
				db.execSQL(Study.CREATE);
				db.execSQL("drop table if exists "+Task.TABLE);
				db.execSQL(Task.CREATE);
			}
		}
	}
	
	private DBHelper dbh;
	private SQLiteDatabase db;
	
	/**
	 * this constructor exists for initializing alarms
	 * to do updates from the smi you need a valid email and password
	 * @param context
	 */
	private DrdatSmi2TaskList(Context context) {
		this.context = context;
		dbh = new DBHelper(context);
	}
	
	/** 
	 * set alarms - grabs every alarm for every participant
	 * @return number of alarms set
	 */
	public static PendingIntent[] setAllAlarms(Context ctx) {
		/* 
		 * singleton pattern: aren't I clever ... 
		 * but avoids possibility of people trying to do an update 
		 * w/o an email / password which won't work 
		 */
		DrdatSmi2TaskList tl = new DrdatSmi2TaskList(ctx);
		return tl.setAllAlarms();
	}
	
	public PendingIntent[] setAllAlarms() {
		ArrayList<PendingIntent> alarms = new ArrayList<PendingIntent>();
		try {
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			SQLiteDatabase db = dbh.getReadableDatabase();
			String query = 
				"select * from "+Task.TABLE+" where current_timestamp between start and end";
			Log.i(LOG_ALARM,"running "+query);
			Cursor c = db.rawQuery(query,null);
			if (!c.moveToFirst()) return null;

			do {
				Integer[] valid_days = parseDaysOfWeek(c.getString(c.getColumnIndex("daysofweek")));
				Date[] tsod = parseTimesOfDay(c.getString(c.getColumnIndex("timesofday")));
				int study_id = c.getInt(c.getColumnIndex("study_id"));
				int task_id = c.getInt(c.getColumnIndex("task_id"));
				String task_name = c.getString(c.getColumnIndex("task_name"));
				String schedule = 
					c.getString(c.getColumnIndex("daysofweek")) + "\n" +
					c.getString(c.getColumnIndex("timesofday"));
				
				Log.i(LOG_ALARM,context+": ("+study_id+"/"+task_id+") "+task_name+" "+schedule);
				if (tsod == null || tsod.length == 0) {
					continue;
				}
				for (Date date: tsod) {
					Intent i = new Intent("com.google.android.drdat.gui.TASK_BROADCAST");					
					i.putExtra("study_id", study_id);
					i.putExtra("task_id", task_id);
					i.putExtra("valid_days", valid_days);
					i.putExtra("task_name", task_name);
					i.putExtra("schedule", schedule);
					
					PendingIntent alarm = 
						PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
					Log.i(LOG_ALARM, 
							context+": "+alarm+": ("+study_id+"/"+task_id+") "+task_name+" "+date.toString());
					am.set(AlarmManager.RTC_WAKEUP, date.getTime(), alarm);
					alarms.add(alarm);
				}
				c.moveToNext();
			} while (c.moveToNext());
			c.close();
			db.close();
			
		} catch (Exception e) {
			Log.e(LOG_TAG,"setAlarms: "+e.toString()+": "+e.getMessage());
			e.printStackTrace();
		}
		Log.d(LOG_ALARM,"found "+alarms.size()+" alarms ");
		if (!alarms.isEmpty()) {
			PendingIntent[] iary = new PendingIntent[alarms.size()];
			for (int i=0; i<alarms.size(); i++) {
				iary[i] = (PendingIntent) alarms.get(i);
			}
			return iary;
		}
		return null;
	}
	
	/**
	 * take list of all alarms from setAllAlarms and cancel them
	 * this will really only work from the activity where the alarms got started 
	 * TODO: see if there is a way to identify any alarms you may have started without
	 *       having to explicitly save them
	 * @param ctx - context
	 * @param alarms - saved list of alarms to cancel
	 */
	public static void clearAllAlarms(Context ctx, PendingIntent[] alarms) {
		DrdatSmi2TaskList tl = new DrdatSmi2TaskList(ctx);
		tl.clearAllAlarms(alarms);
	}
	
	public void clearAllAlarms(PendingIntent[] alarms) {
		for (PendingIntent alarm: alarms) {
			alarm.cancel();
			Log.i(LOG_ALARM,context+": clearing "+alarm);
		}
	}
	
	public DrdatSmi2TaskList(Context context, String email, String passwordMD5) {
		this.context = context;
		this.email = email;
		this.passwordMD5 = passwordMD5;
		raw = "";
		study = new Study();
		study.email = email;
		study.passwordMD5 = passwordMD5;
		tasks = new ArrayList<Task>();
		dbh = new DBHelper(context);
	}
	
	public void reload() throws DrdatSmi2TaskListException {
		if (email == null || passwordMD5 == null) {
			throw new DrdatSmi2TaskListException(
					"reload: missing either email ("+email+") or password ("+passwordMD5+")"
			);
		}
		findTasks().toHtml().saveAll();
	}
	
	public void finalize() {
		if (db != null) db.close();
		dbh.close();
	}
	
	/**
	 * save the task list with schedule to the db
	 * this should only be run via the load() method
	 * @return this object
	 */
	private DrdatSmi2TaskList saveAll() {
		Cursor c = null;
		try {
			db = dbh.getWritableDatabase();
			db.execSQL("delete from "+Task.TABLE+" where email=? and password=? ",study.getAllKey());
			db.execSQL("delete from "+Study.TABLE+" where email=? and password=? ",study.getAllKey());
			REFRESHED = true;
			
			for (Task task: tasks.toArray(new Task[tasks.size()])) {
				db.insert(Task.TABLE, null, task.getValues());
			}
			
			c = db.query(Study.TABLE,new String[] { "study_id" },Study.getSelection(),study.getKey(),null,null,null);
			
			if (c == null || c.getCount() == 0) {
				db.insert(Study.TABLE, null, study.getValues());
			} else {
				db.update(Study.TABLE, study.getValues(), Study.getSelection(), study.getKey());
			}
			db.close();
			c.close();
			
		} catch (Exception e) {
			Log.e(LOG_TAG,"saveAll: "+e+": "+e.getMessage());
		}
		return this;
	}
	
	/**
	 * get the raw task data from the smi web server
	 * @return this object
	 */
	public DrdatSmi2TaskList findTasks() {
		URL url;
		
		try {
			url = new URL(
					context.getString(R.string.SmiUrl) + 
					"phone.php?do=getTaskList&email=" + email + "&password=" + passwordMD5
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
	
	/**
	 * parse the raw data retrieved from the smi and fill study and tasks with study and task objects
	 * @throws IOException
	 */
	private void parseTasks() throws IOException {
		if (raw.length() == 0) throw new IOException("no xml data found!");
		
		int studystart = raw.indexOf("<study_id>",0);
		studystart += "<study_id>".length();
		int studyend = raw.indexOf("</study_id>",studystart);
		
		study.study_id = new Integer(raw.substring(studystart,studyend));
		study.email = email;
		study.passwordMD5 = passwordMD5;

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
			task.email = email;
			task.passwordMD5 = passwordMD5;
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
				} else if (tag == "daysofweek" && datum.matches(dowRE)) {
					task.daysofweek = datum;
				} else if (tag == "timesofday" && datum.matches(todRE)) {
					task.timesofday = datum;
				}
				pos = end;
			}
			tasks.add(task);
			taskstart = taskend;
		}
	}

	/**
	 * take the abstract study and tasks arrays and make html that we can use in a WebView
	 * @return
	 */
	public DrdatSmi2TaskList toHtml() {
		Task[] task_ary = tasks.toArray(new Task[tasks.size()]);
		
		String options = "";
		String desc = "";
		for (int i = 0; i < task_ary.length; i++) {
			String name = task_ary[i].task_name;
			String opttitle = "<b>"+name+":</b><br>"+task_ary[i].daysofweek+"<br>"+task_ary[i].timesofday;
			options += "<option value=\""+task_ary[i].task_id+"\" >" + name +"</option>\n";
			desc += opttitle + "<p>";
		}
		study.html = 
			"<html><head></head>" +
			"<body><center>" +
			"<form " +
			"onSubmit=\"DrdatListTasks.getTask(0+study_id.value,0+task.options[task.selectedIndex].value); " +
			"return false;\">" +
			"<input type=hidden name=study_id value=\"" + study.study_id + "\">"+
			"<select name=\"task\"><option></option>"+options+"\n"+
			"</select><p><input type=submit value=\"Open Task\"></form>" +
			"</center>"+desc+"</body></html>";
		return this;
	}
	
	/**
	 * gets cursor into study data for a given participant
	 * used as part of the content provider interface for the CL
	 * note the data is not guaranteed to be fresh use the reload() method to freshen 
	 * @return cursor to study data (should be one record for a given email / password)
	 */
	public Cursor getStudyCursor() {
		db = dbh.getReadableDatabase();
		Cursor c = db.query(Study.TABLE, Study.getFields(), Study.getAllSelection(), study.getAllKey(), null, null, null);
		return c;
	}
	
	/**
	 * gets cursor into tasks for a given participant
	 * used as part of the content provider interface for the CL 
	 * note the data is not guaranteed to be fresh use the reload() method to freshen 
	 * @return cursor to a list of task data
	 */
	public Cursor getTaskListCursor() {
		db = dbh.getReadableDatabase();
		Cursor c = db.query(Task.TABLE, Task.getFields(), Study.getAllSelection(), study.getAllKey(), null, null, null);
		return c;
	}
	
	// some utility functions
	/**
	 * utility to parse a schedule string and turn it into a set of Dates
	 * that might be used to set the alarm
	 * this filters out junk values
	 * 
	 * @param sched string of HH:MM pairs 
	 * @return array of dates that can be used to set alarms
	 */
	public Date[] parseTimesOfDay(String tsod) {
		ArrayList<Date> times = new ArrayList<Date>();
		tsod.replace(',', ';'); // just in case we've got a malformed string
		for (String time: tsod.split(";")) {
			Log.d(LOG_ALARM,"time "+time);
			String[] hm = time.split(":");
			if (hm.length == 2) {
				int hour = new Integer(hm[0]);
				int min = new Integer(hm[1]);
				Log.d(LOG_ALARM,"time "+time+" = hour "+hour+" minute "+min);
				if (hour >= 0 && hour <= 23 && min >= 0 && min <= 59) {
					Date d = new Date();
					d.setHours(hour);
					d.setMinutes(min);
					times.add(d);
				}
			}
		}
		Log.d(LOG_TAG, tsod+" times "+times);
		if (!times.isEmpty()) {
			Date[] timeary = new Date[times.size()];
			for (int i=0; i<times.size(); i++) {
				timeary[i] = (Date) times.get(i);
			}
			return timeary;
		}
		return null;
	}
	
	/**
	 * take the days of week string and turn it into an array of numerical days of week
	 * 
	 * @param dsow
	 * @return array of days of week
	 */
	public Integer[] parseDaysOfWeek(String dsow) {
		ArrayList<Integer> days = new ArrayList<Integer>();
		dsow.replace(';', ',');
		for (String dow: dsow.split(",")) {
			dow = dow.toLowerCase();
			Integer day = new Integer(-1);
			if (dow.matches("mo.*")) {
				day = Calendar.MONDAY; 
			} else if (dow.matches("tu.*")) {
				day = Calendar.TUESDAY; 
			} else if (dow.matches("we.*")) {
				day = Calendar.WEDNESDAY; 
			} else if (dow.matches("th.*")) {
				day = Calendar.THURSDAY; 
			} else if (dow.matches("fr.*")) {
				day = Calendar.FRIDAY; 
			} else if (dow.matches("sa.*")) {
				day = Calendar.SATURDAY; 
			} else if (dow.matches("su.*")) {
				day = Calendar.SUNDAY;
			}
			if (day >= 0) days.add(day);
		}
		Log.d(LOG_TAG,dsow+" days "+days);
		if (!days.isEmpty()) {
			Integer[] dayary = new Integer[days.size()];
			for (int i=0; i<days.size(); i++) {
				dayary[i] = (Integer) days.get(i);
			}
			return dayary;
		}
		return null;
	}	
}
