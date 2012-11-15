// This is a UTF-8 File

// button container
new DefaultWidget({
	type:'w_bcontainer',
	desc:'Agrupamento de Botões',
	extend:{
		create:function create (ctx,id,data,childrenToAdopt){ // Bound to dnd
			var elem = new Element('div',{'id':id,'class':'widget'}).set('text','Button Container: ');
			elem.addClass(ctx.type);
			elem.adopt(new Element('span',{'class':'demo label'}));
	
			var buttonHolder = new Element('ul',{'class':'buttons'});
			buttonHolder.addEvent(DragNSort.ADOPT_EVENT, function(liElem) {
				var pctx;
				pctx = liElem.retrieve(DragNSort.CTX_PROP);
				pctx.inContainer=true;
				pctx.containerType=ctx.type;
				pctx.container=elem;
				FormEditor.debug("Adopted a button into this container: "+ctx.type);
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
			ctx.holder = buttonHolder;
			childrenToAdopt.push(elem);
			childrenToAdopt.push(new Element('div',{'class':'container'}).adopt(buttonHolder));
			return elem;
		},
		destroy:function destroy(ctx){
			// todo remover remover eventos associados a este "widget"
			ctx.holder.getChildren().each(function (liElem){
				var child = liElem.retrieve(DragNSort.CTX_PROP);
				if(child && child.destroy) child.destroy.call(this,child);
			},this);
		},

		getHolder: function getHolder() {
			return this.holder;
		},

		acceptItem:function acceptItem(ctx,item) {
			return item.hasClass('w_button');
		},
		createDrops:function createDrops(ctx,holders,item) {
			var ee = new Element('div', {'class':'dropPlace'});
			ee.set('text','Drop button here...');
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
	properties:[
		{name:'title',description:'Cabeçalho',type:'text','default':''}
	]
});

//Regular button
new DefaultWidget({type:'w_button',desc:'Botão', properties:[
	{name:'text',description:'Button Text',type:'string','default':'Button'},
	{name:'type',description:'Tipo',type:'values','default':'submit',
		values:[
		        {value:'submit',description:'Submeter'},
		        {value:'reset',description:'Limpar'},
		        {value:'print',description:'Imprimir'},
		        {value:'cancel',description:'Cancelar Processo'},
		        {value:'custom',description:'Definido por Utilizador'}
		        ]
	}
]});

