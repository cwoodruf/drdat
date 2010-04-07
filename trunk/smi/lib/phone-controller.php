<?php

// Author Morgan Schinkel mps9@sfu.ca

class PhoneAction extends DoIt { 
	public $actions = array(
		'' => 'doNothing',
		'getTaskList' => 'getTaskList',
		'sendData' => 'sendData',
		'getTask' => 'getTask',
	);
	
   function __construct() {
		parent::__construct('do');
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
	//valid input, sanity check, if it really needs to do anything, action
	
	function getTask() {
		$s = new PhoneTask;	
		print $s->forms2xml($_REQUEST['task_id'],$_REQUEST['study_id']);
	}

	
	function sendData() {
		$xml = $_REQUEST['xmlstring'];
		//print xml;
	
 		return 'sendData';
	}
}
?>