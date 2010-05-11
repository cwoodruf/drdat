package com.google.android.drdat.gui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Activity class that uses a WebView component which lets us select a task
 * we want to do. The DrdatCL content provider DrdatTasklist provides us with
 * the html.
 * 
 * @author cal
 *
 */
public class DrdatListTasks extends Activity {
	public static int task_id;
	public static int study_id;
	private Activity me;
	private final String LOG_TAG = "DRDAT LIST TASKS";
	private WebView mWebView;
	
	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		me = this;

		try {
			String html = getTaskList();
			setContentView(R.layout.tasklist);
			mWebView = (WebView) findViewById(R.id.webview_tasklist);
	        WebSettings webSettings = mWebView.getSettings();
	        webSettings.setSavePassword(false);
	        webSettings.setSaveFormData(false);
	        webSettings.setJavaScriptEnabled(true);
	        webSettings.setSupportZoom(false);
	        mWebView.addJavascriptInterface(new JavascriptInterface(), "DrdatListTasks");
	        mWebView.loadData(html,"text/html","utf-8");
		} catch (Exception e) {
			Log.e(LOG_TAG,"DrdatListTasks: "+e+": "+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Gets our login data and grabs our task list.
	 *  
	 * @return an html string with the task selection form.
	 */
	private String getTaskList() {
		Login.retrieveLastLogin(this);
		Cursor c = getContentResolver().query(
				Uri.parse(getString(R.string.TaskListUrl)),
				new String[] { "study" }, // can be study or tasklist 
				null, // where part of the query 
				new String[] {
						Login.getEmail(),
						Login.getPasswordMD5(),
				},
				null
		);
		// study returns study_id, study_name, and html
		// tasklist returns an unordered list of tasks
		String html = "";
		if (c.moveToFirst()) { 
			html = c.getString(c.getColumnIndex("html"));
			c.close();
		} 
		return html;
	}

	/**
	 * Class that lets us retrieve the study_id and task_id from the 
	 * selection form. Starts the form display activity DrdatForms.
	 * 
	 * @author cal
	 *
	 */
	private class JavascriptInterface {
		public JavascriptInterface() {}
		
		@SuppressWarnings("unused")
		public void getTask(String study, String task) {
			try {
				study_id = Integer.parseInt(study);
				task_id = Integer.parseInt(task);
				Intent intent = new Intent("com.google.android.drdat.gui.INSTRUCTIONS");
				intent.putExtra("study_id", study_id);
				intent.putExtra("task_id", task_id);
				me.startActivity(intent);
			} catch (Exception e) {
				Log.e(LOG_TAG,"getTask: "+e.toString()+": "+e.getMessage());
				study_id = task_id = -1;
			}
		}
	}
}