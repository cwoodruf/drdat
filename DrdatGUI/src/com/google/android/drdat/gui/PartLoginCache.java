package com.google.android.drdat.gui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * save the email and password to a resource file
 * pretty simple minded: doesn't really do much checking
 * TODO: do better checking and maybe optionally turn off saving password
 * TODO: how to encrypt information?
 * 
 * @author cal
 *
 */

public class PartLoginCache {
	private String email;
	private String password;
	private String passwordMD5;
	private Activity activity;
	private SharedPreferences prefs;
	private Editor edPrefs;
	public static final int MD5_LEN = 32;
	
	public PartLoginCache(Activity a) {
		activity = a;
		prefs = activity.getSharedPreferences(activity.getString(R.string.PartLoginFile), Context.MODE_PRIVATE);
		edPrefs = prefs.edit();
		email = prefs.getString("email", "");
		password = prefs.getString("password", "");
		passwordMD5 = PasswordEncoder.encode(password);
	}
	
	public PartLoginCache(Activity a, String em, String pw) {
		activity = a;
		email = em;
		password = pw;
		passwordMD5 = PasswordEncoder.encode(pw);
		prefs = activity.getSharedPreferences(activity.getString(R.string.PartLoginFile), Context.MODE_PRIVATE);
		edPrefs = prefs.edit();		
		edPrefs.putString("email", email);
		edPrefs.putString("password", password);
		edPrefs.putString("passwordMD5", passwordMD5);
		edPrefs.commit();
	}
	
	public void setPassword(String password) {
		this.password = password;
		this.passwordMD5 = PasswordEncoder.encode(password);
		edPrefs.putString("passwordMD5", passwordMD5).commit();
		edPrefs.putString("password", password).commit();
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getPasswordMD5() {
		return passwordMD5;
	}
	
	public void setEmail(String email) {
		this.email = email;
		edPrefs.putString("email", email).commit();
	}
	
	public String getEmail() {
		return email;
	}
}
