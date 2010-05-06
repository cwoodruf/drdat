package com.google.android.drdat.cl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * doesn't actually do anything just refreshes the alarms
 * @author cal
 *
 */
public class AlarmRefresh extends BroadcastReceiver {
	private final String LOG_TAG = "DRDAT ALARM REFRESH";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG,"running setAllAlarms from AlarmRefresh");
		DrdatSmi2TaskList.setAllAlarms(context);
		Toast.makeText(context, "reset alarms!", Toast.LENGTH_LONG).show();
	}

}
