// This is a UTF-8 File

// Inject some extra properties available only in Document Widget control
var DocumentWidgetInjectableProperties=
	[
	    {name:'title',description:'Title',type:'text','default':'Title'},
	    {name:'message',description:'Text Message',type:'text','default':'message'},
		{name:'_docignoreme_lnk_',description:'Link Download',type:'header','default':''},
		{name:'lnk_disable',description:'Ocultar',type:'values','default':'false',
			values:[
					{description:'Não',value:'false'},
					{description:'Sim',value:'true'},
					{description:'Expressão',value:'expression',
						extra:[{name:'readonlyExpr',description:'Condição',type:'string','default':''}]
					}
				   ]
		},
		{name:'lnk_label',description:'Etiqueta',type:'string','default':''},
		{name:'lnk_text',description:'Descritivo',type:'string','default':''},
		
		{name:'_docignoreme_mod_',description:'Modificar',type:'header','default':''},
		{name:'mod_disable',description:'Ocultar',type:'values','default':'false',
			values:[
					{description:'Não',value:'false'},
					{description:'Sim',value:'true'},
					{description:'Expressão',value:'expression',
						extra:[{name:'readonlyExpr',description:'Condição',type:'string','default':''}]
					}
				   ]
		},
		{name:'mod_label',description:'Etiqueta',type:'string','default':''},
		
		{name:'_docignoreme_del_',description:'Remover',type:'header','default':''},
		{name:'del_disable',description:'Ocultar',type:'values','default':'false',
			values:[
					{description:'Não',value:'false'},
					{description:'Sim',value:'true'},
					{description:'Expressão',value:'expression',
						extra:[{name:'readonlyExpr',description:'Condição',type:'string','default':''}]
					}
				   ]
		},
		{name:'del_label',description:'Etiqueta',type:'string','default':''},
		
		{name:'_docignoreme_upl_',description:'Upload',type:'header','default':''},
		{name:'upl_disable',description:'Ocultar',type:'values','default':'false',
			values:[
					{description:'Não',value:'false'},
					{description:'Sim',value:'true'},
					{description:'Expressão',value:'expression',
						extra:[{name:'readonlyExpr',description:'Condição',type:'string','default':''}]
					}
				   ]
		},
		{name:'upl_label',description:'Etiqueta',type:'string','default':''},
		{name:'upl_limit',description:'Limite',type:'string','default':'5'},
		{name:'disable_scanner',description:'Ocultar Scanner',type:'values','default':'true',
			values:[
					{description:'Não',value:'false'},
					{description:'Sim',value:'true'},
					{description:'Expressão',value:'expression',
						extra:[{name:'readonlyExpr',description:'Condição',type:'string','default':''}]
					}
				   ]
		}
];


// document holder
new DefaultWidget({
	type:'w_document_holder',
	desc:'Documento',
	extend:{
		create:function create (ctx,id,data,childrenToAdopt){ // Bound to dnd
			var elem = new Element('div',{'id':id,'class':'widget'}).set('text','Documento: ');
			elem.addClass(ctx.type);
			elem.adopt(new Element('span',{'class':'demo label'}));
			
			var tableHolder = new Element('ul',{'class':'columns'});
			tableHolder.addEvent(DragNSort.ADOPT_EVENT, function(liElem) {
				var pctx;
				pctx = liElem.retrieve(DragNSort.CTX_PROP);
				pctx.inContainer=true;
				pctx.containerType=ctx.type;
				pctx.container=elem;
				
				// injecta propriedades extra
				pctx.containerProperties=DocumentWidgetInjectableProperties;
			}.bind(this));
			
			ctx.item=elem;
			var props = this.getProperties(ctx);
			$each(props, function(val,pos){
				ctx.notifyPropertyChange.call(this,ctx,name,ctx.property_values[name]);
			},this);
			if(data && data.values) {
				$each(data.values, function(value,name){
					ctx.notifyPropertyChange.call(this,ctx,name,value);
				},this);
			}
			ctx.holder = tableHolder;
			childrenToAdopt.push(elem);
			childrenToAdopt.push(new Element('div',{'class':'container'}).adopt(tableHolder));
			return elem;
		},
		destroy:function destroy (ctx){
			// todo remover remover eventos associados a este "widget"
			ctx.holder.getChildren().each(function (liElem){
				var child = liElem.retrieve(DragNSort.CTX_PROP);
				if(child && child.destroy) child.destroy.call(this,child);
			},this);
		},
		getHolder: function getHolder() {
			return this.holder;
		},
		acceptItem:function acceptItem (ctx,item) {
			var catElem;
			if(ctx.holder.getChildren().length > 0) return false;
			
			catElem = DragNSort.getCatalogVar(item.get('id'));
			// access catalog to check if variable is a list
			if(catElem && catElem.type==='Document') return true;
			
			return false;
		},
		createDrops:function createDrops (ctx,holders,item) {
			var ee = new Element('div', {'class':'dropPlace'});
			ee.set('text','Drop Document Variable here...');
			ee = new Element('li',{'class':'sortables '+DragNSort.DROP_GEN_CLASS}).adopt(ee);
			ctx.holder.adopt(ee);
			ctx.holder.getChildren().each(function (elem) {
				holders.push(elem);
			},this);
		},
		notifyPropertyChange: function notifyPropertyChange(ctx,name,newVal) {
			var e;
			ctx.property_values[name] = newVal;
			if(name=='text' || name=='title') {
				e = ctx.item.getElement('span.label');
				if(e) e.set('text',newVal);
			}
		}
	},

	// configurable properties for this element
	properties:[]
});
