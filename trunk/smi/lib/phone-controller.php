<?php

// Author Morgan Schinkel mps9@sfu.ca

class PhoneAction extends DoIt { 
	public $actions = array(
		'' => 'doNothing',
		'getTaskList' => 'getTaskList',
		'sendData' => 'sendData',
		'getTask' => 'getTask',
		'lastModified' => 'lastModified'
	);
	
   function __construct() {
		parent::__construct('do');
	}
	
	function lastModified () {
		$taskid = $_REQUEST['task_id'];
		$s = new Task;
		$s->run("select * from task where task_id='%s'", $taskid);
		$result = $s->resultarray();
		if ($result){
			return $result[0]["last_modified"];
		} else {
			return 'bad task id';
		}
	}
	
	function process(&$a=null) {
		try {
			$this->err = '';
			$s = new Schedule;
			$s->run(
				"select * from participant where email='%s' and password='%s'",
				$_REQUEST['email'], $_REQUEST['password']
			);
			$valid = $s->resultarray();
			if (Check::isdatetime($_REQUEST['timestamp']) == false)
				$this->err .= "bad timestamp. ";
			if (preg_match('/^[a-f\d]{32}$/',$_REQUEST['password']) == false)
				$this->err .= "bad password format. ";
			if (preg_match('/[\w\-\.]+@[\w\-\.]+/', $_REQUEST['email']) == false)
				$this->err .= "bad email username. ";
			if (!$valid)
				$this->err .= "email and password do not match. ";
			if ($this->err) throw new Exception($this->err);
			$print = parent::process($a);
			return $print;
		} catch (Exception $e) {
			return "ERROR : $a {$e->getMessage()}";
		}
	}
		
	function doNothing() {
		return 'doNothing';
	}
		
	function getTaskList() {	
		$s = new PhoneSchedule;
		print $s->tasklist2xml($_REQUEST['study_id']);
		
	}
	
	function getTask() {
		$s = new PhoneTask;	
		print $s->forms2xml($_REQUEST['task_id'],$_REQUEST['study_id']);
	}
	
	function sendData() {
	
	$xml = '
	<data>	
	    <completed>2010-03-15 00:00:15</completed>
	    <participant_id>6</participant_id>
	    <input>
	        <taskitem>
	            <taskitem_id>1</taskitem_id>
	            <instruction>taskitem1 instruct</instruction>
	            <response>taskitem1 response</response>
	        </taskitem>
	        <taskitem>
	            <taskitem_id>2</taskitem_id>
	            <instruction>taskitem2 instruct</instruction>
	            <response>taskitem2 reponse</response>
	        </taskitem>
	    </input>
	</data>';

	$s = simplexml_load_string($xml);
 	$qdata = new Schedule;
 	$qdata->run(
				"select * from responses where taskitem_id='%s' and participant_id='%s'",
				$_REQUEST['taskitem_id'], $_REQUEST['participant_id']
	);
	
	$result = $qdata->resultarray();
	if ($result) {
		$completed = $s->completed;
		$participant_id = $s->participant_id;
		foreach ($s->input->taskitem as $taskitem_id => $id) {
		 	$taskitem_id = $id->taskitem_id;
		 	$instruction = $id->instruction;
		 	$response = $id->response;
	 	$qdata->run("update responses set completed='$completed', response='$response' where 
	 	taskitem_id='$taskitem_id' AND participant_id='$participant_id'");
		}
	} else {
		$completed = $s->completed;
		$participant_id = $s->participant_id;
		foreach ($s->input->taskitem as $taskitem_id => $id) {
		 	$taskitem_id = $id->taskitem_id;
		 	$instruction = $id->instruction;
		 	$response = $id->response;
		 	$qdata->run("insert into responses VALUES('%s', '%s', '%s', '%s', '%s')", $completed, $taskitem_id, $participant_id, $instruction, $response);
			 }
	}
		return 'sendData';
	}
	
}
?>