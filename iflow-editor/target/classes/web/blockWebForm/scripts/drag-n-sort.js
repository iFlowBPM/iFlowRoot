// This is a UTF-8 File

var DragNSort = new Class({
	options: {
		sortableClass:Sortables,
		holder: 'form',
		dragElements: '.widget',
		dragContainer: 'toolbox',
		bullet:'bullet',
		hoverClass:'hoverDropPlace',
		tabFolder: 'tabFolder'
	},

	initialize: function initialize(options) {
		var fctx;
		// merge the options....
		this.setOptions(options);
		
		// initialize
		this.widgetCount=0;
		this.tabCount=0;
		this.formHolder = false;//$(this.options.holder);
		this.bullet = $(this.options.bullet);
		this.formSortable = false;
		this.dragElements = this.options.dragElements;
		this.dragContainer = this.options.dragContainer;
		this.hoverClass = this.options.hoverClass;
		this.tabFolder = $(this.options.tabFolder);
		this.lastHolder = $(this.options.holder).getElement('.info');
		this.selected=false;
		this.deselectElement=this.hideSelected;
		
		// Note to self:
		// se calhar também faz sentido ter um "holder" para guardar o elemento a ser
		// arrastado, item seleccionado, etc. Sempre se poupava o stack de execução.
		
		// start your engines
		//this.createSortable();
		this.addTab($('newTab'), {}, true);
		this.start();
		
		// setup default form properties 
		fctx = DragNSort.FormCtx;
		$each(DragNSort.FormProperties,function(val){
			fctx.notifyPropertyChange.call(this, fctx, val['name'], val['default']);
		},this);
		
	},
	
	getHolder: function getHolder() {
		return this.formHolder;
	},
	
	/////////////////// widget selection \\\\\\\\\\\\\\\\\\\\
	highlight: function highlight(e) {
		if(!e) return;
		var bgcolor = e.getStyle('background-color');
		e.setStyle('background-color', '#ffff99');  // make sortable with blinking lights
		e.effect('background-color', {wait: false,duration:700}).start(bgcolor);
	},
		
	// must do the same to the buttons
	createSortable: function createSortable(selected) {
		this.destroySortable();
		// this.formHolder = selected.getParent();
		var handle = selected.getElement('.dragHandler');
		this.formSortable = new HandySortables(handle,selected,selected.getParent(),{
			'ghost':false,
			'onStart': function(element){
				element.setStyle('opacity', 0.7);
			}.bind(this),
			'onComplete': function(element){
				element.setStyle('opacity', 1);
				if(this.ptable) this.ptable.style.display='';
			}.bind(this)
		}).attach();
	},
	
	destroySortable: function destroySortable() {
		if(!this.formSortable) return; // do nothing
		// kill all events from sortable elements
		// Note: save handler list?
		this.formSortable.detach();
		this.formSortable=false;
	},

	selectElement: function selectElement(elem) {
		// hide menu...
		FormEditor.widgetMenu.close();
		if(this.selected && elem && this.selected.uid == elem.uid) return; // same element.
		this.hideSelected();
		if(elem) {
			this.selected=elem;
			// setup any properties....
			this.buildPropertiesPanel( this.selected.retrieve(DragNSort.CTX_PROP))
			this.controls = this.createControl();
			this.controls.injectTop(elem);
			this.createSortable(elem);
			this.selected.addClass('selected');
		}
	},
	
	addPropertyEntry:function addPropertyEntry(ctx, tbl, val, toNotify, owners) {
		var r,t,v,input,f,txt,lst,opt,name,id,addNotif,extra,value,reqResult,ronly;
		if(!val) return null;
		if(val.disable && val.disable(ctx)) return null;
		id=ctx.id;
		name=val.name;
		value = ctx.property_values[name];
		addNotif = false;
		r = new Element('div',{'class':'tr property','id':'prop_'+name});
		owners = owners||[];
		extra = owners.length;
		if(extra > 0) r.addClass('extra'+extra);
		r.store(DragNSort.PROP_OWNER,owners);
		r.store(DragNSort.PROP_NAME,name);
		r.store(DragNSort.PROP_VALUE,value);
		t = new Element('div',{'class':'td propname'}).adopt(new Element('div').set('text',val.description));
		v = new Element('div',{'class':'td'});
		
		// Check if type is JSON generated...
		if('json' == val.type) {
			FormEditor.debug('JSON val type found. invoking service...');
			FormEditor.debug('Val before: '+JSON.encode(val));
			// make sync request...
			reqResult = FormEditor.editorEvts.notifyBrowserSync(val.url);
			FormEditor.debug('addpropertyEntry Got this: '+JSON.encode(reqResult));
			// If request completed successfully, update property definition
			if(reqResult.ok) val = $merge(val, reqResult.values);
			FormEditor.debug('Val after: '+JSON.encode(val));
		}
		
		// boolean, int, float, string, values
		if('boolean' == val.type || 'bool'==val.type) {
			// make check box
			input = new Element('input',{'type':'checkbox','name':id+'_'+name,'value':value});
			input.checked=(value == 'true' || value==true);
			f = function(e,ctx,property,input,row) {
				if(e) e = new Event(e).stopPropagation();
				txt = input.checked?'true':'false';

				row.store(DragNSort.PROP_VALUE,txt);
				
				try {
					if(val.action) val.action({ctx:ctx,val:val,input:input,r:r});
				} catch(err){}
				
			}.bindWithEvent(this,[ctx,val,input,r]);
		} else if('values' == val.type) {
			// make selection
			// make check box
			input = new Element('select',{'name':id+'_'+name,'value':value});
			input.options.length=0;
			lst = val.values;
			lst.each(function (l,pos) {
				var v,d;
				d=l.description.trim();
				v=l.value.trim();
				opt = new Element('option');
				opt.set('value', v);
				opt.set('text', l.description);
				opt.store(DragNSort.DYNA_PROPS,l['extra']); // keep extra properties in element storage
				input.adopt(opt);
				if(v==value) opt.selected=true;
				if(l['extra']) addNotif = true;
			});
			
			f = function(e,ctx,property,input,tbl,extra,owners,row) {
				var props, children, notif, opt, relTo;
				if(e) e = new Event(e).stopPropagation();
				
				FormEditor.debug('Chamou o change event');
				
				// get all 'extra' properties
				children = tbl.getElements('[class*=extra]');
				FormEditor.debug('Extra num: '+extra);
				// remove all 'extra' properties with 'extra' value greater than current
				if(children) children.each(function (elem) {
					var p, owners;
					// retrieve DnS:Owner
					owners = elem.retrieve(DragNSort.PROP_OWNER)||[];
					
					if(owners.contains(input.name)) elem.destroy(); // o destroy limpa o storage
				});
				
				txt = input.get('value');
				FormEditor.debug('TXT='+txt);
				
				row.store(DragNSort.PROP_VALUE,txt);
				
				relTo = input.getParent().getParent(); // parent '.tr'
				// a falta de melhor
				opt = input.getElement('option[value='+txt+']');
				if(opt) {
					notif = new Array();
					props = opt.retrieve(DragNSort.DYNA_PROPS);
					newOwners = $A(owners);
					newOwners.push(input.name);
					if(props && props.length>0) {
						// build dynamic properties....
						$each(props, function (val,pos) {
							var r;
							empty = false;
							r = this.addPropertyEntry(ctx, tbl, val, notif, newOwners);
							if(r) {
								relTo.grab(r,'after');
								relTo = r;
							}
						}, this);
					}

					notif.each(function (elem) {elem.fireEvent('change');});
				}
				
				try {
					if(val.action) val.action({ctx:ctx,val:val,input:input,r:r});
				} catch(err){}
				
			}.bindWithEvent(this,[ctx,val,input,tbl,extra,owners,r]);
		} else if('header'==val.type){
			input = new Element('input',{'type':'hidden','name':id+'_'+name,'value':value});
			f = function() {
				return false;
			};
			r.removeClass('property');
			t.removeClass('td');
			t.addClass('th');
			v.removeClass('td');
			v.addClass('th');
			v.set('text', '\u00A0');
		} else {
			input = new Element('input',{'type':'text','name':id+'_'+name,'value':value});
			f = function(e,ctx,property,input,row) {
				if(e) e = new Event(e).stopPropagation();
				txt = input.get('value');

				row.store(DragNSort.PROP_VALUE,txt);

				try {
					if(val.action) val.action({ctx:ctx,val:val,input:input,r:r});
				} catch(err){}
				
			}.bindWithEvent(this,[ctx,val,input,r]);
			input.addEvent('focus',function(e){this.select();});
		}
		ronly=val['readonly'];
		if(!$defined(ronly)) ronly='false';
		if(ronly=='true')input.set('disabled','disabled');
		
		if(f) input.addEvent('change', f);
		input.addEvent('blur', function(e,elem) {FormEditor.fieldtk.applyProperty(elem);}.bindWithEvent(this,r));
		v.adopt(input);
		t.addEvent('click', function(e,input){e = new Event(e).stopPropagation();input.focus();}.bindWithEvent(this,input));
		v.addEvent('click', function(e,input){e = new Event(e).stopPropagation();}.bindWithEvent(this,input));
		r.adopt(t);
		r.adopt(v);
		if(addNotif) toNotify.push(input);
		return r;
	},
	
	/*
	 * Build a property configuration panel.
	 * This will be shared by widgets and tabs.
	 */
	buildPropertiesPanel:function buildPropertiesPanel(ctx) {
		var empty,tbl, props, warn, head, notif, type;
		if(!$('autoProperties')) return;
		if(this.ptable) this.ptable.destroy();
		// new properties table
		this.ptable = new Element('div',{'class':'properties'});
		this.pctx = ctx;
		empty = true;
		tbl = new Element('div',{'class':'table'});
		props = this.getProperties(ctx);

		FormEditor.debug('ID='+ctx.id);
		
		notif = new Array();
		
		if(props) {
			$each(props, function (val,pos) {
				var r;
				empty = false;
				r = this.addPropertyEntry(ctx, tbl, val, notif);
				if(r) tbl.adopt(r);
			}, this);
		}
		if(ctx.inContainer && ctx.containerProperties) {
			$each(ctx.containerProperties, function (val,pos) {
				var r;
				empty = false;
				r = this.addPropertyEntry(ctx, tbl, val, notif);
				if(r) tbl.adopt(r);
			}, this);
		}
		if(empty) {
			// tbl.destroy();
			warn = new Element('div').set('text','No properties available');
			this.ptable.adopt(warn);
			this.enablePropButtons(false);
		} else {
			head = new Element('div',{'class':'tr'});
			head.adopt(new Element('div',{'class':'th'}).set('text','Propriedade'));
			head.adopt(new Element('div',{'class':'th'}).set('text','Valor'));
			head.addEvent('click', function(e){e = new Event(e).stopPropagation();}.bindWithEvent(this));
			head.injectTop(tbl);
			this.ptable.adopt(tbl);
			this.enablePropButtons(true);
		}
		
		notif.each(function (elem) {elem.fireEvent('change');});
		// switch properties...
		$('autoProperties').adopt(this.ptable);
//		$('autoProperties').setStyle('display','');
//		$('propertyContainer').setStyle('display','none');
		$('propertyItemTitle').set('text', DragNSort.getWidgetName(ctx.type));
	},
	
	applyProperties:function applyProperties() {
		if(!this.ptable)return;
		if(!this.pctx)return;
		
		this.ptable.getElements('.tr.property').each(function (elem){
			var txt,pname,ctx;
			ctx = this.pctx;
			pname = elem.retrieve(DragNSort.PROP_NAME);
			txt = elem.retrieve(DragNSort.PROP_VALUE);
			txt = $defined(txt)?txt:'';
			if(pname) {
				FormEditor.debug('Setting "'+pname+'"="'+txt+'"');
				
				ctx.property_values[pname]=txt;
				if(ctx.notifyPropertyChange)
					ctx.notifyPropertyChange.call(this,ctx,pname,txt);
			}
		}, this);
	},
	
	applyProperty:function applyProperty(elem) {
		var txt,pname,ctx;
		if(!this.ptable)return;
		if(!this.pctx)return;
		if(!elem)return;
		
		ctx = this.pctx;
		pname = elem.retrieve(DragNSort.PROP_NAME);
		txt = elem.retrieve(DragNSort.PROP_VALUE);
		txt = $defined(txt)?txt:'';
		if(pname) {
			FormEditor.debug('Setting "'+pname+'"="'+txt+'"');

			ctx.property_values[pname]=txt;
			if(ctx.notifyPropertyChange)
				ctx.notifyPropertyChange.call(this,ctx,pname,txt);
		}
	},
	
	revertProperties:function revertProperties() {
		if(!this.pctx)return;
		this.buildPropertiesPanel(this.pctx);
	},
	
	enablePropButtons: function enablePropButtons(enable) {
		var e;
		if((e=$('revertPropertiesButton'))) e.disabled=!enable;
		if((e=$('applyPropertiesButton'))) e.disabled=!enable;
	},
	
	hidePropertiesOnDrag: function hidePropertiesOnDrag(e) {
		// if(this.ptable) this.ptable.style.display='none';
	},
	
	createControl: function createControl() {
		var gen,control,ul,li,dragHandler,removeHandler,readonlyHandler,overrideHandler,hidePropsEvt;
		hidePropsEvt = this.hidePropertiesOnDrag.bindWithEvent(this);
		gen = this.selected.retrieve(DragNSort.CTX_PROP);
		control = new Element('div', {'class':'controls'});
		ul = new Element('ul',{});
		li = new Element('li',{});
		dragHandler = new Element('img',{'class':'dragHandler ctlIcon','src':'images/move.png','title':'drag me'});
		ul.adopt(li.clone().adopt(dragHandler));
		removeHandler = new Element('img', {'class':'trash ctlIcon','src':'images/trash.png','title':'delete'});
		ul.adopt(li.clone().adopt(removeHandler));
		readonlyHandler = new Element('img', {'class':'readonly ctlIcon','src':'images/unlock.png','title':'mark readonly'});
		ul.adopt(li.clone().adopt(readonlyHandler));
		overrideHandler = new Element('img', {'class':'override ctlIcon','src':'images/tooloptions.png','title':'override template'});
		ul.adopt(li.adopt(overrideHandler));
		control.adopt(ul);
		
		// disable an auto generated click event when widget is dragged 
		dragHandler.addEvent('click',function(e) {
			e = new Event(e).stop();
		});
		// hide properties table
		dragHandler.addEvent('mousedown',hidePropsEvt);
		
		removeHandler.addEvent('click',this.removeWidgetEvt.bindWithEvent(this,this.selected));
		readonlyHandler.addEvent('click',this.readonlyEvt.bindWithEvent(this,this.selected));
		overrideHandler.addEvent('click',this.overrideEvt.bindWithEvent(this,this.selected));
		return control;
	},
	
	hideSelected: function hideSelected() {
		// destroy any existing menu...
		var menuDiv,body;
		body=$('body');
		menuDiv = body.retrieve(DragNSort.MENU_PROP);
		if(menuDiv) menuDiv.destroy();
		body.store(DragNSort.MENU_PROP, false);

		if(this.controls) {
			this.controls.destroy();
			this.controls = false;
		}
		if(this.ptable) {
			this.ptable.destroy(); // usar um FX??
			this.ptable = false;
		}
		if(this.selected) {
			this.destroySortable();
			this.selected.removeClass('selected');
			this.selected=false;
		}
		// setup any properties....
		this.buildPropertiesPanel(DragNSort.FormCtx);
//		$('autoProperties').setStyle('display','none');
//		$('propertyContainer').setStyle('display','');
//		$('propertyItemTitle').set('text', 'Formulário');
	},
	
	//////////////////// DRAG AND DROP LOGIC \\\\\\\\\\\\\\\\\\\\\

	initDragElement: function initDragElement(item) {
		item.addEvent('click', function(e) {return false;}); // disable click event
		item.addEvent('mousedown', function(e,item) {
			var clone;
			e = new Event(e).stop();
			this.hideSelected();
			this.removeDrops();  // destroy any existing drop
			var drops = [];
			
			// inject main drop places
			if(this.acceptItem(item)) {
				$$(this.formHolder.getChildren()).each(function (ee) {
					this.makeDrop(drops,this.formHolder,item,ee);
				},this);
				this.makeDrop(drops,this.formHolder,item,false);
			}
			// check if inner widgets have drop zones
			$$(this.formHolder.getChildren()).each(function (liElem) {
				var ctx = liElem.retrieve(DragNSort.CTX_PROP);
				if(ctx && ctx.acceptItem && ctx.createDrops && ctx.acceptItem.call(this,ctx,item)) {
					ctx.createDrops.call(this,ctx,drops,item);
				}
			},this);
			
			if(drops && drops.length > 0) {
				var coords = item.getCoordinates();
				coords.top += window.getScrollTop().toInt();
				coords.left += window.getScrollLeft().toInt();
				clone = item.clone()
				.setStyles(coords) // this returns an object with left/top/bottom/right, so its perfect
				.setStyles({'opacity': 0.7, 'position': 'absolute', 'z-index':1000})
				.inject(document.body);

				this.formHolder.store(DragNSort.DROP_LIST, drops);
				var drag = clone.makeDraggable({
					droppables:drops,
					'onDrop': function(element,droppable) {
						var liElem;
						this.hideDrop(droppable);
						element.destroy();
						if(droppable) {
							liElem = this.createItem(item,false);
							liElem.injectBefore(droppable);
							liElem.getParent().fireEvent(DragNSort.ADOPT_EVENT, [liElem]);
							//this.createSortable();
							this.selectElement(liElem);
						}
						this.removeDrops();
					}.bind(this),
					'onEnter': function(element, droppable) {
						if(!droppable) return;
						// drop.setStyle('background-color', '#98B5C1');
						this.showDrop(droppable);
					}.bind(this),
					'onLeave': function(element, droppable) {
						if(!droppable) return;
						// drop.setStyle('background-color', '#ffffff');
						this.hideDrop(droppable);
					}.bind(this)
					
				}); // this returns the dragged element
	
				drag.start(e); // start the event manual
			}
		}.bindWithEvent(this, item));
	},
	
	start: function start() {
		$(this.dragContainer).getElements(this.dragElements).each(this.initDragElement,this);
	},
	
	removeDrops: function removeDrops() {
		var dropList;
		// retrieve drops from drop list
		dropList = this.formHolder.retrieve(DragNSort.DROP_LIST);
		if(!dropList) {
			FormEditor.debug('drop list is empty...');
			return;
		}

		// remove drop marks
		$$(dropList).each(function (liElem) {
			liElem.removeClass(this.hoverClass);
			
			// check if inner widgets have drops
			var ctx = liElem.retrieve(DragNSort.CTX_PROP);
			if(ctx && ctx.removeDrops) {
				ctx.removeDrops.call(this,data);
			}
			// if is a drop gen, destroy...
			if(liElem.hasClass(DragNSort.DROP_GEN_CLASS)) liElem.destroy();
		},this);
		dropList = this.formHolder.store(DragNSort.DROP_LIST, null);
	},
	
	/**
	 * holder: Element containing sortable elements
	 * item:   Item selected
	 * clone:  Ghost item (element following mouse)
	 * friend: Element in the sortable list (holder child)
	 */
	makeDrop: function makeDrop(drops, holder, item, friend) {
		var drop;
		
		if(friend) {
			drop = friend;
		} else {
			holder.adopt(drop = new Element('li', {'class':'sortables '+DragNSort.DROP_GEN_CLASS}));
			var ee = new Element('div', {'class':'dropPlace'});
			ee.set('text','Drop here...');
			drop.adopt(ee);
		}
		drops.push(drop);
		return drop;
	},
	
	showDrop: function showDrop(drop) {
		drop.addClass(this.hoverClass);
		var coord = drop.getCoordinates();
		var t = coord.top-6;
		var l = coord.left-8;
		if(this.bullet)
			this.bullet.setStyles({'left':l,'top':t,'display':''});
	},
	
	hideDrop: function hideDrop(drop) {
		if(this.bullet)
			this.bullet.setStyle('display','none');
		if(drop)
			drop.removeClass(this.hoverClass);
	},
	
	acceptItem: function acceptItem(item) {
		var catElem, id;
		id=item.get('id');
		catElem = DragNSort.getCatalogVar(id);
		// Do not add list elements to form (only table)
		if(catElem && !catElem.isSingle) return false;
		// otherwise, accept if not a button (only button container)
		return id!='w_button';
	},
	
	/////////////////////////// LOAD AND SAVE \\\\\\\\\\\\\\\\\\\\\\\\\
	//{"tabs":[{"text":"Main","fields":[{"id":"widget_0","type":"w_header","values":{"text":"Patrulha e partilha de documentação"}},{"id":"widget_4","type":"w_subheader","values":{"text":"Serviço de apoio a clientes"}}]},{"text":"Nova Tab","fields":[{"id":"widget_2","type":"w_header","values":{"text":"Patrulha e partilha de documentação"}},{"id":"widget_3","type":"w_subheader","values":{"text":"Alterações de tarifário"}}]}],"properties":{"desc":"","result":"","pagexsl":"default.xsl","printxsl":"default.xsl","autosubmit":"false"}}
	setForm:function setForm(form) {
		var ctx;
		if(!$defined(form) || !$defined(form['tabs']) || !$defined(form['properties'])) return;
		
		ctx = DragNSort.FormCtx;
		// form properties
		$each(form.properties,function(v,n){
			ctx.notifyPropertyChange.call(this,ctx,n,v);
		}, this);
		
		// load form
		this.removeAllTabs();
		$each(form.tabs, function (tab) {
			this.addTab($('newTab'), tab.properties, true);
			this.setValues(tab.fields);
		}, this);
	},
	
	setValues: function setValues (values, inside) {
		$each(values, function(value,pos){
			this.setValueImpl(value,pos,this.formHolder);
		},this);
		//this.createSortable();
		this.deselectElement(this);
	},

	setValue: function setValue (id,opts,holder) {
		this.setValueImpl(opts,id,holder);
		//this.createSortable();
		this.deselectElement(this);
	},
	
	setValueImpl: function setValueImpl (value, key, holder) {
		var item,liItem,gen,subHolder,type;
		type = value.type;
		if(value.type=='process_data')
			type = 'v_'+value.variable;
		
		FormEditor.debug("Var type: "+type+"; value type: "+value.type);
		item = $(type);
		if(item) {
			liElem = this.createItem(item,value);
			if(!holder) holder = this.formHolder;
			holder.adopt(liElem);
			holder.fireEvent(DragNSort.ADOPT_EVENT, [liElem]);
			gen = liElem.retrieve(DragNSort.CTX_PROP);
			if(value.fields && 
					value.fields.length > 0 && 
					gen && 
					gen.getHolder && 
					(subHolder=gen.getHolder())) {
				$each(value.fields, function(field,pos) {
					this.setValueImpl(field,pos,subHolder);
				}, this);
			}
		} else {
			FormEditor.error('ERROR: Unknown type: '+type);
		}
	},
	
	getForm: function getForm() {
		// set "getValues" function to read values from our DragNSort instance
		return {
			'tabs':this.getValues(),
			'properties':DragNSort.FormCtx.property_values
		};
	},
	
	getValues: function getValues () {
		var tabs,values,pos;
		values = [];
		pos=0;
		tabs = this.tabFolder.getElements('.tab');
		tabs.each(function (t) {
			var text,fields,ctx;
			
			ctx = t.retrieve(DragNSort.TAB_CTX);
			text = ctx.property_values['title']||'';// t.getElement('span').get('text');
			fields = this.getHolderValues(t.retrieve(DragNSort.TAB_FORMLAYOUT));
			values[pos] = {'text':text,'fields':fields,'properties':ctx.property_values};
			pos++;
		}.bind(this));
		
		return values;
	},
	
	getHolderValues:function getHolderValues (holder) {
		var obj,data,id,result = [],subHolder;
		if(holder) {
			holder.getChildren().each(function (liElem) {
				data = liElem.retrieve(DragNSort.CTX_PROP);
				if(!data) return;
				id = data.id;
				obj = {id:id,type:data.type,values:data.property_values};
				if(data.getHolder && (subHolder=data.getHolder())) {
					obj.fields = this.getHolderValues(subHolder);
				}
				if(data.process_data) {
					obj.variable = data.var_id.substring(2);
					FormEditor.debug('Variable is: '+obj.variable);
				}
				result.push(obj);
			}, this);
		}
		return result;
	},
	
	//////////////////// WIDGET MANIPULATION \\\\\\\\\\\\\\\\\\\\\
	addCatalogVar: function addCatalogVar (catContainer,name,desc,type,list,single) {
		var cat;
		name = DragNSort.registerCatalogVar(name,desc,type,list,single);
		cat = DragNSort.getCatalogVar(name);
		DragNSort.generateCatalogWidget(catContainer, cat)
		this.initDragElement($(name));
	},
	
	// aqui esta o codigo que estava em Widgets
	registerWidget: function registerWidget(gen) {
		if(gen.process_data) { // restore...
			$(gen.var_id).setStyle('display','none');
		}
	},

	unregisterWidget: function unregisterWidget(elem) {
		var gen = elem.retrieve(DragNSort.CTX_PROP);
		if(gen && gen.process_data) { // restore...
			$(gen.var_id).setStyle('display','');
		}
		
		if(gen && gen.destroy) gen.destroy.call(this,gen);
	},

	createItem: function createItem(elem,data) {
		var num = this.widgetCount++;
		var type = elem.id;
		var proc_data = elem.hasClass('process_data');
		if(proc_data) type='process_data';
		var id = 'widget_'+num;
		var titleElem;
		var gen = {'type':type,'id':id,'process_data':proc_data,'property_values':{}};
		if(proc_data) gen.var_id=elem.id;
		var ww = this.getWidgetCtl(type);
		$extend(gen,ww);
		$each(this.getProperties(gen),function(e,name) {
			gen.property_values[e.name]=e['default'];
		});
		var childrenToAdopt = [];
		if(gen && gen.create) {
			titleElem=gen.create.call(this, gen, id, data, childrenToAdopt);
		} else {
			titleElem = elem.clone();
			titleElem.setProperty('id',id);
			childrenToAdopt.push(titleElem);
			if(gen && gen.loadData) gen.loadData.call(this, gen, data);
		}
		// inject a nonbreak space
		titleElem.grab(document.createTextNode('\u00A0'),'top');
		titleElem.grab(document.createTextNode('\u00A0'),'bottom');
		
		gen.item=titleElem;
		this.registerWidget(gen); // register widget in main context
		var liElem = new Element('li',{'class':'sortables'});
		titleElem.addEvent('click',function(e,item){
			e = new Event(e).stop();
			this.selectElement(item,this);
		}.bindWithEvent(this,liElem));
		liElem.addEvent('contextmenu', this.handleContextMenu.bindWithEvent(this,liElem));
		/*var control = this.createControl(gen);
		liElem.adopt(control);*/
		// elem.adopt(gen.item);
		childrenToAdopt.each(function(e){liElem.adopt(e);});
		liElem.store(DragNSort.CTX_PROP,gen);
		return liElem;
	},

	getProperties: function getProperties(ctx) {
		var var_type;
		var props = DragNSort.getWidgetProperties(ctx.type);
		if(props) {
			if(ctx.process_data && ctx.var_id) {
				var_type = DragNSort.getCatalogVar(ctx.var_id).type;
				if(var_type && props[var_type]) props = props[var_type];
				else props = props['default'];
			}			
		}
		// could not find any property Try inner containers
		
		return props;
	},
	
	getWidgetCtl: function getWidgetCtl(type) {
		return DragNSort.regwidgets.get(type);
	},
	
	
	/////////////////// TAB HANDLES \\\\\\\\\\\\\\\\\\\\\\
	addTab: function addTab(newTabWgt, tabProperties, autoSwitchTab) {
		var div,img,span, newLayout, newHolder,tabName;
		FormEditor.widgetMenu.close();
		tabName = 'Nova Tab';
		if(tabProperties && tabProperties.name)
			tabName = tabProperties.name;
		
		newHolder = new Element('div',{'class':'formHolder','style':'display:none','id':'holder'+this.tabCount});
		newLayout = new Element('ul', {'class':'formLayout','id':'layout'+this.tabCount});
		newHolder.adopt(newLayout);
		newHolder.inject(this.lastHolder, 'after');
		this.lastHolder = newHolder;
		
		div = new Element('div',{'class':'tab','id':'tab'+this.tabCount});
		span = new Element('span',{'class':'tabTitle'});
		img = new Element('img',{'class':'close',src:'images/close.gif',alt:'Close',title:'Close'});
		span.set('text', tabName);
		div.adopt(span).adopt(img);

		img.addEvent('click', this.removeTab.bindWithEvent(this,div));
		div.addEvent('click', this.switchTab.bindWithEvent(this,div));
		div.addEvent('contextmenu', this.showTabMenu.bindWithEvent(this,div));
		
		// tab holder management
		div.store(DragNSort.TAB_FORMLAYOUT,newLayout);
		div.store(DragNSort.TAB_FORMHOLDER,newHolder);
		
		div.inject(newTabWgt,'before');
		div.appendText(' ', 'after');
		div.store(DragNSort.TAB_CTX, DragNSort.createTabContext(div,tabName,tabProperties));
		
		this.tabCount++;
		
		if(autoSwitchTab) this.switchTab(false, div);
	},
	
	switchTab:function switchTab(evt, tab) {
		var selTab, layout, holder, input, span, tabCtx, tbl, id;
		if(evt) new Event(evt).stop();
		FormEditor.widgetMenu.close();
		// build a properties stuff for tabs
		selTab = this.tabFolder.getElement('.selectedTab');
		
		tabCtx = tab.retrieve(DragNSort.TAB_CTX);
		this.buildPropertiesPanel(tabCtx);
		
		if(selTab && tab && selTab.uid == tab.uid) { // same tab
			// Do something
		} else {
			if(selTab) selTab.removeClass('selectedTab');
			tab.addClass('selectedTab');
			if(this.formHolder) this.formHolder.getParent().setStyle('display','none');
			layout = tab.retrieve(DragNSort.TAB_FORMLAYOUT);
			holder = tab.retrieve(DragNSort.TAB_FORMHOLDER);
			holder.setStyle('display','block');
			this.formHolder = layout;
		}
	},
	
	removeTab: function removeTab(evt, tab, force) {
		var parent, next, container, selTab;
		if(evt) new Event(evt).stop();
		FormEditor.widgetMenu.close();
		
		if(!force) {
			// we cant remove the last one
			if(this.tabFolder.getElements('.tab').length == 1) return;
			selTab = this.tabFolder.getElement('.selectedTab');
			if(selTab.uid == tab.uid) {
				// ok, switch to another tab except "add new tab" tab
				next = tab.getNext();
				if(next && next.uid != $('newTab').uid) {
					this.switchTab(false, next);
				} else {
					next = tab.getPrevious();
					if(next && next.uid != $('newTab').uid) this.switchTab(false, next);
				}
			}
		}
		container = tab.retrieve(DragNSort.TAB_FORMHOLDER);
		container.destroy();
		tab.destroy();
	},
	
	removeAllTabs: function removeAllTabs() {
		// kill existing tabs, since this is a initializer method.
		this.tabFolder.getElements('.tab').each(function (tab) {
			this.removeTab(false, tab, true);
		}.bind(this));
		
		// reset some values...
		this.lastHolder = $(this.options.holder).getElement('.info');
		this.formHolder = false;
		this.widgetCount=0;
		this.tabCount=0;
	},
	
	showTabMenu: function showTabMenu(evt, tab) {
		if(!evt) return;
		var e=new Event(evt).stop();
		FormEditor.widgetMenu.openTab(this,e.page.x,e.page.y);
	},
	
	renameTab: function renameTab(evt, tab) {
		
	},

	
	//////////////////// Event Handling \\\\\\\\\\\\\\\\\\\\\
	handleContextMenu: function handleContextMenu(evt, liElem) {
		var e = new Event(evt);
		if(!e.rightClick) return;
		e.stop();
		FormEditor.widgetMenu.open(this,liElem,e.page.x,e.page.y);
	},
	
	removeWidgetEvt:function removeWidgetEvt(e,liElem) {
		e = new Event(e).stopPropagation();
		var gen = liElem.retrieve(DragNSort.CTX_PROP);
		if(!e.shift && !confirm('Remover '+gen.id+' ?')) return false;
		this.unregisterWidget(liElem);
		liElem.destroy(); // destroy parent 'li'
		return false;
	},
	
	readonlyEvt:function readonlyEvt (e,liElem){
		e = new Event(e).stop();
		var gen = liElem.retrieve(DragNSort.CTX_PROP);
		alert('no readonly'+gen.id);
		// TODO implement....
	},
	
	overrideEvt:function overrideEvt(e,liElem){
		e = new Event(e).stopPropagation();
		var gen = liElem.retrieve(DragNSort.CTX_PROP);
		alert('no override '+gen.id);
		// TODO implement....
	}

});
DragNSort.implement(new Options);


