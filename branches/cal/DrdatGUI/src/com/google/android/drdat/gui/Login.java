package com.google.android.drdat.gui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Uses DrdatCL's DrdatLogin content provider interface to
 * check and cache log in info.
 *  
 * @author cal
 *
 */

public class Login {
	private static String email;
	private static String password;
	private static String passwordMD5;
	
	public static boolean check(Context ctx) {
		ContentResolver cr = ctx.getContentResolver();
		Cursor cur = cr.query(
				Uri.parse(ctx.getString(R.string.LoginUrl)),
				new String[] { "passwordMD5" }, 
				null, // the "where" part of the query normally
				new String[] { email, password },
				null
			);
		if (cur != null && cur.moveToFirst()) {
			passwordMD5 = cur.getString(cur.getColumnIndex("passwordMD5"));
			cur.close();
			return true;
		}
		return false;
	}
	
	public static void retrieveLastLogin(Context ctx) {
		ContentResolver cr = ctx.getContentResolver();
		Cursor cur = cr.query(Uri.parse(ctx.getString(R.string.LoginUrl)),null,null,null,null);
		if (cur != null && cur.moveToFirst()) {
			email = cur.getString(cur.getColumnIndex("email"));
			password = cur.getString(cur.getColumnIndex("password"));
			passwordMD5 = cur.getString(cur.getColumnIndex("passwordMD5"));
		}
		cur.close();
	}
	
	public static void setPassword(String pw) {
		password = pw;
	}
	
	public static String getPassword() {
		return password;
	}
	
	public static String getPasswordMD5() {
		return passwordMD5;
	}
	
	public static void setEmail(String em) {
		email = em;
	}
	
	public static String getEmail() {
		return email;
	}
}
