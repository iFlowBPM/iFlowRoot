drop table if exists cma_lkp_opcoes_pedidos;
drop table if exists cma_lkp_estados_civis;
drop table if exists cma_lkp_papeis_cliente;
drop table if exists cma_accoes;
drop table if exists cma_perfis_aprovacao;
drop table if exists cma_tipo_processo;
drop table if exists cma_tipo_documento;
drop table if exists cma_requerimento;
drop table if exists cma_tarefa_colaborativa;
drop table if exists cma_processo;
drop table if exists cma_assunto_expediente;

create table cma_assunto_expediente (
	id varchar(50) primary key,
	descricao varchar(200)
)

insert into cma_assunto_expediente values
('OBRAS', 'Obras'),
('PUB', 'Publicidade'),
('CEM', 'Cemitérios'),
('TRANS', 'Transportes'),
('AMB', 'Ambiente'),
('COM', 'Comércio'),
('ZZZZZZ', 'Outro');

create table cma_tarefa_colaborativa (
	id integer auto_increment primary key,
	id_tarefa varchar(50),
	id_requerimento varchar(50),
  id_fluxo integer,
  assunto varchar(200),
  comentario varchar(2000)
);

create table cma_processo (
	id integer primary key,
	pasta_processo varchar(200)
);

create table cma_requerimento (
	id integer auto_increment primary key,
	id_requerimento varchar(50),
	id_processo varchar(50),
  id_fluxo integer,
  pid integer,
  assunto varchar(200),
  comentario varchar(2000),
	nome_cliente varchar(200),
	nome_representante varchar(200),
	data_requerimento date
);

-- para requerimentos
create table cma_tipo_processo (
  id_tipo_processo varchar(100),
  id_tipo_processo_num int,
  grupo varchar(60),
  pasta_processo_base varchar(200),
  tipo_processo_macro char(1),
  perfil_responsavel varchar(100),
  descricao varchar(200)
);

create table cma_tipo_documento (
	id_tipo_documento integer auto_increment primary key,
	id_tipo_processo varchar(100),
	nome varchar(200),
	descricao varchar(2000),
	tipo integer
);

create table cma_lkp_opcoes_pedidos (
  id integer primary key,
  nome varchar(120)
);

create table cma_lkp_estados_civis (
  id integer primary key,
  nome varchar(120)
);

create table cma_lkp_papeis_cliente (
  id integer primary key,
  nome varchar(120)
);

create table cma_accoes (
  id integer auto_increment primary key,
	tipo varchar(20),
  descricao_terp varchar(120),
  descricao_primp varchar(120),
  resultado varchar(120),
  autoriza integer(1),
  rejeita integer(1)
);

create table cma_perfis_aprovacao (
	perfil varchar(120),
	nivel_tecnico integer(2),
	nivel_operacional integer(2),
	nivel_tecnico_superior integer(2)
);

create or replace view cma_v_accoes_por_perfil as
select
    id,
	perfil,
	descricao,
	resultado,
	encaminhar,
	aprovar,
	devolver
from
	cma_accoes a,
	cma_perfis_aprovacao p
where
	(permissao_tecnico > 0 and permissao_tecnico <= nivel_tecnico) or
	(permissao_operacional > 0 and permissao_operacional <= nivel_operacional) or
	(permissao_tecnico_superior > 0 and permissao_tecnico_superior <= nivel_tecnico_superior)
;

insert into cma_perfis_aprovacao values
('Aprov/AssistenteTecnico', 1, 0, 0),
('Aprov/CoordenadorTecnico', 2, 0, 0),
('Aprov/AssistenteOperacional', 0, 1, 0),
('Aprov/EncarregadoOperacional', 0, 2, 0),
('Aprov/EncarregadoGeralOperacional', 0, 2, 0),
('Aprov/TecnicoSuperior', 0, 0, 1),
('Aprov/ChefeDivisao', 3, 3, 2),
('Aprov/Vereador', 4, 4, 3),
('Aprov/Presidente', 5, 5, 4);

