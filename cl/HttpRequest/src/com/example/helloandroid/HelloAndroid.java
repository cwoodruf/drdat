package com.example.helloandroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HelloAndroid extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleHTTPRequest req = 
        	//new SimpleHTTPRequest("http://cmpt276:group2wiki@cmpt276.ath.cx:8002/morgan/");
        	//new SimpleHTTPRequest("http://cmpt276.ath.cx:8002/cal/testsmi/phonetest.php?do=getTaskList");
        	new SimpleHTTPRequest("http://cmpt276.ath.cx:8002/cal/testsmi/phonetest.php?do=getTask&task_id=15");
        TextView tv = new TextView(this);
        tv.setText(req.getOutput());
        setContentView(tv);
    }
}
