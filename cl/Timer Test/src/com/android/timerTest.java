package com.android;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class timerTest extends Activity {
	Timer t = new Timer();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView tv = new TextView(this);
        tv.setText("Testing timer - scheduling alternative");
        time();
        setContentView(tv);
    }
    
	public void printing2(){
		Log.i("test", "beep");
	}
	
	public void printDate(){
		Date d = new Date();
		Log.i("date", d.toString());
	}
	
	public void time(){
		t.schedule(new TimerTask() {
			public void run() {
				printDate();
			}
		//}, 0, 1000*60*60); //display once an hour
		}, 0, 1000); //display once a second
	}
	
	public void stop(){
		t.cancel();
	}
}