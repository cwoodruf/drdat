<?php /* Smarty version 2.6.26, created on 2010-02-16 16:35:58
         compiled from study.tpl */ ?>
<?php require_once(SMARTY_CORE_DIR . 'core.load_plugins.php');
smarty_core_load_plugins(array('plugins' => array(array('function', 'study', 'study.tpl', 1, false),array('function', 'inputwidget', 'study.tpl', 22, false),array('function', 'tasks', 'study.tpl', 34, false),array('modifier', 'replace', 'study.tpl', 20, false),array('modifier', 'capitalize', 'study.tpl', 20, false),)), $this); ?>
<?php echo smarty_function_study(array('study_id' => $this->_tpl_vars['study_id']), $this);?>

<center>
<a href="index.php">Home</a>

<h4>
<?php if (! isset ( $this->_tpl_vars['study_id'] )): ?>
New Study
<?php else: ?>
<?php echo $this->_tpl_vars['study']['study_title']; ?>
 &nbsp;&nbsp; <span class="i">(<?php echo $this->_tpl_vars['study']['startdate']; ?>
 to <?php echo $this->_tpl_vars['study']['enddate']; ?>
)</span>
<?php endif; ?>
</h4>

<form action="index.php" name="studyform" id="studyform" method="post">
<input type=hidden name=study_id value="<?php echo $this->_tpl_vars['study_id']; ?>
">
<table cellpadding=5 cellspacing=0 border=0 width=<?php echo $this->_tpl_vars['tbwidth']; ?>
>

<?php $_from = $this->_tpl_vars['schema']['study']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }if (count($_from)):
    foreach ($_from as $this->_tpl_vars['field'] => $this->_tpl_vars['fdata']):
?>
<tr>
<td valign=top><?php echo ((is_array($_tmp=((is_array($_tmp=$this->_tpl_vars['field'])) ? $this->_run_mod_handler('replace', true, $_tmp, '_', ' ') : smarty_modifier_replace($_tmp, '_', ' ')))) ? $this->_run_mod_handler('capitalize', true, $_tmp) : smarty_modifier_capitalize($_tmp)); ?>
: </td>
<td>
<?php echo smarty_function_inputwidget(array('field' => $this->_tpl_vars['field'],'fdata' => $this->_tpl_vars['fdata'],'input' => $this->_tpl_vars['study']), $this);?>

</td>
</tr>

<?php endforeach; endif; unset($_from); ?>

<tr><td colspan=2 align=right><input type=submit name=action value="Save Study"></td></tr>
</table>
</form>
<script>document.studyform.study_title.focus();</script>

<h4>Tasks - <a href="index.php?action=Create+Task&study_id=<?php echo $this->_tpl_vars['study_id']; ?>
" class="editlink i">Create a task</a></h4>
<?php echo smarty_function_tasks(array('study_id' => $this->_tpl_vars['study_id']), $this);?>

<table><tr align=left><td>
<ul class="tasklist">
<?php $_from = $this->_tpl_vars['tasks']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }if (count($_from)):
    foreach ($_from as $this->_tpl_vars['num'] => $this->_tpl_vars['tdata']):
?>

<?php if ($this->_tpl_vars['tdata']['task_id']): ?>
<li><a href="index.php?action=Show+Task&task_id=<?php echo $this->_tpl_vars['tdata']['task_id']; ?>
&study_id=<?php echo $this->_tpl_vars['study_id']; ?>
" class="editlink">
<?php echo $this->_tpl_vars['tdata']['task_title']; ?>
</a> &nbsp; <?php echo $this->_tpl_vars['tdata']['startdate']; ?>
 to <?php echo $this->_tpl_vars['tdata']['enddate']; ?>
 &nbsp; 
<a href="index.php?action=Confirm+Remove+Task&study_id=<?php echo $this->_tpl_vars['study_id']; ?>
&task_id=<?php echo $this->_tpl_vars['tdata']['task_id']; ?>
" i
   class="editlink i">remove</a>
<?php endif; ?>

<?php endforeach; endif; unset($_from); ?>
</ul>
</td></tr></table>
</center>
