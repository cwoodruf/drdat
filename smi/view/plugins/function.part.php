<?php
/**
 * get data for a participant and create a smarty $part variable
 */
function smarty_function_part($params,&$smarty) {
	if (!Check::digits($params['part_id'],($empty=false))) return;
	$p = new Participant;
	$smarty->assign('part',$p->getone($params['part_id']));
}

