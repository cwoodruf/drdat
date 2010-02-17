<?php
/**
 * run var_export on the variable given in the var parameter
 */
function smarty_function_var_export($params,&$smarty) {
	$html = "</center><pre>\n";
	$html .= var_export($params['var'],true);
	$html .= "</pre><center>\n";
	return $html;
}

