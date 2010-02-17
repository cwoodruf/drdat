<?php /* Smarty version 2.6.26, created on 2010-02-16 15:54:15
         compiled from task.tpl */ ?>
<?php require_once(SMARTY_CORE_DIR . 'core.load_plugins.php');
smarty_core_load_plugins(array('plugins' => array(array('function', 'task', 'task.tpl', 1, false),array('function', 'study', 'task.tpl', 6, false),array('function', 'inputwidget', 'task.tpl', 33, false),array('function', 'schedule', 'task.tpl', 47, false),array('modifier', 'replace', 'task.tpl', 31, false),array('modifier', 'capitalize', 'task.tpl', 31, false),)), $this); ?>
<?php echo smarty_function_task(array('task_id' => $this->_tpl_vars['task_id']), $this);?>

<center>
<a href="index.php">Home</a>

<?php if ($this->_tpl_vars['study_id']): ?>
<?php echo smarty_function_study(array('study_id' => $this->_tpl_vars['study_id']), $this);?>

<p>
<a href="index.php?action=Show+Study&study_id=<?php echo $this->_tpl_vars['study_id']; ?>
">Back to study <?php echo $this->_tpl_vars['study']['study_title']; ?>
</a>
<?php endif; ?>

<h4>
<?php if (! isset ( $this->_tpl_vars['task_id'] )): ?>
New Task 
<?php else: ?>
<?php echo $this->_tpl_vars['task']['task_title']; ?>

<?php endif; ?>
</h4>

<form action="index.php" name="taskform" id="taskform" method="post">
<input type=hidden name=task_id value="<?php echo $this->_tpl_vars['task_id']; ?>
">
<?php if ($this->_tpl_vars['study_id']): ?>
<input type=hidden name=study_id value="<?php echo $this->_tpl_vars['study_id']; ?>
">
<input type=hidden name=startdate value="<?php echo $this->_tpl_vars['study']['startdate']; ?>
">
<input type=hidden name=enddate value="<?php echo $this->_tpl_vars['study']['enddate']; ?>
">
<?php endif; ?>
<table cellpadding=5 cellspacing=0 border=0 width=<?php echo $this->_tpl_vars['tbwidth']; ?>
>

<?php $_from = $this->_tpl_vars['schema']['task']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }if (count($_from)):
    foreach ($_from as $this->_tpl_vars['field'] => $this->_tpl_vars['fdata']):
?>
<tr>
<td valign=top><?php echo ((is_array($_tmp=((is_array($_tmp=$this->_tpl_vars['field'])) ? $this->_run_mod_handler('replace', true, $_tmp, '_', ' ') : smarty_modifier_replace($_tmp, '_', ' ')))) ? $this->_run_mod_handler('capitalize', true, $_tmp) : smarty_modifier_capitalize($_tmp)); ?>
: </td>
<td>
<?php echo smarty_function_inputwidget(array('field' => $this->_tpl_vars['field'],'fdata' => $this->_tpl_vars['fdata'],'input' => $this->_tpl_vars['task']), $this);?>

</td>
</tr>

<?php endforeach; endif; unset($_from); ?>

<tr><td colspan=2 align=right><input type=submit name=action value="Save Task"></td></tr>
</table>
</form>
<script>document.taskform.task_title.focus();</script>

<?php if ($this->_tpl_vars['task_id'] && $this->_tpl_vars['study_id']): ?>

<h4>Schedule</h4>
<?php echo smarty_function_schedule(array('task_id' => $this->_tpl_vars['task_id'],'study_id' => $this->_tpl_vars['study_id']), $this);?>


<form action="index.php" name="schedform" id="schedform" method="post">
<table cellpadding=5 cellspacing=0 border=0 width=<?php echo $this->_tpl_vars['tbwidth']; ?>
>

<?php $_from = $this->_tpl_vars['schema']['schedule']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }if (count($_from)):
    foreach ($_from as $this->_tpl_vars['field'] => $this->_tpl_vars['fdata']):
?>
<?php if ($this->_tpl_vars['field'] == $this->_tpl_vars['primary']): ?><?php continue; ?><?php endif; ?>
<?php if ($this->_tpl_vars['fdata']['hide']): ?>
<?php echo smarty_function_inputwidget(array('field' => $this->_tpl_vars['field'],'fdata' => $this->_tpl_vars['fdata'],'input' => $this->_tpl_vars['schedule']), $this);?>

<?php else: ?>
<tr>
<td valign=top><?php echo ((is_array($_tmp=((is_array($_tmp=$this->_tpl_vars['field'])) ? $this->_run_mod_handler('replace', true, $_tmp, '_', ' ') : smarty_modifier_replace($_tmp, '_', ' ')))) ? $this->_run_mod_handler('capitalize', true, $_tmp) : smarty_modifier_capitalize($_tmp)); ?>
: </td>
<td>
<?php echo smarty_function_inputwidget(array('field' => $this->_tpl_vars['field'],'fdata' => $this->_tpl_vars['fdata'],'input' => $this->_tpl_vars['schedule']), $this);?>

</td>
</tr>
<?php endif; ?>
<?php endforeach; endif; unset($_from); ?>

<tr><td colspan=2 align=right><input type=submit name=action value="Save Schedule"></td></tr>
</table>
</form>

<?php endif; ?>

</center>
