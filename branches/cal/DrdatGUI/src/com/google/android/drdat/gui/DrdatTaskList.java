package com.google.android.drdat.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import android.app.Activity;
import android.util.Log;

public class DrdatTaskList {
	private Activity context;
	private String raw;
	private ArrayList<String> tasks;
	private ArrayList<Integer> task_ids;
	private final String LOG_TAG = "DRDAT TASKLIST";
	// private final String[] allTags = 
	// { "tasklist","study_id","task","task_id","task_name","schedule","start","end","frequency","timeofday" };
	private String[] relevantTags = { "task_id", "task_name" };
	
	/*
	 * example: 
	 *
    <study_id>9</study_id>
    <task>
        <task_id>9</task_id>
        <task_name>third drdat task</task_name>
        <schedule>
            <start>2009-02-28</start>
            <end>2010-11-15</end>
            <frequency>1</frequency>
            <timeofday>11:30;23:30</timeofday>
        </schedule>
    </task>
     *
	 * 
	 */

	
	public DrdatTaskList(Activity c) {
		context = c;
		raw = "";
		task_ids = new ArrayList<Integer>();
		tasks = new ArrayList<String>();
		findTasks();
	}
	
	public void findTasks() {
		URL url;
		
		try {
			url = new URL(context.getString(R.string.SmiUrl) + "/tasklist.php");
			Log.i(LOG_TAG,"findTasks: downloading " + url.toExternalForm());

			BufferedReader in = new BufferedReader(
					new InputStreamReader(url.openStream()));
			
			String str = new String();
			while ((str = in.readLine()) != null) {
				raw += str + " ";
			}
			in.close();
			parseTasks();

		} catch (Exception e) {
			Log.e(LOG_TAG,"findTasks: error "+e.toString()+": "+e.getMessage());
		}
	}
	
	public void parseTasks() throws IOException {
		if (raw.length() == 0) throw new IOException("no xml data found!");
		
		int pos = 0;
		int task_id;
		String task_name;
		
		while (pos != -1) {
			task_name = "";
			task_id = -1;
			for (String tag: relevantTags) {
				String starter = "<"+tag+">";
				
				pos = raw.indexOf(starter,pos);
				if (pos == -1) break;
				
				int end = raw.indexOf("</"+tag+">",pos);
				if (end == -1) break;
				
				pos += starter.length();
				String datum = raw.substring(pos, end).trim();

				if (tag == "task_id") {
					try {
						task_id = new Integer(datum);
					} catch (Exception e) {
						task_id = -1;
					}
				} else if (tag == "task_name") {
					task_name = datum;
				}
				pos = end;
			}
			if (task_name != "" && task_id > 0) {
				tasks.add(task_name);
				task_ids.add(task_id);
			}
		}
	}
	public String toHtml() {
		String[] task_ary = tasks.toArray(new String[tasks.size()]);
		Integer[] task_id_ary = task_ids.toArray(new Integer[task_ids.size()]);
		String html = 
			"<html><head></head>" +
			"<body><center><form " +
			"onSubmit=\"DrdatListTasks.getTask('' + this.task.options[this.task.selectedIndex].value); return false;\">" + 
			"<select name=\"task\"><option></option>\n";
		
		for (int i = 0; i < task_id_ary.length; i++) {
			int task_id = task_id_ary[i];
			String task_name = task_ary[i];
			html += "<option value=\""+task_id+"\">"+task_name+"</option>\n";
		}
		
		html += "</select><p><input type=submit value=\"Open Task\"></form></center></body></html>";
		return html;
	}
}
