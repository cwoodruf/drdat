package com.google.android.drdat.cl;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

// what the drdat gui app uses to get cached login information
public class DrdatLogin extends ContentProvider {
	private DrdatLoginCache cache;
	private final String LOG_TAG = "DRDAT LOGIN";
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// we never delete logins rather we keep a record of all login attempts
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return "DrdatLogin";
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String email = values.getAsString("email");
		String password = values.getAsString("password");
		cache = new DrdatLoginCache(getContext(),email,password);
		try {
			if (!cache.validate()) return null;
			cache.save();
		} catch (Exception e) {
			Log.e(LOG_TAG, "DrdatLogin insert: "+e.toString()+": "+e.getMessage());
		}
		return uri;
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		cache = new DrdatLoginCache(getContext());
		
		if (selectionArgs != null && selectionArgs.length > 0) {
			cache.setEmail(selectionArgs[0]);
			cache.setPassword(selectionArgs[1]);
			if (!cache.validate()) return null; // done as a courtesy - however requires an extra web request
			// saving the cache logs this login event
			try {
				cache.save();
			} catch (Exception e) {
				Log.e(LOG_TAG, "DrdatLogin insert: "+e.toString()+": "+e.getMessage());
			}
			// setting the email and password has the side effect of 
			// making getCursor only look for that email and password
			return cache.getCursor();
		}
		// retrieve the last login w/o regard to who logged in
		return cache.retrieveLastLogin();
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// we always insert query only gets the last record
		return 0;
	}
}
