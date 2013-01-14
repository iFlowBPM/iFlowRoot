// this is a UTF-8 File

///////////////////////// COMMON FUNCTIONS \\\\\\\\\\\\\\\\\\\\\\\\\\
if(!window['FormEditor']) window.FormEditor={};

FormEditor.isTemplate = false;

FormEditor.initComplete = false;

///////////// Simple logger \\\\\\\\\\\\
FormEditor.error=function error (msg, ex) {
	FormEditor.logMsg('error', msg, ex);
};

FormEditor.debug=function debug (msg, ex) {
	FormEditor.logMsg('debug', msg, ex);
};

FormEditor.logMsg=function logMsg (type, msg, ex) {
	if(!$defined(msg))return;
	if($defined(ex)) msg = msg + ' Exception: '+ex.name+' - '+ex.message;
	
	var status = $('status');
	if(status) {
		new Element('div',{'class':'logger_'+type+' logMsg'}).set('text', msg).inject(status,'top');
	}
		
	// if firebug console exists, log
	if(window['console']) window.console.log('FormEditor: %s',msg);
	// if xul console is enabled, log message
	if(FormEditor.sendLogEvent) {
		var elem,evt;
		// create dummy element
		if(!FormEditor.logEventElem) {
			elem = document.createElement(FormEditor.logEventTag);
			elem.setAttribute('style','display:none');
			elem.setAttribute(FormEditor.logEventTypeAttr,'logger_info'); // default message type
			elem.appendChild(document.createTextNode(" ")); // dummy message
			document.documentElement.appendChild(elem);
			FormEditor.logEventElem = elem;
		}
		// create custom event
		evt = document.createEvent('Event');
		evt.initEvent(FormEditor.logEventName, true, true);// event name, send event thru event chain, cancelable
		FormEditor.logEventElem.setAttribute(FormEditor.logEventTypeAttr,'logger_'+type); // message type
		FormEditor.logEventElem.firstChild.nodeValue=msg; // set text message
		FormEditor.logEventElem.dispatchEvent(evt); // fire event
	}
};

FormEditor.showWindowEnv=function showWindowEnv() {
	$each(window,function(value,name) {
		FormEditor.debug('window.'+name+'='+$type(value));
	});
};

FormEditor.clear=function clear() {
	var status = $('status');
	if(status) {
		status.empty();
	}
};


FormEditor.sendLogEvent=false;
FormEditor.logEventTag='div';
FormEditor.logEventTypeAttr='class';
FormEditor.logEventName='feLogEvt';

// misc

FormEditor.initBasicEvents=function initBasicEvents (elem, fn) {
	elem = $(elem);
	elem.addEvent('contextmenu', fn);
	// elem.addEvent('click', fn);
};

FormEditor.stopEvt=function stopEvt (evt) {
	new Event(evt).stopPropagation();
	FormEditor.widgetMenu.close();
	return false;
};

