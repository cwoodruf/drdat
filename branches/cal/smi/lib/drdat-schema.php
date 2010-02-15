<?php
if (__SMI__) die("no direct access.");
# these constants are defined in lib/.localsettings.php
$DRDAT = array(
	'host' => DRDAT_DBHOST,
	'login' => DRDAT_DBLOGIN,
	'pw' => DRDAT_DBPW,
	'db' => DRDAT_DB,
);

# built from mysqldump of drdat database
# use this to build forms, do input checking etc.

$tables = array();

/**
 * the $tables array describes each table in the db in an abstract way
 * and is used by other objects to allow us to abstract how we work with data
 */

# entities:
$tables['participant'] = array(
  'participant_id' => array( 'type' => 'int', 'size' => 11, 'key' => true ),
  'firstname' => array( 'type' => 'varchar', 'size' => 64 ),
  'lastname' => array( 'type' => 'varchar', 'size' => 64 ),
  'phone' => array( 'type' => 'varchar', 'size' => 32 ),
  'email' => array( 'type' => 'varchar', 'size' => 128 ),
  'password' => array( 'type' => 'varchar', 'size' => 64 ),
);

$tables['researcher'] = array(
  'researcher_id' => array( 'type' => 'int', 'size' => 1, 'key' => true  ),
  'lastname' => array( 'type' => 'varchar', 'size' => 64 ),
  'firstname' => array( 'type' => 'varchar', 'size' => 64 ),
  'position' => array( 'type' => 'varchar', 'size' => 128 ),
  'institution' => array( 'type' => 'varchar', 'size' => 128 ),
);

$tables['study'] = array(
  'study_id' => array( 'type' => 'int', 'size' => 1, 'key' => true  ),
  'study_title' => array( 'type' => 'varchar', 'size' => 128 ),
  'description' => array( 'type' => 'text', 'rows' => 5, 'cols' => 60 ),
  'startdate' => array( 'type' => 'date', 'size' => 20 ),
  'enddate' => array( 'type' => 'date', 'size' => 20 ),
);

$tables['task'] = array(
  'task_id' => array( 'type' => 'int', 'size' => 1, 'key' => true  ),
  'task_title' => array( 'type' => 'varchar', 'size' => 128 ),
  'task_notes' => array( 'type' => 'text', 'rows' => 5, 'cols' => 60 ),
  'last_mnodified' => array( 'type' => 'timestamp', 'size' => 20 ),
);

$tables['taskitem'] = array(
  'taskitem_id' => array( 'type' => 'int', 'size' => 1, 'key' => true  ),
  'instruction' => array( 'type' => 'varchar', 'size' => 255 ),
  'format' => array( 'type' => 'text', 'rows' => 5, 'cols' => 60 ),
);

# relations
# associates a task with a study for a given period of time
$tables['schedule'] = array(
  'PRIMARY KEY' => array('task_id','study_id'),
  'task_id' => array( 'type' => 'int', 'size' => 11 ),
  'study_id' => array( 'type' => 'int', 'size' => 11 ),
  'startdate' => array( 'type' => 'date', 'size' => 20 ),
  'enddate' => array( 'type' => 'date', 'size' => 20 ),
  'timesofday' => array( 'type' => 'varchar', 'size' => 255 ),
  'last_mnodified' => array( 'type' => 'timestamp', 'size' => 20 ),
);

# groups task items into forms for each task
$tables['form'] = array(
  'PRIMARY KEY' => array('task_id','taskitem_id','form_ord'),
  'form_ord' => array( 'type' => 'int', 'size' => 11 ),
  'task_id' => array( 'type' => 'int', 'size' => 11 ),
  'taskitem_id' => array( 'type' => 'int', 'size' => 11 ),
);

# associates participants to a study
$tables['enrollment'] = array(
  'PRIMARY KEY' => array('participant_id','study_id'),
  'participant_id' => array( 'type' => 'int', 'size' => 11 ),
  'study_id' => array( 'type' => 'int', 'size' => 11 ),
  'enrolled' => array( 'type' => 'datetime', 'size' => 20 ),
);

