<?php
# use this file for code relating to user input
if (__SMI__) die("no direct access.");

class Action {
	public static $action;

	public static $unblocked = array(
		'Sign Up' => true,
	);

	public static $actions = array(
		'' => 'home',
		'Log In' => 'login',
		'Log Out' => 'logout',
		'Sign Up' => 'signup',
		'Edit Researcher' => 'researcheredit',
		'Save Researcher Profile' => 'researchersave',
		'Create Study' => 'createstudy',
		'Show Study' => 'showstudy',
		'Save Study' => 'savestudy',
	);

	public static function get() {
		return $_REQUEST['action'];
	}

	public static function valid() {
		return self::$actions[$_REQUEST['action']] ? true : false;
	}

	public static function unblocked() {
		if (!isset(self::$action)) 
			self::$action = $_REQUEST['action'];
		return self::$unblocked[self::$action] ? true : false;
	}
}
	
class Doit {
	private $actions;
	private $error;

	public function __construct() {
		$this->actions = Action::$actions;
	}

	public function process($action) {
		$func = $this->actions[$action];
		if (empty($func) or !method_exists('Doit',$func)) $func = $this->actions[''];
		return $this->$func();
	}
	
	public function err(Exception $e=null) {
		if ($e) $this->error = $e->getMessage();
		return $this->error;
	}

	# methods that check user input and do something
	# they always return the name of a smarty template to show
	public function home() {
		return 'home.tpl';
	}

	public function login() {
		$user = new Login;
		if ($user->valid()) return 'home.tpl';
		else return 'login.tpl';
	}

	public function logout() {
		session_unset();
		return 'login.tpl';
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
			if ($r->ins( 
				array(
					'email' => $_POST['email'], 
					'password' => md5($_POST['password'])) ) === false) 
				throw new Exception($r->err());

			$rid = $r->getid();
			$_SESSION['user'] = $r->getone($rid);
			return 'home.tpl';
 
		} catch (Exception $e) {
			$this->err($e);
			return 'login.tpl';
		}
	}

	public function researchersave() {
		# note that all this input is relatively free form so just having the db escape it is ok
		$r = new Researcher;
		$rid = $_SESSION['user']['researcher_id'];
		$r->upd(
			$rid,
			array(
				'firstname' => $_POST['firstname'],
				'lastname' => $_POST['lastname'],
				'institution' => $_POST['institution'],
				'position' => $_POST['position'],
				'phone' => $_POST['phone'],
			)
		);
		$_SESSION['user'] = $r->getone($rid);
		return 'researcher.tpl';
	}

	public function researcheredit() {
		return 'researcher.tpl';
	}

	public function createstudy() {
		return 'study.tpl';
	}

	public function showstudy() {
		if (Check::isd($_REQUEST['study_id'],($empty=false))) 
			View::assign('study_id',$_REQUEST['study_id']);
		return 'study.tpl';
	}

	public function savestudy() {
		try {
			# check login
			if (!Check::isd($rid = $_SESSION['user']['researcher_id'])) 
				throw new Exception('invalid researcher id!');

			# check input values
			foreach ($_POST as $pfield => $pvalue) {
				$_POST[$pfield] = trim($pvalue);
			}
			if (empty($_POST['study_title'])) 
				throw new Exception('need a study title!');

			if (!Check::isdate($_POST['startdate'],false)) 
				throw new Exception('bad startdate format should be YYYY-MM-DD');

			if (!Check::isdate($_POST['enddate'],false)) 
				throw new Exception('bad enddate format should be YYYY-MM-DD');

			# first create or save the study
			$s = new Study;
			if (Check::isd($_POST['study_id'],($empty=false))) {

				$study_id = $_POST['study_id'];
				unset($_POST['study_id']);

				if ($s->upd($study_id,$_POST) === false) throw new Exception($s->err());
			} else {
				if ($s->ins($_POST) === false) throw new Exception($s->err());
				$study_id = $s->getid();
			}

			# associate the study with the researcher
			$r = new Research;
			if ($r->ins( array( 'study_id' => $study_id, 'researcher_id' => $rid ) ) === false) 
				throw new Exception($r->err());

			View::assign('study_id', $study_id);
			return 'study.tpl';

		} catch (Exception $e) {
			$this->err($e);
			View::assign('error',$this->error);
			return 'error.tpl';
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
		return is_array($this->data) and isset($this->data['email']);
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
		$s = trim($s);
		return preg_match('#^\w[\w\.\-]*@\w[\w\.\-]*\.\w+$#', $s);
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

