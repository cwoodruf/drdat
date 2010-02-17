<center>
<form action="index.php" method="post">
<a href="{$backurl}">Go Back</a>
<h4>{$question}</h4>
<p>

{foreach from=$data key=field item=value}
<input type="hidden" name="{$field}" value="{$value}">
{/foreach}

<input type="submit" name="action" value="{$action}">
</form>
</center>
