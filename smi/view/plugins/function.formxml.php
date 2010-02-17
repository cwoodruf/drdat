<?php
/**
 * take our raw form data and make an xml representation of it
 * that should be exactly what gets sent to the phones
 */
function smarty_function_formxml($params,&$smarty) {
	if (!Check::digits($params['task_id'],($empty=false))) return;
	# study_id is needed for the scheduling information for the task
	if (!Check::digits($params['study_id'],($empty=false))) return;
	$t = new Task;
	return htmlentities($t->forms2xml($params['task_id'],$params['study_id']));
}

