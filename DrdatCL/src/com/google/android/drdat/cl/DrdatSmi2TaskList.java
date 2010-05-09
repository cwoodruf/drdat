package com.google.android.drdat.cl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import com.google.android.drdat.cl.R;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This is the workhorse object that manages task scheduling. 
 * It also contains the code for managing alarms for task notifications.
 * DrdatGUI uses this indirectly through the DrdatTasklist content provider
 * to get task scheduling information.
 * 
 * To get data on what a given participant is supposed to do initially we
 * query the smi via a web request. The smi sends some xml similar to this:
 * 
 *  <study_id>9</study_id>
 *   <task>
 *       <task_id>9</task_id>
 *       <task_name>third drdat task</task_name>
 *       <schedule>
 *           <start>2009-02-28</start>
 *           <end>2010-11-15</end>
 *           <daysofweek>Mon,Tues</daysofweek>
 *           <timesofday>11:30;23:30</timesofday>
 *       </schedule>
 *   </task>
 * ... followed by more tasks ...
 *  
 * We make the broad assumption that there will only be one study per
 * email / password but multiple tasks. We use Task and Study objects to 
 * manage the acquired data from the smi's xml output. 
 * @author cal
 *
 */
public class DrdatSmi2TaskList {
	private final String LOG_TAG = "DRDAT SMI TO TASKLIST";
	private final String LOG_ALARM = "DRDAT ALARM";
	public static boolean REFRESHED = false;
	private boolean httpFailed = true;
	private final long MINUTES = 60000;
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

	public Study getStudy() {
		return study;
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}

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
	 * constructor for an individual task list
	 * 
	 * @param context
	 * @param email
	 * @param passwordMD5
	 */
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
	 * clean up any left over database handles.
	 */
	public void finalize() {
		try {
			if (db != null) db.close();
			dbh.close();
		} catch (Exception e) {
			//ignore
		}
	}
	
