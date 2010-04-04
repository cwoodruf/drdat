<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
# use this file for code relating to data access and business logic
if (__SMI__) die("no direct access.");
# dbabstracter will need the schema file 
require('lib/drdat-schema.php');

# note that this is part of another project in google code 
# http://code.google.com/p/dbabstracter4php/
require('.db/abstract-mysql.php');
require('.db/abstract-common.php');

/*
 * entity classes
 */
class Researcher extends Entity {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT, $tables['researcher'], 'researcher');
	}

	public function validate($email,$password) {
		try {
			if (!Check::isemail($email)) throw new Exception("bad email!");
			$md5pw = md5($password);
			$this->run(
				"select * from researcher where email='%s' and password='%s'",
				$email, $md5pw
			);
			$row = $this->getnext();
			$this->free();
			return $row;

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}
}

class Study extends Entity {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables['study'], 'study');
	}
	
	public function study($rid,$study_id) {
		try {
			if (!Check::digits($rid)) throw new Exception("bad researcher id!");
			if (!Check::digits($study_id)) throw new Exception("bad study id!");
			$this->run(
				"select study.*,research.visible ".
				"from study join research using (study_id) ".
				"where research.researcher_id=%u and study.study_id=%u ",
				$rid, $study_id
			);
			$row = $this->getnext();
			$this->free();
			return $row;

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}
	public function studies($rid,$all=false) {
		try {
			if (!Check::digits($rid,($empty=false))) throw new Exception("bad researcher id!");
			if (!$all) $showvisible = " and research.visible = 1 ";
		
			$this->run(
				"select study.*,research.visible ".
				"from study join research using (study_id) ".
				"where research.researcher_id=%u ".
				$showvisible.
				"order by startdate desc",
				$rid,$visible
			);
			return $this->resultarray();

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}
}

class Participant extends Entity {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables['participant'], 'participant');
	}

	public function studyparts($rid, $study_id, $active=1) {
		try {
			if (!Check::digits($rid,($empty=false))) throw new Exception("bad researcher id!");
			if (!Check::digits($study_id,($empty=false))) throw new Exception("bad study id!");
			if (is_integer($active)) $active = " and active = $active ";

			$this->run(
				"select participant.*,enrollment.active ". 
				"from participant join enrollment using (participant_id) ".
				"join research using (study_id) ".
				"where enrollment.study_id=%u and research.researcher_id=%u $active ",
				$study_id, $rid
			);
			return $this->resultarray();
			
		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}
}

