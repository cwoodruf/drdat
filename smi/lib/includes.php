<?php
if (__SMI__) die("no direct access.");
# not included in svn
@include_once('lib/.localsettings.php');

# from a separate svn library: http://code.google.com/p/dbabstracter4php
# checking functions used by model and controller
require_once('.db/check.php');

# db access and business logic
require_once('lib/model.php');

# manage user input
require_once('lib/controller.php');

# show something - need this to be included after model.php
require_once('lib/view.php');
