{task task_id=$task_id}
<a href="index.php">Home</a>
&nbsp;-&nbsp;
<a href="index.php?action=Show+Task&task_id={$task_id}&study_id={$study_id}">
Return to task {$task.task_title}</a>
<p>
<form action="index.php" id="formform" method="post">
<input type=hidden name=task_id value="{$task_id}">
<input type=hidden name=study_id value="{$study_id}">

<h3>Create forms for task {$task.task_title}</h3>

<table cellpadding=5 cellspacing=0 border=0>
<tr><td>
<h4>Instructions</h4>
</td></tr>
<tr><td>
To be useable on a phone each task must have at least one form. Forms contain
instructions followed by input widgets. An instruction does not have to have an input widget.
<p>
To create the forms for this task enter the question or instructions 
On the following line optionally enter a widget type for data entry
For the checkbox and dropdown widgets you will need to add items for that widget
You may only have one widget per question.
Questions may have multiple lines.
</td></tr>
<tr><td>
<h4>Formatting codes</h4>
<table align=center cellpadding=3 cellspacing=0 border=1 class="nobgcolor">
<th>code</th><th>description</th>
<tr><td>#</td><td>ignore text after line</td><tr>
<tr><td>--</td><td>new form (any following characters are ignored</td></tr>
<tr><td>i:</td><td>an instruction or question</td></tr>
<tr><td>w:</td><td>input widget: can be one of none, text, dropdown, checkbox</td></tr>
<tr><td>o:</td><td>option - the dropdown and checkbox widgets require at least one option</td></tr>
</td></tr>
</table>
</td></tr>
<tr><td>
<h4>Forms
{if $task.formtext}
- <a href="index.php?action=Preview+Forms&study_id={$study_id}&task_id={$task_id}"
     class="editlink i">Preview</a>
{/if}
</h4>
</td></tr>
<tr><td align=right>
<input type=submit name=action value="Save Forms">
</td></tr>
<tr><td>
<textarea name=formtext rows=40 cols=80>{$task.formtext}</textarea>
</td></tr>
</table>
</form>
