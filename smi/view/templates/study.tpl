{study study_id=$study_id}
<center>
<a href="index.php">Home</a>

<h4>
{if !isset($study_id)}
New Study
{else}
{$study.study_title} &nbsp;&nbsp; <span class="i">({$study.startdate} to {$study.enddate})</span>
{/if}
</h4>

<form action="index.php" name="studyform" id="studyform" method="post">
<input type=hidden name=study_id value="{$study_id}">
<table cellpadding=5 cellspacing=0 border=0 width={$tbwidth}>

{* use the schema data to help us build a form *}
{foreach from=$schema.study key=field item=fdata}
<tr>
<td valign=top>{$field|replace:'_':' '|capitalize}: </td>
<td>
{inputwidget field=$field fdata=$fdata study=$study}
</td>
</tr>

{/foreach}

<tr><td colspan=2 align=right><input type=submit name=action value="Save Study"></td></tr>
</table>
</form>
<script>document.studyform.study_title.focus();</script>
</center>