	/** 
	 * set alarms - grabs every alarm for every participant
	 * @return number of alarms set
	 */
	public static ArrayList<Intent> getCurrentAlarms(Context ctx) {
		/* 
		 * singleton pattern: aren't I clever ... 
		 * but avoids possibility of people trying to do an update 
		 * w/o an email / password which won't work 
		 */
		DrdatSmi2TaskList tl = new DrdatSmi2TaskList(ctx);
		return tl.getCurrentAlarms();
	}
	/**
	 * Grabs any notifications that might need to be set based on task scheduling info.
	 *  
	 * @return an array list of intents 
	 */
	public ArrayList<Intent> getCurrentAlarms() {
		
		try {
			SQLiteDatabase db = dbh.getReadableDatabase();
			String query = 
				"select * from "+Task.TABLE+" where current_timestamp between start and end";
			Log.i(LOG_ALARM,"running "+query);
			Cursor c = db.rawQuery(query,null);
			if (!c.moveToFirst()) return null;
			ArrayList<Intent> intents = new ArrayList<Intent>();
			
			do {
				int[] valid_days = parseDaysOfWeek(c.getString(c.getColumnIndex("daysofweek")));
				Date[] tsod = parseTimesOfDay(c.getString(c.getColumnIndex("timesofday")));
				int study_id = c.getInt(c.getColumnIndex("study_id"));
				int task_id = c.getInt(c.getColumnIndex("task_id"));
				String task_name = c.getString(c.getColumnIndex("task_name"));

				Log.i(LOG_ALARM,context+": ("+study_id+"/"+task_id+") "+task_name);
				if (tsod == null || tsod.length == 0) {
					continue;
				}
				long thisminute = System.currentTimeMillis() / MINUTES;
				for (Date date: tsod) {
					// be a bit fuzzy with the time check 
					long minute = date.getTime() / MINUTES;
					if ( minute != thisminute) continue;
					Log.d(LOG_TAG, "found "+minute+" vs "+thisminute);
					
					Intent i = new Intent("com.google.android.drdat.gui.TASK_BROADCAST");					
					i.putExtra("study_id", study_id);
					i.putExtra("task_id", task_id);
					i.putExtra("valid_days", valid_days);
					i.putExtra("task_name", task_name);
					i.putExtra("timestamp", date.getTime());
					i.putExtra("schedule", date.toString());
					intents.add(i);
				}
			} while (c.moveToNext());
			c.close();
			db.close();
			return intents;
			
		} catch (Exception e) {
			Log.e(LOG_TAG,"setAlarms: "+e.toString()+": "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Attempt to update the drdat_tasks database by querying the smi.
	 * If findTasks() fails to get a response from the smi then we try and use
	 * the cached data from the db instead.
	 * 
	 * @throws DrdatSmi2TaskListException
	 */
	public void reload() throws DrdatSmi2TaskListException {
		if (email == null || passwordMD5 == null) {
			throw new DrdatSmi2TaskListException(
					"reload: missing either email ("+email+") or password ("+passwordMD5+")"
			);
		}
		findTasks();
	}
	
	/**
	 * Static method that attempts to find all participants on the system and refresh
	 * everything for them. This will also refresh all forms. Similar idea to 
	 * DrdatUpdateSchedule but designed to happen in the background.
	 */
	public static void refreshEverything(Context context) {
		DrdatSmi2TaskList tl = new DrdatSmi2TaskList(context);
		Log.d(tl.LOG_TAG, "refreshing everything ...");
		try {
			Cursor c = tl.getAllParticipants();
			if (c != null && c.moveToFirst()) {
				Log.d(tl.LOG_TAG,"got all "+c.getCount()+" participants");
				do {
					String email = c.getString(c.getColumnIndex("email"));
					String passwordMD5 = c.getString(c.getColumnIndex("password"));
					Log.d(tl.LOG_TAG,"participant "+email+" "+passwordMD5);
			
					DrdatSmi2TaskList tasks = new DrdatSmi2TaskList(context,email,passwordMD5);
					tasks.reload();
                	DrdatSmi2Task forms = new DrdatSmi2Task(context);
                	forms.deleteForms(email, passwordMD5);
                	
                	for (Task task: tasks.getTasks()) {
                		Log.d(tasks.LOG_TAG,"inserting data for "+task.task_id+" "+email+" "+passwordMD5);
                		forms.insertTask(task.study_id, task.task_id, email, passwordMD5);
                	}
                	tasks.finalize();
                	forms.finalize();
                	
				} while (c.moveToNext());		
			}
			tl.finalize();
			
		} catch (Exception e) {
			Log.e(tl.LOG_TAG,"DrdatSmi2TaskList refreshEverything: "+e+": "+e.getMessage());		
		}
	}

	/**
	 * Get a list of all the email / password pairs in the drdat_studies table
	 */
	public Cursor getAllParticipants() {
		try {
			String taskq = "select distinct email, password from "+Study.TABLE;
			db = dbh.getReadableDatabase();
			return db.rawQuery(taskq, null);
			
		} catch (Exception e) {
			Log.e(LOG_TAG,"DrdatSmi2TaskList getAllParticipants: "+e+": "+e.getMessage());
		}
		return null;
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
	 * Gets the raw task data from the smi web server. If this fails
	 * the httpFailed variable is set to true so we can tell the end
	 * user that the update failed.
	 * 
	 * If the web request succeeds we then parse the xml received and
	 * then cache the data to the db.
	 * 
	 * @return this object so we can chain this
	 */
	public DrdatSmi2TaskList findTasks() {
		URL url;
		httpFailed = true;		
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
			httpFailed = false;
			parseTasks();
			toHtml();
			saveAll();

		} catch (Exception e) {
			Log.e(LOG_TAG,"findTasks: error "+e+": "+e.getMessage());
			fillTasksFromCursor();
			if (tasks.size() > 0) {
				fillStudyFromCursor(tasks.get(0));
			}
		}
		return this;
	}
	
	/**
	 * Parse the raw data retrieved from the smi and fill 
	 * study and tasks with study and task objects. This does not 
	 * save anything to the db.
	 * 
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
	 * Take the abstract study and tasks arrays and make 
	 * html that we can use in a WebView. This is for the 
	 * task selector in DrdatGUI's DrdatListTasks activity.
	 * 
	 * @return this object for chaining methods
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
	 * Gets cursor into study data for a given participant.
	 * Used as part of the content provider interface for the CL.
	 * Note the data is not guaranteed to be fresh use the reload() method to freshen.
	 *  
	 * @return cursor to study data (should be one record for a given email / password)
	 */
	public Cursor getStudyCursor() {
		db = dbh.getReadableDatabase();
		Cursor c = db.query(Study.TABLE, Study.getFields(), Study.getAllSelection(), study.getAllKey(), null, null, null);
		return c;
	}
	/**
	 * Uses cached study data in db to fill the study member.
	 * Assumes only one study - may need to be updated to work with other studies.
	 * Mainly exists for when the internet connection is down.
	 * 
	 * @parm tasks: a list of tasks - use the first study_id to find study
	 * @return study member
	 */
	public Study fillStudyFromCursor(Task task) {
		int study_id = task.study_id;
		study = new Study();
		Cursor c = db.rawQuery(
				"select * from "+Study.TABLE+" where study_id=?", 
				new String[] { Integer.toString(study_id) }
			);
		if (c != null && c.moveToFirst()) {
			study.study_id = study_id;
			study.email = email;
			study.passwordMD5 = passwordMD5;
			study.study_name = c.getString(c.getColumnIndex("study_name"));
			study.raw = c.getString(c.getColumnIndex("raw"));
			study.html = c.getString(c.getColumnIndex("html"));
			c.close();
		}
		return study;
	}
	
	/**
	 * Gets cursor into tasks for a given participant.
	 * Used as part of the content provider interface for the CL. 
	 * Note the data is not guaranteed to be fresh use the reload() method to freshen.
	 *  
	 * @return cursor to a list of task data
	 */
	public Cursor getTaskListCursor() {
		db = dbh.getReadableDatabase();
		Cursor c = db.query(Task.TABLE, Task.getFields(), Study.getAllSelection(), study.getAllKey(), null, null, null);
		return c;
	}
	
	/**
	 * uses a cursor from getTaskListCursor() and fills the tasks array list so we can make html
	 * @return
	 */
	public ArrayList<Task> fillTasksFromCursor() {
		Cursor c = getTaskListCursor();
		tasks = new ArrayList<Task>();
		if (c != null && c.moveToFirst()) {
			do {
				Task task = new Task();
				task.study_id = c.getInt(c.getColumnIndex("study_id"));
				task.task_id = c.getInt(c.getColumnIndex("task_id"));
				task.email = email;
				task.passwordMD5 = passwordMD5;
				task.startDate = c.getString(c.getColumnIndex("start"));
				task.endDate = c.getString(c.getColumnIndex("end"));
				task.daysofweek = c.getString(c.getColumnIndex("daysofweek"));
				task.timesofday = c.getString(c.getColumnIndex("timesofday"));
				task.task_name = c.getString(c.getColumnIndex("task_name"));
				
			} while (c.moveToNext());
			c.close();
		}
		return tasks;
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
	
	/*
     *  the Calendar equivalents don't use the same day enumeration
	 *  as Date's toDay() function so we have to make our own constants
	 */
	public final int SUNDAY = 0; 
	public final int MONDAY = 1; 
	public final int TUESDAY = 2; 
	public final int WEDNESDAY = 3; 
	public final int THURSDAY = 4; 
	public final int FRIDAY = 5; 
	public final int SATURDAY = 6; 

	public int[] parseDaysOfWeek(String dsow) {
		ArrayList<Integer> days = new ArrayList<Integer>();
		dsow.replace(';', ',');
		for (String dow: dsow.split(",")) {
			dow = dow.toLowerCase();
			Integer day = new Integer(-1);
			if (dow.matches("mo.*")) {
				day = MONDAY; 
			} else if (dow.matches("tu.*")) {
				day = TUESDAY; 
			} else if (dow.matches("we.*")) {
				day = WEDNESDAY; 
			} else if (dow.matches("th.*")) {
				day = THURSDAY; 
			} else if (dow.matches("fr.*")) {
				day = FRIDAY; 
			} else if (dow.matches("sa.*")) {
				day = SATURDAY; 
			} else if (dow.matches("su.*")) {
				day = SUNDAY;
			}
			if (day >= 0) days.add(day);
		}
		Log.d(LOG_TAG,dsow+" days "+days);
		if (!days.isEmpty()) {
			int[] dayary = new int[days.size()];
			for (int i=0; i<days.size(); i++) {
				dayary[i] = (Integer) days.get(i);
			}
			return dayary;
		}
		// we assume the task happens every day
		// unless we are given a specific set of days
		return new int[] { 
				SUNDAY,
				MONDAY,
				TUESDAY,
				WEDNESDAY,
				THURSDAY,
				FRIDAY,
				SATURDAY
		};
	}

	public boolean isHttpFailed() {
		return httpFailed;
	}	
}