FormEditor.initPage=function initPage () {
	var size, a, b, wgtToolbox, wid;
	wid=0;
	
	// new Asset.javascript('scripts/catalog.js', {id: 'w_catalog_script'});
	FormEditor.editorEvts=new FormEditorEvents();
	
	// stupid IE: no matter how much you drag, IE will always select your text
	$(document.body).ondragstart=function () {return false; }; //IE drag hack 

	// status and windowing system
	MochaUI.Desktop = new MochaUI.Desktop();
	MochaUI.Dock = new MochaUI.Dock({
		dockPosition: 'bottom'
	});
	MochaUI.Modal = new MochaUI.Modal();
	
	MochaUI.Desktop.desktop.setStyles({
		'visibility': 'visible'
	});
	
	MochaUI.NewWindowsFromHTML = new MochaUI.NewWindowsFromHTMLP({
		closable:false,
		restrict:true,
		onClose: function () {
			return false;
		}
	});
	FormEditor.catContainer = $('cat_toolbox').getFirst();
	wgtToolbox = $('wgt_toolbox');
	
	// generate widgets
	
	DragNSort.generateWidgets(FormEditor.catContainer,wgtToolbox);
	
	FormEditor.widgetMenu = new PopUpMenu();

	FormEditor.fieldtk = new DragNSort({
		dragElements:'div.widget',
		dragContainer: 'toolbox'
	});
	FormEditor.editorEvts.getValues=FormEditor.fieldtk.getForm.bind(FormEditor.fieldtk);

	if ($('newTab')){
		$('newTab').addEvent('click', function (evt) {
			new Event(evt).stop();
			FormEditor.fieldtk.addTab($('newTab'),false,true);
		});
	}

	$('add_cat').addEvent('click',function(evt) {
		var cancel, w;
		evt = new Event(evt).stop();
		w = 'addCatalogWindow'+wid++;

		new MochaUI.Window({
			'id':w,
			'title':'Adicionar ao Catálogo',
			/*'type':'modal',*/
			'loadMethod':'xhr',
			'contentURL':FormEditor.editorEvts.fixResource('addvar.html'),
			closable:true,
			width:320,
			height:200,
			onContentLoaded:FormEditor.loadAddVarHelper
		});
		// FormEditor.initBasicEvents(w,FormEditor.stopEvt);
	});
	
	$('form').addEvent('contextmenu', FormEditor.stopEvt);
//	['dockWrapper','toolboxWindow','propertiesWindow'].each(function (elem) {
	['dockWrapper','toolboxWindow'].each(function (elem) {
		FormEditor.initBasicEvents(elem,FormEditor.stopEvt);
	});
	window.addEvent('contextmenu', function (evt) {
		evt = new Event(evt).stop();
		FormEditor.widgetMenu.openDefault(FormEditor.fieldtk, evt.page.x,evt.page.y);
	});
	window.addEvent('click',function(e){
		e = new Event(e);
		// hide menu...
		FormEditor.widgetMenu.close();
		if(e.rightClick) {
			e.stop();
		} else {
			FormEditor.fieldtk.deselectElement();
		}
	});

	$('desktopNavbar').getElements('.returnFalse').each(function (elem){
		FormEditor.initBasicEvents(elem,FormEditor.stopEvt);
	});
	
	
	$('closeEditorLink').addEvent('click',function(evt) {
		evt = new Event(evt).stop();
		FormEditor.editorEvts.okPressed();
	});
	$('saveFormLink').addEvent('click',function(evt) {
		evt = new Event(evt).stop();
		FormEditor.editorEvts.saveValues();
	});
	$('promoteToTemplateLink').addEvent('click',function(evt) {
		evt = new Event(evt).stop();
		var cancel, w;
		evt = new Event(evt).stop();
		w = 'promoteWindow'+wid++;

		new MochaUI.Window({
			'id':w,
			'title':'Promover a Template',
			/*'type':'modal',*/
			'loadMethod':'xhr',
			'contentURL':'promote.html',
			closable:true,
			width:320,
			height:200,
			onContentLoaded:FormEditor.loadPromoteTemplateHelper
		});
		FormEditor.initBasicEvents(w,FormEditor.stopEvt);
	});
	
	$('cancelEditorLink').addEvent('click',function(evt) {
		evt = new Event(evt).stop();
		FormEditor.editorEvts.cancelPressed();
	});
	$('helpWindowLink').addEvent('click',function(evt) {
		evt = new Event(evt).stop();
		FormEditor.loadHelp();
	});
	
	$('applyPropertiesButton').addEvent('click',function(evt) {
		evt = new Event(evt).stop();
		FormEditor.debug('Apply button pressed!');
		FormEditor.fieldtk.applyProperties();
		FormEditor.debug('Apply button done!');
		return false;
	});
	$('revertPropertiesButton').addEvent('click',function(evt) {
		evt = new Event(evt).stop();
		FormEditor.debug('Revert button pressed!');
		FormEditor.fieldtk.revertProperties();
		FormEditor.debug('Revert button done!');
		return false;
	});
	
	$('previewFormLink').addEvent('click',function(evt) {
		evt = new Event(evt).stop();
		FormEditor.debug('Preview button pressed!');
		FormEditor.editorEvts.previewPressed();
		FormEditor.debug('Preview button done!');
		return false;
	});
	// show form properties
	FormEditor.editorEvts.requestInit();
	
	FormEditor.fieldtk.deselectElement();
	FormEditor.debug('Query string: '+location.search.substring(1));
	FormEditor.debug('initPage finished');
	
	// add event to 'onLoad'
	window.addEvent('load', FormEditor.repositionWindows);
};

FormEditor.loadHelp=function loadHelp() {
    new MochaUI.Window({
        id: 'helpWindow',
        title: 'Ajuda',
        loadMethod: 'iframe',
        contentURL: 'help.html',
        width: 340,
        height: 150
    });
};