// ISTO JÁ MUDOU!! ACTUALIZAR!!
/*
Function: DragNSort.registerWidget
	Register a widget in DragNSort engine.

Arguments:
	w_name  - string; the name of the widget. This name will be used as widget type in instances.
	options - an Object, see options below.

Options:
	create - function(ctx,id,data); initialization function called when a widget is dropped in the form.
		Arguments: 
			ctx - the widget context.
			id - the widget id.
			data (optional) - object to initialize widget.

	destroy - function(ctx) - optional; called when widget is removed. 
		Arguments:
			ctx - the widget context.
			
	createSortable - function(ctx) - optional; called to create an internal Sortables instance in this widget container.
		Arguments:
			ctx - the widget context.
			
	registerWidget - function(ctx,gen) - optional; called when a new widget is dropped inside this widget container. 
		Arguments:
			ctx - the widget context.
			gen - the new widget context
			
	save - function(ctx) - optional; make an object to save this widget
		Arguments:
			ctx - the widget context.
			
	createDrops - function(ctx,holders,item,clone) - optional; called drag starts 
		Arguments:
			ctx - the widget context.
			holders - list; Add all drop zone elements to this list, if any.
			item - element; Element to drag
			clone - element; Ghost element following mouse.
			
	removeDrops - function(ctx) - optional; called when drag stop/canceled
		Arguments:
			ctx - the widget context.
			
	properties - object; Object with the following structure: prop_id:options
		Options:
			description - string; property description.
			type - string; property type. Can be one of boolean, int, float, string, values.
			default - (any); default value for this property.
			values - string; comma separated list of possible values (only if type is 'values').

*/
DragNSort.widgetList = new Array();
DragNSort.regwidgets = new Hash();
DragNSort.widgetNames = new Hash();
DragNSort.widgetProperties = new Hash();
DragNSort.catalog = new Hash();
DragNSort.registerWidget=function registerWidget(name, desc, options) {
	DragNSort.widgetList.include(name);
	DragNSort.widgetProperties.set(name, options.properties);
	options['properties'] = undefined;
	DragNSort.regwidgets.set(name, options);
	DragNSort.widgetNames.set(name, desc);
};
DragNSort.getWidgetName=function getWidgetName(wgt) {
	return DragNSort.widgetNames.get(wgt);
};
DragNSort.getCatalogVar=function getCatalogVar(name) {
	return DragNSort.catalog.get(name);
};
DragNSort.getWidgetProperties=function getWidgetProperties(name) {
	return DragNSort.widgetProperties.get(name);
};

