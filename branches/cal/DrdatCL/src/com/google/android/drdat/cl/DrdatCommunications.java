package com.google.android.drdat.cl;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DrdatCommunications extends Activity {
	private Activity me;
	private PendingIntent[] alarms;
	private AlarmManager dailyCron;
	private PendingIntent dailyOp;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        me = this;
		
        
        ListView l = (ListView) findViewById(R.id.list);
        l.setTextFilterEnabled(true);
        
        l.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		// When clicked, show a toast with the TextView text
        		String clicked = ((TextView) view).getText().toString();
        		if (clicked == getString(R.string.UpdateSchedule)) {
        			Intent i = new Intent("com.google.android.drdat.UPDATE_SCHEDULE");
        			startActivity(i);
        			
        		} else if (clicked == getString(R.string.Notify)) {
        			// turn off any alarms we may have started
        			if (alarms != null) {
        				DrdatSmi2TaskList.clearAllAlarms(me,alarms);
        			}
        			// turn eternal alarms on the task notification alarms are for specific times of day
        			// so we have to reload them periodically 
        			dailyCron = (AlarmManager) me.getSystemService(ALARM_SERVICE);
        			Intent i = new Intent("com.google.android.drdat.cl.ALARM_REFRESH");
        			dailyOp = PendingIntent.getBroadcast(me, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        			dailyCron.setRepeating(
        					AlarmManager.RTC_WAKEUP, // how to interpret next argument 
        					System.currentTimeMillis(), // start right away
        					60000,
        					dailyOp // what to do
        				);
        			    				
        		} else if (clicked == getString(R.string.NotifyStop)) {
        			if (dailyCron != null) dailyCron.cancel(dailyOp);
        			
    				DrdatSmi2TaskList.clearAllAlarms(me, alarms);
    				new AlertDialog.Builder(me)
    					.setTitle("DRDAT Scheduler")
    					.setMessage("Cleared alarms.")
    					.setNeutralButton("Ok", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,	int which) {
								dialog.cancel();
							}
						})
						.show();
        		}
        	}
        });
    }
}