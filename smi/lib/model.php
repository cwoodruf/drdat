<?php
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
		parent::__construct($DRDAT, $tables, 'researcher');
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
		parent::__construct($DRDAT,$tables,'study');
	}
	
	public function study($rid,$study_id) {
		try {
			if (!Check::digits($rid)) throw new Exception("bad researcher id!");
			if (!Check::digits($study_id)) throw new Exception("bad study id!");
			$this->run(
				"select study.* ".
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
	public function studies($rid,$visible=0) {
		try {
			if (!Check::digits($rid,($empty=false))) throw new Exception("bad researcher id!");
			$this->run(
				"select study.* ".
				"from study join research using (study_id) ".
				"where research.researcher_id=%u ".
				"and research.visible > %u ".
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
		parent::__construct($DRDAT,$tables,'participant');
	}
}

class Task extends Entity {
	private $forms;
	private $form;
	private $task;

	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables,'task');
	}

	public function tasks($study_id,$rid,$all=false) {
		try {
			if (!Check::digits($study_id,($empty=false))) throw new Exception("bad study id!");
			if (!Check::digits($rid)) throw new Exception("bad researcher id!");
			if ($rid) $rquery = "and research.researcher_id=%u";
			if (!$all) $showall = "and schedule.startdate >= study.startdate ";
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
		$raw = trim($this->task['formtext']);
		$lines = explode("\n", preg_replace('/\r/','',$raw));
		$q = $w = $i = false;
		$instructions = array();
		$this->form = 0;
		$items = array();
		$widget = '';
		foreach ($lines as $line) {
			if (preg_match('/^\s*#/', $line)) 
				continue;

			if (preg_match('#^--#', $line)) {
				$this->addinstruction($instruction,$widget,$items);
				$this->form++;
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
			$instruction .= "\n".htmlentities($line);
		}
		$this->addinstruction($instruction,$widget,$items);
		return $this->forms;
	}

	private function addinstruction(&$instruction, &$widget, &$items) {
		if (preg_match('/\w/',$instruction)) {
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
		$instruction = '';
		$widget = '';
		$items = array();
	}
	/**
	 * make the $forms member and output it as xml
	 * php does not seem to provide a way to do this automatically?
	 */
	public function forms2xml($task_id, $study_id) {
		# this will set the forms and task members
		$s = new Schedule;
		$sched = $s->getone(array('task_id' => $task_id, 'study_id' => $study_id));
		$this->parseforms($task_id);
		$xml = <<<XML
<?xml version="1.0" encoding="UTF-8"?>
<task>
    <task_id>{$task_id}</task_id>
    <task_name>{$this->task['task_title']}</task_name>
    <notes>{$this->task['task_notes']}</notes>
    <schedule>
        <start>{$sched['startdate']}</start>
        <end>{$sched['enddate']}</end>
        <frequency>{$sched['frequency']}</frequency>
        <timeofday>{$sched['timesofday']}</timeofday>
    </schedule>

XML;
		foreach ($this->forms as $form) {
			$xml .= <<<XML
    <form>

XML;
			$num = 0;
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
		parent::__construct($DRDAT,$tables,'research');
	}
}

class Schedule extends Relation {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables,'schedule');
	}
}

class Enrollment extends Relation {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables,'enrollment');
	}
}

