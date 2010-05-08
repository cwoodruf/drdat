/**
 * 
 */
package com.google.android.drdat.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author cal
 *
 */
public class PartLogin extends Activity {
	private EditText emailView;
	private EditText passwordView;
	private Button login;
	private PartLogin me;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.partlogin);
        
        me = this;
        Login.retrieveLastLogin(this);
        
        emailView = (EditText) findViewById(R.id.PartLoginEmail);
        emailView.setText(Login.getEmail());
        
        passwordView = (EditText) findViewById(R.id.PartLoginPassword);
        passwordView.setText(Login.getPassword());
        
        login = (Button) findViewById(R.id.PartLoginButton);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Login.setEmail(emailView.getText().toString());
            	Login.setPassword(passwordView.getText().toString());
            	if (Login.check(me)) {
            		Intent intent = null;
            		Bundle extras = me.getIntent().getExtras(); 
            		if (extras != null && extras.containsKey("task_id")) {
            			// try and clear notification even if this wasn't started from a notification
            			TaskBroadcast.clearNotification(extras.getInt("task_id"));
            			// start instructions using same extra parameters
            			intent = new Intent("com.google.android.drdat.gui.INSTRUCTIONS");
            			intent.putExtras(me.getIntent());            			
            		} else {
            			intent = new Intent("com.google.android.drdat.gui.DRDATTASKS");
            		}
	            	me.startActivity(intent);
	            	me.finish();
            	} else {
            		new AlertDialog.Builder(me)
		            .setTitle("DRDAT ERROR")
		            .setMessage("No active participant "+Login.getEmail()+" with that password found!")
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
