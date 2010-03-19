<?php

class PhoneAction extends DoIt { 
	public $actions = array(
		'' => 'doNothing',
		'getTaskList' => 'getTaskList',
		'sendData' => 'sendData',
		'getTask' => 'getTask',
	);
	
   function __construct() {
		parent::__construct('do');
	}
		
	function doNothing() {

		
	return 'doNothing';
	}
		
	function getTaskList() {	
		return 'getTaskList';
	}
	
	function sendData() {
		return 'sendData';
	} 
	
	function getTask() {
		
		
		return 'getTask';
	}

}
?>