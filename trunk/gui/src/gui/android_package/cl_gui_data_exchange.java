package gui.android_package;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class cl_gui_data_exchange extends Service{

	private AlertDialog myDialog ;
	
	public void onCreate(Bundle savedInstanceState) {
		//super.onCreate(); 

		Log.d("test","aazzzzzzz");
		Bundle bundle = savedInstanceState;//this.getIntent().getExtras();
		String default_keyword = bundle.getString( "EMAIL");
		// set the result to be returned. 
		//setResult(RESULT_OK, null); 
		
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return (IBinder)intent;
	}

 	
	
    

}
