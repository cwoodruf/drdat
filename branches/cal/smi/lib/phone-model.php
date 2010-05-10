<?php

class PhoneSchedule extends Schedule {
	public function __construct(){
		parent::__construct();
	}
	
	public function tasklist2xml($email,$password) {
		try {
			if ((is_array($tasklist = $this->tasklist($email,$password))) === false) 
				throw new Exception("Invalid Study ID");
				
				return $this->tasks2xml($tasklist);
		} catch (Exception $e) {
			return "ERROR : {$e->getMessage()}";
		}
	}
	
}

function insert_drdat_data($keys) {
	global $DRDAT, $tables;
	$drdat_data = new Entity($DRDAT, $tables['drdat_data'], 'drdat_data');
	$task = new Task;
	try {
		$drdat_data->ins($keys);
		$task->upd($keys['task_id'], array('forms_locked' => 1));
		return "OK ".$keys['sent'];

	} catch (Exception $e) {
		return $e->getMessage();
	}
}

