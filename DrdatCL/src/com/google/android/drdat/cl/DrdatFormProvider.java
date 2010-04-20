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

public class DrdatFormProvider {
	private final String LOG_TAG = "DRDAT FORM PROVIDER";
	private boolean refreshTasks = false;
	private Context context;
	private DBHelper dbh;
	private SQLiteDatabase db;
	private static final String DB_NAME = "drdat_forms";
	private static final String DB_TABLE = "drdat_forms";
	private static final int DB_VERSION = 1;
	private static final String DB_ROWID = "task_id='?' and study_id='?' and email='?' and password='?'";
	private static final String DB_CREATE = "create table " + DB_TABLE
			+ "(study_id integer, task_id integer, email varchar(64), password varchar(32), forms text, "
			+ "constraint " + DB_TABLE + "_pkey primary key (study_id, task_id, email, password))";

	public DrdatFormProvider(Context ctx) { 
		init(ctx,refreshTasks); 
	}
	public DrdatFormProvider(Context ctx, boolean refresh) {
		init(ctx,refresh); 
	}
	private void init(Context ctx, boolean refresh) {		
		dbh = new DBHelper(context);
		db = dbh.getWritableDatabase();
		context = ctx; 
		refreshTasks = refresh; 
	}
	protected void finalize() {
		dbh.close();
	}
	/**
	 * from the notepad tutorial
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
	 * grab the forms either from the local db or the smi
	 * fills the forms class member
	 * 
	 */
	public String getRawForms(int study_id, int task_id, String email, String passwordMD5) {
		try {
			String html = "";
			
			Cursor c = null;
			// first see if we have this in our local db
			String[] selectionArgs = new String[] { 
					Integer.toString(task_id), 
					Integer.toString(study_id), 
					email,
					passwordMD5
			}; 
 
			c = db.query(
					DB_TABLE, 
					new String[] { "forms" }, 
					DB_ROWID, selectionArgs,
					null,null,null
				);
			Log.i(LOG_TAG,"email "+email+" pw "+passwordMD5+" study_id "+study_id+" task_id "+task_id);
			
			if (refreshTasks || c == null || c.getCount() == 0) {
				URL url = new URL(context.getString(R.string.SmiUrl)
						+ "tasktest.php");
				Log.i(LOG_TAG, "getting url: " + url.toExternalForm());
				
				BufferedReader in = new BufferedReader(
						new InputStreamReader(url.openStream()));
				
				String str = new String();
				while ((str = in.readLine()) != null) {
					html += str + " ";
				}
				in.close();
				
				ContentValues values = new ContentValues();
		
				if (c == null || c.getCount() == 0) {
					Log.i(LOG_TAG,"sqlite: inserting!");
					values.put("forms", html);
					values.put("study_id", study_id);
					values.put("task_id", task_id);
					values.put("email", email);
					values.put("password", passwordMD5);
					db.insert(DB_TABLE, "forms", values);
					
				} else {
					c.close();
					Log.i(LOG_TAG,"sqlite: updating!");
					values.put("forms", html);
					db.update(DB_TABLE, values, DB_ROWID, selectionArgs);
				}
				
			} else {
				Log.i(LOG_TAG,"sqlite: found a row!");
				c.moveToFirst();
				html = c.getString(c.getColumnIndex("forms"));
				c.close();
			}
			return html;
			
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		return "";
	}
}
