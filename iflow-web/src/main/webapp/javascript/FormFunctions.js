function addFile(filename) {

	var newFile = document.getElementById('f_filename').cloneNode(true);
	var newFileButton = document.getElementById('b_filename').cloneNode(true);
	var fileInsertLocation = document.getElementById('dummyfile');


	newFile.name='f_' + filename;
	newFile.id='f_' + filename;
	newFile.innerHTML = filename;

	newFileButton.name='b_' + filename;
	newFileButton.id='b_' + filename;

	newFileButton.setAttribute('onclick',"remove('" + filename + "')");

	var newdiv = document.createElement('div');
	newdiv.setAttribute('id', filename);
	newdiv.setAttribute('name', filename);

	fileInsertLocation.parentNode.insertBefore(newdiv,fileInsertLocation);

	newdiv.appendChild(newFile);
	newdiv.appendChild(newFileButton);
}

function scan(fileVar) {
	var fileName = document.FileApplet.scan(fileVar);
	if(fileName) {
		addFile(fileName);
	}
}

function load(fileVar){

	var fileName = document.FileApplet.load(fileVar);
	if(fileName) {
		addFile(fileName);
	}

}

function remove(filename){
	var fileDiv = document.getElementById(filename);
	var fileDivParent = fileDiv.parentNode;

	fileDivParent.removeChild(fileDiv);
	document.FileApplet.remove(filename);
}

function sendFiles(location){
	if(!document.FileApplet) return true;
	// Fetch flowid, pid and subpid and then append to location.
	var flowid = document.getElementById('flowid').value;
	var pid = document.getElementById('pid').value;
	var subpid = document.getElementById('subpid').value;
	var sfileIDS = document.FileApplet.sendAll(location, flowid, pid, subpid, document.cookie);
	if(sfileIDS && sfileIDS != '') {
	  fileIDS = sfileIDS.split(';');
	  for (i=0;i<fileIDS.length;i++) {
	    if(fileIDS[i] && fileIDS[i] != '') {
		  var varName = document.FileApplet.getVariableName(fileIDS[i]);
		  currentElement = document.createElement("input");
		  currentElement.setAttribute("type", "hidden");
		  currentElement.setAttribute("name", varName + "_new_[" + i + "]");
		  currentElement.setAttribute("id",  varName + "_new_[" + i + "]");
		  currentElement.setAttribute("value", fileIDS[i]);

		  document.forms[0].appendChild(currentElement);
		}
	  }
	}
	return true;
}

// Removi daqui a funcao setApplet()
// Agora eh gerada automaticamente. (ver abaixo)

function checkAppletButtons() {
	var elem;
	if (!(document.FileApplet && document.FileApplet.canScan && document.FileApplet.canScan() != false)) {
		elem = document.getElementById('scanfile');
		if(elem)
			elem.style.display='none';
	}
	if (!(document.FileApplet && document.FileApplet.canSign && document.FileApplet.canSign() != false)) {
		elem = document.getElementById('signfile');
		if(elem)
			elem.style.display='none';
	}
}

function caltasks(id, format) {
	if (!format) {
		format = "%d/%m/%Y";
	}
	var showsHourHalf = format.indexOf('%I') > -1;
	var showsHourFull = format.indexOf('%H') > -1;
	var showsHour = showsHourHalf || showsHourFull;
	var showsMinute = format.indexOf('%M') > -1;
	var showsSeconds = format.indexOf('%S') > -1;
	var showsTime = showsHour || showsMinute || showsSeconds;
	var timeFormat = "24"; // keep default 24
	if (showsHourHalf) {
		timeFormat = "12";
	}
	if (showsHourFull) {
		timeFormat = "24";
	}
	Calendar.setup({
		inputField     :    id,   // id of the input field
		ifFormat       :    format,       // format of the input field
		showsTime      :    showsTime,
		timeFormat     :    timeFormat,
		electric       :    false
	});
}
function CheckEmptyFields() {
	var dml, fields, input, type, name, value;
	dml = document.forms[0]; 
	fields = ""; 
	for(var i=0 ; i < dml.elements.length; i++) {
		input = dml.elements[i];
		if(!input) continue; // unnecessary
		type = input.type;
		if(type=='object') continue;
		name = '';
		value = '';
		if(input['name'])
			name = input.name; 
		if(input['value'])
			value = input.value;
		
		if ((type=='text' || type=='textarea' || type=='password') && 
			 value=='' && (typeof document.anchors[name] != 'undefined')	&& document.anchors[name].name == name) { 
			fields = fields + 'O campo ' + name + ' é de preenchimento obrigatório\n'; 
		}
	}

	if (fields.length > 0) { 
		alert(fields); 
		return false; 
	} 

	return true; 
} 

