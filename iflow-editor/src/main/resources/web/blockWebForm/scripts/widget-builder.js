// This is a UTF-8 File

// Acrescenta os campos seguintes a classe DragNSort

var DefaultWidget = new Class({
	initialize: function initialize (widgetObj){
		var ext;
		if($type(widgetObj)=='string') widgetObj = JSON.decode(widgetObj);
		this.type=widgetObj.type;
		this.properties=widgetObj.properties;
		if(widgetObj.extend) {
			ext = widgetObj.extend;
			if($type(ext)=='string') {
				try {
					ext = eval('('+ext+')');
				} catch (err) {
					ext={};
				}
			}
			$extend(this, ext);
		}
		DragNSort.registerWidget(widgetObj.type, widgetObj.desc, this);
	},
	create:function create (ctx,id,data,childrenToAdopt) {
		var elem = $(ctx.type).clone();
		var props = this.getProperties(ctx);
		elem.id=id;
		elem.addClass(ctx.type);
		ctx.item=elem;
		$each(props, function(val,pos){  // FIXME este pedaco esta mal. Pode variar com propriedades dinamicas
			var name = val.name;
			if(data && data.values && data.values[name]) {
				ctx.notifyPropertyChange.call(this,ctx,name,data.values[name]);
			} else {
				ctx.notifyPropertyChange.call(this,ctx,name,ctx.property_values[name]);
			}
		},this);
		childrenToAdopt.push(elem);
		return elem;
	},
	notifyPropertyChange: function notifyPropertyChange(ctx,name,newVal) {
		var e,val,pname;
		ctx.property_values[name] = newVal;
		
		// todo make ajax request
		
		if(name=='text' || name=='title') {
			e = ctx.item.getElement('span.label');
			if(e) {
				if(newVal && newVal != '') {
					e.removeClass('titleError');
					e.set('text',newVal);
				} else {
					e.addClass('titleError');
					// procurar propname
					pname = '';
					var props = this.getProperties(ctx);
					$each(props, function(val,pos){
						FormEditor.debug('Tioma toma: '+val.name);
						if(val.name==name) {
							pname=val.description;
							return false;
						}
						return true;
					});
					e.set('text',"<Propriedade '"+pname+"' não está definida...>");
				}
			}
		}
	},
	getType:function getType(ctx){
		if(ctx)
			return ctx.type;
		return this.type;
	},
	getContextMenu:function getContextMenu() {
		var result = [];
		
		result.push({'text':'Reset Properties','event':function(e){alert('reset');}});
		result.push({'text':'Copy to Form','event':function(e){alert('copy');}});
		
		return result;
	},
	getWidgetName:function getWidgetName() {
		return 'Elemento';
	}
});

// helper para não ficar com um bocado de codigo gigantesco no 
var CatHelper = {
	// datasource txt
	dsTxt:[
			{name:'separator',description:'Separador',type:'string','default':';'},
			{name:'selValues',description:'Valores',type:'string','default':''},
			{name:'selDesc',description:'Descritivos',type:'string','default':''}
	       ], 
   	// datasource sn
   	dsSN:[
   			{name:'selYes',description:'Valor Sim',type:'string','default':'1'},
   			{name:'selNo',description:'Valor Não',type:'string','default':'0'},
   			{name:'selInvert',description:'Inverter Ordem',type:'boolean','default':'false'}
   	       ], 
   	// datasource catalog
   	dsCat:[
			{name:'selValuesVar',description:'Lista Valores',type:'string','default':''},
			{name:'selDescVar',description:'Lista de Descritivos',type:'string','default':''}
   	       ], 
	// datasource database
	dsBD:[
			{name:'selDSName',description:'DataSource',type:'string','default':''},
			{name:'selQuery',description:'Query SQL',type:'string','default':''}
	       ],
   	// datasource ldap
   	dsLDAP:[
   			{name:'selLDAP',description:'Query LDAP',type:'string','default':''}
   	       ]
};

