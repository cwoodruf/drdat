package com.google.android.drdat.gui;

import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.webkit.WebView;

public class DrdatFormCollector {
	private HashMap<String,String> queryMap;
	private String query = "";
	private String action;
	private final String LOG_TAG = "DRDAT FORM";
	private DrdatFormCache forms;
	private WebView mWebView;
	private Activity context;
	private static final String DB_NAME = "drdat_data";
	private static final String DB_TABLE = "drdat_data";
	private static final int DB_VERSION = 1;
	private static final String DB_CREATE = 
			"create table " + DB_TABLE
			+ "(study_id integer, task_id integer, "  
			+ "email varchar(64), password varchar(64), "
			+ "query text, ts datetime default current_timestamp)";

	
    DrdatFormCollector(Activity c, DrdatFormCache fc, WebView wv) {
    	queryMap = new HashMap<String,String>();
    	context = c;
    	forms = fc;
    	mWebView = wv;
    }
    
    /**
     * grab data from the web form and save it
     * here we assume that each field name is unique
     * if this is not the case we should use another form
     * of storage
     * otherwise we use a hashmap to avoid duplication errors
     * if a form is refilled more than once
     * 
     * @param data
     */
    public void saveFields(String data) {
    	int NAME = 0;
    	int VALUE = 1;
    	try {
	    	String[] keyvals = data.split("&");
	    	Log.i(LOG_TAG,"saveFields: keyvals="+keyvals.length);
	    	
	    	for (int i = 0; i < keyvals.length; i++) {

	    		String[] keyval = keyvals[i].split("=");
	    		if (keyval.length != 2) continue;
	    		
	    		if (keyval[NAME].equals("action")) {
	    			setAction(keyval[VALUE]);
	    			continue;
	    		}
	    		if (keyval[NAME].equals("")) {
	    			continue;
	    		}
	    		Log.i(LOG_TAG,"saveFields: field: "+keyval[NAME]+"="+keyval[VALUE]);
	    		
	    		if (!queryMap.containsKey(keyval[NAME])) 
	    			query += keyvals[i] + "&";
	    		
	    		queryMap.put(keyval[NAME], keyval[VALUE]);
	
	    	}
    		Log.i(LOG_TAG, data);
        	Log.i(LOG_TAG, query);
  
    	} catch (Exception e) {
    		Log.e(LOG_TAG,"saveFields error: "+e.toString()+": "+e.getMessage());
    	}
    	try {
	    	if (getAction().matches(".*next.*")) {
	    		forms.nextForm();
			} else if (getAction().matches(".*prev.*")) {
	    		forms.prevForm();
	    	} else if (getAction().matches(".*save.*data.*")) {
	    		if (saveQueryToDB()) { 
		            new AlertDialog.Builder(context)
		            .setTitle("DRDAT")
		            .setMessage("Data saved. Thank you.")
		            .setNeutralButton("Close", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								context.finish();
							}
		            })
		            .create()
		            .show();
	    		} else {
		            new AlertDialog.Builder(context)
		            .setTitle("DRDAT ERROR")
		            .setMessage("Error saving data! Please try again.")
		            .setNeutralButton("Ok", null)
		            .create()
		            .show();
	    		}
	    	}
	    	
	    	Log.d(LOG_TAG,"about to run loadData for form "+forms.getCurrForm()+" action "+getAction());
			mWebView.loadData(forms.generate(), forms.getMime(), forms.getEncoding());
	    } catch (Exception e) {
    		Log.e(LOG_TAG,"saveFields display next form error: "+e.toString()+": "+e.getMessage());
    	}
    }
    /**
     * save the data we've accumulated 
     * for testing try and send it back to the smi
     * however that last step is the purview of cl
     * so this is just a temporary test (maybe)
     */
    public boolean saveQueryToDB() {
    	Log.i(LOG_TAG, "saveQueryToDB: will be sending "+query+" context "+context.toString());
    	try {
			DBHelper dbh = new DBHelper(context);
			SQLiteDatabase db = dbh.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put("query", query);
			values.put("study_id", forms.getStudy_id());
			values.put("task_id", forms.getTask_id());
			
			PartLoginCache login = new PartLoginCache(context);
			values.put("email", login.getEmail());
			String encoded = PasswordEncoder.encode(login.getPassword());
			values.put("password", encoded);
			
			db.insert(DB_NAME, null, values);			
			return true;
			
    	} catch (Exception e) {
    		Log.e(LOG_TAG,"saveQueryToDB: db error: "+e.toString()+": "+e.getMessage());
    	}
    	return false;
    }

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

    // automatically generated getters and setters
	public void setQueryMap(HashMap<String,String> q) {
		this.queryMap = q;
	}
	public HashMap<String,String> getQueryMap() {
		return queryMap;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String q) {
		query = q;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}
}
