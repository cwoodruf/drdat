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

class SMIAction extends DoIt {
	/**
	 * actions that don't require login
	 */
	public $unblocked = array(
		'Sign Up' => true,
	);

	/**
	 * every action with its associated method
	 */
	public $actions = array(
		'' => 'home',
		'Log In' => 'login',
		'Log Out' => 'logout',
		'Sign Up' => 'signup',
		'Edit Researcher' => 'researcheredit',
		'Save Researcher Profile' => 'researchersave',
		'Researcher Password' => 'researcherpass',
		'Save Researcher Password' => 'saveresearcherpass',
		'Create Study' => 'createstudy',
		'Show Study' => 'showstudy',
		'Save Study' => 'savestudy',
		'Confirm Hide Study' => 'confirmtogglestudy',
		'Hide Study' => 'togglestudy',
		'Confirm Activate Study' => 'confirmtogglestudy',
		'Activate Study' => 'togglestudy',
		'Confirm Remove Task' => 'confirmtoggletask',
		'Remove Task' => 'toggletask',
		'Confirm Activate Task' => 'confirmtoggletask',
		'Activate Task' => 'toggletask',
		'Create Task' => 'createtask',
		'Show Task' => 'showtask',
		'Save Task' => 'savetask',
		'Save Schedule' => 'saveschedule',
		'Edit Forms' => 'editforms',
		'Save Forms' => 'saveforms',
		'Preview Forms' => 'previewforms',
		'Preview Tasklist' => 'tasklist',
		'Participants' => 'participants',
		'Show Participant' => 'showpart',
		'Save Participant' => 'savepart',
		'Enroll Participants' => 'enrollparts',
		'Participant Password' => 'partpass',
		'Save Participant Password' => 'savepartpass',
		'Confirm Remove Participant' => 'confirmchangepartstatus',
		'Remove Participant' => 'changepartstatus',
		'Reinstate Participant' => 'changepartstatus',
	);

