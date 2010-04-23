/**
 * 
 */
package com.google.android.drdat.cl;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author cal
 *
 */
public class DrdatTasklist extends ContentProvider {
	private String email;
	private String passwordMD5;
	public final String TYPE = "DrdatTasklist";
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// read only
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// read only
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		// read only so nothing really to do
		return true;
	}
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(
			Uri uri, 
			String[] projection, 
			String selection,
			String[] selectionArgs, 
			String sortOrder
		) 
	{
		email = selectionArgs[0];
		passwordMD5 = selectionArgs[1];
		DrdatSmi2TaskList tl = new DrdatSmi2TaskList(getContext(),email,passwordMD5);
		tl.reload();
		if (projection[0] == "tasklist") {
			return tl.getTaskListCursor();
		} else {
			return tl.getStudyCursor();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// read only
		return 0;
	}

}
