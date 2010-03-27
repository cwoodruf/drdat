<?php
// a stub script for testing the phone to smi interface
header('content-type: text/plain');
$do = $_REQUEST['do'];
switch ($do) {
case 'getTaskList': 
	print getTaskList(); 
break;
case 'getTask': 
	print getTask(); 
break;
case 'sendData': 
default: 
	var_export($_REQUEST);
}

function getTask() {
	switch ($_REQUEST['task_id']) {
	case 14:
	return <<<XML
<task>
    <task_id>14</task_id>
    <task_name>taskA</task_name>
    <notes>this is taskA</notes>
    <schedule>
        <start>2010-02-21</start>
        <end>2010-06-21</end>
        <frequency>1</frequency>
        <timeofday>10:00;15:00;20:00;</timeofday>
    </schedule>
    <form>
        <taskitem>
            <taskitem_id>1</taskitem_id>
            <instruction>taskitem1 instruct</instruction>
            <format>none</format>
        </taskitem>
        <taskitem>
            <taskitem_id>2</taskitem_id>
            <instruction>taskitem2 instruct</instruction>
            <format>dropdown:itemA&itemB&itemC&itemD</format>
        </taskitem>
    </form>
</task>
XML;
	case 15:
	return <<<XML
<task>
    <task_id>15</task_id>
    <task_name>taskB</task_name>
    <notes>this is taskB</notes>
    <schedule>
        <start>2010-02-21</start>
        <end>2010-06-21</end>
        <frequency>2</frequency>
        <timeofday>12:00;16:00</timeofday>
    </schedule>
    <form>
        <taskitem>
            <taskitem_id>3</taskitem_id>
            <instruction>taskitem3 instruct</instruction>
            <format>dropdown</format>
        </taskitem>
        <taskitem>
            <taskitem_id>4</taskitem_id>
            <instruction>taskitem4 instruct</instruction>
            <format>checkbox:item1&item2&item3</format>
        </taskitem>
    </form>
</task>
XML;
	case 16:	
	return <<<XML
<task>
    <task_id>16</task_id>
    <task_name>taskC</task_name>
    <notes>this is taskC</notes>
    <schedule>
        <start>2010-02-21</start>
        <end>2010-06-21</end>
        <frequency>3</frequency>
        <timeofday>14:00</timeofday>
    </schedule>
    <form>
        <taskitem>
            <taskitem_id>5</taskitem_id>
            <instruction>taskitem5 instruct</instruction>
            <format>text</format>
        </taskitem>
    </form>
</task>
XML;
	case 17:
	return <<<XML
<task>
    <task_id>17</task_id>
    <task_name>taskD</task_name>
    <notes>this is taskD</notes>
    <schedule>
        <start>2010-02-21</start>
        <end>2010-03-21</end>
        <frequency>4</frequency>
        <timeofday>12:00;16:00</timeofday>
    </schedule>
    <form>
        <taskitem>
            <taskitem_id>6</taskitem_id>
            <instruction>taskitem6 instruct</instruction>
            <format>checkbox:item%204&item%205&item%206</format>
        </taskitem>
    </form>
</task>
XML;
	}
}

function getTaskList() {
	return <<<XML
<?xml version="1.0" encoding="UTF-8"?>
<tasklist>
    <study_id>14</study_id>
    <task>
        <task_id>14</task_id>
        <task_name>taskA</task_name>
        <schedule>
            <start>2010-02-21</start>
            <end>2010-06-21</end>
            <frequency>1</frequency>
            <timeofday>10:00;15:00;20:00</timeofday>
        </schedule>
    </task>
    <task>
        <task_id>15</task_id>
        <task_name>taskB</task_name>
        <schedule>
            <start>2010-02-21</start>
            <end>2010-06-21</end>
            <frequency>2</frequency>
            <timeofday>12:00;16:00</timeofday>
        </schedule>
    </task>
    <task>
        <task_id>16</task_id>
        <task_name>taskC</task_name>
        <schedule>
            <start>2010-02-21</start>
            <end>2010-06-21</end>
            <frequency>3</frequency>
            <timeofday>14:00</timeofday>
        </schedule>
    </task>
    <task>
        <task_id>17</task_id>
        <task_name>taskD</task_name>
        <schedule>
            <start>2010-02-21</start>
            <end>2010-06-21</end>
            <frequency>4</frequency>
            <timeofday>12:00;16:00</timeofday>
        </schedule>
    </task>
</tasklist>
XML;
}

