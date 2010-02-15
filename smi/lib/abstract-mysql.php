<?php

/**
 * base class for working with mysql 
 * this should be the only place where mysql specific code shows up
 * needs an array $tables with schema information - 
 * this can be generated from the mysql schema information
 */
abstract class Mysql {
	private $db;
	private $conn;
	private $schema;
	private $error;
	private $query;
	private $result;
	private $lastid;

	public function __construct($dbdata) {
		$this->connect($dbdata);
		$this->db = $dbdata;
	}

	/**
	 * each group of entities should have a common way to connect to the db
	 */
	public function connect($d) {
		$this->conn = mysql_connect($d['host'],$d['login'],$d['pw']);
		if (!$this->conn) throw new Exception("can't connect: ".mysql_error($this->conn));

		$res = mysql_select_db($d['db'], $this->conn);
		if (!$res) throw new Exception("can't select database: ".mysql_error($this->conn));

		return $this->conn;
	}


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

/**
 * class to define entities: 
 * tables that have stand alone records 
 * these tables are assumed to only have one field that is the key
 * you can define this key in the $tables array or simply use {table name}_id 
 */
abstract class Entity extends Mysql {
	private $tables; 
	private $table;
	private $key;
		
	public function __construct($db,$tb) {
		global $drdat_tables; 
		$this->tables = $drdat_tables;

		parent::__construct($db);

		if ($tb and is_array($this->tables[$tb])) {
			$this->table = $tb;
			$this->schema = $tables[$tb];
			if (isset($tables[$tb]['PRIMARY KEY'])) $this->key = $tables[$tb]['PRIMARY KEY'];
			else $this->key = $tb."_id";
		}
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
 * class to define relations: ie tables that connect other tables together
 * with relations you need to keep track of more than one table so 
 * we need to add some functionality to handle that gracefully
 */
class Relation extends Entity {
	private $relates;

	public function __construct($db,$tb,$relates) {
		global $drdat_tables; 
		parent::__construct($db,$tb);
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
	 * update delete and select operations should be defined for each relation
	 * since these are identified by more than one value $id is necessarily an array
	 */
	public function upd($id,$data) { 
		if (!is_array($id)) throw new Exception("upd: relation id is not an array");
	}
	public function del($id) { 
		if (!is_array($id)) throw new Exception("upd: relation id is not an array");
	}
	public function getone($id) { 
		if (!is_array($id)) throw new Exception("upd: relation id is not an array");
	}
}

