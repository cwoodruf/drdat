<?php
/**
 * main entry point for all request processing
 */

# if __SMI__ is not false then its not defined - php defines unknown strings to true (ps: gah!)
define('__SMI__',false);
session_start();
require_once('lib/includes.php');

View::head();

print <<<HTML
<h3>Study management portal - test version
    (<a href="index.php?action=Log+Out" class="editlink i">Log Out</a>)</h3>

HTML;

$user = new Login;
if (!Action::unblocked() and !$user->valid()) {
	View::display('login.tpl');
	exit();
} else {
	$do = new Doit;
	$template = $do->process(Action::get());
	View::display($template);
}

View::foot();

