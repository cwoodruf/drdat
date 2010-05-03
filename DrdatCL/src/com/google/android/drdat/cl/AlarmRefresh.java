package com.google.android.drdat.cl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmRefresh extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		DrdatSmi2TaskList.setAllAlarms(context);
	}

}
