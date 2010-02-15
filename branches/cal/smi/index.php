<h3>Study management portal - test version</h3>
<?php
# if __SMI__ is not false then its not defined - php defines unknown strings to true (ps: gah!)
define('__SMI__',false);

session_start();
require_once('lib/includes.php');

$user = new Login;
if (!$user->valid()) {
	$smarty->display('login.tpl');
	exit();
}

