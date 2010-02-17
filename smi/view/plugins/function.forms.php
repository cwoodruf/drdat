<?php
/**
 * take our raw form data and make a data structure out of it
 * that can be used in templates
 */
function smarty_function_forms($params,&$smarty) {
	if (!Check::digits($params['task_id'],($empty=false))) return;
	$t = new Task;
	$smarty->assign('forms', $t->parseforms($params['task_id']));
}

