/**
 * 
 */
package com.google.android.drdat.gui;

import android.app.Activity;
import android.content.Intent;
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
	private PartLoginCache cache;
	private PartLogin me;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.partlogin);
        
        me = this;
        cache = new PartLoginCache(this);
        
        emailView = (EditText) findViewById(R.id.PartLoginEmail);
        emailView.setText(cache.getEmail());
        
        passwordView = (EditText) findViewById(R.id.PartLoginPassword);
        passwordView.setText(cache.getPassword());
        
        login = (Button) findViewById(R.id.PartLoginButton);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	cache.setEmail(emailView.getText().toString());
            	cache.setPassword(passwordView.getText().toString());
            	Intent formsIntent = new Intent("com.google.android.drdat.gui.INSTRUCTIONS");
            	me.startActivity(formsIntent);
            	me.finish();
            }
        });

	}

}
