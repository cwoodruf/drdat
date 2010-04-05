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
				
				return $this->tasks2xml($tasklist);
		} catch (Exception $e) {
			return "ERROR : {$e->getMessage()}";
		}
	}
	
}

class PhoneTask extends Task {
	private function datafy($task_id,$study_id) {
		$s = new Schedule;
		$this->sched = $s->getone(array('task_id' => $task_id, 'study_id' => $study_id));
		if (!count($this->sched)) 
			throw new Exception("No schedule!");
		$this->parseforms($task_id);
		if (!is_array($this->forms)) 
			throw new Exception("No forms!");
	}
	public function forms2xml($task_id,$study_id) { //make MORE CHECKS & throw exceptions this is for tasks
		try {
			$this->datafy($task_id,$study_id);
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
		
		$html = <<<HTML
    <h3 class="task_title">{$this->task['task_title']}</h3>
HTML;
		$num = 0;
		if (is_array($this->forms)) {
			foreach ($this->forms as $form) {
				$html .= <<<HTML
    <div id="form$id" class="form">

HTML;
				foreach ($form as $idata) {
					$num++;
					$instruction = trim($idata['instruction']);
					$html .= <<<HTML
        <div class="taskitem">
            <input type="hidden" name="taskitem_id[]" value="$num">
            <input type="hidden" name="instruction[]" value="$instruction">
            <h4 class="instruction">$num $instruction</h4>
            <div class="format">{$idata['format']}</div>
        </div>

HTML;
				}
				$html .= <<<HTML
    </div>
HTML;
			}
		}
	}
}
