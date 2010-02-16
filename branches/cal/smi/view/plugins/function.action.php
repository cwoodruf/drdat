<?php
/**
 * show sanitized action name
 */
function smarty_function_action($params,&$smarty) {
	$action = Action::get();
	if ($action == 'Log In') {
		if ($_SESSION['user']) {
			return 'Home';
		}
	}
	return htmlentities(substr($action,0,VIEW_MAXFIELDSIZE));
}

