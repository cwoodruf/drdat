package com.google.android.drdat.cl;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class DrdatShowSchedule extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.schedule);
	    TextView tv = (TextView) findViewById(R.id.DrdatCLScheduleTitle);
	    UpdateLoginCache login = new UpdateLoginCache(this);
	    tv.setText("Tasks for: "+login.getEmail());
        DrdatSmi2TaskList tl = new DrdatSmi2TaskList(this, login.getEmail(), login.getPasswordMD5());
        Cursor tasks = tl.getTaskListCursor();
        while (tasks.moveToNext()) {
        	tasks.getString(0);
        }
	    
	}

}
