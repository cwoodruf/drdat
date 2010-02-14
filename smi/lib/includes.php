<?php
if (__SMI__) die("no direct access.");
# the following file is not stored in subversion for security reasons
# see lib/example.localsettings.php
@include_once('lib/.localsettings.php');
# db access
# does stuff to data, doesn't necessarily automate anything 
require_once('lib/model.php');
# manage user input
# input/output data received handled in controllert
require_once('lib/controller.php');
# show something
require_once('lib/view.php');
