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




public class participant_task_list extends Activity  {
	
	private Button SelectButton;
	private Button CancelButton;
	private AlertDialog myDialog ;
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_task_list);
    	
    	
       
    	Bundle bundle = getIntent().getExtras();
    	String default_keyword = bundle.getString( "EMAIL");
    	// set the result to be returned. 
    	//setResult(RESULT_OK, null);   
    	
  	   
    	// if(default_keyword.equals("true")) {
    		
			myDialog = new AlertDialog.Builder(this).create();
        	myDialog.setTitle("DRDAT");
        	myDialog.setMessage(default_keyword);
        	//myDialog.setMessage("true");
        	myDialog.setButton2("OK", new DialogInterface.OnClickListener() {
						        		public void onClick(DialogInterface dialog, int which) {
						        		    //return;
						        		}}); 
        	myDialog.show();

    	/*}else{
			myDialog = new AlertDialog.Builder(this).create();
        	myDialog.setTitle("DRDAT");
        	myDialog.setMessage(default_keyword+" wwww");
        	myDialog.setButton2("OK", new DialogInterface.OnClickListener() {
						        		public void onClick(DialogInterface dialog, int which) {
						        		    //return;
						        		}}); 
        	myDialog.show(); 
    	}*/
    	
        this.SelectButton = (Button)this.findViewById(R.id.SelectButton);
               this.SelectButton.setOnClickListener(new View.OnClickListener() {
											        //@Override
											        public void onClick(View v) {
											        	select_a_Task();//
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

    protected void select_a_Task() {
    	/* Create an Intent to start * MySecondActivity. */ 
    	//Intent i = new Intent( this, participant_data_entry.class); 
    	Intent i = new Intent( this, participant_data_entry_multiform.class); 
    	/* Send intent to the OS to make * it aware that we want to start * MySecondActivity as a SubActivity. */ 
    	startActivity(i);
    	}

	

}
