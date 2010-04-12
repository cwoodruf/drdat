package com.google.android.drdat.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import java.net.*;
import java.io.*;

/**
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
 *
 */
public class DrdatForms extends Activity {

    private static final String LOG_TAG = "DRDAT GUI";

    private WebView mWebView;

    private Context myApp;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        myApp = this;
        setContentView(R.layout.main);
        mWebView = (WebView) findViewById(R.id.webview_form);
        
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
        try {
	        String str = new String();
	        String js = new String();
	        AssetManager am = getAssets();
	        BufferedReader jsin = new BufferedReader(new InputStreamReader(am.open("demo.js")));
	        while ((str = jsin.readLine()) != null) {
	        	js += str;
	        }
	        jsin.close();
	        String html = "<html><head><script language=\"javascript\">"+js+"</script></head><body>" +
	        			"<form action=\"javascript:void(0);\" onSubmit=\"save(this);\">\n";
	        
	        URL url = new URL(getString(R.string.SmiUrl) + "tasktest.php");
	        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	        while ((str = in.readLine()) != null) {
	        	html += str + " ";
	        }
	        in.close();
	        html += "<input type=submit name=action value=submit></form></body></html>\n";
	        // Log.i(LOG_TAG,"HTML PAGE: " + html);
	        mWebView.loadData(html,"text/html","utf-8");
        } catch (Exception e) {
        	Log.e(LOG_TAG, "URL ERROR: " + e.getMessage());
        }
    }

    final class DemoJavaScriptInterface {

        DemoJavaScriptInterface() {
        }
        public void saveFields(String data) {
        	Log.i(LOG_TAG, data);
        }
    }

    /**
     * Provides a hook for calling "alert" from javascript. Useful for
     * debugging your javascript.
     */
    class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(myApp)
	            .setTitle("DRDAT")
	            .setMessage(message)
	            .setPositiveButton(android.R.string.ok,
	                    new AlertDialog.OnClickListener()
	                    {
	                        public void onClick(DialogInterface dialog, int which)
	                        {
	                            result.confirm();
	                        }
	                    })
	            .setCancelable(false)
	            .create()
	            .show();

            return true;
        }
    }
}