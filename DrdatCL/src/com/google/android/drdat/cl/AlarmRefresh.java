package com.google.android.drdat.cl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * doesn't actually do anything just refreshes the alarms
 * @author cal
 *
 */
public class AlarmRefresh extends BroadcastReceiver {
	private final String LOG_TAG = "DRDAT ALARM REFRESH";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = DrdatSmi2TaskList.getCurrentAlarm(context);
		if (i != null) {
			Log.d(LOG_TAG,"sending broadcast: "+i);
			context.sendBroadcast(i);
		}
	}

}