class Task extends Entity {
	public $forms;
	public $form;
	public $task;

	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables['task'], 'task');
	}

	public function tasks($study_id,$rid,$all=false) {
		try {
			if (!Check::digits($study_id,($empty=false))) 
				throw new Exception("bad study id!");
			if (!Check::digits($rid)) 
				throw new Exception("bad researcher id!");
			if ($rid) $rquery = "and research.researcher_id=%u";
			if (!$all) $showall = "and schedule.active = 1 ";
			$this->run(
				"select task.*,schedule.* ".
				"from task join schedule using (task_id) ".
				"join research using (study_id) ".
				"join study using (study_id) ".
				"where schedule.study_id=%u $rquery $showall ",
				$study_id, $rid
			);
			return $this->resultarray();

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}

	/**
	 * turn the formtext field into a data structure that
	 * can be used to make xml or test the form output
	 */
	public function parseforms($task_id) {
		$this->task = $this->getone($task_id);
		return $this->parseformstring();
	}
	
	public function parseformstring() {
		$raw = trim($this->task['formtext']);
		$lines = explode("\n", preg_replace('/\r/','',$raw));
		$q = $w = $i = false;
		$instructions = array();
		$instruction = null;
		$this->form = 0;
		$items = array();
		$widget = '';
		foreach ($lines as $line) {
			if (preg_match('/^\s*#/', $line)) 
				continue;

			if (preg_match('#^--#', $line)) {
				$this->addinstruction($instruction,$widget,$items);
				if (count($this->forms[$this->form])) $this->form++;
				continue;
			}

			if (preg_match('#^(\w):(.*)#',$line, $m)) {
				$code = strtolower($m[1]);
				$details = trim($m[2]);
				switch($code) {
					case 'i': 
						$this->addinstruction($instruction,$widget,$items);
						$instruction = htmlentities($details);
					break;
					case 'w': 
						if (preg_match('#^(checkbox|dropdown|none|text)$#',$details)) {
							$widget = $details;
						}
					break;
					case 'o': 
						$items[] = urlencode($details);
					break;
				}
				continue;
			} 
			if ($instruction !== null and $widget == '') 
				$instruction .= "\n".htmlentities($line);
		}
		$this->addinstruction($instruction,$widget,$items);
		return $this->forms;
	}

	private function addinstruction(&$instruction, &$widget, &$items) {
		if ($instruction !== null) {
			$format = '';
			switch ($widget) {
				case 'checkbox':
				case 'dropdown':
					if (count($items)) {
						$format = $widget.':'.implode('&',$items);
					}
				break;
				case 'none':
				case 'text':
					$format = $widget;
				break;
				default: $format = 'none';
			}	
			$this->forms[$this->form][] = 
				array(
					'instruction' => $instruction,
					'format' => $format,
				);
		}
		$instruction = null;
		$widget = '';
		$items = array();
	}

	/**
	 * make the $forms member and output it as xml
	 * this is what gets sent when a phone requests details for a task
	 */
	public function forms2xml($task_id, $study_id) {
		# this will set the forms and task members
		$s = new Schedule;
		$sched = $s->getone(array('task_id' => $task_id, 'study_id' => $study_id));
		$this->parseforms($task_id);
		return $this->formstring2xml($sched);
	}
	
	public function formstring2xml ($sched=null) {
		
		$xml = <<<XML
<?xml version="1.0" encoding="UTF-8"?>
<task>
    <task_id>{$task_id}</task_id>
    <task_name>{$this->task['task_title']}</task_name>
    <notes>{$this->task['task_notes']}</notes>

XML;
		if (is_array($sched)) {
			$xml .= <<<XML
    <schedule>
        <start>{$sched['startdate']}</start>
        <end>{$sched['enddate']}</end>
        <frequency>{$sched['frequency']}</frequency>
        <timeofday>{$sched['timesofday']}</timeofday>
    </schedule>

XML;
		}
		$num = 0;
		foreach ($this->forms as $form) {
			$xml .= <<<XML
    <form>

XML;
			foreach ($form as $idata) {
				$num++;
				$instruction = trim($idata['instruction']);
				$xml .= <<<XML
        <taskitem>
            <taskitem_id>$num</taskitem_id>
            <instruction>$instruction</instruction>
            <format>{$idata['format']}</format>
        </taskitem>

XML;
			}
			$xml .= <<<XML
    </form>

XML;
		}
		$xml .= <<<XML
</task>

XML;
		return $xml;
	}
}

/* 
 * relation classes
 */
class Research extends Relation {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables['research'], 'research');
	}
}

class Schedule extends Relation {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables['schedule'], 'schedule');
	}

	public function tasklist($study_id) {
		try {
			if (!Check::digits($study_id,($empty=false))) 
				throw new Exception("bad study id!");
			$this->run(
				"select task.task_id, task.task_title, schedule.* ".
				"from task join schedule using (task_id) ".
				"join study using (study_id) ".
				"where study.study_id=%u ".
				"and study.startdate <= schedule.startdate ".
				"and study.enddate >= schedule.enddate ".
				"and schedule.active = 1 ",
				$study_id
			);
			return $this->resultarray();

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}

	/**
	 * what gets sent to the phone when a request for 
	 * the tasks for a participant is made
	 */
	public function tasklist2xml($study_id) {
		if (($tasklist = $this->tasklist($study_id)) === false)
			die($this->err());
			return $this->tasks2xml($tasklist);
	}
	
	public function tasks2xml($tasklist) {
		$xml = <<<XML
<?xml version="1.0" encoding="UTF-8"?>
<tasklist>
    <study_id>$study_id</study_id>

XML;
		foreach ($tasklist as $task) {
			$xml .= <<<XML
    <task>
        <task_id>{$task['task_id']}</task_id>
        <task_name>{$task['task_title']}</task_name>
        <schedule>
            <start>{$task['startdate']}</start>
            <end>{$task['enddate']}</end>
            <frequency>{$task['frequency']}</frequency>
            <timeofday>{$task['timesofday']}</timeofday>
        </schedule>
    </task>

XML;
		}
		$xml .= <<<XML
</tasklist>

XML;
		return $xml;
	}
}

class Enrollment extends Relation {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables['enrollment'], 'enrollment');
	}
}

