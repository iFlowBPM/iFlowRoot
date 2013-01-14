// This is a UTF-8 File

var TableWidgetInjectableProperties=[
	{name:'_ignoreme2_',description:'Configurar\u00A0Tabela',type:'header','default':''},
	{name:'tblalign',description:'Alinhamento',type:'values','default':'left',
		values:[{value:'left',description:'Esquerda'},{value:'center',description:'Centro'},{value:'right',description:'Direita'}]
	}
];

// table
new DefaultWidget({
	type:'w_table',
	desc:'Tabela',
	extend:{
		create:function create (ctx,id,data,childrenToAdopt){ // Bound to dnd
			var elem = new Element('div',{'id':id,'class':'widget'}).set('text','Table: ');
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
				pctx.containerProperties=TableWidgetInjectableProperties;
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
			catElem = DragNSort.getCatalogVar(item.get('id'));
			// access catalog to check if variable is a list
			if(catElem && catElem.isList) return true;
			
			return false;
		},
		createDrops:function createDrops (ctx,holders,item) {
			var ee = new Element('div', {'class':'dropPlace'});
			ee.set('text','Drop list here...');
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
	    {name:'title',description:'Table title',type:'text','default':'Table title'},
		{name:'max_row',description:'Max row count',type:'int','default':'-1'}
	]
});
