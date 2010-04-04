<?php
// Author Morgan Schinkel
//

class PhoneSchedule extends Schedule {
	public function __construct(){
		parent::__construct();
	}
	//if (is_array($ary) and count($ary))
	
	public function tasklist2xml($study_id) {
		try {
			if ((is_array($tasklist = $this->tasklist($study_id))) === false) 
				throw new Exception("Invalid Study ID");
				
				return $this->tasks2xml($tasklist);
		} catch (Exception $e) {
			return "ERROR : {$e->getMessage()}";
		}
	}
	
}

class PhoneTask extends Task {
	
	public function parseforms($task_id) { //make MORE CHECKS & throw exceptions this is for tasks
		try {
			if (((count($this->task = $this->getone($task_id)))) == 1) 
				throw new Exception("Invalid Task ID");
		
			
				return $this->parseformstring();
		} catch (Exception $e) {
			return "ERROR : {$e->getMessage()}";
		}
	
	}
	
}