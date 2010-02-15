<?php
# use this file for code relating to data access and business logic
if (__SMI__) die("no direct access.");
# we need the schema file 
require('lib/drdat-schema.php');
# note that this is part of another project in google code 
# dbabstracter4php
require('db/abstract-mysql.php');

/*
 * entity classes
 */
class Researcher extends Entity {
	public function __construct() {
		global $DRDAT;
		parent::__construct($DRDAT, 'researcher');
	}
}

class Study extends Entity {
	public function __construct() {
		global $DRDAT;
		parent::__construct($DRDAT,'study');
	}
}

class Participant extends Entity {
	public function __construct() {
		global $DRDAT;
		parent::__construct($DRDAT,'participant');
	}
}

class Task extends Entity {
	public function __construct() {
		global $DRDAT;
		parent::__construct($DRDAT,'task');
	}
}

class Taskitem extends Entity {
	public function __construct() {
		global $DRDAT;
		parent::__construct($DRDAT,'taskitem');
	}
}

/* 
 * relation classes
 */
class Schedule extends Relation {
	public function __construct() {
		global $DRDAT;
		parent::__construct($DRDAT,'schedule',array('study','task'));
	}
}

/**
 * the form class is not a simple relation as we want to keep the order of the forms
 * for the task not just the relation between task and taskitem
 */
class Form extends Relation {
	public function __construct() {
		global $DRDAT;
		parent::__construct($DRDAT,'form',array('task','taskitem'));
	}
}

class Enrollment extends Relation {
	public function __construct() {
		global $DRDAT;
		parent::__construct($DRDAT,'enrollment',array('study','participant'));
	}
}

