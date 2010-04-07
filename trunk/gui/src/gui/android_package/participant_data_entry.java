/*-- 
--------------------------------------
Author: KOUAME Kouadio Klebair (kkk7@sfu.ca)
Code written for CMPT276 team project (DRTA), Spring 2010
--------------------------------------
*/

package gui.android_package;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class participant_data_entry extends Activity{

	private Button SubmitButton;
	private Button CancelButton;
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_data_entry);
        
        this.SubmitButton = (Button)this.findViewById(R.id.SubmitButton);
               this.SubmitButton.setOnClickListener(new View.OnClickListener() {
											        //@Override
											        public void onClick(View v) {
											        	//
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

  

}

