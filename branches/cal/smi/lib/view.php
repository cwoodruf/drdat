<?php
# use this for code relating to what an end user would see 
if (__SMI__) die("no direct access.");

define('VIEW_MAXFIELDSIZE', 80);
define('VIEW_MAXROWS', 50);
define('VIEW_MINFIELDSIZE', 5);
define('VIEW_MINROWS', 2);

# see http://smarty.net/ for more information
require_once('.smarty/Smarty.class.php');
$smarty = new Smarty;
$smarty->template_dir = getcwd().'/view/templates';
$smarty->compile_dir = getcwd().'/view/templates_c';
$smarty->plugins_dir[] = getcwd().'/view/plugins';
$smarty->config_dir = getcwd().'/view/configs';

/**
 * convenience class to access smarty outside of view.php or templates
 */
class View {
	public static $smarty;

	public static function init($s) {
		if (is_object($s)) self::$smarty = $s;
	}

	public static function assign($var,$value) {
		self::$smarty->assign($var,$value);
	}

	public static function head() {
		self::$smarty->display('header.tpl');
	}

	public static function foot() {
		self::$smarty->display('footer.tpl');
	}

	public static function display($template) {
		self::$smarty->display($template);
	} 
}

View::init($smarty);

# this is defined in lib/drdat-schema.php
View::assign('schema', $tables);



