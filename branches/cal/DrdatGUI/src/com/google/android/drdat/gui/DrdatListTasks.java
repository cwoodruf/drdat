package com.google.android.drdat.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class DrdatListTasks extends Activity {
	private DrdatTaskList tasks;
	public static int task_id;
	private Activity me;
	private final String LOG_TAG = "DRDAT LIST TASKS";
	private WebView mWebView;
	
	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		me = this;

		try {
			tasks = new DrdatTaskList(me);
			String html = tasks.toHtml();
			setContentView(R.layout.tasklist);
			mWebView = (WebView) findViewById(R.id.webview_tasklist);
	        WebSettings webSettings = mWebView.getSettings();
	        webSettings.setSavePassword(false);
	        webSettings.setSaveFormData(false);
	        webSettings.setJavaScriptEnabled(true);
	        webSettings.setSupportZoom(false);
	        mWebView.addJavascriptInterface(new JavascriptInterface(), "DrdatListTasks");
	        mWebView.loadData(html,"text/html","utf-8");
	        Log.i(LOG_TAG,html);
		} catch (Exception e) {
			Log.e(LOG_TAG,"DrdatListTasks: "+e.toString()+": "+e.getMessage());
		}
	}
	private class JavascriptInterface {
		public JavascriptInterface() {}
		
		@SuppressWarnings("unused")
		public void getTask(String selected) {
			try {
				task_id = Integer.parseInt(selected);
				Log.i(LOG_TAG, "got select option "+selected+" integer is "+task_id);
				Intent intent = new Intent("com.google.android.drdat.gui.INSTRUCTIONS");
				Log.i(LOG_TAG, "got new intent");
				me.startActivity(intent);
				me.finish();
			} catch (Exception e) {
				Log.e(LOG_TAG,"getTask: "+e.toString()+": "+e.getMessage());
				task_id = -1;
			}
		}
	}
}
