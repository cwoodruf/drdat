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
			if (($this->timestamp=Check::isdatetime($_REQUEST['timestamp'])) == false)
				$this->err .= "bad timestamp. ";
			if (preg_match('/^[a-f\d]{32}$/',$this->password) == false)
				$this->err .= "bad password format. ";
			if (preg_match('/[\w\-\.]+@[\w\-\.]+/', $this->email) == false)
				$this->err .= "bad email username. ";
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
		
		//print tasklist2xml($_REQUEST['study_id']);;
	}
	
	
	
	function getTask() {
		$s = new Task;	
		print $s->forms2xml($_REQUEST['task_id'],$_REQUEST['study_id']);
	}

	
	function sendData() {
		//$xml = $_REQUEST['xmlstring'];
		//print xml;
	
 		return 'sendData';
	}
}
?>