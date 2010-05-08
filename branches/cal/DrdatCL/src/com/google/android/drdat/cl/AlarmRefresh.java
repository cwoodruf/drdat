package com.google.android.drdat.cl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @author cal
 *
 */
public class AlarmRefresh extends BroadcastReceiver {
	private static String TAG = "DRDAT ALARM REFRESH";
	private final String LOG_TAG = AlarmRefresh.TAG;
	private static AlarmManager dailyCron;
	private static PendingIntent dailyOp;
	private static AlarmState state = AlarmState.UNSET;
	public static long SIXTYSECS = 60;
	
	private static enum AlarmState {
		UNSET, STARTED, STOPPED
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = DrdatSmi2TaskList.getCurrentAlarm(context);
		if (i != null) {
			Log.d(LOG_TAG,"sending broadcast: "+i);
			context.sendBroadcast(i);
		}
	}

	public static void setAlarm(Context me, long checkevery) {
		Intent i = new Intent("com.google.android.drdat.cl.ALARM_REFRESH");
		AlarmRefresh.dailyOp = PendingIntent.getBroadcast(me, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmRefresh.dailyCron = (AlarmManager) me.getSystemService(Context.ALARM_SERVICE);
		AlarmRefresh.dailyCron.setRepeating(
				AlarmManager.RTC_WAKEUP, // how to interpret next argument 
				System.currentTimeMillis(), // start right away
				(checkevery * 1000),
				AlarmRefresh.getDailyOp() // what to do
			);
		state = AlarmState.STARTED;
	}

	public static void clearAlarm() {
		if (AlarmRefresh.dailyCron != null) {
			AlarmRefresh.dailyCron.cancel(AlarmRefresh.dailyOp);
		}
		state = AlarmState.STOPPED;
	}
	
	public static PendingIntent getDailyOp() {
		return dailyOp;
	}

	public static AlarmManager getDailyCron() {
		return dailyCron;
	}
	
	/**
	 * check whether the alarm was deliberately stopped
	 * we can use this to decide whether to leave the alarm off
	 * when the task manager gets started: 
	 * by default it will try and start notifications
	 * @return true if we deliberately stopped the alarm false otherwise
	 */
	public static boolean wasStopped() {
		boolean stopped = (state == AlarmState.STOPPED ? true : false);
		Log.d(TAG, "alarm state = "+state+", alarm deliberately stopped = "+stopped);
		return stopped;
	}
}
