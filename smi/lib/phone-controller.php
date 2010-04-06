<?php

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
			if (Check::isdatetime($_REQUEST['timestamp']) == false)
				$this->err .= "bad timestamp. ";
			if (preg_match('/^[a-f\d]{32}$/',$_REQUEST['password']) == false)
				$this->err .= "bad password format. ";
			if (preg_match('/[\w\-\.]+@[\w\-\.]+/', $_REQUEST['email']) == false)
				$this->err .= "bad email username. ";
			if ($this->err) throw new Exception($this->err);
			$print = parent::process($a);
			return $print;
		} catch (Exception $e) {
			return "ERROR : $a {$e->getMessage()}";
		}
	}
		
	function doNothing() {
		return "possible actions (do=...):\n".var_export($this->actions,true).
			"\nrequest data:\n".var_export($_REQUEST,true);
	}
		
	function getTaskList() {	
		$s = new PhoneSchedule;
		return $s->tasklist2xml($_REQUEST['study_id']);
	}
	//valid input, sanity check, if it really needs to do anything, action
	
	function getTask() {
		$s = new PhoneTask;	
		return $s->forms2html($_REQUEST['task_id'],$_REQUEST['study_id']);
	}
	
	function sendData() {
		return var_export($_REQUEST,true);
	}
}