var CatProperties = {
		'Document':
			[
			 	{name:'disabled',description:'Ocultar',type:'values','default':'false',
      			values:[
      					{description:'Não',value:'false'},
      					{description:'Sim',value:'true'},
      					{description:'Expressão',value:'expression',
      						extra:[{name:'disableExpr',description:'Condição',type:'string','default':''}]
      					}
      				   ]
			 	},
	      		{name:'text',description:'Descritivo',type:'string','default':''},
	      		{name:'input',description:'Tipo input',type:'values','default':'doc_link', values:[{description:'Link documento',value:'doc_link'}]}

			 ],
		'default':
			[
        		{name:'disabled',description:'Ocultar',type:'values','default':'false',
      			values:[
      					{description:'Não',value:'false'},
      					{description:'Sim',value:'true'},
      					{description:'Expressão',value:'expression',
      						extra:[{name:'disableExpr',description:'Condição',type:'string','default':''}]
      					}
      				   ]
      		},
      		{name:'readonly',description:'Só Leitura',type:'values','default':'false',
      			values:[
      					{description:'Não',value:'false'},
      					{description:'Sim',value:'true'},
      					{description:'Expressão',value:'expression',
      						extra:[{name:'readonlyExpr',description:'Condição',type:'string','default':''}]
      					}
      				   ]
      		},
      		{name:'input',description:'Tipo input',type:'values','default':'text',
      			values:[
      					{description:'Caixa de Texto',value:'text',
      						extra:[
      			  					{name:'width',description:'Tamanho',type:'string','default':'10'},
      								{name:'maxsize',description:'Tamanho Máximo',type:'string','default':'40'}
      							  ]},
      					{description:'Saída de Texto',value:'label'},
      					{description:'Caixa de Password',value:'password',
      		  				extra:[
      					 		{name:'width',description:'Tamanho',type:'string','default':'10'},
      				   			{name:'maxsize',description:'Tamanho Máximo',type:'string','default':'40'}
      					 		]},
      					{description:'Lista de Seleccção',value:'select',
      				 		extra:[
      				 			   {name:'source',description:'Origem de Dados',type:'values','default':'cat',
      				 				   values:[
      				 				           {value:'cat',description:'Variaveis de catalogo', extra:CatHelper.dsCat},
      				 				           {value:'dbq',description:'Query Base de Dados', extra:CatHelper.dsDB},
      				 				           {value:'ldap',description:'Query LDAP', extra:CatHelper.dsLDAP},
      				 				           {value:'sn',description:'Sim/Não', extra:CatHelper.dsSN},
      				 				           {value:'txt',description:'Valores separados por virgulas', extra:CatHelper.dsTxt}
      				 				           ]}
      				 			   ]},
      					{description:'Caixa de Data',value:'date'},
      					{description:'Area de Texto',value:'textarea',
      						 extra:[
      								{name:'rows',description:'Linhas',type:'string','default':'5'},
      								{name:'cols',description:'Colunas',type:'string','default':'40'},
      								{name:'maxsize',description:'Tamanho Máximo',type:'string','default':''}
      								]},
      					{description:'Checkbox',value:'checkbox'}
      					]
      		},
      		{name:'override',description:'Sobrepor Catálogo',type:'values','default':'false',
      			values:[
      					{description:'Não',value:'false'},
      					{description:'Sim',value:'true', 
      						extra: [{name:'text',description:'Descritivo',type:'string','default':''},
      						        {name:'format',description:'Formatação',type:'string','default':''}]
      					}
      				   ]
      		}
      	]

};

//special case: process data
DragNSort.registerWidget('process_data', 'Dados de Processo', {
	loadData:function loadProcData(gen,data) {
		if(data && data.values) {
			$each(data.values, function(value,name){
				gen.notifyPropertyChange.call(this,gen,name,value);
			},this);
		}
	},
	notifyPropertyChange: function notifyProcDataChange (gen,name,newVal) {
		gen.property_values[name] = newVal;
		// TODO implement (type, labels, etc)
	},
	skipWidgetCreation:true,
	process_data:true,
	properties:CatProperties
});

var CatalogWidget = new Class({
	initialize: function(widgetObj){
		if($type(widgetObj)=='string') widgetObj = JSON.decode(widgetObj);
		DragNSort.registerCatalogVar(widgetObj.name, widgetObj.desc, widgetObj.type, widgetObj.list, widgetObj.single);
	}
});

