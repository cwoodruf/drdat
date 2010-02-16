<?php
/**
 * get data for an individual study based on 
 * the user's researcher_id and a supplied study_id
 * creates $study smarty variable
 */
function smarty_function_study($params,&$smarty) {
	if (!Check::isd($params['study_id'])) {
		$smarty->assign('study',array());
		return;
	}
	$s = new Study;
	$study = $s->study(
		$_SESSION['user']['researcher_id'], 
		$params['study_id']
	);
	$smarty->assign('study',$study);
}

