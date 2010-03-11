<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
# use this file for code relating to user input
if (__SMI__) die("no direct access.");

class Doit {
	// actions that don't require login
	public $unblocked;
	// actions and callbacks defined in subclass
	public $actions;
	// last error result
	public $error;

	public static function process() {
		# find the method from the map of action -> method
		$action = self::get();
		$func = self::$actions[$action];
		# if it doesn't exist use the default
		if (empty($func) or !method_exists($this,$func)) 
			$func = self::$actions[''];
		# run the function and return the result (a template name for the page to view)
		return self::$func();
	}
	
	public static function get() {
		return $_REQUEST['action'];
	}

	public static function valid() {
		return self::$actions[$_REQUEST['action']] ? true : false;
	}

	public static function unblocked() {
		return self::$unblocked[self::$action] ? true : false;
	}

	public static function err(Exception $e=null) {
		if ($e) self::$error = $e->getMessage();
		return self::$error;
	}

}

