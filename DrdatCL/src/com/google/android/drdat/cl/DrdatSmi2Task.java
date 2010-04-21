package com.google.android.drdat.cl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.android.drdat.cl.R;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.database.Cursor;
import android.database.sqlite.*;

public class DrdatSmi2Task {
	
	private final String LOG_TAG = "DRDAT FORM PROVIDER";
	
	private boolean refreshTasks = false;
	private Context context;
	
	private DBHelper dbh;
	private SQLiteDatabase db;

	private static final String DB_NAME = "drdat_forms";
	private static final String DB_TABLE = "drdat_forms";
	private static final int DB_VERSION = 1;
	private static final String DB_ROWID = "study_id=? and task_id=? and email=? and password=?";
	private static final String DB_CREATE = "create table " + DB_TABLE
			+ "(study_id integer, task_id integer, email varchar(64), password varchar(32), forms text, "
			+ "constraint " + DB_TABLE + "_pkey primary key (study_id, task_id, email, password))";

	public DrdatSmi2Task(Context ctx) { 
		init(ctx,refreshTasks); 
	}
	public DrdatSmi2Task(Context ctx, boolean refresh) {
		init(ctx,refresh); 
	}
	private void init(Context ctx, boolean refresh) {
		context = ctx;
		refreshTasks = refresh; 
		dbh = new DBHelper(context);
		db = dbh.getWritableDatabase();
	}
	protected void finalize() {
		dbh.close();
	}
	/**
	 * from the notepad tutorial - interface to our form db
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
			db.execSQL(DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
	/**
	 * container for our query data
	 * @author cal
	 *
	 */
	private class FormData {
		// raw form data
		public int study_id;
		public int task_id;
		public String email;
		public String passwordMD5;
		
		// containers for db funcs
		public String[] args;
		public String[] projection;

		public FormData(int study_id, int task_id, String email, String pw) {
			this.study_id = study_id;
			this.task_id = task_id;
			this.email = email;
			this.passwordMD5 = pw;
			
			args = new String[] { 
					Integer.toString(study_id), 
					Integer.toString(task_id), 
					email,
					passwordMD5
			}; 
			
			projection = new String[] { "forms" };
 
		}
	}
	/**
	 * grab the forms either from the local db or the smi
	 * fills the forms class member
	 * 
	 */
	public Cursor getRawForms(
			int study_id, 
			int task_id, 
			String email, 
			String passwordMD5
		) 
	{
		Cursor c = null;
		try {
			FormData fd = new FormData(study_id,task_id,email,passwordMD5);
			
			Log.i(LOG_TAG,"Looking for: email "+fd.email+" pw "+fd.passwordMD5+" study_id "+fd.study_id+" task_id "+fd.task_id);
			c = db.query(DB_TABLE, fd.projection, DB_ROWID, fd.args, null, null, null); 

			
			if (refreshTasks || c == null || c.getCount() == 0) {
				boolean insert = false;
				if (c == null || c.getCount() == 0) insert = true;
				else c.close();
				
				Log.i(LOG_TAG,"Inserting: email "+fd.email+" pw "+fd.passwordMD5+" study_id "+fd.study_id+" task_id "+fd.task_id);
				refreshTask(insert, fd);
				return db.query(DB_TABLE, fd.projection, DB_ROWID, fd.args, null, null, null); 
				
			} else {
				Log.i(LOG_TAG,"sqlite: found a row!");
			}
			
		} catch (Exception e) {
			Log.e(LOG_TAG, "getRawForms: "+e.toString()+": "+e.getMessage());
		}
		return c;
	}

	private void refreshTask(boolean insert, FormData fd) {
		try {
			URL url = new URL(context.getString(R.string.SmiUrl)
					+ "tasktest.php");
			Log.i(LOG_TAG, "getting url: " + url.toExternalForm());
			
			BufferedReader in = new BufferedReader(
					new InputStreamReader(url.openStream()));
			
			String html = "";
			String str = new String();
			while ((str = in.readLine()) != null) {
				html += str + " ";
			}
			in.close();
			
			ContentValues values = new ContentValues();
	
			if (insert) {
				Log.i(LOG_TAG,"sqlite: inserting!");
				values.put("forms", html);
				values.put("study_id", fd.study_id);
				values.put("task_id", fd.task_id);
				values.put("email", fd.email);
				values.put("password", fd.passwordMD5);
				db.insert(DB_TABLE, "forms", values);
				
			} else {
				Log.i(LOG_TAG,"sqlite: updating!");
				values.put("forms", html);
				db.update(DB_TABLE, values, DB_ROWID, fd.args);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "refreshTask: "+e.toString()+": "+e.getMessage());
		}
	}
}
