package com.google.android.drdat.cl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * save the email and password to a resource file
 * pretty simple minded: doesn't really do much checking
 * when saving in the db save every attempt so we have a record of login attempts
 * TODO: do better checking and maybe optionally turn off saving password
 * TODO: how to encrypt information?
 * 
 * @author cal
 *
 */

public class DrdatLoginCache {
	private String email;
	public static final String EMAIL_PAT = 
		"[a-zA-Z0-9_][a-zA-Z0-9\\.\\-_]*@[a-zA-Z0-9][a-zA-Z0-9\\.\\-]*\\.[a-zA-Z]{2,4}";
	private String password;
	public static final int MIN_PW_LEN = 6;
	private String passwordMD5;
	public static final int MD5_LEN = 32;
	private String url;
	private String LOG_TAG = "DRDAT LOGIN CACHE";
	
	/**
	 * from the notepad tutorial - interface to our task and scheduling db
	 * 
	 * {@linkplain http://developer.android.com/resources/tutorials/notepad/codelab/NotepadCodeLab.zip}
	 * {@linkplain http://developer.android.com/resources/tutorials/notepad/index.html
	 * 
	 * @author android 
	 *
	 */
	private static String DB_NAME = "drdat_login";
	private static int DB_VERSION = 2;
	private static String DB_TABLE = "drdat_login";
	private static String DB_CREATE = 
		"create table " + DB_TABLE + " (login_id integer auto_increment, " + 
		"email varchar(64), password varchar(64), passwordMD5 varchar(" + MD5_LEN + "), " +
		"ts datetime default current_timestamp)";
	
	private final class DBHelper extends SQLiteOpenHelper {

		DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("drop table if exists "+DB_TABLE);
			db.execSQL(DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (oldVersion != newVersion) {
				db.execSQL("drop table if exists "+DB_TABLE);
				db.execSQL(DB_CREATE);
			}
		}
	}
	
	private SQLiteDatabase db;
	private DBHelper dbh;

	public DrdatLoginCache(Context ctx) {
		init(ctx);
		Cursor cur = getCursor(); // just get the latest login
		if (cur != null && cur.getCount() > 0 && cur.moveToFirst()) {
			email = cur.getString(cur.getColumnIndex("email"));
			password = cur.getString(cur.getColumnIndex("password"));
			passwordMD5 = cur.getString(cur.getColumnIndex("passwordMD5"));
		}
	}
	
	public DrdatLoginCache(Context ctx, String em, String pw) {
		email = em;
		password = pw;
		passwordMD5 = PasswordEncoder.encode(pw);
		init(ctx);
	}
	
	public boolean init(Context ctx) {
		try {
			url = ctx.getString(R.string.SmiUrl);
			db = (dbh = new DBHelper(ctx)).getWritableDatabase();
		} catch (Exception e) {
			Log.e(LOG_TAG,"init: "+e.toString()+": "+e.getMessage());
			return false;
		}
		return true;
	}
	
	public void finalize() {
		db.close();
		dbh.close();
	}
	
	public void save() throws DrdatLoginException {
		if (email.length() == 0) throw new DrdatLoginException("save: no email!");
		if (!email.matches(EMAIL_PAT)) throw new DrdatLoginException("save: bad email format");
		if (password.length() < MIN_PW_LEN) throw new DrdatLoginException("save: password too short");

		ContentValues values = new ContentValues();
		values.put("email", email);
		values.put("password", password);
		passwordMD5 = PasswordEncoder.encode(password);
		values.put("passwordMD5", passwordMD5);
		db.insert(DB_TABLE, null, values);
	}
	
	public boolean validate() {
		boolean valid = false;
		try {
			URL validator = new URL(
					url += "phone.php?do=validateLogin&email="+Uri.encode(email)+"&password="+Uri.encode(passwordMD5));
			BufferedReader in = new BufferedReader(new InputStreamReader(validator.openStream()));
			String buf = in.readLine();
			if (buf.matches("OK.*")) valid = true;
			Log.i(LOG_TAG, "validate: "+buf+" ("+validator+")");
			in.close();
			return valid;
		} catch (Exception e) {
			Log.e(LOG_TAG,"validate: "+e.toString()+": "+e.getMessage());
		}
		return false;
	}
	
	public Cursor retrieveLastLogin() {
		return getCursor(null,null);
	}
	
	public Cursor getCursor() {
		return getCursor(email,passwordMD5);
	}
	
	public Cursor getCursor(String em, String pw) {
		String where = null;
		String[] whereData = null;
		if (em != null && pw != null) {
			where = " email=? and passwordMD5=? ";
			whereData = new String[] { em, pw };
		} 
		// grab the most recent login 
		Cursor cur = db.query(
				DB_TABLE,
				new String[] { "email", "password", "passwordMD5" }, 
				where, whereData,
				null, null,
				/* order by */ "ts desc", /* limit */ "1"
			);
		return cur;
	}

	public void setPassword(String password) {
		this.password = password;
		this.passwordMD5 = PasswordEncoder.encode(password);
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getPasswordMD5() {
		return passwordMD5;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}	
}
