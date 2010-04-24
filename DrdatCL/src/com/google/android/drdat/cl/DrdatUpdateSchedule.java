package com.google.android.drdat.cl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DrdatUpdateSchedule extends Activity {
	private EditText passwordView;
	private EditText emailView;
	private Activity me;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.update);
        
	    me = this;
	    UpdateLoginCache cache = new UpdateLoginCache(me);
        emailView = (EditText) findViewById(R.id.UpdateLoginEmail);
        emailView.setText(cache.getEmail());
        
        passwordView = (EditText) findViewById(R.id.UpdateLoginPassword);
        passwordView.setText(cache.getPassword());
        
        Button login = (Button) findViewById(R.id.UpdateLoginButton);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String email = emailView.getText().toString();
            	String pw = passwordView.getText().toString();
                UpdateLoginCache login = new UpdateLoginCache(me,email,pw);
                DrdatSmi2TaskList tasks = new DrdatSmi2TaskList(me,login.getEmail(),login.getPasswordMD5());
                tasks.reload();
                Intent i = new Intent("com.google.android.drdat.SHOW_SCHEDULE");
                me.startActivity(i);
            }
        });

	}
}
