<h3>Study management portal</h3>
<?php
# if __SMI__ is not false then its not defined - php defines unknown strings to true (ps: gah!)
define('__SMI__',false);
require_once('lib/includes.php');
# first check user input
# checking user input should dispatch some work to: 
# the model - manipulate some data ... maybe 
# and then the view - show some response to the end user
# most likely the controller will call the other code and
# all we do here is call a controller function or method
