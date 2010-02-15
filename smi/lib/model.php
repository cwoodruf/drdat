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
			$row = $this->getone();
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

