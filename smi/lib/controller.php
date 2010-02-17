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
		'Confirm Hide Study' => 'confirmhidestudy',
		'Hide Study' => 'hidestudy',
		'Confirm Remove Task' => 'confirmremovetask',
		'Remove Task' => 'removetask',
		'Create Task' => 'createtask',
		'Show Task' => 'showtask',
		'Save Task' => 'savetask',
		'Save Schedule' => 'saveschedule',
		'Create Taskitem' => 'createtaskitem',
		'Show Taskitem' => 'showtaskitem',
		'Save Taskitem' => 'savetaskitem',
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
		if (Check::digits($_REQUEST['study_id'],($empty=false))) 
			View::assign('study_id',$_REQUEST['study_id']);
		return 'study.tpl';
	}

	public function confirmhidestudy() {
		View::assign('data',
			array('study_id' => $_REQUEST['study_id'], 'visible' => 0)
		);
		View::assign('backurl',"index.php");
		View::assign('question',"Really hide this study? Study will not be deleted.");
		View::assign('action','Hide Study');
		return 'confirm.tpl';
	}

	public function hidestudy() {
		try {
			if (!Check::digits($rid = $_SESSION['user']['researcher_id'])) 
				throw new Exception('invalid researcher id!');
			if (!Check::digits(($study_id = $_POST['study_id']),($empty=false))) 
				throw new Exception('invalid study id!');
			$r = new Research;

			$visible = $_REQUEST['visible'] ? 1 : 0;
			if ($r->upd(
				array('researcher_id' => $rid,'study_id' => $study_id),
				array('visible'=> $visible)
			   ) === false) {
				throw new Exception($r->err());
			}
			return 'home.tpl';

		} catch (Exception $e) {
			$this->err($e);
			View::assign('error',$this->error);
			return 'error.tpl';
		}
			
	}

	public function savestudy() {
		try {
			# check login
			if (!Check::digits($rid = $_SESSION['user']['researcher_id'])) 
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

			list($_POST['startdate'],$_POST['enddate']) = 
				Check::order($_POST['startdate'],$_POST['enddate']);

			# first create or save the study
			$s = new Study;
			if (Check::digits($_POST['study_id'],($empty=false))) {

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

	public function confirmremovetask() {
		View::assign('data',
			array('study_id' => $_REQUEST['study_id'], 'task_id' => $_REQUEST['task_id'])
		);
		View::assign('backurl',"index.php?action=Show+Study&study_id={$_REQUEST['study_id']}");
		View::assign('question',"Really remove task from this study? Task will not be deleted.");
		View::assign('action','Remove Task');
		return 'confirm.tpl';
	}
	/**
	 * removes the schedule for the task but doesn't delete the task
	 */
	public function removetask() {
		try {
			if (!Check::digits($_REQUEST['study_id'],($empty=false))) 
				throw new Exception("bad study id!");
			else $study_id = $_REQUEST['study_id'];

			if (!Check::digits($_REQUEST['task_id'])) 
				throw new Exception("bad task id!");
			else $task_id = $_REQUEST['task_id'];
			$s = new Schedule;

			if ($s->upd(
				array('study_id' => $study_id, 'task_id' => $task_id),
				array('startdate' => '0000-00-00', 'enddate' => '0000-00-00')
			   ) === false) 
				throw new Exception($s->err());

			View::assign('study_id',$study_id);
			return 'study.tpl';

		} catch (Exception $e) {
			$this->err($e);
			View::assign('error',$this->error);
			return 'error.tpl';
		}
	}

	public function createtask() {
		if (Check::digits($_REQUEST['study_id'],($empty=false))) 
			View::assign('study_id',$_REQUEST['study_id']);
		return 'task.tpl';
	}

	public function showtask() {
		if (Check::digits($_REQUEST['study_id'],($empty=false))) 
			View::assign('study_id',$_REQUEST['study_id']);
		if (Check::digits($_REQUEST['task_id'],($empty=false))) 
			View::assign('task_id',$_REQUEST['task_id']);
		return 'task.tpl';
	}

	public function savetask() {
		try {
			if (!Check::digits($_POST['study_id'],($empty=false))) 
				throw new Exception("bad study id!");
			else $study_id = $_POST['study_id'];

			if (!Check::digits($_POST['task_id'])) 
				throw new Exception("bad task id!");
			else $task_id = $_POST['task_id'];

			// start and end date are taken from the study itself
			if (Check::isdate($_POST['startdate'])) 
				$startdate = $_POST['startdate'];
			if (Check::isdate($_POST['enddate'])) 
				$enddate = $_POST['enddate'];
			list($startdate,$enddate) = Check::order($startdate,$enddate);

			unset($_POST['startdate']);
			unset($_POST['enddate']);
			unset($_POST['study_id']);
			unset($_POST['task_id']);


			$t = new Task;
			if ($task_id) {			
				if ($t->upd($task_id,$_POST) === false) 
					throw new Exception($t->err());
			} else {
				if ($t->ins($_POST) === false) 
					throw new Exception($t->err());
				$task_id = $t->getid();
				$s = new Schedule;
				if ($s->ins(
					array(
						'task_id' => $task_id, 
						'study_id' => $study_id,
						'enddate' => $enddate,
						'startdate' => $startdate,
					)
					) === false) {
					throw new Exception($s->err());
				}
			}

			View::assign('task_id',$task_id);
			View::assign('study_id',$study_id);
			return 'task.tpl';

		} catch (Exception $e) {
			$this->err($e);
			View::assign('error',$this->error);
			return 'error.tpl';
		}
	}

	public function saveschedule() {
		try {
			if (!Check::digits($_POST['study_id'],($empty=false))) 
				throw new Exception("bad study id!");
			else $study_id = $_POST['study_id'];

			if (!Check::digits($_POST['task_id'])) 
				throw new Exception("bad task id!");
			else $task_id = $_POST['task_id'];

			// start and end date are taken from the study itself
			if (Check::isdate($_POST['startdate'])) 
				$startdate = $_POST['startdate'];
			else throw new Exception("bad startdate");

			if (Check::isdate($_POST['enddate'])) 
				$enddate = $_POST['enddate'];
			else throw new Exception("bad enddate");

			list($startdate,$enddate) = Check::order($startdate,$enddate);

			$st = new Study;
			$study = $st->getone($study_id);
			if ($startdate < $study['startdate']) $startdate = $study['startdate'];
			if ($enddate > $study['enddate']) $enddate = $study['enddate'];

			$timesofday = trim($_POST['timesofday']);
			if (!preg_match('#^(?:\d\d?\:\d\d;|\d\d?\:\d\d$)*$#',$timesofday)) {
				throw new Exception("bad timesofday - format HH:MM;...");
			}

			$s = new Schedule;
			if ($s->upd(
				array('task_id'=>$task_id,'study_id'=>$study_id),
				array('startdate'=>$startdate,'enddate'=>$enddate,'timesofday'=>$timesofday)
			   ) === false) {
				throw new Exception($s->err());
			}

			View::assign('task_id',$task_id);
			View::assign('study_id',$study_id);
			return 'task.tpl';

		} catch (Exception $e) {
			$this->err($e);
			View::assign('error',$this->error);
			return 'error.tpl';
		}
	}

	public function createtaskitem() {
		return 'taskitem.tpl';
	}

	public function showtaskitem() {
		return 'taskitem.tpl';
	}

	public function savetaskitem() {
		return 'taskitem.tpl';
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

