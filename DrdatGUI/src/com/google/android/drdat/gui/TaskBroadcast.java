package com.google.android.drdat.gui;

import java.util.Date;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class TaskBroadcast extends BroadcastReceiver {
	private static NotificationManager nm;
	private String LOG_TAG = "DRDAT TASK BROADCAST";
	private static int notifications = 0;
	
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
		

		TaskBroadcast.nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		CharSequence contentTitle = (CharSequence) extras.get("task_name");
		CharSequence contentText = (CharSequence) extras.get("schedule");
		
		// just using the plain Notification() constructor w/o parameters doesn't seem to work here
		Notification n = new Notification(R.drawable.icon,contentTitle,System.currentTimeMillis());
		n.defaults |= Notification.DEFAULT_LIGHTS;
		n.defaults |= Notification.DEFAULT_VIBRATE;
		
		int study_id = extras.getInt("study_id");
		int task_id = extras.getInt("task_id");
		String url = "drdat://"+study_id+"/"+task_id;
		Log.d(LOG_TAG, "uri: "+url+" Uri object "+Uri.parse(url));
		Intent i = new Intent(
				"com.google.android.drdat.gui.PARTLOGIN",
				Uri.parse("drdat://"+study_id+"/"+task_id) // make intent unique to task
			);
		i.putExtras(extras);
		PendingIntent contentIntent = 
			PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_CANCEL_CURRENT);

		n.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		TaskBroadcast.notifications++;
		TaskBroadcast.nm.notify(task_id, n); 
	}

	public static void clearNotification(int notification) {
		try {
			nm.cancel(notification);
		} catch (Exception e) {
			// ignore errors
		}
	}

	public static int getNotificationCount() {
		return notifications;
	}

}
