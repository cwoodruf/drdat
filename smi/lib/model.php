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
}

class Taskitem extends Entity {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables,'taskitem');
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

/**
 * the form class is not a simple relation as we want to keep the order of the forms
 * for the task not just the relation between task and taskitem
 */
class Form extends Relation {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables,'form');
	}
}

class Enrollment extends Relation {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables,'enrollment');
	}
}

