drop table if exists cma_cliente_municipal;
drop table if exists cma_tipo_obra;
drop table if exists cma_qualidade_requerente;
drop table if exists cma_tipo_obra_edificacao;
drop table if exists cma_opcoes_predio_abrangido;
drop table if exists cma_cp_documentos;
drop table if exists cma_audit;
drop table if exists cma_cliente_municipal;

create table cma_cliente_municipal (
	num_cliente mediumint not null auto_increment primary key,
	idcivil varchar(8),
	bi varchar(8),	
	nif varchar(9),
	nome varchar(200),
	data_nascimento varchar(10),
	bi_emitido varchar(10),
	bi_arquivo varchar(50),
	estado_civil varchar(50),
	profissao varchar(100),
	morada varchar(200),
	localidade varchar(100),
	freguesia varchar(100),
	concelho varchar(100),
	cp varchar(50),
	telefone varchar(15),
	telemovel varchar(15),
	email varchar(150)
);

create table cma_tipo_obra (
	codigo int primary key,
	name varchar(50),
	opcoes_predio_abrangido varchar(50)
);

create table cma_qualidade_requerente (
	codigo int primary key,
	name varchar(50)
);

create table cma_tipo_obra_edificacao (
	codigo int primary key,
	name varchar(50)
);

create table cma_opcoes_predio_abrangido (
	codigo int primary key,
	name varchar(100)
);

create table cma_cp_documentos (
	codigo int primary key,
	name varchar(767),
	tipos_obra varchar(20),
	obrigatorio varchar(20),
	constraint un_cp_docs_name unique (name)
);

create table cma_audit (
	id mediumint not null auto_increment primary key,
	num_cliente varchar(100),
	nome_cliente varchar(100),
	numproc varchar(128),
	estado varchar(128),
	agendado_em varchar(128),
	flowid int,
	pid int,
	fechado int(1) default 0
);


insert into cma_tipo_obra values (1,'Obras de Edificação','1,2,3,4');
insert into cma_tipo_obra values (2,'Obras de Urbanização','1,2,3');
insert into cma_tipo_obra values (3,'Operação de Loteamento','1,2');


insert into cma_qualidade_requerente values (1,'Proprietário');
insert into cma_qualidade_requerente values (2,'Locatário');
insert into cma_qualidade_requerente values (99,'Outra');

insert into cma_tipo_obra_edificacao values (1,'Construção');
insert into cma_tipo_obra_edificacao values (2,'Recostrução');
insert into cma_tipo_obra_edificacao values (3,'Ampliação');
insert into cma_tipo_obra_edificacao values (4,'Alteração');

insert into cma_opcoes_predio_abrangido values (1,'Plano Director Municipal de Abrantes');
insert into cma_opcoes_predio_abrangido values (2,'Plano de Pormenor/Urbanização');
insert into cma_opcoes_predio_abrangido values (3,'Alvará de Loteamento');
insert into cma_opcoes_predio_abrangido values (4,'Propriedade Horizontal');

