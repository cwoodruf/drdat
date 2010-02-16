<?php
/**
 * show a list of study links
 */
function smarty_function_studies($params,&$smarty) {
	$s = new Study;
	$smarty->assign('studies', $s->studies($_SESSION['user']['researcher_id']));
}

