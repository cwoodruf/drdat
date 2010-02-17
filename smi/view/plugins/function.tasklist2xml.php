<?php
/**
 * return the tasklist as an xml document that 
 * would be used by the drdat app on a phone
 */
function smarty_function_tasklist2xml($params,&$smarty) {
	if (!Check::digits($params['study_id'],($empty=false))) return;
	$s = new Schedule;
	return htmlentities($s->tasklist2xml($params['study_id']));
}

