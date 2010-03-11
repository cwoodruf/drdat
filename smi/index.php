<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/

/**
 * main entry point for all request processing
 */

# if __SMI__ is not false then its not defined - php defines unknown strings to true (ps: gah!)
define('__SMI__',false);
session_start();
require_once('lib/includes.php');

View::head();

print "<h3>SMI - test version";

$user = new Login;
$action = new SMIAction;
if (!$action->unblocked() and !$user->valid()) {
	print "</h3>\n";
	View::display('login.tpl');
	exit();
} else {
	if ($action->get() == 'Log Out') print "</h3>\n";
	else print "(<a href=\"index.php?action=Log+Out\" class=\"editlink i\">Log Out</a>)</h3>\n";
	$template = $action->process();
	View::display($template);
}

View::foot();

