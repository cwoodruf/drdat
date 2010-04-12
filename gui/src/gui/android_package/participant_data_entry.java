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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class participant_data_entry extends Activity   implements View.OnClickListener{

	private Button SubmitButton;
	private Button CancelButton;
	private AlertDialog myDialog ;
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);
        	setContentView(R.layout.participant_data_entry);
        
       
	        this.SubmitButton = (Button)this.findViewById(R.id.SubmitButton);
	        this.SubmitButton.setOnClickListener(this);
		        
	        this.CancelButton = (Button)this.findViewById(R.id.CancelButton);
	        this.CancelButton.setOnClickListener(this);
	        
        
    }

    protected void submit_form1() {
    	/* Create an Intent to start * MySecondActivity. */ 
		//Intent i = new Intent( this, participant_data_entry_multiform.class); 
		/* Send intent to the OS to make * it aware that we want to start * MySecondActivity as a SubActivity. */ 
		//startActivity(i);
	}  
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == this.SubmitButton ||  v == this.CancelButton)
		{
			    myDialog = new AlertDialog.Builder(this).create();
        	    myDialog.setTitle("DRDAT");
				if(v == this.SubmitButton){
		        	myDialog.setMessage("Do you want to submit those data?");
			       	myDialog.setButton("Yes", new DialogInterface.OnClickListener() {
			    		public void onClick(DialogInterface dialog, int which) {
			    			submit_form1();//
			    		} });
				} else if(v == this.CancelButton){
		        	myDialog.setMessage("Do you want to cancel those data?");
		        	myDialog.setButton("Yes", new DialogInterface.OnClickListener() {
		        		public void onClick(DialogInterface dialog, int which) {
		        			finish();
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

