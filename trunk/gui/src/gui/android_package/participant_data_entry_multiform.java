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
import android.widget.Button;

public class participant_data_entry_multiform extends Activity  implements View.OnClickListener{

	private Button NextButton;
	private Button CancelButton;
	private AlertDialog myDialog ;
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_data_entry_multiform);
        
 
	        this.NextButton = (Button)this.findViewById(R.id.NextButton);
	        this.NextButton.setOnClickListener(this);

	        this.CancelButton = (Button)this.findViewById(R.id.CancelButton);
	        this.CancelButton.setOnClickListener(this);
	        
       
    }

    protected void next_form2() {
    	/* Create an Intent to start * MySecondActivity. */ 
    	Intent i = new Intent( this, participant_data_entry_multiform2.class); 
    	/* Send intent to the OS to make * it aware that we want to start * MySecondActivity as a SubActivity. */ 
    	startActivity(i);
    	}    

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == this.NextButton || v == this.CancelButton)
		{
				myDialog = new AlertDialog.Builder(this).create();
		    	myDialog.setTitle("DRDAT");
				if(v == this.NextButton){
					myDialog.setMessage("Do you want to go to the next screen?");
			       	myDialog.setButton("Yes", new DialogInterface.OnClickListener() {
			    		public void onClick(DialogInterface dialog, int which) {
			    			next_form2();//
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
