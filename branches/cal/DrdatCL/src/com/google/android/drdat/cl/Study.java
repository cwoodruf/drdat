package com.google.android.drdat.cl;

import android.content.ContentValues;
import android.database.Cursor;

// note that password is really the md5 encoded password
public class Study {
	public int study_id = -1;
	public String study_name = "";
	public String raw = "";
	public String html = "";
	public String email = "";
	public String passwordMD5 = "";
	public static final String TABLE = "drdat_studies";
	public static final String CREATE = 	 
		"create table " + TABLE + 
		"(study_id integer, email varchar(64), password varchar(32), " + 
		"study_name varchar(255), raw text, html text, " +
		"constraint " + TABLE + "_pkey primary key (study_id, email, password))";
	
	public static String[] getFields() {
		return new String[] {
				"study_id",
				"study_name",
				"raw",
				"html"
		};
	}
	
	public Study() {}
	
	public Study(Cursor c) {
		study_id = c.getInt(0);
		study_name = c.getString(1);
		email = c.getString(2);
		passwordMD5 = c.getString(3);
		raw = c.getString(4);
		html = c.getString(5);
	}
	
	public String toString() {
		return "("+study_id+") "+study_name;
	}
	
	public static String getSelection() {
		return "study_id=? and email=? and password=?";
	}
	
	public String[] getKey() {
		return new String[] {
				Integer.toString(study_id),
				email,
				passwordMD5
		};
	}
	
	public static String getAllSelection() {
		return "email=? and password=?";
	}
	
	public String[] getAllKey() {
		return new String[] {
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