DragNSort.CTX_PROP='drag-n-sort:ctx';
DragNSort.TAB_FORMLAYOUT='drag-n-sort:tabFormHolder';
DragNSort.TAB_FORMHOLDER='drag-n-sort:tabFormLayout';
DragNSort.TAB_CTX='drag-n-sort:tabCtx';
DragNSort.DROP_LIST='drag-n-sort:drop-list';
DragNSort.DROP_GEN_CLASS='dropGen';
DragNSort.ADOPT_EVENT='dnsAdopt';

DragNSort.DYNA_PROPS='drag-n-sort:Extra';
DragNSort.PROP_OWNER='drag-n-sort:Owner';
DragNSort.PROP_NAME='drag-n-sort:Name';
DragNSort.PROP_VALUE='drag-n-sort:Value';

// Drag and Sort Adopt element - usually bound to UL elements to be notified when a new item is adopted
Element.Events.dnsAdopt = {};

// Insere um widget correspondente a uma variavel do processo
DragNSort.generateCatalogWidget = function generateCatalogWidget(catContainer, cat) {
	var div, span;
	div = new Element('div',{'id':cat.name,'class':'widget process_data'});
	span = new Element('span',{'class':'tags'});
	div.adopt(span);
	div.adopt(new Element('span',{'class':'demo label'}).set('text',cat.desc));
	
	span.adopt(new Element('img',{'src':'images/'+cat.type+'.png'}));

	catContainer.grab(div,'before');
};