function sum(var1, var2, vardest) { 
	var field1 = document.getElementById(var1); 
	var field2 = document.getElementById(var2); 
	var fielddest = document.getElementById(vardest); 
	var fieldhidden = document.getElementById(vardest + "_HIDDEN");

	fielddest.value = (field1.value-0) + (field2.value-0);

	if (fieldhidden != null) { 
		fieldhidden.value = fielddest.value; 
	} 
}

function disableForm() {
	if (document.getElementById('_formLoadingDiv')) {
		document.getElementById('_formLoadingDiv').style.display='';
		if (document.forms && document.forms.length > 0 && document.forms[0])
			document.forms[0].style.display='none';
	}
	backToTop();
}
function enableForm() {
	var el = document.getElementById('_formLoadingDiv');
	if (el) {
		el.style.display='none';
		el.style.backgroundColor='';
		el.style.border='';
		SetOpacity(el, 100);
		if (document.forms && document.forms.length > 0 && document.forms[0])
			document.forms[0].style.display='';
	}
}
function camouflageForm() {
	var el = document.getElementById('_formLoadingDiv');
	if (el) {
		el.style.display='';
		el.style.backgroundColor='lightBlue';
		el.style.border='2px outset lightBlue';
		el.style.opacity='0.5';
		SetOpacity(el, 50);
	
		// you've got to love IE...specially 6 and it's z-index bug!! solution: hide selects!
		var IE6=(navigator.userAgent.toLowerCase().indexOf('msie 6') != -1) && (navigator.userAgent.toLowerCase().indexOf('msie 7') == -1);
		if (IE6) {
			el.focus();
	        var elements = document.documentElement.getElementsByTagName('select');	     
	        for (var i=0; i<elements.length; i++) {
	        	elements[i].style.visibility = 'hidden';
	        }
		}
	}	
}
function SetOpacity(elem, opacityAsInt) {
	var opacityAsDecimal = opacityAsInt;
	if (opacityAsInt > 100) {
		opacityAsInt = opacityAsDecimal = 100;
	} else if (opacityAsInt < 0) {
		opacityAsInt = opacityAsDecimal = 0; 
	}
	opacityAsDecimal /= 100;
	if (opacityAsInt < 1) {
		opacityAsInt = 1; // IE7 bug, text smoothing cuts out if 0
	}
	elem.style.opacity = (opacityAsDecimal);
	elem.style.filter  = "alpha(opacity=" + opacityAsInt + ")";
}
function backToTop() {
	if (parent && ((typeof parent.scroll) == 'function' || (typeof parent.scroll) == 'object')) {
		parent.scroll(0,0);
	} else if ((typeof scroll) == 'function' || (typeof scroll) == 'object') {
		scroll(0,0);
	}
}

function showAlert(msg, type, title) {
	var finalMsg = '';
	if (type != null && title != null) {
		finalMsg = type + ": " + title + "\n\n";
	}
	else if (type != null) {
		finalMsg = type + "\n\n";
	}
	else if (title != null) {
		finalMsg = title + "\n\n";
	}
	finalMsg = finalMsg + msg;
	var ta=document.createElement("textarea");
	ta.innerHTML=finalMsg.replace(/</g,"&lt;").replace(/>/g,"&gt;");
	alert(ta.value);	
}

var dialogList = new Array();
function showPopUp(containerid, panelid, header, body, width, height) {
	var panel;
	for (var item in dialogList) {
		if (panelid == dialogList[item].id) {
			panel = dialogList[item];
			break;
		}
	}
	if (!panel) {
		var options = {
				visible:false,
				draggable:true,
				close:true,
				fixedcenter:false,
				constraintoviewport:true,
				modal:true
		};

		if (width) { options['width'] = width; }
		if (height) { options['height'] = height; }
		panel = new YAHOO.widget.Panel(panelid,  options);
	    panel.setHeader('<div class=\'tl\'></div><span>' + header + '</span><div class=\'tr\'></div>');
	    panel.setBody(body);
	    panel.render(containerid);
		dialogList.push(panel);
	}
    panel.show();
    return panel;
}
window.addEvent('domready', function() {
	//store titles and text
	$$('img.tipz').each(function(elz,index) {
		var content = elz.title.split('::');
		elz.store('tip:title', content[0]);
		elz.store('tip:text', content[1]);
	});
	//create the tooltips
	var tipz = new Tips('.tipz',{
		className: 'tipz',
		fixed: true,
		hideDelay: 50,
		showDelay: 50
	});
});

function textfieldChangeOnEnter(id) {
	if (typeof iFlowFormFields == "undefined") {
		iFlowFormFields = {};
	}
	var el = document.getElementById(id); 
	if (el) {
		iFlowFormFields[id] = el.value;
	}
}
function textfieldChangeOnExit(id) {
	if (typeof iFlowFormFields != "undefined") {
		var prevVal = iFlowFormFields[id];
		var el = document.getElementById(id); 
		if (el) {
			if (el.value === prevVal) {
				return false;
			}
		}
	}
	return true;
}