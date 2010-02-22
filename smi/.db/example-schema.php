<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/

# example schema file used by dbabstracter
# you would use a schema called "myschema.php" like this:
#
# require_once('myschema.php');
# require_once('abstract-mysql.php');
# require_once('abstract-common.php');

# db login information
# these constants are defined elsewhere - preferably a location invisible to outsiders
$db = array(
	'host' => _DBHOST,
	'login' => _DBLOGIN,
	'pw' => _DBPW,
	'db' => _DB,
);


/**
 * the $tables array describes each table in the db in an abstract way
 * and is used by other objects to allow us to abstract how we work with data
 * the fields other than 'key' are ignored by dbabstracter but can be useful 
 * for automatically checking input or generating forms and are left in 
 * as a sort of serving suggestion
 * 
 * TODO: create tool for generating this array from a database schema
 */

$tables = array();

# example entity:
$tables['participant'] = array(
  'participant_id' => array( 'type' => 'int', 'size' => 11, 'key' => true ),
  'firstname' => array( 'type' => 'varchar', 'size' => 64 ),
  'lastname' => array( 'type' => 'varchar', 'size' => 64 ),
  'phone' => array( 'type' => 'varchar', 'size' => 32 ),
  'email' => array( 'type' => 'varchar', 'size' => 128 ),
  'password' => array( 'type' => 'varchar', 'size' => 64 ),
);

$tables['study'] = array(
  'study_id' => array( 'type' => 'int', size => 11, 'key' => true ),
  'startdate' => array( 'type' => 'date', size => 20 ),
  'enddate' => array( 'type' => 'date', size => 20 ),
);

# example relation
# associates participants to a study
$tables['enrollment'] = array(
  'PRIMARY KEY' => array('participant_id' => 'participant', 'study_id' => 'study'),
  'participant_id' => array( 'type' => 'int', 'size' => 11 ),
  'study_id' => array( 'type' => 'int', 'size' => 11 ),
  'enrolled' => array( 'type' => 'datetime', 'size' => 20 ),
);

