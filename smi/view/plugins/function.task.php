<?php
/**
 * get task information for the specified task
 */
function smarty_function_task($params,&$smarty) {
	$t = new Task;
	if (!Check::digits($params['task_id'],($empty=false))) return;
	$smarty->assign('task',$t->getone($params['task_id']));
}

