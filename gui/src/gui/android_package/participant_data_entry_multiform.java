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
import android.widget.Button;

public class participant_data_entry_multiform extends Activity{

	private Button NextButton;
	private Button CancelButton;
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_data_entry_multiform);
        
        this.NextButton = (Button)this.findViewById(R.id.NextButton);
               this.NextButton.setOnClickListener(new View.OnClickListener() {
											        //@Override
											        public void onClick(View v) {
											        	next_form2();//
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

    protected void next_form2() {
    	/* Create an Intent to start * MySecondActivity. */ 
    	Intent i = new Intent( this, participant_data_entry_multiform2.class); 
    	/* Send intent to the OS to make * it aware that we want to start * MySecondActivity as a SubActivity. */ 
    	startActivity(i);
    	}    

}