	# below are all the methods that check user input and do something
	# they always return the name of a smarty template to show
	# note: if you were writing this so that many people could work on it
	# you'd want to split things up into different files in some way and 
	# use a class -> file -> action or something like that
	public function home() {
		# show all studies
		View::assign('all', $_REQUEST['all'] ? 1 : 0);
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
			self::$err($e);
			return 'login.tpl';
		}
	}

	public function researchersave() {
		# note that all this input is relatively free form so just having the db escape it is ok
		$r = new Researcher;
		$rid = $_SESSION['user']['researcher_id'];
		$r->upd( $rid, $_POST );
		$_SESSION['user'] = $r->getone($rid);
		return 'researcher.tpl';
	}

	public function researcherpass() {
		View::assign('user',$_SESSION['user']);
		View::assign('researcher_id',$_SESSION['user']['researcher_id']);
		return 'researcherpass.tpl';
	}

	public function saveresearcherpass() {
		try {
			if (!Check::digits($rid = $_SESSION['user']['researcher_id'])) 
				throw new Exception("invalid researcher id!");

			if ($_POST['password'] != $_POST['confirmpassword']) 
				throw new Exception("passwords don't match!");
			else $password = $_POST['password'];

			if (!Check::validpassword($password)) 
				throw new Exception(Check::err());

			$r = new Researcher;
			if ($r->upd($rid, array('password' => md5($password))) === false)
				throw new Exception($r->err());
	
			return 'home.tpl';

		} catch (Exception $e) {
			self::$err($e);
			View::assign('error',self::$error);
			return 'error.tpl';
		}
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

		View::assign('all', $_REQUEST['all'] ? 1 : 0);

		return 'study.tpl';
	}

	public function confirmtogglestudy() {
		View::assign('data',
			array(
				'study_id' => $_REQUEST['study_id'], 
				'visible' => $_REQUEST['visible'] ? 1 : 0
			)
		);
		View::assign('backurl',"index.php");
		if ($_REQUEST['visible']) {
			View::assign('question',"Really activate this study?");
			View::assign('action','Activate Study');
		} else {
			View::assign('question',"Really hide this study? Study will not be deleted.");
			View::assign('action','Hide Study');
		}
		return 'confirm.tpl';
	}

	public function togglestudy() {
		try {
			if (!Check::digits($rid = $_SESSION['user']['researcher_id'])) 
				throw new Exception('invalid researcher id!');
			if (!Check::digits(($study_id = $_POST['study_id']),($empty=false))) 
				throw new Exception('invalid study id!');
			$r = new Research;

			$visible = $_REQUEST['visible'] ? 1 : 0;
			if ($r->upd(
				array('researcher_id' => $rid,'study_id' => $study_id),
				array('visible' => $visible)
			   ) === false) {
				throw new Exception($r->err());
			}
			return 'home.tpl';

		} catch (Exception $e) {
			self::$err($e);
			View::assign('error',self::$error);
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
			self::$err($e);
			View::assign('error',self::$error);
			return 'error.tpl';
		}
	}

	public function confirmtoggletask() {
		View::assign('data',
			array(
				'study_id' => $_REQUEST['study_id'], 
				'task_id' => $_REQUEST['task_id'],
				'active' => $_REQUEST['active'] ? 1 : 0,
			)
		);

		View::assign('backurl',"index.php?action=Show+Study&study_id={$_REQUEST['study_id']}");

		if ($_REQUEST['active']) {
			View::assign('question',"Activate this task for this study?");
			View::assign('action','Activate Task');
		} else {
			View::assign('question',"Really remove task from this study? Task will not be deleted.");
			View::assign('action','Remove Task');
		}

		return 'confirm.tpl';
	}

	/**
	 * removes the schedule for the task but doesn't delete the task
	 */
	public function toggletask() {
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
				array('active' => $_REQUEST['active'] ? 1 : 0)
			   ) === false) {
				throw new Exception($s->err());
			}

			View::assign('study_id',$study_id);
			return 'study.tpl';

		} catch (Exception $e) {
			self::$err($e);
			View::assign('error',self::$error);
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
			self::$err($e);
			View::assign('error',self::$error);
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
			self::$err($e);
			View::assign('error',self::$error);
			return 'error.tpl';
		}
	}

	public function editforms() {
		if (Check::digits($_REQUEST['study_id'],($empty=false))) 
			View::assign('study_id',$_REQUEST['study_id']);
		if (Check::digits($_REQUEST['task_id'],($empty=false))) 
			View::assign('task_id',$_REQUEST['task_id']);
		return 'forms.tpl';
	}

	public function saveforms() {
		try {
			if (!Check::digits($_POST['study_id'],($empty=false))) 
				throw new Exception("bad study id!");
			else $study_id = $_POST['study_id'];

			if (!Check::digits($_POST['task_id'])) 
				throw new Exception("bad task id!");
			else $task_id = $_POST['task_id'];

			$t = new Task;
			if ($t->upd($task_id, array('formtext' => trim($_POST['formtext']))) === false)
				throw new Exception($t->err());

			View::assign('study_id',$study_id);
			View::assign('task_id',$task_id);
			return 'formpreview.tpl';

		} catch (Exception $e) {
			self::$err($e);
			View::assign('error',self::$error);
			return 'error.tpl';
		}
	}

	public function previewforms() {
		if (Check::digits($_REQUEST['study_id'],($empty=false))) 
			View::assign('study_id',$_REQUEST['study_id']);
		if (Check::digits($_REQUEST['task_id'],($empty=false))) 
			View::assign('task_id',$_REQUEST['task_id']);
		return 'formpreview.tpl';
	}

	public function tasklist() {
		if (Check::digits($_REQUEST['study_id'],($empty=false))) 
			View::assign('study_id',$_REQUEST['study_id']);
		return 'tasklist.tpl';
	}

	public function participants() {
		if (Check::digits($_REQUEST['study_id'],($empty=false))) 
			View::assign('study_id',$_REQUEST['study_id']);

		View::assign('all', $_REQUEST['all'] ? 1 : 0);

		return 'participants.tpl';
	}

	public function showpart() {
		if (Check::digits($_REQUEST['study_id'],($empty=false))) 
			View::assign('study_id',$_REQUEST['study_id']);
		if (Check::digits($_REQUEST['participant_id'],($empty=false))) 
			View::assign('part_id',$_REQUEST['participant_id']);
		return 'participant.tpl';
	}

	public function savepart() {
		try {
			if (Check::digits($_POST['study_id'],($empty=false))) 
				$study_id = $_POST['study_id'];
			else throw new Exception("bad study id!");

			if (Check::digits($_POST['participant_id'])) 
				$part_id = $_POST['participant_id'];
			else throw new Exception("bad participant id!");
			
			if (!Check::isemail($_POST['email'])) 
				throw new Exception("need a valid email!");

			if (empty($_POST['firstname'])) 
				throw new Exception("need a first name!");
				
			unset($_POST['participant_id']);
			unset($_POST['study_id']);

			$p = new Participant;
			if ($part_id) {
				if ($p->upd($part_id,$_POST) === false) {
					throw new Exception($p->err());
				}
			} else {
				if ($p->ins($_POST) === false) {
					throw new Exception($p->err());
				}
				$part_id = $p->getid();

				$e = new Enrollment;
				if ($e->ins(
					array(
						'study_id'=>$study_id,
						'participant_id'=>$part_id,
						'enrolled'=>date('Y-m-d H:i:s')
					)
				   ) === false) { 
					throw new Exception($e->err());
				}
			}

			View::assign('study_id',$study_id);
			View::assign('part_id',$part_id);

			return 'participant.tpl';

		} catch (Exception $e) {
			self::$err($e);
			View::assign('error',self::$error);
			return 'error.tpl';
		}
	}

	public function enrollparts() {
		if (Check::digits($_REQUEST['study_id'],($empty=false))) 
			View::assign('study_id',$_REQUEST['study_id']);
		return 'participants.tpl';
	}

	public function partpass() {
		if (Check::digits($_REQUEST['study_id'],($empty=false))) 
			View::assign('study_id',$_REQUEST['study_id']);
		if (Check::digits($_REQUEST['participant_id'],($empty=false))) 
			View::assign('part_id',$_REQUEST['participant_id']);
		return 'partpass.tpl';
	}

	public function savepartpass() {
		try {
			if (Check::digits($_POST['study_id'],($empty=false))) 
				$study_id = $_POST['study_id'];
			else throw new Exception("bad study id!");

			if (Check::digits($_POST['participant_id'],($empty=false))) 
				$part_id = $_POST['participant_id'];
			else throw new Exception("need a participant id!");

			if ($_POST['password'] != $_POST['confirmpassword']) 
				throw new Exception("passwords don't match!");
			else $password = $_POST['password'];

			if (!Check::validpassword($password)) 
				throw new Exception(Check::err());

			$p = new Participant;
			if ($p->upd($part_id,array('password' => md5($password))) == false)
				throw new Exception($p->err());

			View::assign('study_id',$study_id);
			return 'participants.tpl';

		} catch (Exception $e) {
			self::$err($e);
			View::assign('error',self::$error);
			return 'error.tpl';
		}
	}


	public function confirmchangepartstatus() {
		View::assign('data',
			array(
				'study_id' => $_REQUEST['study_id'],
				'participant_id' => $_REQUEST['participant_id'],
				'active' => $_REQUEST['active'] ? 1 : 0,
			) 
		);
		View::assign('backurl',"index.php?action=Participants&study_id={$_REQUEST['study_id']}");
		if ($_REQUEST['active']) {
			View::assign('question',"Really reinstate this participant?");
			View::assign('action','Reinstate Participant');
		} else {
			View::assign('question',
				"Really remove this participant? Participant record will not be deleted.");
			View::assign('action','Remove Participant');
		}
		return 'confirm.tpl';
	}

	public function changepartstatus() {
		try {
			if (Check::digits($_POST['study_id'],($empty=false))) 
				$study_id = $_POST['study_id'];
			else throw new Exception("bad study id!");

			if (Check::digits($_POST['participant_id'])) 
				$part_id = $_POST['participant_id'];
			else throw new Exception("bad participant id!");

			$active = $_POST['active'] ? 1 : 0;

			$e = new Enrollment;
			if ($e->upd(
					array(
						'participant_id' => $part_id,
						'study_id' => $study_id
					),
					array( 'active' => $active )
			    ) === false) {
				throw new Exception($e->err());
			}

			View::assign('study_id', $study_id);
			return 'participants.tpl';

		} catch (Exception $e) {
			self::$err($e);
			View::assign('error',self::$error);
			return 'error.tpl';
		}
	}
}
	
