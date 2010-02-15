<h3>Study management portal - test version</h3>
<?php
# if __SMI__ is not false then its not defined - php defines unknown strings to true (ps: gah!)
define('__SMI__',false);

session_start();
require_once('lib/includes.php');

$action = $_REQUEST['action'];

$user = new Login;
if (!$unblocked[$action] and !$user->valid()) {
	$smarty->display('login.tpl');
	exit();
}

$do = new Doit;
$template = $do->process($action);
$smarty->display($template);