-- accoes para listar
insert into cma_accoes (tipo, descricao_terp, descricao_primp, resultado, autoriza, rejeita) values
('devolver', 'Devolver', 'Devolvo', 'Devolvido', 0, 0),
('encaminhar', 'Encaminhar', 'Encaminho', 'Encaminhado', 0, 0),
('informar', 'Informar', 'Informo', 'Informado', 0, 0),
('pre_sugestao', 'Emitir Sugestão de Decisão', 'Emitir Sugestão de Decisão', 'Emitir Sugestão de Decisão', 0, 0),
('pre_parecer', 'Concordar/Não Concordar', 'Concordo/Não Concordo', 'Informado', 0, 0),
('pre_decisao', 'Decisão', 'Decido', 'Decidido', 0, 0),
('pre_reuniao', 'Para Reunião de Executivo', 'Reunião de Executivo', 'Reunião de Executivo', 0, 0),
('parecer', 'Concordar', 'Concordo', 'Concordado', 0, 0),
('parecer', 'Não Concordar', 'Não Concordo', 'Não Concordado', 0, 0),
('decisao', 'Aprovar', 'Aprovo', 'Aprovado', 1, 0),
('decisao', 'Não Aprovar', 'Não Aprovo', 'Não Aprovado', 0, 1),
('decisao', 'Autorizar', 'Autorizo', 'Autorizado', 1, 0),
('decisao', 'Não Autorizar', 'Não Autorizo', 'Não Autorizado', 0, 1),
('decisao', 'Deferir', 'Defiro', 'Deferido', 1, 0),
('decisao', 'Indeferir', 'Indefiro', 'Indeferido', 0, 1),
('decisao', 'Homologar', 'Homologo', 'Homologado', 1, 0),
('decisao', 'Não Homologar', 'Não Homologo', 'Não Homologado', 0, 1),
('decisao', 'À reunião para aprovação', 'Para Aprovação', 'À reunião para aprovação', 1, 0),
('decisao', 'À reunião para conhecimento', 'Para Conhecimento', 'À reunião para conhecimento', 1, 0);

insert into cma_lkp_opcoes_pedidos values
(1,'Informação Complementar ao Munícipe'),
(2,'Parecer Externo'),
(3,'Informação Técnica'),
(4,'Informação'),
(5,'Notificação'),
(6,'Ofício'),
(7,'Comunicar Decisão'),
(8,'Despacho');

insert into cma_lkp_estados_civis values
(1,'Solteiro/a'),
(2,'Casado/a'),
(3,'Viúvo/a'),
(4,'Divorciado/a');

insert into cma_lkp_papeis_cliente values
(1,'Proprietário'),
(2,'Arrendatário'),
(3,'Advogado'),
(4,'Solicitador'),
(5,'Notário'),
(6,'Mandatário'),
(7,'Interessado'),
(8,'Cabeça de casal'),
(9,'Herdeiro'),
(10,'Usufrutuário'),
(11,'Procurador'),
(12,'Administrador'),
(13,'Explorador'),
(14,'Outro');

insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_CEM_PEDRA_SEPULTURA','58','CEMITÉRIOS','Sites/Arquivo/CMA/Serviços urbanos/Cemitérios/','E','Atendimento/DSU',' Colocação de pedra em sepultura');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_CEM_SEG_VIA_ALVARA','59','CEMITÉRIOS','Sites/Arquivo/CMA/Serviços urbanos/Cemitérios/','E','Atendimento/DSU',' 2ª via do alvará de sepultura');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_CEM_AVERBAMENTO','60','CEMITÉRIOS','Sites/Arquivo/CMA/Serviços urbanos/Cemitérios/','E','Atendimento/DSU',' Averbamento de alvará');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_CEM_COVAL','61','CEMITÉRIOS','Sites/Arquivo/CMA/Serviços urbanos/Cemitérios/','E','Atendimento/DSU',' Concessão de coval (Edital nº 12/06)');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_CEM_CREMACAO','62','CEMITÉRIOS','Sites/Arquivo/CMA/Serviços urbanos/Cemitérios/Inumações ou cremações/','E','Atendimento/DSU',' Inumação ou cremação');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_CEM_TRANSLADACAO','63','CEMITÉRIOS','Sites/Arquivo/CMA/Serviços urbanos/Cemitérios/Trasladações de cadáveres ou ossadas/','E','Atendimento/DSU',' Trasladação de cadáveres ou ossadas');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_TRANS_ART_50','64','TRANSPORTES E TRANSITO','Sites/Arquivo/CMA/Serviços urbanos/Viação e trânsito/','E','Atendimento/DSU',' Estacionamento proibido - Art. 50 Código de Estrada');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_TRANS_AUTOCARRO','65','TRANSPORTES E TRANSITO','Sites/Arquivo/CMA/Apoio e manutenção/Transportes/Candidaturas à utilização de autocarro municipal/','E','Atendimento/DMT','Candidatura à utilização de autocarro municipal');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_TRANS_TAXIS','66','TRANSPORTES E TRANSITO','Sites/Arquivo/CMA/Serviços urbanos/Taxis/','E','Atendimento/DSU','Táxis');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_TRANS_MATRICULA_CICLOMOTORES','67','TRANSPORTES E TRANSITO','Sites/Arquivo/CMA/Taxas e licenças/Ciclomotores/','E','Atendimento/DSU','Certificado de matrícula - Ciclomotores');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_AMB_RUIDO','68','AMBIENTE E ESPAÇOS VERDES','Sites/Arquivo/CMA/Assuntos ambientais/Licenças de ruído/','E','Atendimento/DOGU',' Licença de ruído ');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_AMB_DESTRUICAO_VEGETAL','69','AMBIENTE E ESPAÇOS VERDES','Sites/Arquivo/CMA/Assuntos ambientais/','E','Atendimento/DOGU','Destruição do revestimento vegetal para fins não agricolas');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_DESP_PROVAS','70','DESPORTO E RECREIO','Sites/Arquivo/CMA/Cultura desporto e recreio/Desporto e recreio/','E','Atendimento/DSU/DDJ',' Provas desportivas');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_DESP_ARRAIAIS','71','DESPORTO E RECREIO','Sites/Arquivo/CMA/Cultura desporto e recreio/Desporto e recreio/','E','Atendimento ',' Arraiais, romarias, bailes e outros divertimentos públicos');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_DESP_MAQ_DIVERSAO','72','DESPORTO E RECREIO','Sites/Arquivo/CMA/Cultura desporto e recreio/Desporto e recreio/','E','Atendimento ',' Licença de utilização de máquinas de diversão');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_DESP_MAQ_MOD_423','73','DESPORTO E RECREIO','Sites/Arquivo/CMA/Cultura desporto e recreio/Desporto e recreio/','E','Atendimento ',' Máquinas de diversão - Mod. 423');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_DESP_RECINTOS','74','DESPORTO E RECREIO','Sites/Arquivo/CMA/Cultura desporto e recreio/Desporto e recreio/','E','Atendimento ',' Lic. de utiliz. de recintos de espect. de natur. não artística');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_DESP_RECINTO_ITENERANTE','75','DESPORTO E RECREIO','Sites/Arquivo/CMA/Cultura desporto e recreio/Desporto e recreio/','E','Atendimento ',' Lic. Instal. de recinto itenerante/improv/diversão provisória');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_DESP_CARTA_CACADOR','76','DESPORTO E RECREIO','Sites/Arquivo/CMA/Taxas e licenças/Carta de caçador/','E','Atendimento ',' Carta de caçador');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_MERC_MERCADO_DIARIO','77','MERCADOS E FEIRAS','Sites/Arquivo/CMA/Serviços urbanos/Mercados e feiras/','E','Atendimento/DSU',' Mercado diário municipal');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_MERC_FEIRA_SEMANAL','78','MERCADOS E FEIRAS','Sites/Arquivo/CMA/Serviços urbanos/Mercados e feiras/','E','Atendimento/DSU',' Feira semanal de Abrantes');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_MERC_MERCADO_SEMANAL_GROSSO','79','MERCADOS E FEIRAS','Sites/Arquivo/CMA/Serviços urbanos/Mercados e feiras/','E','Atendimento/DSU',' Mercado semanal de Abrantes comércio por grosso');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_MERC_S_MATIAS','80','MERCADOS E FEIRAS','Sites/Arquivo/CMA/Serviços urbanos/Mercados e feiras/','E','Atendimento/DSU',' Feira de S. Matias');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_EST_HORARIO','81','ESTABELECIMENTOS','Sites/Arquivo/CMA/Actividades económicas/','E','Atendimento ','Horário de funcionamento de estabelecimento ');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_EST_DECL_INSTAL_COMERCIAIS','82','ESTABELECIMENTOS','Sites/Arquivo/CMA/Actividades económicas/','E','Atendimento','Decl. de instal., modif. e de encerr. estab. Comerc. e serviços');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_EST_DECL_INSTAL_RESTAURACAO','83','ESTABELECIMENTOS','Sites/Arquivo/CMA/Actividades económicas/','E','Atendimento ','Declaração de inst., mod e enc. Estab. de restaur. ou bebidas');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_VEND_UNIDADE_MOVEL','84','VENDA AMBULANTE','Sites/Arquivo/CMA/Actividades económicas/Comércio e serviços/Vendedores ambulantes e feirantes/','E','Atendimento/DSU','Unidade móvel para venda de produtos alimentares');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_VEND_VENDEDOR','85','VENDA AMBULANTE','Sites/Arquivo/CMA/Actividades económicas/Comércio e serviços/Vendedores ambulantes e feirantes/','E','Atendimento ','Vendedor ambulante');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_CONTROLO_METROLOGIA','86','CONTROLO DE METROLOGIA','Sites/Arquivo/CMA/Actividades económicas/Comércio/Aferições/','E','Atendimento/DSU','Controlo de metrologia');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_PUB_LICENCAS','87','PUBLICIDADE','Sites/Arquivo/CMA/Actividades económicas/Industria/Publicidade/','E','Atendimento ','Licenças de publicidade');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_PUB_SINAIS_INDICATIVOS','88','PUBLICIDADE','Sites/Arquivo/CMA/Actividades económicas/Industria/Publicidade/','E','Atendimento ','Sinais indicat. para estabel. de restauração e/ou bebidas misto');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_OCUPACAO_ESPACO_PUBLICO','89','OCUPAÇÃO DO ESPAÇO PÚBLICO MUNICIPAL','Sites/Arquivo/CMA/Taxas e Licenças/Ocupação de espaço público municipal/','E','Atendimento','Ocupação de espaço público municipal');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_PROT_FOGO_ARTIFICIO','90','PROTECÇÃO CIVIL','Sites/Arquivo/CMA/Segurança pública/Protecção civil/','E','Atendimento/Protecção civil','Utilização de fogo de artifício em espaço rural');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_PROT_QUEIMADAS','91','PROTECÇÃO CIVIL','Sites/Arquivo/CMA/Segurança pública/Protecção civil/','E','Atendimento/Protecção civil','Realização de queimadas');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_COMP_JUNCAO_ELEMENTOS','52','PROCESSOS COMPLEMENTARES','Está dependente do processo  ao qual se vai juntar','E','Todos',' Junção de elementos');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_COMP_AVERBAMENTOS','92','PROCESSOS COMPLEMENTARES','Está dependente do processo  ao qual se vai juntar','E','Todos','Averbamento');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_COMP_PRORROGACAO_PRAZOS','54','PROCESSOS COMPLEMENTARES','Está dependente do processo  ao qual se vai juntar','E','Todos',' Prorrogação de prazos');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Ext_REQ_GENERICO_EXT','93','REQUERIMENTO GENÉRICO EXTERNO','Sites/Arquivo/CMA/Requerimento Genérico Externo','E','Todos','Requerimento Genérico Externo');
insert into cma_tipo_processo (id_tipo_processo, id_tipo_processo_num, grupo, pasta_processo_base, tipo_processo_macro,perfil_responsavel, descricao) values ('adHoc_Int_REQ_GENERICO_INT','94','REQUERIMENTO GENÉRICO INTERNO','Sites/Arquivo/CMA/Requerimento Genérico Interno','I','Todos','Requerimento Genérico Interno');