-- validar obrigatorios !!!!!
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (1, '1,2,3','1,2,3','Documentos comprovativos da qualidade de titular de qualquer direito que confira a faculdade de realização da operação');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (2, '1,2,3','1,2,3','Certidão da descrição e de todas as inscrições em vigor emitida pela conservatória do registo predial referente ao prédio ou prédios abrangidos');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (3, '1','1','Extractos das plantas de ordenamento, zonamento e de implantação dos planos municipais de ordenamento do território vigentes e das respectivas plantas de condicionantes, da planta síntese do loteamento, se existir, e planta à escala de 1:2500 ou superior, com a indicação precisa do local onde se pretende executar a obra');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (4, '1,3','','Extractos das plantas do plano especial de ordenamento do território vigente');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (5, '1','','Projecto de arquitectura');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (6, '1,3','','Memória descritiva e justificativa');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (7, '1','','Estimativa do custo total da obra');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (8, '1','','Calendarização da execução da obra');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (9, '1,2,3','1,2,3','Especificações a que se refere o art.º77 do decreto-Lei n.º555/99 de 16/12 na redacção da Lei 60/2007 de 4/09');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (10,'1,2,3','','Cópia da notificação da câmara municipal a comunicar a aprovação de um pedido de informação prévia, quando esta existir e estiver em vigor');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (11,'1,2,3','1,2,3','Termos de responsabilidade subscritos pelos autores dos projectos e coordenador de projecto quanto ao cumprimento das normas legais e regulamentares aplicáveis, nos termos do anexo I e II da Portaria 232/2008, de 11 de Março');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (12,'1,2','','Acessibilidades - desde que inclua tipologias do artigo 2.º do Decreto -Lei n.º 163/2006');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (13,'1','','Projecto de estabilidade que inclua o projecto de escavação e contenção periférica');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (14,'1','','Projecto de alimentação e distribuição de energia eléctrica e projecto de instalação de gás, quando exigível, nos termos da lei');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (15,'1','','Projecto de redes prediais de água e esgotos');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (16,'1','','Projecto de águas pluviais');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (17,'1','','Projecto de arranjos exteriores');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (18,'1','','Projecto de instalações telefónicas e de telecomunicações');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (19,'1','','Estudo de comportamento térmico');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (20,'1','','Projecto de instalações electromecânicas, incluindo as de transporte de pessoas e ou mercadorias');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (21,'1','','Projecto de segurança contra incêndios em edifícios');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (22,'1','','Projecto acústico');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (23,'1','','Declaração de conformidade regulamentar no âmbito do Regulamento das Características do Comportamento Térmico dos Edifícios e do Regulamento dos Sistemas Energéticos e de Climatização dos Edifícios');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (24,'1','','Apólice de seguro de construção, quando for legalmente exigível');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (25,'1,2','','Apólice de seguro que cubra a responsabilidade pela reparação dos danos emergentes de acidentes de trabalho, nos termos previstos na Lei n.º 100/97, de 13 de Setembro');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (26,'1','','Termo de responsabilidade assinado pelo director técnico da obra nos termos da Portaria 216-E/2008, de 3 de Março');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (27,'1,2','','Declaração de titularidade de alvará emitido pelo Instituto da Construção e do Imobiliário (InCI, I. P.), com habilitações adequadas à natureza e valor, da obra, ou título de registo emitido por aquela entidade, com subcategorias adequadas aos trabalhos a executar, a verificar através da consulta do portal do InCI, I. P pela entidade licenciadora, no prazo previsto, para a rejeição da comunicação prévia');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (28,'1,2','','Livro de obra, com menção do termo de abertura');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (29,'1,2','','Plano de segurança e saúde');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (30,'2','','Projectos da engenharia das especialidade que integram a obra, designadamente das infra -estruturas viárias, redes de abastecimento de águas, esgotos e drenagem, de gás, de electricidade, de telecomunicações, arranjos exteriores, devendo cada projecto conter memória descritiva e justificativa, bem como os cálculos, se for caso disso, e as peças desenhadas, em escala tecnicamente adequada, com os respectivos termos de responsabilidade dos técnicos autores dos projectos');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (31,'2','','Orçamento da obra, por especialidades e global, baseado em quantidades e qualidades dos trabalhos necessários à sua execução, devendo neles ser adoptadas as normas europeias e as portuguesas em vigor ou as especificações do Laboratório Nacional de Engenharia Civil');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (32,'2','','Condições técnicas gerais e especiais do caderno de encargos, incluindo prazos para o início e para o termo da execução dos trabalhos');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (33,'2','','Contrato de urbanização, caso o requerente entenda proceder, desde logo, à sua apresentação');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (34,'2,3','','Estudo que demonstre a conformidade com o Regulamento Geral do Ruído, contendo informação acústica adequada relativa à situação actual e à decorrente da execução da operação de loteamento');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (35,'2','','Cópia da notificação do deferimento do pedido de licenciamento da operação de loteamento');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (36,'2','','Documento comprovativo da prestação de caução');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (38,'2','','Termo de responsabilidade assinado pelo director de fiscalização de obra, nos termos do anexo III da Portaria 238/2008, de 11 de Março');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (39,'2','','Minuta do contrato de urbanização aprovada, quando exista');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (40,'3','','Planta da situação existente, à escala de 1:1000 ou superior, correspondente ao estado e uso actual do terreno e de uma faixa envolvente com dimensão adequada à avaliação da integração da operação na área em que se insere, com indicação dos elementos ou valores naturais e construídos, de servidões administrativas e restrições de utilidade pública, incluindo os solos abrangidos pelos regimes da Reserva Agrícola Nacional e da Reserva Ecológica Nacional e ainda as infra -estruturas existentes');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (41,'3','','Planta de síntese, à escala de 1:1000 ou superior, indicando, nomeadamente, a modelação proposta para o terreno, a estrutura viária, as redes de abastecimento de água e de saneamento, de energia eléctrica, de gás e de condutas destinadas à instalação de infra -estruturas de telecomunicações, a divisão em lotes e sua numeração, finalidade, áreas de implantação e de construção, número de pisos acima e abaixo da cota de soleira e número de fogos, com especificação dos destinados a habitações a custos controlados, quando previstos');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (42,'3','','Planta com áreas de cedência para o domínio municipal');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (43,'3','','Ficha com os elementos estatísticos devidamente preenchida com os dados referentes à operação urbanística a realizar');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (44,'3','','Planta com identificação dos percursos acessíveis, detalhes métricos, técnicos e construtivos e uma peça escrita descrevendo e justificando as soluções adoptadas');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (45,'3','','Plano de acessibilidades que apresente a rede de espaços e equipamentos acessíveis bem como soluções de detalhe métrico, técnico e construtivo, esclarecendo as soluções adoptadas em matéria de acessibilidade a pessoas com deficiência e mobilidade condicionada, nos termos do artigo 3.º do Decreto -Lei n.º 163/2006, de 8 de Agosto');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (46,'3','','Planta de síntese da operação de loteamento em base transparente e, quando exista, em base digital; Descrição pormenorizada dos lotes com indicação dos artigos matriciais de proveniência');
insert into cma_cp_documentos (codigo,tipos_obra,obrigatorio,name) values (47,'3','','Actualização da certidão da conservatória do registo predial anteriormente entregue');