// Associar um evento ao domready para carregar todos os widgets registados
DragNSort.generateWidgets = function generateWidgets(catContainer, wgtContainer) {
	wgtContainer = $(wgtContainer);
	catContainer = $(catContainer);

	DragNSort.widgetList.each(function(wgt){
		var div, span, cat, ctx;
		
		// if catalogo
		cat = DragNSort.getCatalogVar(wgt);
		if(cat) {
			DragNSort.generateCatalogWidget(catContainer, cat)
		} else {
			ctx = DragNSort.regwidgets.get(wgt);
			if(ctx.skipWidgetCreation) return;
			div = new Element('div',{'id':wgt,'class':'widget '+wgt});
			span = new Element('span', {'class':'demo label'});
			wgtContainer.adopt(div.adopt(span.set('text',DragNSort.getWidgetName(wgt))));
		}
	});
};

/*
DragNSort.registerCatalogVar('name','desc','type','dim');
 name: is variable name
 desc: is description
 type: text(default)  or number
 dim: single (default) or array
*/
DragNSort.registerCatalogVar = function registerCatalogVar(variable,desc,type,isList,isSingle) {
	var isNum,isLst;
	if(!variable ) return variable; // cannot be null/undefined
	desc = desc||variable;
	name = 'v_'+variable;
	type = type || 'Text';
	
	DragNSort.catalog.set(name,{'name':name,'desc':desc,'type':type,'isList':isList,'isSingle':isSingle});
	DragNSort.registerWidget(name,desc,$extend({'variable':variable},DragNSort.regwidgets.get('process_data')));
	return name;
};
DragNSort.FormStyleSheets=[{description:'Omissão',value:''}];
DragNSort.PrintStyleSheets=[{description:'Sem Impressão',value:''}];

