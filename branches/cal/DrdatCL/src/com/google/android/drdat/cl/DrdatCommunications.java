package com.google.android.drdat.cl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DrdatCommunications extends Activity {
	private Activity me;
	private TextView t;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        me = this;
		t = (TextView) findViewById(R.id.DrdatCLActionsTitle);
		if (AlarmRefresh.getDailyCron() != null) t.append(getString(R.string.AlarmStart));
		
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
        			
        		} else if (clicked == getString(R.string.ShowSchedule)) {
        			Intent i = new Intent("com.google.android.drdat.SHOW_SCHEDULE");
        			startActivity(i);
            			
        		} else if (clicked == getString(R.string.Notify)) {
        			// turn eternal alarms on the task notification alarms are for specific times of day
        			// so we have to reload them periodically 
        			AlarmRefresh.setAlarm(me,AlarmRefresh.SIXTYSECS);
        			Toast.makeText(me, R.string.AlarmStart, Toast.LENGTH_LONG).show();
        			t.setText(me.getString(R.string.DrdatCLActionsTitle)+"\n"+me.getString(R.string.AlarmStart));
        			    				
        		} else if (clicked == getString(R.string.NotifyStop)) {
        			if (AlarmRefresh.getDailyCron() != null) {
        				AlarmRefresh.clearAlarm();
            			Toast.makeText(me, R.string.AlarmStop, Toast.LENGTH_LONG).show();
            			t.setText(me.getString(R.string.DrdatCLActionsTitle));
            		} else {
            			Toast.makeText(me, R.string.NoAlarms, Toast.LENGTH_LONG).show();
        			}
        		}
        	}
        });
    }
}