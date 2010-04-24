package com.google.android.drdat.cl;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DrdatShowSchedule extends Activity {
	private UpdateLoginCache login;
	private ArrayList<Task> tasks;
	private ArrayList<String> entries;
	private final String LOG_TAG = "DRDAT SHOW SCHEDULE";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.schedule);
	    
	    TextView tv = (TextView) findViewById(R.id.DrdatCLScheduleTitle);
	    
	    login = new UpdateLoginCache(this);
	    tv.setText("Tasks for: "+login.getEmail());
	    
        DrdatSmi2TaskList tl = new DrdatSmi2TaskList(this, login.getEmail(), login.getPasswordMD5());
        Cursor tc = tl.getTaskListCursor();
        
        tasks = new ArrayList<Task>();
        entries = new ArrayList<String>();
        while (tc.moveToNext()) {
        	Task task = new Task(tc);
        	tasks.add(task);
        	entries.add(task.toString());
        	Log.d(LOG_TAG, "task: "+task.toString());
        }
        tc.close();
        
        ListView lv = (ListView) findViewById(R.id.DrdatCLScheduleList);
        Log.d(LOG_TAG,"found list view "+lv);
        
        lv.setAdapter(new ArrayAdapter<Task>(this, R.layout.schedule, R.id.DrdatCLScheduleListItems, tasks));
        Log.d(LOG_TAG,"added list entries");
        
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				Intent i = new Intent();
				i.setClassName("com.google.android.drdat.gui", "com.google.android.drdat.gui.DrdatForms");
				i.putExtra("email", login.getEmail());
				i.putExtra("password", login.getPassword());
				i.putExtra("study_id", tasks.get(position).study_id);
				i.putExtra("task_id", tasks.get(position).task_id);
				startActivity(i);
			}
        });
        Log.d(LOG_TAG,"set click listener");
	}

}
