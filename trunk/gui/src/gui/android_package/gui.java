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




public class gui extends Activity implements View.OnClickListener{
	
	private Button LoginButton;
	private Button closeButton;
	private AlertDialog myDialog ;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	
        this.LoginButton = (Button)this.findViewById(R.id.LoginButton);
        this.LoginButton.setOnClickListener(this);
        
        this.closeButton = (Button)this.findViewById(R.id.closeButton);
        this.closeButton.setOnClickListener(this);
        
    }
    
    static final int SEND_EMAIL_REQUEST = 0;
    
    protected void launchParticipant_Task_list() {
    	/* Create an Intent to start * MySecondActivity. */ 
    	//Intent i = new Intent( this, participant_task_list.class); 
       	//Intent i = new Intent(Intent.ACTION_SEND, null, null, null); 
     	Intent i = new Intent(Intent.ACTION_SEND, null, this, participant_task_list.class); 
     	//Intent i = new Intent(Intent.ACTION_SEND, null, this, participant_data_entry_multiform.class); 
     	//Intent i = new Intent(Intent.ACTION_SEND, null, this, cl_gui_data_exchange.class); 
                  	
    	Bundle b = new Bundle();
    	b.putString("EMAIL", "ANY STRING BSJSJM"); /* Attach Bundle to our Intent. */ 
    	i.putExtras(b);
    	//Log.d("test2","2aazzzzzzz"); 	
    	/* Send intent to the OS to make * it aware that we want to start * MySecondActivity as a SubActivity. */ 
    	//startService(i);
    	//bindService(i, null, 0);
    	startActivity(i);
       	//startActivityForResult( i,  SEND_EMAIL_REQUEST);
    }
    
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	
    	if (requestCode == SEND_EMAIL_REQUEST) { 
    		if (resultCode == RESULT_OK) {   
    			
    			myDialog = new AlertDialog.Builder(this).create();
            	myDialog.setTitle("DRDAT");
            	myDialog.setMessage("true45");
            	myDialog.setButton2("Ok", new DialogInterface.OnClickListener() {
    						        		public void onClick(DialogInterface dialog, int which) {
    						        		    //return;
    						        		}}); 
            	myDialog.show();
    		} 
    	} 	

    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == this.LoginButton){
			launchParticipant_Task_list();
		
			
		} else if(v == this.closeButton){
			myDialog = new AlertDialog.Builder(this).create();
        	myDialog.setTitle("DRDAT");
        	myDialog.setMessage("Do you want to close?");
        	myDialog.setButton("Yes", new DialogInterface.OnClickListener() {
						        		public void onClick(DialogInterface dialog, int which) {
						        			finish();
						        		} }); 
        	myDialog.setButton2("No", new DialogInterface.OnClickListener() {
						        		public void onClick(DialogInterface dialog, int which) {
						        		    //return;
						        		}}); 
        	
        	myDialog.show();
		}
	}
}