{* check if they just signed up *}
{if $smarty.session.user.firstname == ''}

{include file=researcher.tpl}

{else}

<center>
<h4>Welcome {$smarty.session.user.firstname|capitalize} 
- <a href="index.php?action=Edit+Researcher" class="editlink i">Edit profile</a> </h4>

<p>
<h4>My studies - <a href="index.php?action=Create+Study" class="editlink i">Create a study</a></h4>
<p>
{studies}
<table><tr align=left><td>
<ul class="studylist">
{foreach from=$studies key=num item=sdata}

{if $sdata.study_id}
<li><a href="index.php?action=Show+Study&study_id={$sdata.study_id}" class="editlink">
{$sdata.study_title}</a> &nbsp; {$sdata.startdate} to {$sdata.enddate}
{/if}

{/foreach}
</ul>
</td></tr></table>
</center>

{/if}
