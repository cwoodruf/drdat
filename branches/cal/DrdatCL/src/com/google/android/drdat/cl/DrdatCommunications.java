package com.google.android.drdat.cl;

import java.net.URI;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DrdatCommunications extends Activity {
	private NotificationManager notifier;
	private Activity me;
	private final int TESTNOTIFY = 888;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        me = this;
		notifier = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
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
        			Notification no = new Notification(R.drawable.icon, "notification title", System.currentTimeMillis()+10000);
        			Intent intent = new Intent("com.google.android.drdat.gui.PARTLOGIN");
        			PendingIntent pi = PendingIntent.getActivity(me, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        			no.setLatestEventInfo(me, "set event title", null, pi);
        			notifier.notify(TESTNOTIFY, no);
        		} else if (clicked == getString(R.string.NotifyStop)) {
        			notifier.cancel(TESTNOTIFY);
        		}
        	}
        });
    }
}