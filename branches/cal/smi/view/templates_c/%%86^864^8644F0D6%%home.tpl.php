<?php /* Smarty version 2.6.26, created on 2010-02-16 17:11:18
         compiled from home.tpl */ ?>
<?php require_once(SMARTY_CORE_DIR . 'core.load_plugins.php');
smarty_core_load_plugins(array('plugins' => array(array('modifier', 'capitalize', 'home.tpl', 10, false),array('function', 'studies', 'home.tpl', 15, false),)), $this); ?>
<?php if ($_SESSION['user']['firstname'] == ''): ?>

<?php $_smarty_tpl_vars = $this->_tpl_vars;
$this->_smarty_include(array('smarty_include_tpl_file' => "researcher.tpl", 'smarty_include_vars' => array()));
$this->_tpl_vars = $_smarty_tpl_vars;
unset($_smarty_tpl_vars);
 ?>

<?php else: ?>

<center>
<a href="index.php" class="editlink">Refresh</a>
<h4>Welcome <?php echo ((is_array($_tmp=$_SESSION['user']['firstname'])) ? $this->_run_mod_handler('capitalize', true, $_tmp) : smarty_modifier_capitalize($_tmp)); ?>
 
- <a href="index.php?action=Edit+Researcher" class="editlink i">Edit profile</a> </h4>

<p>
<h4>My studies - <a href="index.php?action=Create+Study" class="editlink i">Create a study</a></h4>
<?php echo smarty_function_studies(array(), $this);?>

<table><tr align=left><td>
<ul class="studylist">
<?php $_from = $this->_tpl_vars['studies']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }if (count($_from)):
    foreach ($_from as $this->_tpl_vars['num'] => $this->_tpl_vars['sdata']):
?>

<?php if ($this->_tpl_vars['sdata']['study_id']): ?>
<li><a href="index.php?action=Show+Study&study_id=<?php echo $this->_tpl_vars['sdata']['study_id']; ?>
" class="editlink">
<?php echo $this->_tpl_vars['sdata']['study_title']; ?>
</a> &nbsp; <?php echo $this->_tpl_vars['sdata']['startdate']; ?>
 to <?php echo $this->_tpl_vars['sdata']['enddate']; ?>
 &nbsp;
<a href="index.php?action=Confirm+Hide+Study&study_id=<?php echo $this->_tpl_vars['sdata']['study_id']; ?>
" class="editlink i">
hide</a>
<?php endif; ?>

<?php endforeach; endif; unset($_from); ?>
</ul>
</td></tr></table>
</center>

<?php endif; ?>