FormEditor.loadAddVarHelper = function loadAddVarHelper(elem) {
	var addButtom, i18nChild, selection, checkbox;
	addButton = $('addVarButton');
	if(!addButton) return;
	// setup messages...
	
	if(window['FormEditorMessages']) FormEditorMessages.updateMessages(elem);
	
	addButton.addEvent('click', function (evt) {
		var form,name,desc,type,cat,initval,search,format;
		evt = new Event(evt).stop();
		// retrieve values
		form = elem.getElement('form');
		// TODO validate values
		name = $(form['name']).get('value');
		desc = $(form['description']).get('value');
		type = $(form['type']).get('value');
		initval = $(form['defaultValue']).get('value');
		search = $(form['isSearchable']).get('value');
		format = $(form['defaultFormat']).get('value');

		FormEditor.editorEvts.notifyBrowser('scripts/addCatalogVar.js', function callback (response,txt) {
			if(response) {
				if(response.ok) {
					FormEditor.fieldtk.addCatalogVar(FormEditor.catContainer,response.name,response.description,response.type,response.list,response.single);
					FormEditor.debug("Variable created.");
				} else {
					FormEditor.error("Error: invalid variable: "+response.error);
				}
			} else {
				FormEditor.error("Error: Invalid Response: "+txt);
			}
		}, {name:name,description:desc,type:type,defaultValue:initval,searchable:search,defaultFormat:format});
		elem.getElement('form').reset();

	});
	
//	checkbox = $('addVarIsSearchable');
//	checkbox.removeEvents();
//	checkbox.addEvent('click', function (e) {
//		e = new Event(e);
//		e.stop();
//	});

	selection = $('addCatTypeSelect');
	if(!selection) return;
	selection.addEvent('change', function (evt) {
		var value;
		value = this.get('value');
		FormEditor.debug('Selected value is: '+value);
		this.setStyle('background-image', 'url(images/'+value+'.png)');
	});

};

FormEditor.loadPromoteTemplateHelper = function loadPromoteTemplateHelper(elem) {
	var okButtom,i18nChild;
	okButtom = $('promoteOkButton');
	if(!okButtom) return;
	// setup messages...
	
	if(window['FormEditorMessages']) FormEditorMessages.updateMessages(elem);
	
	okButtom.addEvent('click', function (evt) {
		var form,name,desc,type,cat;
		evt = new Event(evt).stop();
		// retrieve values
		form = elem.getElement('form');
		// TODO validate values
		name = $(form['name']).get('value');
		
		if(name.trim()=='') {
			alert('Indique um nome');
			return;
		}

		FormEditor.editorEvts.notifyBrowser('scripts/doPromote.js',FormEditor.loadPromoteTemplateHelperCB, {name:name,force:false,form:FormEditor.editorEvts.getValues()});
	});

};

FormEditor.loadPromoteTemplateHelperCB = function loadPromoteTemplateHelperCB(response,txt) {
	if(response && response.ok) {
		$('promoteTemplateWarning').setStyle('display','none');
		if (response.invalid) {
			$('promoteTemplateWarning').setStyle('display','');
			$('promoteWarningDiv').set('text','O nome da template é inválido.');
		} else if(response.exists) {
			$('promoteTemplateWarning').setStyle('display','');
			$('promoteWarningDiv').set('text','Já existe uma template com o mesmo nome.');
			if(confirm('Já existe uma template com o mesmo nome. Substituir?')) {
			  FormEditor.editorEvts.notifyBrowser('scripts/doPromote.js',FormEditor.loadPromoteTemplateHelperCB, {name:response.name,force:true,form:FormEditor.editorEvts.getValues()});
			}
		} else {
			FormEditor.debug("Done. Reloading...");
			window.location.reload();
		}
	} else {
		$('promoteTemplateWarning').setStyle('display','');
		$('promoteWarningDiv').set('text','Ocorreu um erro durante a conversão para template');
		FormEditor.error("Error: Invalid Response: "+txt);
	}
};

/*
FormEditor.loadPromoteTemplateHelperCB2 = function loadPromoteTemplateHelperCB2(response,txt) {
	if(response && response.ok) {
		FormEditor.debug("Done. Reloading...");
		window.location.reload();
	} else {
		FormEditor.error("Error: Invalid Response: "+txt);
	}
};
*/

/**
 * Reajusta as janelas para ficar com o seguinte layout:
 *  | FFF TT |
 *  | FFF TT |
 *  | SSS PP |
 *  
 *  SSS (status window) está alinhada ao fundoe tem a mesma largura de FFF
 *  PP (properties window) está alinada ao fundo com altura e largura de 250
 *  TT (toolbox window) está alinhado ao topo e ocupa todo o espaço até PP
 *  FFF (form window) ocupa o espaço restante
 *  
 *  TODO definir umas constantes com os tamanhos por omissão
 * @return
 */
