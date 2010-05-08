package com.google.android.drdat.cl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootRestart extends BroadcastReceiver {
	private final String LOG_TAG = "DRDAT BOOT";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(LOG_TAG, "starting alarm refresh...");
		AlarmRefresh.setAlarm(context, AlarmRefresh.SIXTYSECS);
	}

}
