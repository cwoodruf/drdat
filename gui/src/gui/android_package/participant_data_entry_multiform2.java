/*-- 
--------------------------------------
Author: KOUAME Kouadio Klebair (kkk7@sfu.ca)
Code written for CMPT276 team project (DRTA), Spring 2010
--------------------------------------
*/

package gui.android_package;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class participant_data_entry_multiform2 extends Activity  implements View.OnClickListener{

	private Button SubmitButton;
	private Button PreviousButton;
	private Button CancelButton;
	private AlertDialog myDialog ;
	
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
        this.SubmitButton.setOnClickListener(this);
        
        this.PreviousButton = (Button)this.findViewById(R.id.PreviousButton);
        this.PreviousButton.setOnClickListener(this);
	        
        this.CancelButton = (Button)this.findViewById(R.id.CancelButton);
        this.CancelButton.setOnClickListener(this);
        
  
    }

    protected void submit_form1() {
    	/* Create an Intent to start * MySecondActivity. */ 
		//Intent i = new Intent( this, participant_data_entry_multiform.class); 
		/* Send intent to the OS to make * it aware that we want to start * MySecondActivity as a SubActivity. */ 
		//startActivity(i);
	}    

    protected void previous_form1() {
    	/* Create an Intent to start * MySecondActivity. */ 
		Intent i = new Intent( this, participant_data_entry_multiform.class); 
		/* Send intent to the OS to make * it aware that we want to start * MySecondActivity as a SubActivity. */ 
		startActivity(i);
	}    


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == this.SubmitButton || v == this.PreviousButton || v == this.CancelButton)
		{
				myDialog = new AlertDialog.Builder(this).create();
		    	myDialog.setTitle("DRDAT");
				if(v == this.SubmitButton){
		        	myDialog.setMessage("Do you want to submit those data?");
			       	myDialog.setButton("Yes", new DialogInterface.OnClickListener() {
			    		public void onClick(DialogInterface dialog, int which) {
			    			submit_form1();//
			    		} }); 
				} else if(v == this.PreviousButton){
					myDialog.setMessage("Do you want to go to the previous screen?");
			    	myDialog.setButton("Yes", new DialogInterface.OnClickListener() {
			    		public void onClick(DialogInterface dialog, int which) {
			    			previous_form1();//
			    		} });       	
		  		} else if(v == this.CancelButton){
		         	myDialog.setMessage("Do you want to cancel those data?");
			       	myDialog.setButton("Yes", new DialogInterface.OnClickListener() {
			    		public void onClick(DialogInterface dialog, int which) {
			    			finish();//
			    		} }); 
		  		}
		       	myDialog.setButton2("No", new DialogInterface.OnClickListener() {
		    		public void onClick(DialogInterface dialog, int which) {
		    		    //return;
		    		}}); 
		    	myDialog.show();
		}
	}	  

	
}
