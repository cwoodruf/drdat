function save(form) {
	data = new String();
	for (i=0; i<form.elements.length; i++) {
		if (form.elements[i].type == 'checkbox') {
			if (form.elements[i].checked) val = form.elements[i].value;
			else val = '';
		} else {
			val = form.elements[i].value;
		}
		data += window.encodeURIComponent(form.elements[i].name) + "=" +
			window.encodeURIComponent(val) + "&";
	}
	demo.saveFields(data);
	alert("your data was logged");
}	