insert into cma_tipo_documento (id_tipo_processo, nome, descricao, tipo) values
('aHoc_EXT_OCUPACAO_ESPACO_PUBLICO', 'Planta de Localização', 'Planta de Localização à escala 1:2000, com o local assinalado a vermelho.',1),
('aHoc_EXT_OCUPACAO_ESPACO_PUBLICO', 'Alvará de Licença ou Decl. Prévia', 'Cópia do alvará de Licença de Utilização, do Alvará Sanitário ou de Declaração Prévia',1),
('aHoc_EXT_OCUPACAO_ESPACO_PUBLICO', 'Memória Descritiva', 'Memória descritiva referindo cores materiais e restantes características (os equipamentos a colocar no Centro Histórico de Abrantes não poderão conter referência ou menção a marcas comerciais, devendo possuir cor sóbria e material de boa qualidade), com excepção dos equipamentos propriedade do município',1),
('aHoc_EXT_OCUPACAO_ESPACO_PUBLICO', 'Imagens do Equipamento', 'Fotografia, catálogo ou desenho do equipamento a utilizar, indicando as dimensões e localização pretendida',1),
('aHoc_EXT_OCUPACAO_ESPACO_PUBLICO', 'Desenho do equipamento', 'Desenho do equipamento à escala 1:10 ou 1:20, indicando as respectivas dimensões',1),
('aHoc_EXT_OCUPACAO_ESPACO_PUBLICO', 'Desenha da Implantação', 'Desenho indicando a área de implantação requerida, à escala 1:200',1);

insert into cma_tipo_documento (id_tipo_processo, nome, descricao, tipo) values
('aHoc_EXT_ATENDIMENTO_GERAL', 'Outros Documentos', 'Documento anexos ao pedido',2);

alter table cma_cliente_municipal modify cp varchar(30);
alter table cma_requerimento add nome_cliente varchar(200);
alter table cma_requerimento add nome_representante varchar(200);
alter table cma_requerimento add data_requerimento date;

alter table cma_requerimento add pid integer;