<?php
// Author Morgan Schinkel
//

class PhoneSchedule extends Schedule {
	public function __construct(){
		parent::__construct();
	}
	//if (is_array($ary) and count($ary))
	
	public function tasklist2xml($study_id) {
		try {
			if ((is_array($tasklist = $this->tasklist($study_id))) === false) 
				throw new Exception("Invalid Study ID");
				
				return $this->tasks2xml($study_id,$tasklist);
		} catch (Exception $e) {
			return "ERROR : {$e->getMessage()}";
		}
	}
	
}

class PhoneTask extends Task {

	public function forms2xml($task_id,$study_id) { //make MORE CHECKS & throw exceptions this is for tasks
		try {
			$s = new Schedule;
			$this->sched = $s->getone(array('task_id' => $task_id, 'study_id' => $study_id));
			if (!count($this->sched)) 
				throw new Exception("No schedule!");
			$this->parseforms($task_id);
			if (!is_array($this->forms)) 
				throw new Exception("No forms!");
			return $this->formstring2xml();
		} catch (Exception $e) {
			return "ERROR : {$e->getMessage()}";
		}
	}

	public function forms2html($task_id,$study_id) {
		try {
			$this->parseforms($task_id,'html');
			if (!is_array($this->forms)) 
				throw new Exception("No forms!");
			return $this->formstring2html();
		} catch (Exception $e) {
			return "ERROR : {$e->getMessage()}";
		}
	}
	public function formstring2html() {
		
		$formnum = 1;
		if (is_array($this->forms)) {
			foreach ($this->forms as $form) {
				$html .= <<<HTML
    <div id="form$formnum" class="form">

HTML;
				foreach ($form as $idata) {
					$instruction = trim($idata['instruction']);
					if ($idata['format']) {
						$html .= <<<HTML
        <div class="taskitem">
            <h4 class="instruction">$instruction</h4>
            <div class="format">{$idata['format']}</div>
        </div>

HTML;
					} else {
						$html .= <<<HTML
        <div class="taskitem">
            <h4 class="instruction">$instruction</h4>
        </div>

HTML;
					}
				}
				$formnum++;
				$html .= <<<HTML
    </div>
<!-- split -->

HTML;
			}
		}
		return $html;
	}
}
