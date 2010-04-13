package com.google.android.drdat.gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.database.Cursor;
import android.database.sqlite.*;

public class DrdatFormCache {
	private final String LOG_TAG = "DRDAT FORM CACHE";
	private int study_id;
	private int task_id;
	private String rowID;
	private boolean refreshTasks = false;
	private String[] forms;
	private int currForm;
	private String mime = "text/html";
	private String encoding = "utf-8";
	private String htmlstart;
	private String htmlend;
	private Context context;
	private static final String DB_NAME = "drdat_forms";
	private static final String DB_TABLE = "drdat_forms";
	private static final int DB_VERSION = 1;
	private static final String DB_CREATE = "create table " + DB_TABLE
			+ "(study_id integer, task_id integer, forms text, "
			+ "constraint " + DB_TABLE + "_pkey primary key (study_id, task_id))";

	public DrdatFormCache(Context ctx, int sid, int tid) {
		init(ctx,sid,tid,false);
	}

	public DrdatFormCache(Context ctx, int sid, int tid, boolean refresh) {
		init(ctx,sid,tid,refresh);
	}
	private void init(Context ctx, int sid, int tid, boolean refresh) {
		context = ctx;
		task_id = tid;
		study_id = sid;
		rowID = "task_id=" + task_id + " and study_id=" + study_id;
		refreshTasks = refresh;
		getForms();
		setCurrForm(0);
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
	 * get the javascript and other resources (eg css) needed to start the html
	 * page we cannot use references to outside resources as loadData will fail
	 * to load them for us
	 * 
	 * @return the start portion of the WebView html
	 */
	public String getHtmlstart() {
		if (htmlstart == null) {

			String js = new String();

			AssetManager am = context.getAssets();
			try {
				// we need this javascript to save entered form data
				BufferedReader jsin = new BufferedReader(new InputStreamReader(
						am.open("demo.js")));
				String str;
				while ((str = jsin.readLine()) != null) {
					js += str;
				}
				jsin.close();

				// for the loadData method to work we have to load any
				// javascript and css as a string
				// we can't refer to external files as a security precaution -
				// not sure how helpful that is
				htmlstart = "<html><head><script language=\"javascript\">"
						+ js
						+ "</script></head><body>"
						+ "<form action=\"javascript:void(0);\" onSubmit=\"save(this);\">\n";
			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage());
			}

		}
		return htmlstart;
	}

	/**
	 * make the button bar
	 * 
	 * @param formId
	 *            which form we are looking at
	 * @return html string with appropriate buttons
	 */
	public String getHtmlend() {
		String submit = "<input type=submit onClick=\"saveaction(this);\" ";
		if (currForm <= 0) {
			htmlend = "&lt; prev "+submit+" value=\"next &gt;\">";
		} else if (currForm >= forms.length-2) {
			htmlend = submit+" value=\"&lt; prev\"> "+submit+" value=\"save data\">";
		} else {
			htmlend = submit+" value=\"&lt; prev\"> "+submit+" value=\"next &gt;\">";
		}
		htmlend += "</form></body></html>";
		return htmlend;
	}

	/**
	 * generate all the html for the WebView sews together the htmlstart current
	 * form and htmlend strings will choke if you haven't set up the forms or
	 * there aren't any forms
	 * 
	 * @param currForm
	 * @return html string for the WebView
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public String generate() throws ArrayIndexOutOfBoundsException {
		getForms();
		if (currForm < 0 || currForm >= forms.length)
			throw new ArrayIndexOutOfBoundsException();
		String html = getHtmlstart() + forms[currForm] + getHtmlend();
		return html;
	}

	/**
	 * grab the forms either from the local db or another content provider
	 * fills the forms class member
	 * 
	 */
	private void getForms() {
		if (forms == null) {
			try {
				String html = "";
				
				DBHelper dbh = new DBHelper(context);
				SQLiteDatabase db = dbh.getWritableDatabase();
				Cursor c = null;
				// first see if we have this in our local db
				c = db.query(DB_TABLE, new String[] { "forms" }, rowID, null, null, null, null);
				Log.i(LOG_TAG,"sqlite: looking for "+rowID);

				// couldn't find anything so lets grab it some other way
				Log.i(LOG_TAG,"getForms: refreshTasks: "+refreshTasks);
				
				if (refreshTasks || c == null || c.getCount() == 0) {
					// TODO get this to work with task_id and study_id params
					// this really should be going through the cl which can do
					// the requesting
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
						db.insert(DB_TABLE, "forms", values);
						
					} else {
						c.close();
						Log.i(LOG_TAG,"sqlite: updating!");
						values.put("forms", html);
						db.update(DB_TABLE, values, rowID, null);
					}
					
				} else {
					Log.i(LOG_TAG,"sqlite: found a row!");
					c.moveToFirst();
					html = c.getString(c.getColumnIndex("forms"));
					c.close();
				}
				
				forms = html.split("<!-- split -->");
				dbh.close();
				
			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage());
			}
		}
	}
	// methods for moving through forms
	public void prevForm() {
		if (currForm > 0) currForm--;
		else currForm = 0;
	}
	public void nextForm() {
		if (currForm < forms.length-1) currForm++;
		else currForm = forms.length-1;
	}
	// automatically generated getters and setters
	public void setStudy_id(int study_id) {
		this.study_id = study_id;
	}

	public int getStudy_id() {
		return study_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}

	public int getTask_id() {
		return task_id;
	}

	public void setCurrForm(int currForm) {
		this.currForm = currForm;
	}

	public int getCurrForm() {
		return currForm;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public String getMime() {
		return mime;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}
}
