package com.google.android.drdat.gui;

import java.util.TreeMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.webkit.WebView;

public class DrdatFormCollector {
	private TreeMap<String,String> queryMap;
	private String query = "";
	private String action;
	private static String TAG = "DRDAT FORM";
	private final String LOG_TAG = DrdatFormCollector.TAG;
	private WebView mWebView;
	private Context context;
	private DrdatFormCache forms;
	private static final String DB_NAME = "drdat_data";
	private static final String DB_TABLE = "drdat_data";
	private static final int DB_VERSION = 1;
	private static final String DB_CREATE = 
			"create table " + DB_TABLE
			+ "(study_id integer, task_id integer, "  
			+ "email varchar(64), password varchar(64), "
			+ "query text, ts datetime default current_timestamp, sent datetime)";
	private DBHelper dbh;
	private SQLiteDatabase db;
	
	DrdatFormCollector(Activity c) {
		context = c;
    	startDB(c);
	}
	
	DrdatFormCollector(Context c) {
		context = c;
    	startDB(c);
	}
	
    DrdatFormCollector(Activity c, DrdatFormCache fc, WebView wv) {
    	startDB(c);
    	queryMap = new TreeMap<String,String>();
    	context = c;
    	forms = fc;
    	mWebView = wv;
    }
    
    private void startDB(Context context) {
    	finalize();
    	dbh = new DBHelper(context);
    	db = dbh.getWritableDatabase();
    }
    
    protected void finalize() {
    	if (db != null) db.close();
    	if (dbh != null) dbh.close();
    }
    
    /**
     * this func is mapped to the onload event for the page and fills the form with
     * any data we currently know about
     * 
     * @param name
     * @return
     */
    public String getField(String name) {
    	try {
    		if (queryMap.containsKey(name)) {
    			return queryMap.get(name);
    		}
    	} catch (Exception e) {
    		Log.e(LOG_TAG,"getField error for "+name+". Exception "+e.toString()+": "+e.getMessage());
    	}
		return "";
    }
    /**
     * this func is mapped to the onSubmit event and saves any new data for this form
     * to our queryMap abstraction of the cgi query
     * 
     * @param name
     * @param value
     */
    public void setField(String name, String value) {
		if (name.equals("action")) {
			setAction(value);
			return;
		}
		if (name.equals("")) {
			return;
		}
		queryMap.put(name, value);

		Log.i(LOG_TAG, "setField: name " + name + " value " + value);
    }

    /**
     * respond to an action on the form
     * you can move forward, go back or save the data
     * 
     * this is currently decoupled from actually 
     * sending the data to make it easier to test
     *  
     * @param data
     */
    public void doAction(String action) {
    	try {
    		this.action = action;
	    	if (getAction().matches(".*next.*")) {
	    		forms.nextForm();
			} else if (getAction().matches(".*prev.*")) {
	    		forms.prevForm();
	    	} else if (getAction().matches(".*save.*data.*")) {
	            new AlertDialog.Builder(context)
		            .setTitle("DRDAT")
		            .setMessage("Save Entered Data?")
		            .setNegativeButton("Don't Save", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							((Activity) context).finish();							
						}
		            })
		            .setNeutralButton("Cancel", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
		            })
		            .setPositiveButton("Save", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (saveQueryToDB()) {
						            new AlertDialog.Builder(context)
						            .setTitle("DRDAT")
						            .setMessage("Data saved. Thank you.")
						            .setNeutralButton("Ok", new OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											((Activity) context).finish();
										}
						            })
						            .create()
						            .show();
									
								} else {
						            new AlertDialog.Builder(context)
						            .setTitle("DRDAT ERROR")
						            .setMessage("Error saving data! Please try again.")
						            .setNeutralButton("Ok", new OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											dialog.cancel();
										}
						            })
						            .create()
						            .show();
								}
							}
		            })
		            .create()
		            .show();
	    	}
	    	
	    	Log.d(LOG_TAG,"about to run loadData for form "+forms.getCurrForm()+" action "+getAction());
			mWebView.loadData(forms.generate(), forms.getMime(), forms.getEncoding());
	    } catch (Exception e) {
    		Log.e(LOG_TAG,"saveFields display next form error: "+e.toString()+": "+e.getMessage());
    	}
    }
    /**
     * save the data we've accumulated 
     * the cl will periodically gather this data and 
     * send it to smi via a content provider interface
     * with this gui
     */
    public boolean saveQueryToDB() {
    	try {
			query = "";
			for (Object name: queryMap.keySet()) {
				query += name + "=" + queryMap.get(name) + "&";
			}
			Log.i(LOG_TAG,"saving query "+query);
    	} catch (Exception e) {
    		Log.e(LOG_TAG,"error generating query "+query);
    		Log.e(LOG_TAG,"exception "+e.toString()+": "+e.getMessage());
    	}
    	try {
			ContentValues values = new ContentValues();
			values.put("query", query);
			values.put("study_id", forms.getStudy_id());
			values.put("task_id", forms.getTask_id());
			
			values.put("email", Login.getEmail());
			values.put("password", Login.getPasswordMD5());
			
			db.insert(DB_NAME, null, values);
			return true;
			
    	} catch (Exception e) {
    		Log.e(LOG_TAG,"saveQueryToDB: db error: "+e.toString()+": "+e.getMessage());
    	}
    	return false;
    }
    
    public static Cursor query(
    		Context context,
    		String[] projection, 
    		String selection, 
    		String[] selectionArgs, 
    		String sortOrder) 
    {
    	DrdatFormCollector data = new DrdatFormCollector(context);
    	return data.query(projection, selection, selectionArgs, sortOrder);
    }
    		
    public Cursor query(
    		String[] projection, 
    		String selection, 
    		String[] selectionArgs, 
    		String sortOrder) 
    {
    	Cursor c = null;
		try {
			c = db.query(DrdatFormCollector.DB_TABLE,projection,selection,selectionArgs,null,null,sortOrder);
			
		} catch (Exception e) {
			Log.e(TAG,"DrdatFormCollector.clearSent: "+e+": "+e.getMessage());
		}
    	return c;
    }
    
    public static int update(
    		Context context,
    		ContentValues values,
    		String whereClause,
    		String[] whereArgs) 
    {
    	DrdatFormCollector data = new DrdatFormCollector(context);
    	return data.update(values, whereClause, whereArgs);
    }
    
    public int update(
    		ContentValues values,
    		String whereClause,
    		String[] whereArgs) 
    {
    	int changed = -1;
    	try {
    		changed = db.update(DrdatFormCollector.DB_TABLE, values, whereClause, whereArgs);
    		
    	} catch (Exception e) {
    		Log.e(DrdatFormCollector.TAG,"DrdatFormCollector.update: "+e+": "+e.getMessage());
    	}
    	return changed;
    }

    public static int delete(Context context, String selection, String[] selectionArgs) {
    	DrdatFormCollector data = new DrdatFormCollector(context);
    	return data.delete(selection, selectionArgs);    	
    }
    
    public int delete(String whereClause, String[] whereArgs) {
    	int changed = -1;
    	try {
    		changed = db.delete(DrdatFormCollector.DB_TABLE, whereClause, whereArgs);
    		
    	} catch (Exception e) {
    		Log.e(DrdatFormCollector.TAG,"DrdatFormCollector.delete: "+e+": "+e.getMessage());
    	}
    	return changed;
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
	public void setQueryMap(TreeMap<String,String> q) {
		this.queryMap = q;
	}
	public TreeMap<String,String> getQueryMap() {
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
