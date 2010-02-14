<?php
# use this file for code relating to data access and business logic
if (__SMI__) die("no direct access.");
require('lib/drdat-schema.php');

abstract class Mysql {
	private $conn;
	private $schema;
	private $error;
	private $query;
	private $result;
	private $lastid;

	public function __construct() {
		$this->connect();
	}

	/**
	 * each group of entities should have a common way to connect to the db
	 */
	public abstract function connect();

	/**
	 * insert update delete and select operations should be defined for each entity
	 */
	public abstract function ins($data);
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
				$sprintflist[] = "'".$this->quote($arg)."'";
			}
			$qbuilder = 'return sprintf($this->query,'.implode(',',$sprintflist).');';
			$this->query = eval($qbuilder);
		} 
		$this->result = mysql_query($this->query,$this->conn);
		if (!$this->result) throw new Exception("query run error: ".mysql_error($this->conn));
		return $this->result;
	}

	/**
	 * make a basic array of a result set
	 * run should have been run first to create a result set
	 */
	public function resultarray($keep=false) {
		if (!is_resource($this->result)) return false;
		while ($row = mysql_fetch_assoc($this->result,$this->conn)) {
			$out[] = $row;
		}
		if (!$keep) $this->free();
		return $out;
	}
	/**
	 * run the mysql get_last_insert_id() function
	 */
	public function getid() {
		$this->free();
		$this->run("select get_last_insert_id()");
		$row = $this->getnext();
		$this->free();
		return $this->lastid = $row[0];
	}
	/**
	 * simply get the next row
	 */
	public function getnext() {
		if (!is_resource($this->result)) return false;
		return mysql_fetch_assoc($this->result);
	}
	/**
         * clear a query result
	 */
	public function free() {
		if (!is_resource($this->result)) return false;
		return mysql_free_result($this->result);
	}
	/**
	 * run input through a string cleanup function
	 */
	public function quote($str,$quote=null) {
		return $quote.mysql_real_escape_string($str,$this->conn).$quote;
	}
	/** 
	 * save an error message or print it
	 */
	public function err(Exception $e = null) {
		if ($e != null) {
			$this->error = $e->getMessage();
		}
		return $this->error;
	} 
}

abstract class DRDAT extends Mysql {
	private $tables; 
	private $table;
	private $key;
		
	public function __construct($tb) {
		global $drdat_tables; 
		$this->tables = $drdat_tables;

		parent::__construct();

		if ($tb and is_array($this->tables[$tb])) {
			$this->table = $tb;
			$this->schema = $tables[$tb];
			if (isset($tables[$tb]['PRIMARY KEY'])) $this->key = $tables[$tb]['PRIMARY KEY'];
			else $this->key = $tb."_id";
		}
	}

	public function connect() {
		$this->conn = mysql_connect(DRDAT_DBHOST, DRDAT_DBLOGIN, DRDAT_DBPW);
		if (!$this->conn) throw new Exception("can't connect: ".mysql_error($this->conn));
		$res = mysql_select_db(DRDAT_DB, $this->conn);
		if (!$res) throw new Exception("can't select database: ".mysql_error($this->conn));
		return $this->conn;
	}

	public function ins($data) {
		try {
			if (!preg_match('#^\w+$#', $this->table)) 
				throw new Exception("missing valid table name in ins!");
			foreach ($this->schema as $field => $fdata) {
				if ($fdata['insert ignore']) continue;
				$idata[$field] = $this->quote($data['field'],"'");
			}
			$insert = "insert into {$this->table} (".implode(",",array_keys($idata)).") ".
					"values (".implode(",",array_values($idata)).")";
			$this->run($insert);
			return $this->getid();
		} catch(Exception $e) {
			$this->err($e);
			return false;
		}
	}

	public function upd($id,$data) {
		try {
			if (!preg_match('#^\w+$#', $this->table)) 
				throw new Exception("missing valid table name in upd!");
			foreach ($this->schema as $field => $fdata) {
				$udata[$field] = "$field=".$this->quote($data['field'],"'");
			}
			$update = "update {$this->table} set ".implode(",", $udata)." where {$this->key}=%u";
			$this->run($update,$id);
			return $this->result;
		} catch(Exception $e) {
			$this->err($e);
			return false;
		}
	}

	public function del($id) {
		try {
			if (!preg_match('#^\w+$#', $this->table)) 
				throw new Exception("missing valid table name in upd!");
			$this->run("delete from {$this->table} where {$this->key}=%u", $id);
			return $this->result;
		} catch(Exception $e) {
			$this->err($e);
			return false;
		}
	}

	public function getall() {
		try {
			if (!preg_match('#^\w+$#', $this->table)) 
				throw new Exception("missing valid table name in upd!");
			$this->run("select * from {$this->table} order by lastname, firstname");
			return $this->resultarray();
		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}

	public function getone($id) {
		try {
			if (!preg_match('#^\w+$#', $this->table)) 
				throw new Exception("missing valid table name in upd!");
			$this->run("select * from {$this->table} where {$this->key}=%u", $id);
			return $this->resultarray();
		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}
}

/**
 * with relations you need to keep track of more than one table so 
 * we need to add some functionality to handle that gracefully
 */
class DRDATrel extends DRDAT {
	private $relates;

	public function __construct($tb,$relates) {
		global $drdat_tables; 
		parent::__construct($tb);
		if (is_array($relates)) {
			foreach($relates as $table) {
				if ($this->tables[$table]) $this->relates[] = $table;
			}
			# this should not happen but to avoid confusion ...
			if (!is_array($this->key)) {
				$this->key = array($this->key);
			}
		}
	}

	/**
	 * insert update delete and select operations should be defined for each entity
	 * since these are identified by more than one value $id is necessarily an array
	 */
	public function ins($data) { 
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

/*
 * entity classes
 */
class Researcher extends DRDAT {
	public function __construct() {
		parent::__construct('researcher');
	}
}

class Study extends DRDAT {
	public function __construct() {
		parent::__construct('study');
	}
}

class Participant extends DRDAT {
	public function __construct() {
		parent::__construct('participant');
	}
}

class Task extends DRDAT {
	public function __construct() {
		parent::__construct('task');
	}
}

class Taskitem extends DRDAT {
	public function __construct() {
		parent::__construct('taskitem');
	}
}

class Schedule extends DRDATrel {
	public function __construct() {
		parent::__construct('schedule',array('study','task'));
	}
}

/* 
 * relation classes
 *
 * this class is not a simple relation as we want to keep the order of the forms
 * for the task not just the relation between task and taskitem
 */
class Form extends DRDATrel {
	public function __construct() {
		parent::__construct('form',array('task','taskitem'));
	}
}

class Enrollment extends DRDATrel {
	public function __construct() {
		parent::__construct('enrollment',array('study','participant'));
	}
}

