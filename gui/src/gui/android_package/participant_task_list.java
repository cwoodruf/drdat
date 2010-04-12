/*-- 
--------------------------------------
Author: KOUAME Kouadio Klebair (kkk7@sfu.ca)
Code written for CMPT276 team project (DRTA), Spring 2010
--------------------------------------
*/

package gui.android_package;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;




public class participant_task_list extends Activity  implements View.OnClickListener{
	
	private GridView Participant_Task_GridView;
	private Button SelectButton;
	private Button CancelButton;
	private AlertDialog myDialog ;
	private CharSequence Task_id;
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_task_list);
        
        //load task list
    	String TaskList =	"<tasklist>"+
							"	<study_id>study_idinteger</study_id>"+
							"	<task>"+
							"        <task_id>1</task_id>"+
							"        <task_name>text1</task_name>"+
							"        <schedule>"+
							"            <start>date1</start>"+
							"            <end>enddate1</end>"+
							"            <frequency>frequencyinteger1</frequency>"+
							"            <timeofday>structured text1</timeofday>"+
							"        </schedule>"+
							"    </task>"+
							"    <task>"+
							"        <task_id>2</task_id>"+
							"        <task_name>text2</task_name>"+
							"        <schedule>"+
							"            <start>date2</start>"+
							"            <end>enddate2</end>"+
							"            <frequency>frequencyinteger2</frequency>"+
							"            <timeofday>structured text2</timeofday>"+
							"        </schedule>"+
							"    </task>"+					
							"</tasklist>";
      
  	    //create gui listener
        this.Participant_Task_GridView = (GridView) findViewById(R.id.Participant_Task_GridView);
        this.Participant_Task_GridView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				TextView tv= (TextView)arg1;
				Task_id = tv.getText();
				
				myDialog = new AlertDialog.Builder(participant_task_list.this).create();
	        	myDialog.setTitle("DRDAT");
	        	myDialog.setMessage("you have selected "+ tv.getText());
	        	//myDialog.setMessage(""+arg2);   //position
	        	myDialog.setButton2("Ok", new DialogInterface.OnClickListener() {
							        		public void onClick(DialogInterface dialog, int which) {
							        		    //return;
							        		}}); 
	        	myDialog.show();				
				
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        }); 
        this.Participant_Task_GridView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				TextView tv= (TextView)arg1;
				Task_id = tv.getText();
				
				myDialog = new AlertDialog.Builder(participant_task_list.this).create();
	        	myDialog.setTitle("DRDAT");
	        	myDialog.setMessage("you have selected "+ tv.getText());
	        	//myDialog.setMessage(""+arg2);   //position
	        	myDialog.setButton2("Ok", new DialogInterface.OnClickListener() {
							        		public void onClick(DialogInterface dialog, int which) {
							        		    //return;
							        		}}); 
	        	myDialog.show();				
			
			}
        	
        });
        //fill the gridview with  TaskList
        this.Participant_Task_GridView.setAdapter(new MyAdapter(this, TaskList));
               
        this.SelectButton = (Button)this.findViewById(R.id.SelectButton);
        this.SelectButton.setOnClickListener(this);
	        
        this.CancelButton = (Button)this.findViewById(R.id.CancelButton);
        this.CancelButton.setOnClickListener(this);
        
        
    }
	

	
    protected void select_a_Task() {
    	/* Create an Intent to start * MySecondActivity. */ 
    	if ( Task_id.equals("1")){
    		Intent i = new Intent( this, participant_data_entry.class); 
    	   	/* Send intent to the OS to make * it aware that we want to start * MySecondActivity as a SubActivity. */ 
        	startActivity(i);
    	}else if (Task_id.equals("2")){
       		Intent i = new Intent( this, participant_data_entry_multiform.class); 
    	   	/* Send intent to the OS to make * it aware that we want to start * MySecondActivity as a SubActivity. */ 
        	startActivity(i);
    	}else{
    		//unknown task
			myDialog = new AlertDialog.Builder(this).create();
        	myDialog.setTitle("DRDAT");
        	myDialog.setMessage("Only Task_id =1 and Task_id =2 are implemented so far");
        	myDialog.setButton2("Ok", new DialogInterface.OnClickListener() {
						        		public void onClick(DialogInterface dialog, int which) {
						        		    //return;
						        		}}); 
        	myDialog.show();   		
    	}
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == this.SelectButton){
			
			select_a_Task();//
			
		} else if(v == this.CancelButton){
			myDialog = new AlertDialog.Builder(this).create();
        	myDialog.setTitle("DRDAT");
        	myDialog.setMessage("Do you want to cancel?");
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

	
	
	//gridview adapter and TaskList(xmlString) parsing
	public class MyAdapter extends BaseAdapter {
		
		private Context context; 
		
		private ArrayList<String> textsList = new ArrayList<String>(); //dynamic array beneath gridview adapter
		
		public MyAdapter(Context context, String xmlString) { 
			
			NodeList tasklist;
			NodeList taskNodes;  
			NodeList taskNodeChilds;
			NodeList taskNodeChildChilds;
			NodeList taskNodeChildChildChilds;
			
		    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		    builderFactory.setValidating(false);           // and validating parser features
		    builderFactory.setNamespaceAware(true);        // Set namespace aware
		    builderFactory.setIgnoringElementContentWhitespace(true); 
			this.context = context; 
		    DocumentBuilder builder = null;
		  	Document xmlDoc = null;
			  
  		    try {
  		    	
 		    	builder = builderFactory.newDocumentBuilder();  // Create the parser
		      	xmlDoc = builder.parse(new InputSource(new StringReader(xmlString)));
		      	
		      	tasklist=  xmlDoc.getElementsByTagName("task"); //nodes <task>
			    
		      	for(int i = 0 ; i<=tasklist.getLength()-1 ; i++) { 
		      		
		      		taskNodes =  tasklist.item(i).getChildNodes() ;
		      		for(int j = 0 ; j<=taskNodes.getLength()-1 ; j++) {
			            if(taskNodes.item(j).hasChildNodes() )
		      			{	taskNodeChilds = taskNodes.item(j).getChildNodes();
			            	for(int k = 0 ; k<=taskNodeChilds.getLength()-1 ; k++) {
			  		            if (!taskNodeChilds.item(k).hasChildNodes() )
			            		{
					              if (taskNodeChilds.item(k) != null)	
					              {	
					            	  textsList.add( taskNodeChilds.item(k).getNodeValue());
					              }
					            }
			  		            else
			  		            {
			  		            	taskNodeChildChilds = taskNodeChilds.item(k).getChildNodes();
					            	for(int r = 0 ; r<=taskNodeChildChilds.getLength()-1 ; r++) {
					  		            if (!taskNodeChildChilds.item(r).hasChildNodes() )
					            		{ //<task_id>   
					  		              //<task_name>
					  		              //<notes>
							              if (taskNodeChildChilds.item(r).getNodeValue() != null)	
							              {	
							            	  textsList.add(taskNodeChildChilds.item(r).getNodeValue());
							              }
							            }
					  		            else
					  		            { //<schedule>
					  		              //<form>
					  		            	taskNodeChildChildChilds = taskNodeChilds.item(r).getChildNodes();
							            	for(int s = 0 ; s<=taskNodeChildChildChilds.getLength()-1 ; s++) {
							  		            if (!taskNodeChildChildChilds.item(s).hasChildNodes() )
							            		{ //<schedule><start>
							  		              //<schedule><end>
							  		              //<schedule><frequency>
							  		              //<schedule><timeofday>
							  		              if (taskNodeChildChildChilds.item(s).getNodeValue() !=null)	
									              {	
									            	  textsList.add(taskNodeChildChildChilds.item(s).getNodeValue());
									              }
									              else
									              {//<form><taskitem>
									               //<form><taskitem>
									               //.......  
									              }
									            }
							            	}
					  		            }
					            	}
			  		            }
			            	}
			            }
		      		}
			    }
		      
		    } catch(ParserConfigurationException e) {
		    	//texts[6]="66_"+e.getMessage();
		    } catch(SAXException e) {
		    	//texts[7]="71_"+e.getMessage();
		    } catch(IOException e) {
		    	//texts[7]="72_"+e.getMessage();
		    } catch (Exception e) {
		    	//texts[7]="73_"+e.getMessage();
		    } 
  
			
		} 
		 
		public int getCount() { 
		    return textsList.size();
		} 
		 
		public Object getItem(int position) { 
		    return null; 
		} 
		 
		public long getItemId(int position) { 
		    return 0; 
		} 
		 
		public View getView(int position, View convertView, ViewGroup parent) { 
		    TextView tv; 
		    if (convertView == null) { 
		        tv = new TextView(context); 
		       // tv.setLayoutParams(new GridView.LayoutParams(85, 30)); 
		    } 
		    else { 
		        tv = (TextView) convertView; 
		    } 
		 
		    tv.setText(textsList.get(position)); 
		    return tv; 
		} 

		
	}	


}


