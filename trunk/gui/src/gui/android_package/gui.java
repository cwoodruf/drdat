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
import android.view.View.OnClickListener;
import android.widget.Button;




public class gui extends Activity {
	
	private Button LoginButton;
	private Button closeButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	
        this.LoginButton = (Button)this.findViewById(R.id.LoginButton);
        this.LoginButton.setOnClickListener(new OnClickListener() {
											        //@Override
											        public void onClick(View v) {
											        	
											        	
											        	
											        	Bundle bundle = getIntent().getExtras();
											        	String default_keyword = bundle.getString( StartingSubactivities.MY_DEFAULTSTRING_ID);
											        	if(default_keyword='true') {
											        		launchParticipant_Task_list();
											        	}else{
											        		
											        	}
											        }
        									}); 
        
        this.closeButton = (Button)this.findViewById(R.id.closeButton);
        this.closeButton.setOnClickListener(new View.OnClickListener() {
											        //@Override
											        public void onClick(View v) {
											        	finish();
											        }
        									}); 
        
        
        
    }
    protected void launchParticipant_Task_list() {
    	/* Create an Intent to start * MySecondActivity. */ 
    	Intent i = new Intent( this, participant_task_list.class); 
    	/* Send intent to the OS to make * it aware that we want to start * MySecondActivity as a SubActivity. */ 
    	startActivity(i);
    	}
}