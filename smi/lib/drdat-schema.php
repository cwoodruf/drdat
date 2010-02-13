<?php
if (__SMI__) die("no direct access.");
# built from mysqldump of drdat database
# use this to build forms, do input checking etc.

$tables = array();

$tables['form'] = array(
  'form_ord' => array( 'type' => 'int', 'size' => 11 ),
  'task_id' => array( 'type' => 'int', 'size' => 11 ),
  'taskitem_id' => array( 'type' => 'int', 'size' => 11 ),
);

$tables['participant'] = array(
  'participant_id' => array( 'type' => 'int', 'size' => 11 ),
  'firstname' => array( 'type' => 'varchar', 'size' => 64 ),
  'lastname' => array( 'type' => 'varchar', 'size' => 64 ),
  'phone' => array( 'type' => 'varchar', 'size' => 32 ),
  'email' => array( 'type' => 'varchar', 'size' => 128 ),
  'password' => array( 'type' => 'varchar', 'size' => 64 ),
);

$tables['researcher'] = array(
  'researcher_id' => array( 'type' => 'int', 'size' => 11 ),
  'lastname' => array( 'type' => 'varchar', 'size' => 64 ),
  'firstname' => array( 'type' => 'varchar', 'size' => 64 ),
  'position' => array( 'type' => 'varchar', 'size' => 128 ),
  'institution' => array( 'type' => 'varchar', 'size' => 128 ),
);

$tables['schedule'] = array(
  'task_id' => array( 'type' => 'int', 'size' => 11 ),
  'study_id' => array( 'type' => 'int', 'size' => 11 ),
  'startdate' => array( 'type' => 'date', 'size' => 20 ),
  'enddate' => array( 'type' => 'date', 'size' => 20 ),
  'timesofday' => array( 'type' => 'varchar', 'size' => 255 ),
);

$tables['study'] = array(
  'study_id' => array( 'type' => 'int', 'size' => 11 ),
  'study_title' => array( 'type' => 'varchar', 'size' => 128 ),
  'description' => array( 'type' => 'text', 'rows' => 5, 'cols' => 60 ),
  'startdate' => array( 'type' => 'date', 'size' => 20 ),
  'enddate' => array( 'type' => 'date', 'size' => 20 ),
);

$tables['task'] = array(
  'task_id' => array( 'type' => 'int', 'size' => 11 ),
  'task_title' => array( 'type' => 'varchar', 'size' => 128 ),
  'task_notes' => array( 'type' => 'text', 'rows' => 5, 'cols' => 60 ),
);

$tables['taskitem'] = array(
  'taskitem_id' => array( 'type' => 'int', 'size' => 11 ),
  'instruction' => array( 'type' => 'varchar', 'size' => 255 ),
  'format' => array( 'type' => 'text', 'rows' => 5, 'cols' => 60 ),
);

