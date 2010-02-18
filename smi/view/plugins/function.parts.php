<?php
/**
 * get data for all participants in a study and create a smarty $parts variable
 */
function smarty_function_parts($params,&$smarty) {
	if (!Check::digits($params['study_id'],($empty=false))) return;

	if ($params['all']) $active = null;
	else $active = 1;

	$p = new Participant;
	$smarty->assign(
		'parts',
		$p->studyparts(
			$_SESSION['user']['researcher_id'],
			$params['study_id'],
			$active
		)
	);
}

