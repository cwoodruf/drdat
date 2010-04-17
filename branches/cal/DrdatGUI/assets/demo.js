var action = "";

function saveaction(input) {
	action = input.value;
}

function fill(form) {
	for (i=0; i<form.elements.length; i++) {
		var t = form.elements[i].type; 
		name = window.encodeURIComponent(form.elements[i].name);
		value = DrdatForms.getField(name); 
		if (t == 'checkbox' && value != '') {
			form.elements[i].checked = true;
		} else if (t == 'text' || t.match(/select.*/)) { 
			form.elements[i].value = window.decodeURIComponent(value);
		}
	}
	return true;
}

function save(form) {
	for (i=0; i<form.elements.length; i++) {
		if (form.elements[i].name == 'action') continue;
		if (form.elements[i].type == 'submit') continue;
		if (form.elements[i].type == 'checkbox') {
			if (form.elements[i].checked) val = form.elements[i].value;
			else continue;
		} else {
			val = form.elements[i].value;
		}
		name = window.encodeURIComponent(form.elements[i].name);
		value = window.encodeURIComponent(val);
		DrdatForms.setField(name, value);
	}
	DrdatForms.doAction(action);
	return false; 
}	
