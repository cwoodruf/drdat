/**
 * 
 */
package com.google.android.drdat.gui;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author cal
 *
 */
public class DrdatDataProvider extends ContentProvider {

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) { 
		return DrdatFormCollector.delete(getContext(),selection,selectionArgs); 
	}
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		return "DrdatDataProvider";
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// we don't add records externally
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		// we don't create a db here 
		return false;
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
			String sortOrder) 
	{
		return DrdatFormCollector.query(getContext(),projection,selection,selectionArgs,sortOrder);
	}
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(
			Uri uri, 
			ContentValues values, 
			String selection,
			String[] selectionArgs) 
	{
		return DrdatFormCollector.update(getContext(),values,selection,selectionArgs);
	}
}