FormEditor.repositionWindows=function repositionWindows () {
	var size, wsize, wx, wy, a, b, fWin, pWin, tWin, fwidth, fright, fheight, dif;
	fWin = $('theFormWindow');
	pWin = $('propertiesWindow');
	tWin = $('toolboxWindow');
	MochaUI.focusWindow(fWin);
	wsize = window.getSize();
	wx = wsize.x.toInt();
	wy = wsize.y.toInt();
	// do resizes and stuff...
	fWin.setStyles({'width':wx-285,'height':wy-160,'top':65,'left':5});
	
	// reposition windows
	size = fWin.getCoordinates();
	fwidth = size.width.toInt();
	fright = size.right.toInt();
	fheight = size.height.toInt();
	pWin.setStyles({'left':fright+15,'width':250,'height':250});
	dif = size.bottom.toInt()-pWin.getCoordinates().bottom.toInt();
	if(dif > 0) {
		pWin.setStyle('top',pWin.getCoordinates().top.toInt()+dif);
	} else {
		dif = 0;
	}
	MochaUI.updateResize(pWin);
	FormEditor.debug('Bottom is: '+size.bottom);
	FormEditor.debug('Bottom is: '+pWin.getCoordinates().bottom);
	tWin.setStyles({'left':fright+15,'width':250,'height':250+dif,'top':size.top.toInt()});
	MochaUI.updateResize(tWin);
	
	if($('statusDialog')) {
		// reposition windows
		b = wy-170;
		
		$('statusDialog').setStyles({'top':b,'width':fwidth,'height':75,'left':size.left.toInt()});
		MochaUI.updateResize($('statusDialog'));
		fWin.setStyle('height',fheight-$('statusDialog').getStyle('height').toInt()-5);
		MochaUI.updateResize(fWin);
	}
	
	MochaUI.focusWindow(fWin);
};

FormEditor.createStatusWindow=function createStatusWindow () {
	var elems;

	// create the new window
	
	elems = new Element('div');
	elems.adopt(
		new Element('div',{'class':'status'})
		.adopt(
			new Element('input',{'type':'button','name':'clearLog','value':'Limpar log','id':'clearLog'}).addEvent('click',function(evt){
				evt = new Event(evt).stop();
				FormEditor.clear();
			})
		)
	).adopt(new Element('div',{id:'status','class':'status'}));
	
	// $(document).adopt(elems);

	var elDimensions = $('theFormWindow').getStyles('height', 'width');
	new MochaUI.Window({
		id: 'statusDialog',
		title: 'Status e Debug',
		content: $(elems),
		type: 'window', // 'window', 'modal' or 'notification'
		width: elDimensions.width.toInt(),
		height: 75,
		minimizable: true,
		maximizable: true,
		collapsible: true,
		resizable: true
	});
	
	
	
	$('statusDialog').setStyles({left:5, visible:'visible'});
	FormEditor.initBasicEvents('statusDialog',FormEditor.stopEvt);
};

FormEditor.initDebugPage=function initDebugPage () {
	var statusA, ffA, opA,ieA;
	
	FormEditor.createStatusWindow();
	
	$('desktopNavbar').getFirst('ul').adopt(
		new Element('li')
			.adopt(new Element('a',{'class':"returnFalse",href:""}).set('text', 'Testes e Debug'))
			.adopt(new Element('ul')
				.adopt(new Element('li').adopt(statusA=new Element('a',{id:'openDebugLink'}).set('text','Janela de Debug')))
				.adopt(new Element('li',{'class':"divider"}).adopt(ieA=new Element('a',{id:'openInternetExplorerLink'}).set('text','Abrir Editor no Internet Explorer')))
				.adopt(new Element('li',{'class':"divider"}).adopt(ffA=new Element('a',{id:'openFirefoxLink'}).set('text','Abrir Editor no Firefox')))
				.adopt(new Element('li',{'class':"divider"}).adopt(opA=new Element('a',{id:'openOperaLink'}).set('text','Abrir Editor no Opera')))
			)
	);
	
	statusA.addEvent('click', function(evt) {
		evt = new Event(evt).stop();
		if(!$('statusDialog')) 	FormEditor.createStatusWindow();
	});
	
	ffA.addEvent('click', function(evt){
		evt = new Event(evt).stop();
		FormEditor.editorEvts.notifyBrowser('scripts/openFirefox.js',function(resp){
			if(resp && resp.ok)
				FormEditor.debug('Pedido efectuado com sucesso');
			else
				FormEditor.debug('Ocorreu um erro no processamento do pedido. Consulte os logs do editor.');
		});
	});
	opA.addEvent('click', function(evt){
		evt = new Event(evt).stop();
		FormEditor.editorEvts.notifyBrowser('scripts/openOpera.js',function(resp){
			if(resp && resp.ok)
				FormEditor.debug('Pedido efectuado com sucesso');
			else
				FormEditor.debug('Ocorreu um erro no processamento do pedido. Consulte os logs do editor.');
		});
	});
	ieA.addEvent('click', function(evt){
		evt = new Event(evt).stop();
		FormEditor.editorEvts.notifyBrowser('scripts/openIExplore.js',function(resp){
			if(resp && resp.ok)
				FormEditor.debug('Pedido efectuado com sucesso');
			else
				FormEditor.debug('Ocorreu um erro no processamento do pedido. Consulte os logs do editor.');
		});
	});
	
	// FormEditor.showWindowEnv();
	FormEditor.debug('Debug mode enabled');
};

// register event
window.addEvent('domready', FormEditor.initPage);
