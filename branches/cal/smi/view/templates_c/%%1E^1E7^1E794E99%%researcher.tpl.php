<?php /* Smarty version 2.6.26, created on 2010-02-15 21:06:39
         compiled from researcher.tpl */ ?>
<?php $this->assign('user', $_SESSION['user']); ?>
<?php $this->assign('tbwidth', 550); ?>
<?php $this->assign('colwidth', '20%'); ?>
<?php $this->assign('inputsize', 60); ?> 

<center>

<?php if ($this->_tpl_vars['user']['firstname'] != ''): ?>
<a href="index.php">Home</a>

<?php endif; ?>

<form action="index.php" name="researcherform"  method=post>
<h3>Researcher profile</h3>
<table cellpadding=5 cellspacing=0 border=0 width="<?php echo $this->_tpl_vars['tbwidth']; ?>
">
<tr><td width="<?php echo $this->_tpl_vars['colwidth']; ?>
">First name: </td>
    <td><input name="firstname" size="<?php echo $this->_tpl_vars['inputsize']; ?>
" value="<?php echo $this->_tpl_vars['user']['firstname']; ?>
">
</td></tr>
<tr><td width="<?php echo $this->_tpl_vars['colwidth']; ?>
">Last name: </td>
    <td><input name="lastname" size="<?php echo $this->_tpl_vars['inputsize']; ?>
" value="<?php echo $this->_tpl_vars['user']['lastname']; ?>
">
</td></tr>
<tr><td width="<?php echo $this->_tpl_vars['colwidth']; ?>
">Institution: </td>
    <td><input name="institution" size="<?php echo $this->_tpl_vars['inputsize']; ?>
" value="<?php echo $this->_tpl_vars['user']['institution']; ?>
">
</td></tr>
<tr><td width="<?php echo $this->_tpl_vars['colwidth']; ?>
">Position: </td>
    <td><input name="position" size="<?php echo $this->_tpl_vars['inputsize']; ?>
" value="<?php echo $this->_tpl_vars['user']['position']; ?>
">
</td></tr>
<tr><td width="<?php echo $this->_tpl_vars['colwidth']; ?>
">Phone: </td>
    <td><input name="phone" size="<?php echo $this->_tpl_vars['inputsize']; ?>
" value="<?php echo $this->_tpl_vars['user']['phone']; ?>
">
</td></tr>
<tr><td colspan=2 align=right>
    <input type=submit name=action value="Save Researcher Profile">
</td></tr>
</table>
</form>
<script>document.researcherform.firstname.focus();</script>
</center>