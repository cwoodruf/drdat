<?php
# use this file for code relating to user input
if (__SMI__) die("no direct access.");

$actions = array(
	'' => 'login',
	'Log In' => 'login',
	'Sign Up' => 'signup',
);
	
class Doit {
	private $actions;
	private $view;
	private $error;

	public function __construct() {
		global $actions, $smarty;
		$this->actions = $actions;
		$this->view = $smarty;
	}

	public function process($action) {
		$func = $this->actions[$action];
		if (empty($func) or !function_exists($func)) $func = $this->actions[''];
		return $this->$func();
	}
	
	public function err(Exception $e=null) {
		if ($e) $this->error = $e->getMessage();
		return $this->error;
	}

	# methods that check user input and do something
	# they always return the name of a smarty template to show
	public function login() {
		$user = new Login;
		if ($user->valid()) return 'home.tpl';
		else return 'login.tpl';
	}

	public function signup() {
		try {
			if ($_POST['email'] != $_POST['emailconfirm']) 
				throw new Exception('signup: emails do not match');
			if (!Check::isemail($_POST['email'])) 
				throw new Exception('signup: bad email format');
			if ($_POST['password'] != $_POST['passwordconfirm']) 
				throw new Exception('signup: passwords do not match');
			if (!Check::validpassword($_POST['password'])) 
				throw new Exception("signup: ".Check::err());

			$r = new Researcher;
			$r->ins( array('email' => $_POST['email'], 'password' => md5($_POST['password'])) );

			return 'home.tpl';
 
		} catch (Exception $e) {
			$this->err($e);
			return 'login.tpl';
		}
	}
}

class Login {
	private $data;

	public function __construct() {
		$this->validate();
	}

	public function validate() {
		if (is_array($_SESSION['user'])) {
			$this->data = $_SESSION['user'];
			return;
		}
		if (empty($_POST['email']) or empty($_POST['password'])) return;
		$r = new Researcher;
		$this->data = $r->validate($_POST['email'],$_POST['password']);
		$_SESSION['user'] = $this->data;
	}

	public function valid() {
		return $this->data === false ? false : true;
	}
}

class Check {
	private $error;

	public static function isw($s,$emptyok=true) {
		if ($emptyok) return preg_match('#^\w*$#', $s);
		return preg_match('#^\w+$#', $s);
	}
	public static function isemail($s,$emptyok=true) {
		if ($emptyok and empty($s)) return true;
		return preg_match('#^[\w][\w\.\-]*@[\w][\w\.\-]*\.[\w+]$#', $s);
	}
	public static function isdate($s,$emptyok=true) {
		if ($emptyok and empty($s)) return true;
		return preg_match('#^\d{4}-\d{2}-\d{2}#', $s);
	}
	public static function istime($s,$emptyok=true) {
		if ($emptyok and empty($s)) return true;
		return preg_match('#^\d{2}\:\d{2}(?:\:\d{2}|)$#', $s);
	}
	public static function isdatetime($s,$emptyok=true) {
		if ($emptyok and empty($s)) return true;
		return preg_match('#^\d{4}-\d{2}-\d{2} \d{2}\:\d{2}(?:\:\d{2}|)#', $s);
	}
	public static function isd($s,$emptyok=true) {
		if ($emptyok) return preg_match('#^\d*$#', $s);
		return preg_match('#^\d+$#', $s);
	}
	public static function validpassword($pw) {
		$rules = "passwords should be 6 or more characters and not contain spaces";
		if (strlen($pw) < 6 or preg_match('#\s#', $pw)) {
			self::err($rules);
			return false;
		}
		return true;
	}
	public static function err($msg=null) {
		if (!empty($msg)) self::$error = $msg;
		return self::$error;
	}
}

