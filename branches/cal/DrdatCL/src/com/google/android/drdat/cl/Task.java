package com.google.android.drdat.cl;

import android.content.ContentValues;
import android.database.Cursor;

public class Task {
	public int study_id = -1;
	public int task_id = -1;
	public String task_name = "";
	public String startDate = "";
	public String endDate = "";
	public String timesofday = "";
	public String daysofweek = "";
	public String raw = "";
	public String email = "";
	public String passwordMD5 = "";
	public static final String TABLE = "drdat_tasks";
	public static final String CREATE = 
		"create table " + TABLE +
		"(study_id integer, task_id integer, email varchar(64), password varchar(32), " +
		"task_name varchar(255), start date, end date, daysofweek text, timesofday text, raw text, " + 
		"constraint " + TABLE + "_pkey primary key (study_id, task_id, email, password))";



	public static String[] getFields() {
		return new String[] {
				"study_id",
				"task_id",
				"task_name",
				"start",
				"end",
				"timesofday",
				"daysofweek"
		};
	}

	public Task() {}
	
	public Task(Cursor c) {
		study_id = c.getInt(0);
		task_id = c.getInt(1);
		task_name = c.getString(2);
		startDate = c.getString(3);
		endDate = c.getString(4);
		timesofday = c.getString(5);
		daysofweek = c.getString(6);
	}
	
	public String toString() {
		return "("+study_id+"/"+task_id+") "+task_name+"\n"+daysofweek+"\n"+timesofday;
	}
	
	public static String getSelection() {
		return "study_id=? and task_id=? and email=? and password=?";
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
		values.put("timesofday", timesofday);
		values.put("daysofweek",daysofweek);
		values.put("raw", raw);
		return values;
	}
}

