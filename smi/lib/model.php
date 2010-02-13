<?php
# use this file for code relating to data access and business logic
if (__SMI__) die("no direct access.");
require('lib/drdat-schema.php');

class DRDAT {
	private $schema;
	private $conn;

	function connect() {
		$this->conn = mysql_connect(DRDAT_DBHOST, DRDAT_DBLOGIN, DRDAT_DBPW);
		if (!$this->conn) die("can't connect: ".mysql_error());
		$res = mysql_select_db(DRDAT_DB, $this->conn);
		if (!$res) die("can't select database: ".mysql_error());
		return $this->conn;
	}
}

class Researcher extends DRDAT {
	__construct() {
		$this->schema = $tables['researcher'];
		$this->connect();
	}	
}

