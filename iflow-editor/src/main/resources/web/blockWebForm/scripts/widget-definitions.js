// type must match items in enum pt.iflow.blocks.webform.WebWidgetEnum

new DefaultWidget({type:'w_header',desc:'Cabeçalho', properties:[
	{name:'text',description:'Texto do Cabeçalho',type:'string','default':'Cabeçalho'}
]});
new DefaultWidget({type:'w_subheader',desc:'Sub Cabeçalho', properties:[
	{name:'text',description:'Texto do Sub Cabeçalho',type:'string','default':'Sub Cabeçalho'}
]});
new DefaultWidget({type:'w_message',desc:'Mensagem Texto', properties:[
	{name:'text',description:'Texto da mensagem',type:'string','default':'Mensagem Texto'},
	{name:'align',description:'alinhamento',type:'values','default':'left',
		values:[{value:'left',description:'Esquerda'},{value:'center',description:'Centro'},{value:'right',description:'Direita'}]
	}
]});
new DefaultWidget({type:'w_spacer',desc:'Espaçador', properties:[
	{name:'size',description:'Tamanho',type:'values','default':'medium',
		values:[{value:'small',description:'Pequeno'},{value:'medium',description:'Médio'},{value:'large',description:'Grande'}]
	}
]});
new DefaultWidget({type:'w_separator',desc:'Separador', properties:[
	{name:'size',description:'Tamanho',type:'values','default':'medium',
		values:[{value:'small',description:'Pequeno'},{value:'medium',description:'Médio'},{value:'large',description:'Grande'}]
	}
]});
new DefaultWidget({type:'w_link',desc:'Link', properties:[
	{name:'text',description:'Texto do link',type:'string','default':''},
	{name:'url',description:'URL',type:'string','default':''},
	{name:'variable',description:'Variavel Catalogo',type:'string','default':''},
	{name:'popup',description:'Abrir nova Janela',type:'boolean','default':'false'}
]});
new DefaultWidget({type:'w_image',desc:'Imagem', properties:[
	{name:'src',description:'URL',type:'string','default':''},
	{name:'title',description:'Descrição',type:'string','default':'title...'},
	{name:'onclick',description:'Acção Click',type:'values','default':'ignore',
		values:[
		    {value:'ignore',description:'Ignorar'},
		    {value:'submit',description:'Submeter formulário'},
		    {value:'link',description:'Abrir Link'}
		]
	}
]});
new DefaultWidget({type:'w_chart',desc:'Gráfico', properties:[
	{name:'type',description:'Tipo Gráfico',type:'values','default':'bar',
		values:[{value:'bar',description:'Barras'},{value:'pie',description:'Pie'}]
	},
	{name:'title',description:'Descrição',type:'string','default':'title...'},
	{name:'xx',description:'Valores no eixo X', type:'string','default':'varX'},
	{name:'title_x',description:'Descrição eixo X',type:'string','default':'title...'},
	{name:'yy',description:'Valores no eixo Y', type:'string','default':'varY'},
	{name:'title_y',description:'Descrição eixo Y',type:'string','default':'title...'}
]});


new DefaultWidget({type:'w_template',desc:'Template', 
	notifyPropertyChange: function notifyPropertyChange(ctx,name,newVal) {
		var e,val,pname;
		ctx.property_values[name] = newVal;

		if(name=='template') {
			e = ctx.item.getElement('span.label');
			if(e) e.set('text','Template: '+(newVal=='-1'?'':newVal));
		}
	},
	properties:[
	{name:'template',description:'Template',type:'json', url:'scripts/list_templates.json',
		'default':'-1',	values:[{value:'-1',description:'Escolha'}]
	},
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
	}
]});


// load other widgets
new Asset.javascript('scripts/document_container.js'+location.search, {id: 'w_document_script'});
new Asset.javascript('scripts/table_container.js'+location.search, {id: 'w_table_script'});
new Asset.javascript('scripts/button_container.js'+location.search, {id: 'w_button_script'});

/** tipos de campo existentes:

-- Widgets

Cabeçalho                                                            OK
Sub Cabeçalho                                                        OK
Mensagem de Texto                                                    OK
Link                                                                 OK
Espaço (espaçador, espaço em branco?)                                OK
Separador (horizontal rule)                                          OK
Imagem                                                               OK
Gráfico                                                              OK
Tabela                                                               OK
Agrupamento de botões                                                OK
Botão                                                                OK

-- Widgets removidos/Não implementados

Tabela de Tamanho Fixo (esta não vai ser implementada no iFlow 4)    XXX
Tabela de Processo (Caso especial, não implementado no iFlow 4)      XXX
Inicio de novo bloco                                                 XXX
Inicio de nova coluna (mesmo bloco)                                  XXX
Inicio Tab Container (desaparece. o formulário é um tab container,   XXX
    apenas se definem tabs para o formulário todo)                   XXX
Fim Tab Container                                                    XXX
Inicio de nova Tab                                                   XXX
Fim Tab                                                              XXX


-- Data input

Caixa de Texto                                                       OK
Caixa de Password                                                    OK
Saída de Texto                                                       (AUTO)
Área de Texto                                                        OK
Caixa de Data                                                        OK
Lista de Selecção                                                    
Lista de Selecção SQL                                                
Lista de Selecção Conector Externo                                   
Ficheiro                                                             


**** TODO Agrupar os widgets e verificar quais os que passam a ter funcionamento específico ou não

*/
