package com.google.android.drdat.cl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * doesn't actually do anything just refreshes the alarms
 * @author cal
 *
 */
public class AlarmRefresh extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		DrdatSmi2TaskList.setAllAlarms(context);
		Toast.makeText(context, "Reset alarms", Toast.LENGTH_LONG);
	}

}
