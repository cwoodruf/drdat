package com.android;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class BeeperControl extends Activity {
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    TextView tv = new TextView(this);
	    tv.setText("Testing beeper");
	    setContentView(tv);
	    beepForAnHour();
    }
    
	public void printing2(){
		Log.i("test", "beep");
	}

	public void beepForAnHour() {
		final Runnable beeper = new Runnable() {
			public void run() { System.out.println("beep"); printing2(); }
		};
		final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);
		scheduler.schedule(new Runnable() {
			public void run() { beeperHandle.cancel(true); }
		}, 60 * 60, SECONDS);
	}
}