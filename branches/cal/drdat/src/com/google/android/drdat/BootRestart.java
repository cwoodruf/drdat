package com.google.android.drdat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Broadcast receiver that is woken up when the phone initially boots.
 * This will start the alarms for notifications, uploads and task data refresh (AlarmRefresh).
 * 
 * @author cal
 *
 */
public class BootRestart extends BroadcastReceiver {
	private final String LOG_TAG = "DRDAT BOOT";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(LOG_TAG, "starting alarm refresh...");
		AlarmRefresh.setAlarm(context, AlarmRefresh.SIXTYSECS);
	}

}
