/*
Script: form-editor-events.js
	Flow Editor web interface events

// This is a UTF-8 File

 */
var FormEditorEvents = new Class({

	initialize: function(getValues) {
		if(getValues) this.getValues = getValues;
		else this.getValues = $lambda({});
		this.sqString = '';
		this.qString = {};
		if(location.search && location.search != '') {
			$each(location.search.substring(1).split('&'), function(tok){
				var s = tok.split('=');
				this.qString[s[0]]=decodeURI(s[1]);
			}, this);
			this.sqString=location.search;
		}
	},
	
	// send a message to the browser
	notifyBrowser: function notifyBrowser (action, responseEvent, values) {
		var request;
		request={};
		$extend(request, this.qString);
		if($defined(values)) request.values=JSON.encode(values);
		new Request.JSON({url:action,method:'post',encoding:'UTF-8',onComplete:responseEvent}).send({data:request});
	},
	
	// send a message to the browser
	notifyBrowserSync: function notifyBrowserSync (action, values) {
		var request,result,responseEvent;
		request={};
		result={ok:false};
		$extend(request, this.qString);
		responseEvent = function (json,text) {
			$extend(result,json);
			FormEditor.debug('Got this: '+JSON.encode(json));
			FormEditor.debug('Merged into this: '+JSON.encode(result));
		};
		if($defined(values)) request.values=JSON.encode(values);
		try {
		  new Request.JSON({url:action,method:'post',async:false,encoding:'UTF-8',onSuccess:responseEvent}).send({data:request});
		} catch(err) {
			FormEditor.error('notifyBrowserSync failed for '+action, err);
		}
		FormEditor.debug('Returning this: '+JSON.encode(result));
		return result;
	},
	
	// special messages
	// sync: save data
	saveValues: function saveValues (callback) {
		return this.notifyBrowser('scripts/save.js',callback||this.syncValuesResponse, this.getValues());
	},
	
	syncValuesResponse: function(resp,txt) {
		FormEditor.debug('Resultado: '+(resp && resp.ok));
	},
	
	// ok: ok button pressed
	okPressed: function () {
		return this.notifyBrowser('scripts/ok.js',this.syncValuesResponse,this.getValues());
	},
	
	// cancel: cancel button pressed
	cancelPressed: function () {
		return this.notifyBrowser('scripts/cancel.js',this.cancelPressedResponse);
	},
	
	// preview
	previewPressed: function previewPressed () {
		return this.notifyBrowser('scripts/preview.js',this.cancelPressedResponse, this.getValues());
	},
	
	cancelPressedResponse: function(resp,txt) {
	},
	
	// init: initialize data
	requestInit: function () {
		return this. notifyBrowser('scripts/init.js',this.initValuesResponse);
	},

	initValuesResponse: function(resp,txt) {
		var initOk = (resp && resp.ok);
		FormEditor.debug('Resultado init: '+initOk);
		try {
			if(initOk && resp.form) { // init is OK and has a form
				FormEditor.fieldtk.setForm(resp.form);
			}
		} catch(err) {
			FormEditor.error('Init failed.', err);
		}
		FormEditor.initComplete=true;
	},
	
	// checkSyntax: Validate iFlowEX syntax for fields
	checkSyntax: function (value) {
		return {'ok':0,'warning':0,'error':0};
	},
	
	loadJavascript: function(file) {
		new Asset.javascript(file+this.sqString);
	},

	loadCSS: function(file) {
		new Asset.css(file+this.sqString);
	},
	
	fixResource: function fixResource(file) {
		return file+this.sqString;
	}
	
});

// Just a reminder... çãé
