<?php
# use this file for code relating to data access and business logic
if (__SMI__) die("no direct access.");
require('lib/drdat-schema.php');

abstract class DRDAT {
	private $schema;
	private $conn;
	private $query;
	private $result;

	public function connect() {
		$this->conn = mysql_connect(DRDAT_DBHOST, DRDAT_DBLOGIN, DRDAT_DBPW);
		if (!$this->conn) throw new Exception("can't connect: ".mysql_error($this->conn));
		$res = mysql_select_db(DRDAT_DB, $this->conn);
		if (!$res) throw new Exception("can't select database: ".mysql_error($this->conn));
		return $this->conn;
	}
	/**
	 * insert update delete and select operations should be defined for each entity
	 */
	public abstract function ins($id,$data);
	public abstract function upd($id,$data);
	public abstract function del($id);
	public abstract function getall();
	public abstract function getone($id);
	/**
	 * run allows us to escape query data selectively and then run a query
	 */
	public function run() {
		$args = func_get_args();
		$this->query = array_unshift($args);
		if (is_array($args)) {
			foreach ($args as $arg) {
				$sprintflist[] = "'".mysql_real_escape_string($arg,$this->conn)."'";
			}
			$qbuilder = 'return sprintf($this->query,'.implode(',',$sprintflist).');';
			$this->query = eval($qbuilder);
		} 
		$this->result = mysql_query($this->query,$this->conn);
		if (!$this->result) throw new Exception("query run error: ".mysql_error($this->conn));
		return $this->result;
	}
}

class Researcher extends DRDAT {
	public function __construct() {
		$this->schema = $tables['researcher'];
		$this->connect();
	}

	public function ins($id,$data) {
	}

	public function upd($id,$data) {
	}

	public function del($id) {
	}

	public function getall() {
	}

	public function getone($id) {
	}
}

