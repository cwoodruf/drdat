<?php /* Smarty version 2.6.26, created on 2010-02-16 16:38:58
         compiled from confirm.tpl */ ?>
<center>
<form action="index.php" method="post">
<a href="<?php echo $this->_tpl_vars['backurl']; ?>
">Go Back</a>
<h4><?php echo $this->_tpl_vars['question']; ?>
</h4>
<p>

<?php $_from = $this->_tpl_vars['data']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }if (count($_from)):
    foreach ($_from as $this->_tpl_vars['field'] => $this->_tpl_vars['value']):
?>
<input type="hidden" name="<?php echo $this->_tpl_vars['field']; ?>
" value="<?php echo $this->_tpl_vars['value']; ?>
">
<?php endforeach; endif; unset($_from); ?>

<input type="submit" name="action" value="<?php echo $this->_tpl_vars['action']; ?>
">
</form>
</center>