DragNSort.notifyTemplateChangeCallback = function notifyTemplateChange () {
	window.location.reload();
};

DragNSort.notifyFormPropertyChange = function notifyFormPropertyChange (ctx, name, val) {
	var e;
	ctx.property_values[name]=val;
	if(name=='desc' && (e=$('formTitleElem'))) {
		if(!val || val == '') {
			e.addClass('formTitleError');
			e.set('text','< Indique um título para o formulário... >');
		} else {
			e.removeClass('formTitleError');
			e.set('text',val);
		}
	} else if(name=='template' && val=='true') {
		FormEditor.debug('This is a template. Must disable some features...');
		FormEditor.isTemplate = true;
		$('tabFolder').setStyle('display','none');
		$('promoteToTemplateLink').setStyle('display','none');
		$('w_template').setStyle('display','none'); // Retirar este quando estiver tudo a funcionar OK
	}
};

DragNSort.FormCtx={
	type:'Form',
	id:'Form',
	property_values:{},
	notifyPropertyChange:DragNSort.notifyFormPropertyChange
};


DragNSort.FormProperties =
	[
	  	{name:'desc',description:'Descrição',type:'string','default':''},
		{name:'result',description:'Resultado',type:'string','default':''},
		{name:'pagexsl',description:'Estilo',type:'values','default':'',
			values:DragNSort.FormStyleSheets
		},
		{name:'printxsl',description:'Impressão',type:'values','default':'',
			values:DragNSort.PrintStyleSheets
		},
		{name:'autosubmit',description:'Avançar na Submissão',type:'boolean','default':'false'},
		{name:'template',description:'Template',type:'boolean','default':'false', 'readonly':'true'}
	];

