<?php
/**
 * find the tasks for this particular study
 */
function smarty_function_tasks($params,&$smarty) {
	$t = new Task;
	if (!Check::digits($params['study_id'])) return;
	$smarty->assign(
		'tasks',
		$t->tasks(
			$params['study_id'],
			$_SESSION['user']['researcher_id']
		)
	);
}

