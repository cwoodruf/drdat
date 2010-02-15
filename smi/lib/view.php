<?php
# use this for code relating to what an end user would see 
if (__SMI__) die("no direct access.");

# see http://smarty.net/ for more information
require_once('.smarty/Smarty.class.php');
$smarty = new Smarty;
$smarty->template_dir = getcwd().'/view/templates';
$smarty->compile_dir = getcwd().'/view/templates_c';
$smarty->plugins_dir = getcwd().'/view/plugins';
$smarty->config_dir = getcwd().'/view/configs';

$smarty->display('test.tpl');
