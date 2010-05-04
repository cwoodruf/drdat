package com.google.android.drdat.gui;

import java.util.Date;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class TaskBroadcast extends BroadcastReceiver {
	private NotificationManager nm;
	private final int NOTME = 1962;
	private String LOG_TAG = "DRDAT TASK BROADCAST";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		
		// first check that the day is a day we should be doing this task
		int[] valid_days = (int[]) extras.getIntArray("valid_days");
		Log.d(LOG_TAG,"found "+valid_days.length+" days to check");
		if (valid_days != null && valid_days.length > 0) { 
			// for some reason sun is 0 and sat is 6 here but in Calendar its 1-7 (Sun == 1)
			int today = (new Date(System.currentTimeMillis())).getDay(); 
			boolean valid_day = true;
			for (int i=0; i<valid_days.length; i++) {
				Log.d(LOG_TAG,"checking "+valid_days[i]+" day vs today "+today+" valid_day found? "+valid_day);
				valid_day = false;
				if (valid_days[i] == today) {
					valid_day = true;
					break;
				}
			}
			if (!valid_day) return;
		}
		Log.d(LOG_TAG,"processing notification now");
		// make a notification to start the app with a certain task
		
		nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification n = new Notification();
		n.defaults |= Notification.DEFAULT_LIGHTS;
		n.defaults |= Notification.DEFAULT_VIBRATE;
		Intent i = new Intent(context, PartLogin.class);
		i.putExtras(extras);
		PendingIntent contentIntent = 
			PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_CANCEL_CURRENT);

		CharSequence contentTitle = (CharSequence) extras.get("task_name");
		CharSequence contentText = (CharSequence) extras.get("schedule");
		Log.d(LOG_TAG,"setting notification: title "+contentTitle+" body "+contentText+" intent "+i);
		n.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		nm.notify(NOTME, n);
	}

}
