<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/

/**
 * class to define entities: 
 * tables that have stand alone records 
 * these tables are assumed to only have one field that is the key
 * you can define this key in the $tables array or simply use {table name}_id 
 */
abstract class Entity extends AbstractDB {
	public $table;
	public $schema;
	public $primary;
		
	public function __construct($db,$tables,$tb) {
		parent::__construct($db);

		if ($tb and is_array($tables[$tb])) {
			$this->table = $tb;
			$this->schema = $tables[$tb];
			if (isset($tables[$tb]['PRIMARY KEY'])) 
				$this->primary = $tables[$tb]['PRIMARY KEY'];
			else $this->primary = $tb."_id";
		}
	}

	public function ins($data) {
		try {
			if (!preg_match('#^\w+$#', $this->table)) 
				throw new Exception("missing valid table name in ins!");
			$idata = array();
			foreach ($this->schema as $field => $fdata) {
				if ($this->iskey($field,$fdata)) continue;
				if (!isset($data[$field])) continue;
				$idata[$field] = $this->quote($data[$field],"'");
			}
			$insert = "insert ignore into {$this->table} (".implode(",",array_keys($idata)).") ".
					"values (".implode(",",array_values($idata)).")";
			$this->run($insert);
			return $this->result;

		} catch(Exception $e) {
			$this->err($e);
			return false;
		}
	}

	public function upd($id,$data) {
		try {
			if (!preg_match('#^\w+$#', $this->table)) 
				throw new Exception("missing valid table name in upd!");
			$udata = array();
			foreach ($this->schema as $field => $fdata) {
				if ($this->iskey($field,$fdata)) continue;
				if (!isset($data[$field])) continue;
				$udata[] = "$field=".$this->quote($data[$field],"'");
			}
			$update = "update {$this->table} set ".implode(",", $udata)." where {$this->primary}=%u";
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
			$this->run("delete from {$this->table} where {$this->primary}=%u", $id);
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
			$this->run("select * from {$this->table}");
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
			$this->run("select * from {$this->table} where {$this->primary}=%u", $id);
			$row = $this->getnext();
			$this->free();
			return $row;

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}

	public function iskey($field,$fdata) {
		if ($field == 'PRIMARY KEY') return true;
		if ($fdata['key']) return true;
		return false;
	}
}

/**
 * class to define relations: ie tables that connect other tables together
 * with relations you need to keep track of more than one table so 
 * we need to add some functionality to handle that gracefully
 */
class Relation extends Entity {

	public function __construct($db,$tables,$tb) {
		parent::__construct($db,$tables,$tb);
	}

	/**
	 * update delete and select operations should be defined for each relation
	 * since these are identified by more than one value $id is necessarily an array
	 */
	public function upd($id,$data) { 
		try {
			$args = $this->splitid($id);
			$key = array_shift($args);
			foreach ($this->schema as $field => $fdata) {
				if ($this->iskey($field,$fdata)) continue;
				if (!isset($data[$field])) continue;
				$set[] = "$field='%s'";
				$vals[] = $data[$field];
			}
			$query = "update {$this->table} set ".implode(",", $set)." where $key";
			$valskeys = array_merge(array($query),$vals,$args);
			call_user_func_array( array($this,'run'), $valskeys );
			return $this->result;

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}

	public function del($id) { 
		try {
			$args = $this->splitid($id);
			$args[0] = "delete from {$this->table} where {$args[0]}";
			call_user_func_array( array($this,'run'), $args );
			return $this->result;

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}

	public function getone($id) { 
		try {
			$args = $this->splitid($id);
			$args[0] = "select * from {$this->table} where {$args[0]}";
			call_user_func_array( array($this,'run'), $args );
			$row = $this->getnext();
			$this->free();
			return $row;

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}

	/**
	 * process an id array in such a way that its easier to use the run method with it
	 * for basic queries 
	 * @param $id - array of primary key field names and values to look for
	 * @return an array with the field string and key values as a single array
	 *         the field string is always the 0th element in the array
	 */
	public function splitid($id) {
		if (!is_array($id)) 
			throw new Exception("splitid: relation id is not an array");
		if (!is_array($this->primary)) 
			throw new Exception("splitid: primary key is not array");

		foreach ($this->primary as $field => $table) {
			if (empty($id[$field])) 
				throw new Exception("splitid: missing $field in id");
			$fields[] = $field."='%s'";
			$ids[] = $id[$field];
		}
		return array_merge( array( '('.implode(' and ', $fields).')' ), $ids );
	}
}

