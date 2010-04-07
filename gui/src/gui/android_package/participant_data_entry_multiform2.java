/*-- 
--------------------------------------
Author: KOUAME Kouadio Klebair (kkk7@sfu.ca)
Code written for CMPT276 team project (DRTA), Spring 2010
--------------------------------------
*/

package gui.android_package;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class participant_data_entry_multiform2 extends Activity{

	private Button SubmitButton;
	private Button PreviousButton;
	private Button CancelButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.participant_data_entry_multiform2);
	
		Spinner rhesus_factor = (Spinner) findViewById(R.id.rhesus_factor);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		this, R.array.rhesus_factor_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rhesus_factor.setAdapter(adapter);
		
		
        
        this.SubmitButton = (Button)this.findViewById(R.id.SubmitButton);
        this.SubmitButton.setOnClickListener(new View.OnClickListener() {
											        //@Override
											        public void onClick(View v) {
											        	//
											        }
        									}); 
               
	    this.PreviousButton = (Button)this.findViewById(R.id.PreviousButton);
        this.PreviousButton.setOnClickListener(new View.OnClickListener() {
										        //@Override
										        public void onClick(View v) {
										        	previous_form1();//
										        }
  									}); 
  
		this.CancelButton = (Button)this.findViewById(R.id.CancelButton);
		this.CancelButton.setOnClickListener(new View.OnClickListener() {
										        //@Override
										        public void onClick(View v) {
										        	finish();
										        }
  									}); 
  
  
  
}

protected void previous_form1() {
	/* Create an Intent to start * MySecondActivity. */ 
	Intent i = new Intent( this, participant_data_entry_multiform.class); 
	/* Send intent to the OS to make * it aware that we want to start * MySecondActivity as a SubActivity. */ 
	startActivity(i);
	}    

	
}
