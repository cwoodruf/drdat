package com.google.android.drdat.cl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
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
	    DrdatLoginCache cache = new DrdatLoginCache(me);
        emailView = (EditText) findViewById(R.id.UpdateLoginEmail);
        emailView.setText(cache.getEmail());
        
        passwordView = (EditText) findViewById(R.id.UpdateLoginPassword);
        passwordView.setText(cache.getPassword());
        
        Button login = (Button) findViewById(R.id.UpdateLoginButton);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String email = emailView.getText().toString();
            	String pw = passwordView.getText().toString();
                DrdatLoginCache login = new DrdatLoginCache(me,email,pw);
                if (login.validate()) {
                	login.save();
                    DrdatSmi2TaskList tasks = new DrdatSmi2TaskList(me,login.getEmail(),login.getPasswordMD5());
                    tasks.reload();
                    Intent i = new Intent("com.google.android.drdat.SHOW_SCHEDULE");
                    me.startActivity(i);
                    me.finish();
                } else {
                	new AlertDialog.Builder(me)
		            .setTitle("DRDAT ERROR")
		            .setMessage("Participant "+email+" is not active in any study with that password!")
		            .setNeutralButton("Ok", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
		            })
		            .create()
		            .show();
                }
            }
        });

	}
}
