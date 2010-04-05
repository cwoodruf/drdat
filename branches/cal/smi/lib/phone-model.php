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
	
	public function forms2xml($task_id,$study_id) { //make MORE CHECKS & throw exceptions this is for tasks
		try {
			$s = new Schedule;
			$sched = $s->getone(array('task_id' => $task_id, 'study_id' => $study_id));
			if (!count($sched)) 
				throw new Exception("No schedule!");
			$this->parseforms($task_id);
			if (!is_array($this->forms)) 
				throw new Exception("No forms!");
			return $this->formstring2xml($sched);

		} catch (Exception $e) {
			return "ERROR : {$e->getMessage()}";
		}
	
	}
	
}
