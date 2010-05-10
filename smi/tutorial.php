<html>
<head>
<link rel=stylesheet type=text/css href=css/main.css>
<title>DRDAT Tutorial</title>
</head>
<body>
<center>
<h3>Distributed Real-time Data Acquision Toolkit (DRDAT) Tutorial</h3>
<table cellpadding=5 cellspacing=0 border=0>
<tr>
<td>
DRDAT is a set of tools that makes it easy for you to design and deploy research studies that use
mobile phones as input devices. It is well suited for types of self-reporting research where a
study participant would need to enter data at a prescribed time. One major advantage of DRDAT
is that any data entered by a study participant will automatically be uploaded to the Study 
Management Interface (SMI) web server. You can track data entry in real time while the
study is ongoing. In addition you can set a schedule for when specific tasks need to be done
and the study participants will be automatically reminded by the mobile device when they need
to complete that task.

<h4>Get started: create a study</h4>
Once you have set up your login for DRDAT sign in and create a study. A study in DRDAT is a container
for a set of tasks done by a group of study participants. To create a new study click "Create a Study"
on your DRDAT home page. 
<p>
A study has a title, description and a start and end date. Make sure that you enter valid dates as
the start and end dates determine whether a participant can submit data for a given study.

<h4>Next step: create tasks</h4>
Once you have saved your new study you will need to add tasks and participants. A task is a set of
data entry forms that can have an associated schedule. The schedule is only a reminder it does
not enforce when a participant can do a task. The data entry forms are simple html forms that 
you can create either directly in html or using a simple form design language. 
<p>
To create a new task click the "Create a task" link in the study page for your new study. Enter
the title for the task (visible to participants on their phones) and notes (not visible) and click
"Save task". At this point you should see the task and a schedule form. The schedule determines when
a study participant would be reminded to do this task. 
<p>
Schedules are not required but are recommended. Even if it is not necessary for a task to be done
at a specific time having a reminder during the day may make it easier for the participant to 
remember to do it. You can also elect to remind a participant only on certain days of the week. So
if you have a task that only needs to be done once per week you can set the reminder for a specific
week day rather than have the reminder happen every day.

<h4>Task forms</h4>
Tasks can have data entry forms. You can make these forms in html or use our simple mark up language.
Remember that the size of a screen on a mobile device is generally very small when you are designing
data entry forms.
<p>
Task forms do not have to include data entry elements and can simply be more detailed instructions
to the end user. See the form entry page for a more detailed description of this markup language.
<p>
To create a set of forms for a task go to the task page (click on the link for the task from the study
page) and click on the "Edit forms" link. If you need to get data from the end user you will need to 
use the form markup langage. However, you can enter raw html including external links. By default 
the form generator makes html for you. (If you wish to enter form elements manually be sure to
use the same cgi variable naming convention as that generated from the mark up language.)
<p>
Data entry widgets on a form can have one of 4 types: none, text, dropdown, checkbox:
<table cellpadding=5 cellspacing=0 border=1 bgcolor=lightgray>
<tr>
<td>none</td><td>No input widget</td>
</tr><tr>
<td>text</td><td>text box</td>
</tr><tr>
<td>dropdown</td><td>drop down selection box that lets you select one item</td>
</tr><tr>
<td>checkbox</td><td>each option is a check box - for when you want to select more than one item</td>
</tr>
</table>
<p>
The dropdown and checkbox require options to select from. The text widget can be entered as "text:20,5"
which would make a multi-line text box with 20 columns and 5 lines. Otherwise it is a single line 
text box.
<p>
To make multiple forms use the "--" mark up (which is made into a special &lt;!-- split --&gt; tag) to
demarcate when one form ends and the other starts. You do not need to define specific html &lt;form&gt;
elements for each form.
<p>
Save the form data to complete the task.

<h4>Next step: enter participant information</h4>
For a participant (or researcher) to use DRDAT on a mobile interface they must be enrolled in a study. 
To enroll yourself or another participant in a study go to the study page for your new study and click
"Enroll Participants".
<p>
In the participants page click "Add a study participant" to add someone to the study. Enter the 
email address and first name at least to make a new study participant. Click "Save participant" to save 
this information. Click "Return to enroll participants page". You should see the participant listed. 
<p>
But we aren't done yet! For the participant to access data from a mobile device you need to set a
password for that participant. Click on "add password" to make a new password for that participant. 
Participants cannot change their passwords.

<h4>Last step: test on a mobile device</h4>
You will need to download <a href="http://drdat.googlecode.com/files/DrdatCL.apk">the task manager</a> and 
<a href="http://drdat.googlecode.com/files/DrdatGUI.apk">the data entry tool</a> on to your android 
mobile device. These applications require android 2.1 (api version 7). 
<h4>Downloading tasks</h4>
After downloading and installing both of the DRDAT mobile apps you will want to download tasks.
Start the DRDAT task manager then click "Update Tasks" from the main menu to update tasks for a 
participant. Next enter a valid participant email and password and press "Update Tasks" to download 
the tasks for the participant. When the download is complete you should see a list of tasks. 
You can press on a task in this list to start it.
<h4>Starting a task</h4>
Participants can start a task in 3 ways. Firstly they can start a task from a notification on the 
mobile device based on the schedule you entered for that task. Secondly they can start the data
entry app and select from a list. Thirdly, they can start a task after doing a manual update as 
described above.
<h4>Data uploads</h4>
DRDAT's form entry tool will try and send data as soon as the participant has saved it. However, if 
this fails it will try to send data every hour until the data is sent. You can also upload data 
manually from the DRDAT Task Manager app. 
<h4>Wiping data</h4>
If you need to wipe participant data you can do that from the DRDAT Task Manager. Scroll down to the 
bottom of the menu and press "Delete tasks and logins". Also be sure to upload any remaining 
participant data and use "Delete uploaded data" to delete it on the mobile device.
<p>
You can only delete entered data once it has been successfully sent back to the SMI. 
</td>
</tr>
</table>
</center>
</body>
</html>


</body>
</html>