DragNSort.widgetNames.set('Form', 'Formulário');
DragNSort.widgetProperties.set('Form',DragNSort.FormProperties);

DragNSort.notifyTabPropertyChange = function notifyTabPropertyChange (ctx, name, val) {
	var tabTitleElem;
	ctx.property_values[name]=val;
	if(name=='title') {
		tabTitleElem = ctx.item.getElement('.tabTitle');
		if(tabTitleElem) tabTitleElem.set('text',val);
	}
};

DragNSort.createTabContext=function createTabContext(div,tabName,tabProperties) {
	var ctx ;
	
	ctx = {
		type:'Tab',
		id:div.id,
		item:div,
		notifyPropertyChange:DragNSort.notifyTabPropertyChange,
		property_values:{title:tabName}
	};
	if(tabProperties) {
		$each(tabProperties,function (txt,pname) {
			ctx.notifyPropertyChange.call(this,ctx,pname,txt);
		});
	}
	return ctx;
}

DragNSort.TabProperties =
	[
	 {name:'title',description:'Descrição',type:'string','default':''},
	 {name:'disabled',description:'Ocultar',type:'values','default':'false',
		 values:[
		         {description:'Não',value:'false'},
		         {description:'Sim',value:'true'},
		         {description:'Expressão',value:'expression',
		        	 extra:[{name:'disableExpr',description:'Condição',type:'string','default':''}]
		         }
		         ]
	 }
	];

DragNSort.widgetNames.set('Tab', 'Tab');
DragNSort.widgetProperties.set('Tab',DragNSort.TabProperties);
