<?php
/**
 * take schema data and make an input widget
 */
function smarty_function_inputwidget($params,&$smarty) {
	# check and grab our data
	if (!is_array($params['fdata'])) return;
	else $fdata = $params['fdata'];

	if (!Check::isw($params['field'])) return;
	else $field = $params['field'];

	if (!is_array($params['study'])) $study = array();
	else $study = $params['study'];

	# configure some dimensions for the display
	if ($fdata['size'] > VIEW_MAXFIELDSIZE) $size = VIEW_MAXFIELDSIZE;
	else $size = $fdata['size'];

	if ($fdata['cols'] > VIEW_MAXFIELDSIZE) $cols = VIEW_MAXFIELDSIZE;
	else $cols = $fdata['cols'];
	
	if ($fdata['rows'] > VIEW_MAXROWS) $rows = VIEW_MAXROWS;
	else $rows = $fdata['rows'];

	if ($rows < VIEW_MINROWS) $rows = VIEW_MINROWS;
	if ($cols < VIEW_MINFIELDSIZE) $cols = VIEW_MINFIELDSIZE;

	# squash and wierdness in the data
	$value = htmlentities($study[$field]);
	if ($fdata['key']) {
		if ($value) return "$value <input type=hidden name=\"$field\" value=\"$value\">";
		return "Create new";
	}

	# output a widget
	switch ($fdata['type']) {
		case 'datetime' : 
			if (!$value) $value = date('Y-m-d H:I:S');
		case 'time' : 
			if (!$value) $value = date('H:I:S');
		case 'date' : 
			if (!$value) $value = date('Y-m-d');
		case 'varchar' : 
		case 'int' : 
			return "<input size=\"$size\" name=\"$field\" value=\"$value\">";
		break;

		case 'text':
			return "<textarea name=\"$field\" rows=\"$rows\" cols=\"$cols\">$value</textarea>";
		break;

		case 'timestamp':
		case 'none':
		default: return $value;
	}
}

