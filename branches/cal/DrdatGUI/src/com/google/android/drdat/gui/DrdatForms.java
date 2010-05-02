package com.google.android.drdat.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * From original WebView demo:
 * 
 * Demonstrates how to embed a WebView in your activity. Also demonstrates how
 * to have javascript in the WebView call into the activity, and how the activity 
 * can invoke javascript.
 * <p>
 * In this example, clicking on the android in the WebView will result in a call into
 * the activities code in {@link DemoJavaScriptInterface#clickOnAndroid()}. This code
 * will turn around and invoke javascript using the {@link WebView#loadUrl(String)}
 * method.
 * <p>
 * Obviously all of this could have been accomplished without calling into the activity
 * and then back into javascript, but this code is intended to show how to set up the 
 * code paths for this sort of communication.
 * ----------------
 * 
 * This has been adapted to work with our online form generating code 
 *
 */
public class DrdatForms extends Activity {

    private static final String LOG_TAG = "DRDAT FORMS";
    private WebView mWebView;
    // these values would be set depending on how the forms object was run
    // either internally from the get task form
    // or from a notification BroadcastReceiver invocation
    private int study_id = 0;
    private int task_id = 0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        mWebView = (WebView) findViewById(R.id.webview_form);
        
        // since we can run this activity from the cl we need to set the email and password
        Intent i = this.getIntent();
        if (i.getStringExtra("email") != null && i.getStringExtra("password") != null) {
	        Login.setEmail(i.getStringExtra("email"));
	        Login.setPassword(i.getStringExtra("password"));
        }
        
        study_id = i.getIntExtra("study_id", study_id);
        task_id = i.getIntExtra("task_id", task_id);
        TextView title = (TextView) findViewById(R.id.webview_title);
        title.setText(title.getText()+" ("+study_id+"/"+task_id+")");
        
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        mWebView.setWebChromeClient(new MyWebChromeClient());
	    try {	    	
	    	DrdatFormCache forms = new DrdatFormCache(this, study_id, task_id);
	    	if (forms.numForms() > 0) {
				DrdatFormCollector queryData = new DrdatFormCollector(this,forms,mWebView);
				mWebView.addJavascriptInterface(queryData, "DrdatForms");
		        mWebView.loadData(forms.generate(), forms.getMime(), forms.getEncoding());
	    	} else {
	    		mWebView.loadData("<h3>No forms found!</h3>", "text/html", "utf-8");
	    	}
        } catch (Exception e) {
        	Log.e(LOG_TAG, "URL ERROR: "+e.toString()+": "+e.getMessage());
        }
    }

    /**
     * Provides a hook for calling "alert" from javascript. Useful for
     * debugging your javascript.
     */
    class MyWebChromeClient extends WebChromeClient {
    	// use this mainly for debugging javascript rather than showing actual alerts
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        	Log.d(LOG_TAG,"javascript debug message: "+message);
            return false; // true seems to kill the page load when alert() happens in an onload event
        }
    }
}