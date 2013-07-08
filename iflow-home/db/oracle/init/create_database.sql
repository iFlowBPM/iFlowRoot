--
-- @table flow      tabela que contem informacao sobre cada flow
-- @field flowid        id do flow
-- @field flowname      nome do flow
-- @field flowfile      ficheiro xml com descricao do flow
-- @field enabled       indica se um flow esta enabled ou nao
-- @field created       data/hora da criacao
--
create table flow (
        flowid                  int,
        flowname                varchar2 (64) constraint flow_flowname_nn not null,
        flowfile                varchar2 (64) constraint flow_flowfile_nn not null,
        enabled                 number(1) default 0,
        created                 date default sysdate,
        organizationid          varchar2(50) not null,
        flowdata                blob constraint flow_flowdata_nn not null,
        flowversion             int,
        modified                date constraint flow_modified_nn not null,
        name_idx0               varchar2(64),
        name_idx1               varchar2(64),
        name_idx2               varchar2(64),
        name_idx3               varchar2(64),
        name_idx4               varchar2(64),
        name_idx5               varchar2(64),
        name_idx6               varchar2(64),
        name_idx7               varchar2(64),
        name_idx8               varchar2(64),
        name_idx9               varchar2(64),
        name_idx10              varchar2(64),
        name_idx11              varchar2(64),
        name_idx12              varchar2(64),
        name_idx13              varchar2(64),
        name_idx14              varchar2(64),
        name_idx15              varchar2(64),
        name_idx16              varchar2(64),
        name_idx17              varchar2(64),
        name_idx18              varchar2(64),
        name_idx19              varchar2(64),
        seriesid				number,
        type_code               varchar2(1) default 'W',
        constraint flow_pk primary key (flowid)
);
create sequence seq_flow increment by 1 start with 1 nocycle order;

--
-- @table flow_roles      tabela que contem informacao sobre permissoes dos flows
-- @field flowid        id do flow
-- @field profileid     id do perfil
-- @field permissions   Permissoes: R (read), W (write), A (administrate), ...
--
create table flow_roles (
        flowid                int,
        profileid             int,
        permissions           varchar2 (16),
        constraint flow_roles_pk primary key (flowid, profileid)
);

--
-- @table flow_state        tabela que contem o estado de cada processo no flow
-- @field flowid        id do flow
-- @field pid           id do processo
-- @field subpid identificador numerico do sub-processo
-- @field state         estado do processo (id do bloco corrente)
-- @field result        resultado do estado
-- @field mdate         data/hora da ultima alteracao do contador
-- @field exit_flag     flag que indica saida do bloco
--
create table flow_state (
        flowid                  int,
        pid                     int,
        subpid                  int default 1,
        state                   int constraint flowstate_state_nn not null,
        result                  varchar2(1024),
        mdate                   date default sysdate,
        exit_flag               number default 0 constraint flowstate_exit_nn not null,
        mid		                number default 0,
        closed      			number(1) default 0,
        canceled    			number(1) default 0,
        constraint flow_state_pk primary key (flowid, pid, subpid)
);

--
-- @table flow_state_history        tabela que contem o historico do estado de cada processo no flow
-- @field flowid        id do flow
-- @field pid           id do processo
-- @field subpid identificador numerico do sub-processo
-- @field state         estado do processo (id do bloco corrente)
-- @field result        resultado do estado
-- @field mdate         data/hora da ultima alteracao do contador
-- @field mid           id da alteracao
-- @field exit_flag     flag que indica saida do bloco
-- @field exit_port     nome do porto de saida do bloco
--
create table flow_state_history (
        flowid                  int constraint flowstatehist_flowid_nn not null,
        pid                     int constraint flowstatehist_pid_nn not null,
        subpid                  int default 1,
        state                   int constraint flowstatehist_state_nn not null,
        result                  varchar2(1024),
        mdate                   date default sysdate,
        mid                     number default 0,
        exit_flag               number default 0 constraint flowstatehist_exit_nn not null,
        undoflag                number(1) default 0,
        exit_port               varchar2(64)
);

--
-- @sequence seq_flow_settings        sequencia para historico das propriedades de um fluxo (mid)
--
create sequence seq_flow_settings increment by 1 start with 1 nocycle order;
--
-- @table flow_settings tabela que contem as propriedades de um fluxo
-- @field flowid        id do flow
-- @field name          nome da propriedade
-- @field description   descricao da propriedade
-- @field value         valor da propriedade
-- @field isQuery       flag que indica se value e uma sql query
-- @field mdate         data/hora da alteracao
--
create table flow_settings (
        flowid                  int,
        name                    varchar2(64),
        description             varchar2(1024),
        value                   varchar2(1024),
        isquery                 number(1) default 0,
        mdate                   date default sysdate,
        constraint flow_settings_pk primary key (flowid, name)
);

--
-- @table flow_settings_history        tabela que contem o historico das propriedades de um fluxo
-- @field flowid        id do flow
-- @field name          nome da propriedade
-- @field description   descricao da propriedade
-- @field value         valor da propriedade
-- @field isQuery       flag que indica se value e uma sql query
-- @field mdate         data/hora da alteracao
-- @field mid           id da alteracao
--
create table flow_settings_history (
        flowid                  int constraint flowsetthist_flowid_nn not null,
        name                    varchar2(64),
        description             varchar2(1024),
        value                   varchar2(1024),
        isquery                 number(1) default 0,
        mdate                   date default sysdate,
        mid                     number
);

--
-- @table activity tabela que contem as actividades (tarefas) agendadas para cada utilizador
-- @field userid identificador alfanumerico do utilizador
-- @field flowid identificador do flow no qual esta a correr o processo
-- @field pid identificador numerico do processo
-- @field type tipo de actividade
-- @field priority prioridade da actividade
-- @field created data de criacao da actividade (data em que a actividade foi agendada para o utilizador)
-- @field started data de inicio da actividade (data em que o utilizador iniciou a actividade e/ou tomou conhecimento da mesma)
-- @field archived data em que a actividade foi arquivada (completada ou nao)
-- @field description descricao da actividade
-- @field url URL associado a actividade
-- @field status estado da actividade
-- @field notify flag que indica se notificacao "agendada"
-- @field delegated flag que indica se a actividade esta delegada
--
create table activity (
    userid          varchar2(100),
    flowid          int,
    pid             int,
    subpid          int default 1,
    type            int,
    priority        int,
    created         date default sysdate,
    started         date default sysdate,
    archived        date default sysdate,
    description     varchar2(256),
    url             varchar2(256),
    status          int,
    notify          number(1) default 0,
    delegated       number(1) default 0,
    profilename     varchar2(256),
    read_flag       number(1) default 1,
    mid		 	    int default 0,
    constraint activity_pk primary key (userid,flowid,pid,subpid)
);

--
-- @table activity_hierarchy tabela que contem a descricao da hierarquia de delegacoes de tarefas
-- @field id identificador numerico de uma delegacao do fatherid para o userid
-- @field fatherid identificador numerico do utilizador que representa o no' pai da hierarquia
-- @field userid identificador alfanumerico do utilizador nesse no'
-- @field ownerid identificador alfanumerico do utilizador a quem a actividade pertence
-- @field flowid identificador numerico do fluxo
-- @field slave flag que indica se e' no' interno ou folha (slave)
-- @field expires data ao fim da qual a delegacao perde o efeito
-- @field permissions R (read), W (write), A (administrate), ...
create table activity_hierarchy (
        hierarchyid int,
        parentid    int default 0 constraint activityhier_parentid_nn not null,
        userid      varchar2(100) constraint activityhier_userid_nn not null,
        ownerid     varchar2(100) constraint activityhier_ownerid_nn not null,
        flowid      number(38) constraint activityhier_flowid_nn not null,
        slave       number(1) default 1,
        expires     date default sysdate,
        permissions varchar2 (16) default null,
        pending     number(1) default 1,
        acceptkey   varchar2(32) default null,
        rejectkey   varchar2(32) default null,
        requested   date default sysdate,
        responded   date default sysdate,
        constraint activity_hierarchy_pk primary key (hierarchyid)
);
create sequence seq_activity_hierarchy increment by 1 start with 1 nocycle order;

create table activity_hierarchy_history (
        hierarchyid int,
        parentid    int default 0 constraint activityhierh_parentid_nn not null,
        userid      varchar2(100) constraint activityhierh_userid_nn not null,
        ownerid     varchar2(100) constraint activityhierh_ownerid_nn not null,
        flowid      number(38) constraint activityhierh_flowid_nn not null,
        started     date default sysdate,
        expires     date default sysdate,
        permissions varchar2 (16) default null,
        requested   date default sysdate,
        responded   date default sysdate,
        constraint activity_hierarchy_history_pk primary key (hierarchyid)
);

--
-- @table activity_history tabela que contem as actividades (tarefas) ja arquivadas de cada utilizador
-- @field userid identificador alfanumerico do utilizador
-- @field flowid identificador do flow no qual esta a correr o processo
-- @field pid identificador numerico do processo
-- @field type tipo de actividade
-- @field priority prioridade da actividade
-- @field created data de criacao da actividade (data em que a actividade foi agendada para o utilizador)
-- @field started data de inicio da actividade (data em que o utilizador iniciou a actividade e/ou tomou conhecimento da mesma)
-- @field archived data em que a actividade foi arquivada (completada ou nao)
-- @field description descricao da actividade
-- @field url URL associado a actividade
-- @field status estado da actividade
-- @field notify flag que indica se notificacao "agendada"
-- @field delegated flag que indica se a actividade esta delegada
--
create table activity_history (
        userid          varchar2(100),
        flowid          int constraint activityhist_flowid_nn not null,
        pid             int constraint activityhist_pid_nn not null,
        subpid          int default 1,
        type            int,
        priority        int,
        created         date default sysdate,
        started         date default sysdate,
        archived        date default sysdate,
        description     varchar2(256),
        url             varchar2(256),
        status          int,
        notify          number(1),
        delegated       number(1) default 0,
        delegateduserid varchar2(100),
        profilename     varchar2(256),
	    read_flag       number(1) default 1,
	    mid 			int default 0,
	    worker 			number(1) default 0,
	    undoflag		number(1) default 0
);

CREATE TABLE APPLICATIONPROFILES (
        APPID           NUMBER(38),
        PROFILEID       NUMBER(38),
        CONSTRAINT PK_APPLICATIONPROFILES PRIMARY KEY (APPID,PROFILEID)
);

CREATE SEQUENCE SEQ_APPLICATIONS START WITH 1 INCREMENT BY 1 MINVALUE 1 NOMAXVALUE CACHE 20 NOCYCLE ORDER;
CREATE TABLE APPLICATIONS (
        APPID           NUMBER(38),
        NAME            VARCHAR2(50) CONSTRAINT NL_APPLICATION_NAME NOT NULL,
        DESCRIPTION   VARCHAR2(125),
        CONSTRAINT PK_APPLICATIONS PRIMARY KEY (APPID),
        CONSTRAINT UK_APPLICATION_NAME UNIQUE (NAME)
);

--
-- @table counter tabela que contem contadores sequenciais para utilizacao em diversos modulos do iFlow
-- @field name nome do contador
-- @field value valor do contador
-- @field modification data/hora da ultima alteracao do contador
--
create table counter (
        name                    varchar2(64),
        value                   number,
        modification            date default sysdate,
        constraint counter_pk primary key (name)
);

--
-- @table dirty_email   tabela com os mails nao enviados pela aplicacao
-- @field eid           id do email
-- @field eserver   email server
-- @field efrom     from do email
-- @field eto           lista de enderecos to do email
-- @field ecc           lista de enderecos cc do email
-- @field esubject      subject do email
-- @field ebody         body do email
-- @field ehtml         flag que indica se email esta em html
-- @field ecreated      data/hora da criacao do email
-- @field etries        Numero de tentativas de envio do email
-- @field enext_send    Data para a proxima tentativa de envio
-- @field elast_send    Data da ultima tentativa de envio
-- @field etls          Usar TLS (ligacao segura)
-- @field eauth         Usar autenticacao
-- @field euser         Utilizador para autenticar no servidor smtp
-- @field epass         Password de autenticacao no servidor smtp
--
create table dirty_email (
    eid                     int constraint dirtyemail_eid_nn not null,
    eserver                 varchar2(256) constraint dirtyemail_eserver_nn not null,
    eport					number default -1,
    efrom                   varchar2(256) constraint dirtyemail_efrom_nn not null,
    eto                     blob constraint dirtyemail_eto_nn not null,
    ecc                     blob,
    esubject                varchar2(1024),
    ebody                   blob,
    ehtml                   number(1) default 0 constraint dirtyemail_ehtml_nn not null,
    ecreated                date default sysdate,
    etries					integer default 0 constraint dirtyemail_etries_nn not null,
    enext_send              date default null,
    elast_send              date default null,
    etls                    number(1) default 0 constraint dirtyemail_etls_nn not null,
    eauth                   number(1) default 0 constraint dirtyemail_eauth_nn not null,
    euser                   varchar2(256),
    epass                   varchar2(256),
    constraint dirty_email_pk primary key (eid)
);

--
-- @sequence seq_documents_id        sequencia para docid de um documento
--
create sequence seq_documents_id  increment by 1 start with 1 nocycle order;
--
-- @table documents      tabela que contem informacao sobre cada document
-- @field docid          id do document
-- @field filename       nome do document
-- @field datadoc        ficheiro xml com descricao do flow
-- @field updated        data/hora da ultima actualizacao
--
create table documents (
    docid       int,
    filename    varchar2 (128) constraint docs_filename_nn not null,
    datadoc     blob,
    docurl		varchar2(2000),
    updated     date default sysdate,
    flowid      int default 0 constraint docs_flowid_nn not null,
    pid         int default 0 constraint docs_pid_nn not null,
    subpid      int default 0 constraint docs_subpid_nn not null,
    numass      int default 0 NOT NULL,
    constraint documents_pk primary key (docid)
);


--
-- @sequence seq_email        sequencia para id da tabela email
--
create sequence seq_email increment by 1 start with 1 nocycle order;
--
-- @table email         tabela com os mails da aplicacao
-- @field eid           id do email
-- @field eserver       email server
-- @field efrom         from do email
-- @field eto           lista de enderecos to do email
-- @field ecc           lista de enderecos cc do email
-- @field esubject      subject do email
-- @field ebody         body do email
-- @field ehtml         flag que indica se email esta em html
-- @field ecreated      data/hora da criacao do email
-- @field etries        Numero de tentativas de envio do email
-- @field enext_send    Data para a proxima tentativa de envio
-- @field elast_send    Data da ultima tentativa de envio
-- @field etls          Usar TLS (ligacao segura)
-- @field eauth         Usar autenticacao
-- @field euser         Utilizador para autenticar no servidor smtp
-- @field epass         Password de autenticacao no servidor smtp
--
create table email (
    eid                     int,
    eserver                 varchar2(256) constraint email_eserver_nn not null,
    eport					number default -1,
    efrom                   varchar2(256) constraint email_efrom_nn not null,
    eto                     blob constraint email_eto_nn not null,
    ecc                     blob,
    esubject                varchar2(1024),
    ebody                   blob,
    ehtml                   number(1) default 0 constraint email_ehtml_nn not null,
    ecreated                date default sysdate,
    etries					integer default 0 constraint email_etries_nn not null,
    enext_send              date default null,
    elast_send              date default null,
    etls                    number(1) default 0 constraint email_etls_nn not null,
    eauth                   number(1) default 0 constraint email_eauth_nn not null,
    euser                   varchar2(256),
    epass                   varchar2(256),
    constraint email_pk primary key (eid)
);

--
-- @sequence seq_event_data        sequencia para id da tabela event_data
--
create sequence seq_event_data increment by 1 start with 1 nocycle order;
--
-- @table event_data tabela com a informacao dos eventos activos
-- @field id            id do evento activo
-- @field fid           id do flow a que o evento pertence
-- @field pid           id do processo a que o evento pertence
-- @field blockid       id do bloco a que o evento pertence
-- @field starttime data de criacao do evento
-- @field type          tipo de evento
-- @field properties    propriedades especificas do tipo de evento e do evento em particular
-- @field processed  flag para indicar se um evento esta a ser processado num det. instante t
--
create table event_data (
    eventid     int,
    fid         int constraint eventdata_fid_nn not null,
    pid         int constraint eventdata_pid_nn not null,
    subpid      int default 1,
    blockid     int constraint eventdata_blockid_nn not null,
    starttime   long,
    type        varchar2(255),
    properties  varchar2(1024),
    processed   int default 0,
    userid      varchar2(100),
    constraint event_data_pk primary key (eventid)
);


--
-- @table forkjoin_blocks  tabela com informacao dos blocos fork/join num fluxo
-- @field flowid            id do flow
-- @field blockid           id do bloco
-- @field type              indicacao do tipo do bloco (Join ou Fork)
--
create table forkjoin_blocks (
    flowid     int,
    blockid    int,
    type       number(1),
    constraint fj_blocks_pk primary key (flowid,blockid)
);

--
-- @table forkjoin_hierarchy tabela com a hierarquia de dependencias entre blocos fork/join
-- @field flowid            id do flow
-- @field parentblockid id do bloco pai
-- @field blockid           id do bloco
--
create table forkjoin_hierarchy (
    flowid        int,
    parentblockid int,
    blockid       int,
    constraint fj_hierarchy_pk primary key (flowid,parentblockid,blockid)
);

--
-- @table forkjoin_mines  tabela com os blocos fork/join minados num subprocesso
-- @field flowid            id do flow
-- @field pid               id do processo
-- @field blockid           id do bloco
-- @field mined         indicacao se a sua lista de dependencias esta minada (contem o subpid)
-- @field locked        indicacao do subpid que bloqueou, 0 se nao estiver bloqueado
--
create table forkjoin_mines (
    flowid     int,
    pid        int,
    blockid    int,
    mined      int,
    locked     int,
    constraint fj_mines_pk primary key (flowid,pid,blockid)
);

--
-- @table forkjoin_state_dep tabela com as dependencias entre blocos fork/join e os outros blocos
-- @field flowid            id do flow
-- @field parentblockid id do bloco pai
-- @field blockid           id do bloco
--
create table forkjoin_state_dep (
    flowid        int,
    parentblockid int,
    blockid       int,
    constraint fj_state_dep_pk primary key (flowid,parentblockid,blockid)
);

--
-- @sequence seq_iflow_errors sequencia para id da tabela iflow_errors
--
create sequence seq_iflow_errors increment  by 1 start with 1 nocycle order;
--
-- @table iflow_errors  tabela com a informacao dos erros que surgiram no iflow
-- @field errorid         identificador do erro
-- @field userid          identificador do utilizador
-- @field pid             identificador do processo
-- @field subpid          identificador do subprocesso
-- @field errortype       identificador do tipo de erro
-- @field description     descricao do erro
--
create table iflow_errors (
    errorid       int,
    userid        varchar2(100) constraint iflowerrors_userid_nn not null,
    created       date default sysdate,
    method        varchar2(128),
    object        varchar2(128),
    flowid        int,
    pid           int,
    subpid        int,
    errortype     int,
    description   varchar2(512) constraint iflowerrors_desc_nn not null,
    constraint iflow_errors_pk         primary key (errorid)
);

--
-- @sequence seq_links_flows sequencia para id da tabela links_flows
--
create sequence seq_links_flows increment  by 1 start with 1 nocycle order;
--
-- @table links_flows   tabela com a informacao dos links da pagina de flows
-- @field linkid            identificacao do link
-- @field parentid      identificacao do link pai
-- @field flowid            identificao do fluxo associado
-- @field name          texto com o nome do link
-- @field url           url atribuido ao link
--
create table links_flows (
    linkid   int,
    parentid int default 0,
    flowid   int default 0,
    name     varchar2(64) default null,
    url      varchar2(256) default null,
    organizationid varchar2(50) default '1' not null constraint nl_links_flows_orgid,
    constraint links_flows_pk primary key (linkid)
);

--
-- @TABLE MODification tabela que contem todas as modificacoes efectuadas a um processo
-- @field flowid id do flow
-- @field pid identificador numerico do processo
-- @field subpid identificador numerico do sub-processo
-- @field mid identificador numerico da modificacao
-- @field mdate data da modificacao
-- @field muser utilizador que efectuou a modificacao
--
create table modification (
    flowid          int,
    pid         int,
    subpid      int default 1,
    mid         int,
    mdate           date default sysdate,
    muser           varchar2(256),
    constraint modification primary key (flowid, pid, subpid, mid)
);

--
-- @sequence seq_new_features sequencia para id da tabela new_features
--
create sequence seq_new_features increment  by 1 start with 1 nocycle order;
--
-- @table new_features tabela com as descricoes das novas funcionalidades
-- @field newfeaturesid id da tabela new_features
-- @field version       identificador da versao do iFlow
-- @field feature       identificador da nova funcionalidade
-- @field description   descricao da nova funcionalidade
-- @field created       data da inclusao da nova funcionalidade
--
create table new_features (
    newfeaturesid  int,
    version        varchar2(64) constraint features_version_nn not null,
    feature        varchar2(128) constraint features_feature_nn not null,
    description    varchar2(1024) constraint features_desc_nn not null,
    created        date default sysdate,
    organizationid varchar2(50),
    constraint new_features_pk primary key (newfeaturesid),
    constraint new_features_uk unique (version,feature)
);

CREATE SEQUENCE SEQ_ORGANIZATIONAL_UNIT START WITH 1 INCREMENT BY 1 MINVALUE 1 NOMAXVALUE CACHE 20 NOCYCLE ORDER;
CREATE TABLE ORGANIZATIONAL_UNITS (
    UNITID                           NUMBER   (38),
    PARENT_ID                        NUMBER   (38) CONSTRAINT NL_ORG_UNIT_PARENT_ID NOT NULL,
    ORGANIZATIONID                   NUMBER   (38) CONSTRAINT NL_ORG_UNIT_ORGID NOT NULL,
    NAME                             VARCHAR2 (50) CONSTRAINT NL_ORG_UNIT_NAME NOT NULL,
    DESCRIPTION                      VARCHAR2 (150),
    CONSTRAINT PK_ORGANIZATIONAL_UNITS PRIMARY KEY (UNITID)
    CONSTRAINT UN_ORGANIZATIONAL_UNITS_NAME UNIQUE (NAME)
);

CREATE SEQUENCE SEQ_ORGANIZATION START WITH 1 INCREMENT BY 1 MINVALUE 1 NOMAXVALUE CACHE 20 NOCYCLE ORDER;
CREATE TABLE ORGANIZATIONS (
    ORGANIZATIONID                   NUMBER   (38) ,
    NAME                             VARCHAR2 (50) CONSTRAINT NL_ORGANIZATION_NAME NOT NULL,
    DESCRIPTION                      VARCHAR2 (150),
    STYLE_URL                        VARCHAR2 (128),
    LOGO_URL                         VARCHAR2 (128),
    LOCKED                           NUMBER   (1) DEFAULT 0 CONSTRAINT NL_ORGANIZATION_LOCKED NOT NULL,
    CONSTRAINT PK_ORGANIZATIONS PRIMARY KEY (ORGANIZATIONID),
    CONSTRAINT UN_ORGANIZATION_NAME UNIQUE (NAME)
);

--
-- @table organization_theme tabela com a informacao da associacao entre organizacoes e temas
-- @field organizationid            identificacao da organizacao
-- @field theme                     tema associado a organizacao
-- @field style_url                  url da css associada a organizacao
-- @field logo_url                  url do logotipo associada a organizacao
--
create table organization_theme (
    organizationid   VARCHAR2 (50),
    theme            varchar2(256)  constraint orgtheme_theme_nn not null,
    style_url        varchar2(256)  constraint orgtheme_styleurl_nn not null,
    logo_url         varchar2(256)  constraint orgtheme_logourl_nn not null,
    menu_location    varchar2(256)  default 'left',
    menu_style       varchar2(256)  default 'list',
    proc_menu_visible NUMBER(1) default 1,
    constraint organization_theme_pk primary key (organizationid)
);

--
-- @table process tabela que contem a informacao relacionada com cada processo
-- @field flowid id do flow
-- @field pid identificador numerico do processo
-- @field subpid identificador numerico do sub processo
-- @field mid identificador numerico da ultima alteracao efectuada ao processo
-- @field created data de criacao do processo
-- @field info texto informativo acerca do estado actual do processo
--
create table process (
    flowid      int,
    pid         int,
    subpid      int default 1,
    pnumber     varchar2(128) constraint process_pnumber_nn not null,
    mid         int constraint process_mid_nn not null,
	creator	    varchar2(100) constraint process_cr_nn not null,
    created     date default sysdate constraint process_cd_nn not null,
	currentuser	varchar2(100) constraint process_cu_nn not null,
    lastupdate  date default sysdate constraint process_lu_nn not null,
    procdata    CLOB,
    closed      number(1) default 0,
    canceled    number(1) default 0,
	idx0        varchar2(1024),
	idx1        varchar2(1024),
	idx2        varchar2(1024),
	idx3        varchar2(1024),
	idx4        varchar2(1024),
	idx5        varchar2(1024),
	idx6        varchar2(1024),
	idx7        varchar2(1024),
	idx8        varchar2(1024),
	idx9        varchar2(1024),
	idx10       varchar2(1024),
	idx11       varchar2(1024),
	idx12       varchar2(1024),
	idx13       varchar2(1024),
	idx14       varchar2(1024),
	idx15       varchar2(1024),
	idx16       varchar2(1024),
	idx17       varchar2(1024),
	idx18       varchar2(1024),
	idx19       varchar2(1024),
	constraint process_pk primary key (flowid, pid, subpid)
);

--
-- @table process_history tabela que contem a informacao relacionada com o historico do processo
-- @field flowid id do flow
-- @field pid identificador numerico do processo
-- @field subpid identificador numerico do sub processo
-- @field mid identificador numerico da eltima alteracao efectuada ao processo
-- @field created data de criacao do processo
-- @field info texto informativo acerca do estado actual do processo
--
create table process_history (
    flowid      int,
    pid         int,
    subpid      int default 1,
    pnumber     varchar2(128) constraint process_hist_pn_nn not null,
    mid         int constraint process_hist_mid_nn not null,
	creator	    varchar2(100) constraint process_hist_cr_nn not null,
    created     date default sysdate constraint process_hist_cd_nn not null,
	currentuser	varchar2(100) constraint process_hist_cu_nn not null,
    lastupdate  date default sysdate constraint process_hist_lu_nn not null,
    procdata    CLOB NULL,
    procdatazip BLOB NULL,
    closed      number(1) default 0,
    canceled    number(1) default 0,
	idx0        varchar2(1024),
	idx1        varchar2(1024),
	idx2        varchar2(1024),
	idx3        varchar2(1024),
	idx4        varchar2(1024),
	idx5        varchar2(1024),
	idx6        varchar2(1024),
	idx7        varchar2(1024),
	idx8        varchar2(1024),
	idx9        varchar2(1024),
	idx10       varchar2(1024),
	idx11       varchar2(1024),
	idx12       varchar2(1024),
	idx13       varchar2(1024),
	idx14       varchar2(1024),
	idx15       varchar2(1024),
	idx16       varchar2(1024),
	idx17       varchar2(1024),
	idx18       varchar2(1024),
	idx19       varchar2(1024),
	undoflag    number(1) default 0,
    constraint process_history_pk primary key (flowid, pid, subpid, mid)
);


CREATE SEQUENCE SEQ_PROFILES START WITH 1 INCREMENT BY 1 MINVALUE 1 NOMAXVALUE CACHE 20 NOCYCLE ORDER;
CREATE TABLE PROFILES (
    PROFILEID             NUMBER   (38),
    NAME                  VARCHAR2 (50) CONSTRAINT NL_PROFILE_NAME NOT NULL,
    DESCRIPTION           VARCHAR2 (125),
    ORGANIZATIONID        VARCHAR2(50) DEFAULT '1' CONSTRAINT NL_PROFILE_ORGID NOT NULL,
    CONSTRAINT PK_PROFILES PRIMARY KEY (PROFILEID)
);

--
-- @table queue_data tabela que contem o valor actual de cada variavel dum processo em queue
-- @field queue_proc_id id do processo em queue
-- @field name nome da variavel
-- @field value valor da variavel
--
create table queue_data (
        queue_proc_id           int constraint queuedata_procid_nn not null,
        name                    varchar2(64),
        value                   varchar2(1024),
        constraint queue_data_pk primary key (queue_proc_id, name)
);

--
-- @sequence seq_queue_proc        sequencia para id da tabela queue_proc
--
create sequence seq_queue_proc increment by 1 start with 1 nocycle order;
--
-- @table queue_proc    tabela com informacao dos processos na queue
-- @field id            id do processo na queue
-- @field object    identificador do tipo de objecto na queue
-- @field groupid   criterio de agrupamento dos processos na queue para um dado tipo de objecto
-- @field flowid        id do flow onde esta criado o processo iflow associado
-- @field pid           id do processo iflow associado
-- @field properties    propriedades associadas ao processo na queue (formato csv)
-- @field creation_date data de criacao do processo na queue
--
create table queue_proc (
    id                      int constraint queueproc_id_nn not null,
    object                  int constraint queueproc_object_nn not null,
    groupid                 varchar2(64),
    flowid                  int,
    pid                     int,
    properties              varchar2(1024),
    creation_date           date default SYSDATE constraint queueproc_creationdate_nn not null ,
    constraint queue_proc_pk primary key (id)
);

CREATE TABLE UNITMANAGERS (
    USERID                        NUMBER   (38),
    UNITID                        NUMBER   (38),
    CONSTRAINT PK_UNITMANAGERS PRIMARY KEY(USERID,UNITID)
);


CREATE TABLE USERPROFILES (
    USERID                           NUMBER   (38),
    PROFILEID                        NUMBER   (38),
    CONSTRAINT PK_USERPROFILES PRIMARY KEY (USERID,PROFILEID)
);

CREATE SEQUENCE SEQ_USERS START WITH 1 INCREMENT BY 1 MINVALUE 1 NOMAXVALUE CACHE 20 NOCYCLE ORDER;
-- droped email_address not null constraint, since oracle treats empty strings as null
CREATE TABLE USERS (
    USERID               NUMBER   (38),
    UNITID               NUMBER   (38),
    USERNAME             VARCHAR2 (50)  CONSTRAINT NL_USERNAME  NOT NULL,
    USERPASSWORD         VARCHAR2 (125) CONSTRAINT NL_USERPASSWORD  NOT NULL,
    EMAIL_ADDRESS        VARCHAR2 (100) DEFAULT '',
    GENDER               CHAR   NOT NULL,
    FIRST_NAME           VARCHAR2 (50),
    LAST_NAME            VARCHAR2 (50),
    PHONE_NUMBER         VARCHAR2 (20),
    FAX_NUMBER           VARCHAR2 (20),
    MOBILE_NUMBER        VARCHAR2 (20),
    COMPANY_PHONE        VARCHAR2 (20),
    SESSIONID            VARCHAR2 (150),
    ACTIVATED            NUMBER   (1) CONSTRAINT NL_USER_ACTIVATED NOT NULL,
    PASSWORD_RESET       NUMBER   (1) DEFAULT 1 NOT NULL,
    ORGADM               NUMBER   (1) DEFAULT 0 NOT NULL,
--    ORGANIZATIONID       NUMBER   (38) DEFAULT 1 NOT NULL,
    CONSTRAINT PK_USERS PRIMARY KEY (USERID),
    CONSTRAINT UK_USERS_USERNAME UNIQUE (USERNAME),
--    CONSTRAINT UK_USERS_EMAIL_ADDRESS UNIQUE (EMAIL_ADDRESS,ORGANIZATIONID),
    CONSTRAINT UK_USERS_SESSIONID UNIQUE (SESSIONID)
);

--
-- @table flow      tabela que contem informacao sobre cada flow
-- @field flowid        id do flow
-- @field flowname      nome do flow
-- @field flowfile      ficheiro xml com descricao do flow
-- @field enabled       indica se um flow esta enabled ou nao
-- @field created       data/hora da criacao
--
create table sub_flow (
        flowid                  int,
        flowname                varchar2(64) constraint sub_flow_flowname_nn not null,
        flowfile                varchar2(64) constraint sub_flow_flowfile_nn not null,
        created                 date default sysdate,
        organizationid          varchar2(50) not null,
        flowdata                blob constraint sub_flow_flowdata_nn not null,
        flowversion             int,
        modified                date constraint sub_flow_modified_nn not null,
        constraint sub_flow_pk primary key (flowid)
);
create sequence seq_sub_flow increment by 1 start with 1 nocycle order;

--
-- @table flow      tabela que contem informacao sobre cada flow
-- @field flowid        id do flow
-- @field flowname      nome do flow
-- @field flowfile      ficheiro xml com descricao do flow
-- @field enabled       indica se um flow esta enabled ou nao
-- @field created       data/hora da criacao
--
create table flow_history (
        id                  int,
        flowid              int constraint flow_history_flowid_nn not null,
        name                varchar2 (64) constraint flow_history_name_nn not null,
        description         varchar2 (64) constraint flow_history_desc_nn not null,
        data                blob constraint flow_history_data_nn not null,
        flowversion         int,
        modified            date constraint flow_history_mod_nn not null,
        "comment"           varchar2(512),
        constraint flow_history_pk primary key (id)
);
create sequence seq_flow_history increment by 1 start with 1 nocycle order;

--
-- @table flow      tabela que contem informacao sobre cada flow
-- @field flowid        id do flow
-- @field flowname      nome do flow
-- @field flowfile      ficheiro xml com descricao do flow
-- @field enabled       indica se um flow esta enabled ou nao
-- @field created       data/hora da criacao
--
create table sub_flow_history (
        id                  int,
        flowid              int constraint sub_flow_history_flowid_nn not null,
        name                varchar2 (64) constraint sub_flow_history_name_nn not null,
        description         varchar2 (64) constraint sub_flow_history_desc_nn not null,
        data                blob constraint sub_flow_history_data_nn not null,
        flowversion         int,
        modified            date constraint sub_flow_history_mod_nn not null,
        "comment"           varchar2(512),
        constraint sub_flow_history_pk primary key (id)
);
create sequence seq_sub_flow_history increment by 1 start with 1 nocycle order;

--
-- @table flow      tabela que contem informacao sobre cada flow
-- @field flowid        id do flow
-- @field flowname      nome do flow
-- @field flowfile      ficheiro xml com descricao do flow
-- @field enabled       indica se um flow esta enabled ou nao
-- @field created       data/hora da criacao
--
create table flow_template (
        name                varchar2(64),
        description         varchar2(64) constraint flow_template_desc_nn not null,
        data                blob constraint flow_template_data_nn not null,
        constraint flow_template_pk primary key (name)
);

--
-- @table flow      tabela que contem informacao sobre cada flow
-- @field flowid        id do flow
-- @field flowname      nome do flow
-- @field flowfile      ficheiro xml com descricao do flow
-- @field enabled       indica se um flow esta enabled ou nao
-- @field created       data/hora da criacao
--
create table sub_flow_template (
        name                varchar2(64),
        description         varchar2(64) constraint sub_flow_template_desc_nn not null,
        data                blob constraint sub_flow_template_data_nn not null,
        constraint sub_flow_template_pk primary key (name)
);

create table user_activation (
        userid number (38) not null,
        organizationid number (38) not null,
        unitid number (38) not null,
        code varchar2 (64) not null,
        created date default sysdate not null,
        primary key (userid,organizationid,unitid)
);


create table event_info (
        name varchar2(64) not null,
        description varchar2 (255) not null,
        primary key (name)
);

-- email modification confirmations
create table email_confirmation (
  userid number (38) not null,
  organizationid number (38) not null,
  email varchar2(100) not null,
  code varchar2(50) not null,
  constraint email_confirmation_pk primary key (userid,organizationid),
  constraint un_email_confirmation_code unique (code)
);


-- User settings table
create table user_settings (
  userid varchar2(100) not null,
  lang varchar2(2),
  region varchar2(2),
  timezone varchar2(64),
  tutorial varchar2(20),
  help_mode number(1) default 1,
  tutorial_mode number(1) default 1,
  constraint user_settings_pk primary key (userid)
);

-- Organization settings table
create table organization_settings (
  organizationid varchar2(32) not null,
  lang varchar2(2),
  region varchar2(2),
  timezone varchar2(64),
  constraint organization_settings_pk primary key (organizationid)
);

-- Notifications table
create sequence seq_notifications;
create table notifications (
  id int,
  created date,
  sender varchar2(192),
  message varchar2(500),
  link VARCHAR(45) DEFAULT 'false' not NULL ,
  constraint notifications_pk primary key (id)
);

create table user_notifications (
  userid varchar2(100) not null,
  notificationid int not null,
  isread number(1) default 0 not null,
  constraint user_notifications_pk primary key (userid,notificationid)
);

-- System users
CREATE SEQUENCE SEQ_SYSTEM_USERS START WITH 1 INCREMENT BY 1 MINVALUE 1 NOMAXVALUE CACHE 20 NOCYCLE ORDER;
CREATE TABLE SYSTEM_USERS (
  userid number(10) NOT NULL,
  username VARCHAR2(50) NOT NULL,
  userpassword VARCHAR2(125) NOT NULL,
  email_address VARCHAR2(100) NOT NULL,
  phone_number VARCHAR2(20),
  mobile_number VARCHAR2(20),
  first_name VARCHAR2(50),
  last_name VARCHAR2(50),
  sessionid VARCHAR2(150),
  CONSTRAINT PK_SYSTEM_USERS PRIMARY KEY (USERID),
  CONSTRAINT UK_SYSTEM_USERS_USERNAME UNIQUE (USERNAME),
  CONSTRAINT UK_SYSTEM_USERS_SESSIONID UNIQUE (SESSIONID)
);

-- insert into system_users values (SEQ_SYSTEM_USERS.NEXTVAL, 'admin', 'F/KbuDOEofgjp7/9yUGnrw==', 'oscar@iknow.pt', '111111111', '9111111', 'Mr', 'Admin', NULL);


CREATE SEQUENCE SEQ_SERIES START WITH 1 INCREMENT BY 1 MINVALUE 1 NOMAXVALUE CACHE 20 NOCYCLE ORDER;
create table series (
	id int,
	organizationid varchar2(50) DEFAULT '1' constraint series_orgid_nn not null,
	created number constraint series_created_nn not null,
	enabled number(1) default 1 constraint series_enabled_nn not null,
	state number(1) constraint series_state_nn not null,
	name varchar2(100) constraint series_name_nn not null,
	kind varchar2(100) constraint series_kind_nn not null,
	pattern varchar2(200) constraint series_pattern_nn not null,
	pattern_field_formats varchar2(200),
	start_with varchar2(200) constraint series_start_nn not null,
	max_value varchar2(200),
	current_value varchar2(200),
	extra_options varchar2(200),	
	constraint pk_series primary key (id),
	constraint uk_series_name unique (name,organizationid)
);

-- process archives
create table activity_archive (
        userid          varchar2(100),
        flowid          int constraint activityarch_flowid_nn not null,
        pid             int constraint activityarch_pid_nn not null,
        subpid          int default 1,
        type            int,
        priority        int,
        created         date default sysdate,
        started         date default sysdate,
        archived        date default sysdate,
        description     varchar2(256),
        url             varchar2(256),
        status          int,
        notify          number(1),
        delegated       number(1) default 0,
        delegateduserid varchar2(100),
        profilename     VARCHAR2(256)
);

create table modification_archive (
    flowid          int,
    pid             int,
    subpid          int default 1,
    mid             int,
    mdate           date default sysdate,
    muser           varchar2(256),
    constraint modification_archive_pk primary key (flowid, pid, subpid, mid)
);

create table process_archive (
    flowid      int,
    pid         int,
    subpid      int default 1,
    pnumber     varchar2(128) constraint processarch_pnumber_nn not null,
    mid         int constraint processarch_mid_nn not null,
	creator	    varchar2(100) constraint processarch_cr_nn not null,
    created     date default sysdate,
	currentuser	varchar2(100) constraint processarch_cu_nn not null,
    lastupdate  date default sysdate,
    procdata    CLOB NULL,
    closed      number(1) default 0,
    archived    date constraint processarch_archived_nn not null,
	idx0        varchar2(1024),
	idx1        varchar2(1024),
	idx2        varchar2(1024),
	idx3        varchar2(1024),
	idx4        varchar2(1024),
	idx5        varchar2(1024),
	idx6        varchar2(1024),
	idx7        varchar2(1024),
	idx8        varchar2(1024),
	idx9        varchar2(1024),
	idx10       varchar2(1024),
	idx11       varchar2(1024),
	idx12       varchar2(1024),
	idx13       varchar2(1024),
	idx14       varchar2(1024),
	idx15       varchar2(1024),
	idx16       varchar2(1024),
	idx17       varchar2(1024),
	idx18       varchar2(1024),
	idx19       varchar2(1024),
    constraint process_archive_pk primary key (flowid, pid, subpid)
);

create table migration_log (
  migrator VARCHAR2(16),
  task VARCHAR2(128) NOT NULL,
  finished date,
  constraint uk_migration_log UNIQUE (migrator, task)
);

create table reporting (
  flowid int,
  pid int,
  subpid int default 1,
  cod_reporting varchar2(1024),
  start_reporting date,
  stop_reporting date,
  ttl date,
  active number(1) default 0
);

create table log (
  log_id int not null,
  log varchar2(2048) default '' constraint log_nn not null,
  username varchar2(50),
  caller varchar2(16),
  method varchar2(16),
  creation_date date not null,
  constraint log_id_pk primary key (log_id)
);

create table flow_state_log (
  flowid int not null,
  pid int not null,
  subpid int DEFAULT 1 constraint flow_state_log_nn not null ,
  state int not null,
  log_id int not null,
  constraint fk_log_id foreign key (log_id)
    references log (log_id)
    on delete cascade
);

create table external_dms (
  dmsid   int not null,
  docid   int not null,
  uuid    varchar2(64) not null,
  scheme  varchar2(64),
  address varchar2(64),
  path    varchar2(64),
  constraint dmsid_pk primary key (dmsid),
  constraint fk_docid foreign key (docid)
    references documents (docid)
    on delete cascade,
  constraint un_external_dms_uuid unique (uuid)
);

create table external_dms_properties (
  dmsid int not null,
  name  varchar2(64),
  value varchar2(1024),
  constraint fk_dmsid foreign key (dmsid)
    references external_dms (dmsid)
    on delete cascade
);

CREATE TABLE UPGRADE_LOG (
  signature	VARCHAR2(125)	NOT NULL,
  executed	NUMBER(1)		DEFAULT 0 NOT NULL,
  error		NUMBER(1)		DEFAULT 0 NOT NULL,
  log_id	NUMBER(11)		NOT NULL,
  PRIMARY KEY (signature),
  FOREIGN KEY (log_id) REFERENCES log(log_id)
);

CREATE TABLE organizations_tabs (
	organizationid NUMBER(38) NOT NULL,
	tabid NUMBER(11) NOT NULL,
  constraint fk_tab_orgid foreign key (organizationid)
    references organizations (organizationid)
    on delete cascade
);

CREATE TABLE profiles_tabs (
  profileid NUMBER(38) NOT NULL,
  tabid NUMBER(11) NOT NULL,
  constraint fk_tab_profiles foreign key (profileid)
    references profiles (profileid)
    on delete cascade
);

alter table flow_roles add constraint flow_roles_flow_fk foreign key (flowid) references flow (flowid);
alter table flow_state add constraint flow_state_flow_fk foreign key (flowid) references flow (flowid);
alter table flow_settings add constraint flow_settings_flow_fk foreign key (flowid) references flow (flowid);
alter table process add constraint process_flow_fk foreign key (flowid) references flow (flowid);
alter table process_history add constraint process_history_flow_fk foreign key (flowid) references flow (flowid);
-- alter table modification add constraint modification_process_fk foreign key (flowid,pid,subpid) references process_history (flowid,pid,subpid);
alter table activity add constraint activity_process_fk foreign key (flowid,pid,subpid) references process (flowid,pid,subpid);
-- alter table activity_history add constraint activity_history_process_fk foreign key (flowid,pid,subpid) references process_history (flowid,pid,subpid);
alter table activity_hierarchy add constraint activity_hierarchy_uk unique (flowid,ownerid,userid);
alter table flow_state_history add constraint flow_state_history_flow_fk foreign key (flowid) references flow (flowid);
alter table flow_settings_history add constraint flow_settings_history_flow_fk foreign key (flowid) references flow (flowid);
alter table queue_data add constraint queue_data_queue_proc_fk foreign key (queue_proc_id) references queue_proc (id);
alter table event_data add constraint event_data_flow_fk foreign key (fid) references flow (flowid);
alter table event_data add constraint event_data_process_fk foreign key (fid,pid,subpid) references process (flowid,pid,subpid);
alter table forkjoin_blocks add constraint fj_blocks_flow_fk foreign key (flowid) references flow (flowid);
alter table forkjoin_mines add constraint fj_mines_flow_fk foreign key (flowid) references flow (flowid);
alter table forkjoin_hierarchy add constraint fj_hierarchy_blocks1_fk foreign key (flowid,parentblockid) references forkjoin_blocks (flowid,blockid);
alter table forkjoin_hierarchy add constraint fj_hierarchy_blocks2_fk foreign key (flowid,blockid) references forkjoin_blocks (flowid,blockid);
alter table forkjoin_state_dep add constraint fj_state_dep_blocks_fk foreign key (flowid,parentblockid) references forkjoin_blocks (flowid,blockid);

ALTER TABLE ORGANIZATIONAL_UNITS ADD CONSTRAINT FK_UNIT_ORGANIZATION FOREIGN KEY
(
ORGANIZATIONID
)
REFERENCES ORGANIZATIONS(
ORGANIZATIONID
)
ENABLE;

ALTER TABLE USERS ADD CONSTRAINT CK_GENDER CHECK (GENDER IN ('M','F')) ENABLE;
ALTER TABLE  USERS ADD CONSTRAINT FK_USERS_ORG_UNIT FOREIGN KEY
(
UNITID
)
REFERENCES ORGANIZATIONAL_UNITS(
UNITID
)
ENABLE;

ALTER TABLE  USERPROFILES ADD CONSTRAINT FK_USERPROFILES_USER FOREIGN KEY
(
USERID
)
REFERENCES USERS(
USERID
)
ENABLE
ADD CONSTRAINT FK_USERPROFILES_PROFILE FOREIGN KEY
(
PROFILEID
)
REFERENCES PROFILES(
PROFILEID
)
ENABLE;

ALTER TABLE APPLICATIONPROFILES ADD CONSTRAINT FK_APPPROFILES_PROFILE FOREIGN KEY
(
APPID
)
REFERENCES APPLICATIONS(
APPID
)
ENABLE
ADD CONSTRAINT FK_APPPROFILES_APPLICATION FOREIGN KEY
(
PROFILEID
)
REFERENCES PROFILES(
PROFILEID
)
ENABLE;


ALTER TABLE UNITMANAGERS ADD CONSTRAINT FK_UNITMANAGERS_USER FOREIGN KEY
(
USERID
)
REFERENCES USERS(
USERID
)
ENABLE
ADD CONSTRAINT FK_UNITMANAGERS_UNIT FOREIGN KEY
(
UNITID
)
REFERENCES ORGANIZATIONAL_UNITS(
UNITID
)
ENABLE;

--
-- indices
--
create index IND_FLOW on FLOW(ENABLED);
create index IND_QUEUE_PROC on QUEUE_PROC(ID,OBJECT,GROUPID);

create index IND_FLOW_STATE on FLOW_STATE(STATE);
create index IND_FLOW_STATE_HISTORY on FLOW_STATE_HISTORY(MDATE,RESULT,MID,EXIT_FLAG);
create index IND_FLOW_STATE_HISTORY2 on FLOW_STATE_HISTORY(FLOWID, PID, SUBPID, MID);

create index IND_PROCESS on PROCESS(CREATED);


ALTER TABLE user_activation
  ADD CONSTRAINT fk_user_activation_org FOREIGN KEY (organizationid)
    REFERENCES organizations (organizationid) ENABLE
  ADD CONSTRAINT fk_user_activation_orgunit FOREIGN KEY (unitid)
    REFERENCES organizational_units (unitid) ENABLE
  ADD CONSTRAINT fk_user_activation_user FOREIGN KEY (userid)
    REFERENCES users (userid) ENABLE;

--ALTER TABLE users
--  ADD CONSTRAINT fk_user_organization FOREIGN KEY (organizationid)
--    REFERENCES organizations (organizationid) ENABLE;
 

-- user notification table constraints
ALTER TABLE user_notifications
  ADD CONSTRAINT fk_user_notifications FOREIGN KEY (notificationid)
    REFERENCES notifications (id)
    ON DELETE CASCADE
  ENABLE;

-- Process index
create index IND_PROCESS_IDX0 on PROCESS(IDX0);
create index IND_PROCESS_IDX1 on PROCESS(IDX1);
create index IND_PROCESS_IDX2 on PROCESS(IDX2);
create index IND_PROCESS_IDX3 on PROCESS(IDX3);
create index IND_PROCESS_IDX4 on PROCESS(IDX4);
create index IND_PROCESS_IDX5 on PROCESS(IDX5);
create index IND_PROCESS_IDX6 on PROCESS(IDX6);
create index IND_PROCESS_IDX7 on PROCESS(IDX7);
create index IND_PROCESS_IDX8 on PROCESS(IDX8);
create index IND_PROCESS_IDX9 on PROCESS(IDX9);
create index IND_PROCESS_IDX10 on PROCESS(IDX10);
create index IND_PROCESS_IDX11 on PROCESS(IDX11);
create index IND_PROCESS_IDX12 on PROCESS(IDX12);
create index IND_PROCESS_IDX13 on PROCESS(IDX13);
create index IND_PROCESS_IDX14 on PROCESS(IDX14);
create index IND_PROCESS_IDX15 on PROCESS(IDX15);
create index IND_PROCESS_IDX16 on PROCESS(IDX16);
create index IND_PROCESS_IDX17 on PROCESS(IDX17);
create index IND_PROCESS_IDX18 on PROCESS(IDX18);
create index IND_PROCESS_IDX19 on PROCESS(IDX19);

create index IND_PROCESS_HISTORY_IDX0 on PROCESS_HISTORY(IDX0);
create index IND_PROCESS_HISTORY_IDX1 on PROCESS_HISTORY(IDX1);
create index IND_PROCESS_HISTORY_IDX2 on PROCESS_HISTORY(IDX2);
create index IND_PROCESS_HISTORY_IDX3 on PROCESS_HISTORY(IDX3);
create index IND_PROCESS_HISTORY_IDX4 on PROCESS_HISTORY(IDX4);
create index IND_PROCESS_HISTORY_IDX5 on PROCESS_HISTORY(IDX5);
create index IND_PROCESS_HISTORY_IDX6 on PROCESS_HISTORY(IDX6);
create index IND_PROCESS_HISTORY_IDX7 on PROCESS_HISTORY(IDX7);
create index IND_PROCESS_HISTORY_IDX8 on PROCESS_HISTORY(IDX8);
create index IND_PROCESS_HISTORY_IDX9 on PROCESS_HISTORY(IDX9);
create index IND_PROCESS_HISTORY_IDX10 on PROCESS_HISTORY(IDX10);
create index IND_PROCESS_HISTORY_IDX11 on PROCESS_HISTORY(IDX11);
create index IND_PROCESS_HISTORY_IDX12 on PROCESS_HISTORY(IDX12);
create index IND_PROCESS_HISTORY_IDX13 on PROCESS_HISTORY(IDX13);
create index IND_PROCESS_HISTORY_IDX14 on PROCESS_HISTORY(IDX14);
create index IND_PROCESS_HISTORY_IDX15 on PROCESS_HISTORY(IDX15);
create index IND_PROCESS_HISTORY_IDX16 on PROCESS_HISTORY(IDX16);
create index IND_PROCESS_HISTORY_IDX17 on PROCESS_HISTORY(IDX17);
create index IND_PROCESS_HISTORY_IDX18 on PROCESS_HISTORY(IDX18);
create index IND_PROCESS_HISTORY_IDX19 on PROCESS_HISTORY(IDX19);


CREATE OR REPLACE TRIGGER TRIG_INSERT_ORGANIZATION
BEFORE INSERT  ON ORGANIZATIONS
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
SELECT SEQ_ORGANIZATION.NEXTVAL INTO :NEW.ORGANIZATIONID FROM DUAL;
END;
/


CREATE OR REPLACE TRIGGER TRIG_INSERT_ORGANIZATIONALUNIT
BEFORE INSERT  ON ORGANIZATIONAL_UNITS
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
SELECT SEQ_ORGANIZATIONAL_UNIT.NEXTVAL INTO :NEW.UNITID FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER TRIG_INSERT_PROFILE
BEFORE INSERT  ON PROFILES
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
SELECT SEQ_PROFILES.NEXTVAL INTO :NEW.PROFILEID FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER TRIG_INSERT_USER
BEFORE INSERT  ON USERS
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
SELECT SEQ_USERS.NEXTVAL INTO :NEW.USERID FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER TRIG_INSERT_SERIES
BEFORE INSERT ON SERIES
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
SELECT SEQ_SERIES.NEXTVAL INTO :NEW.ID FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER TRIG_UPDATE_SERIES
BEFORE UPDATE ON SERIES
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
IF :NEW.STATE < :OLD.STATE THEN
	raise_application_error (-20999,'Invalid state');
END IF;
END;
/
--
-- @view  activity tabela que contem as actividades delegadas a outros
-- @field id identificador da hierarquia de delegacoes
-- @field userid identificador alfanumerico do utilizador
-- @field pid identificador numerico do processo
-- @field ownerid identificador alfanumerico do utilizador a quem a actividade pertence
-- @field flowid identificador numerico do fluxo
create view activity_delegated
    (hierarchyid, userid, pid, subpid, ownerid, flowid, created, type, started, archived, 
    status, notify, priority, description, url, profilename, requested, responded, read_flag,mid) as
    select H.hierarchyid, H.userid, A.pid, A.subpid, A.userid as ownerid, A.flowid, A.created, 
    A.type, A.started, A.archived, A.status, A.notify, A.priority, A.description, A.url, A.profilename,
    H.requested, H.responded, A.read_flag,A.mid
    from activity A, activity_hierarchy H
    where ((A.userid = H.ownerid and H.slave=1) or (A.userid = H.userid and slave=0)) 
    and A.flowid = H.flowid and H.pending=0 and A.delegated <> 0;
-- Tarefas no topo da hierarquia --
-- WHERE A.userid = H.ownerid and H.slave=1 and A.flowid = H.flowid and H.pending=0 and A.delegated = 1;
-- Tarefas na base da hierarquia -- Nao e preciso estar delegado...
-- Tarefas no meio da hierarquia --
-- WHERE  and H.slave=1 and A.flowid = H.flowid and H.pending=0 and A.delegated = 1;
-- updates a given flow settings.
-- if setting does not exist, it creates it.
-- if it exists, it updates it.
-- if it exists and settingtype=1 and setting value is null, then it deletes setting.
-- in all changing cases, the changing is saved in setting historic table.
CREATE OR REPLACE PROCEDURE updateFlowSetting (aflowid IN NUMBER,
                                               amid IN NUMBER,
                                               aname IN VARCHAR2,
                                               adescription IN VARCHAR2,
                                               avalue IN VARCHAR2,
                                               aisquery IN NUMBER,
                                               asettingtype IN NUMBER) IS
  tmp number;
  process number := 0;
  nowdate DATE := SYSDATE;
BEGIN

	select count(1) into tmp from flow_settings where flowid=aflowid and name=aname;

    IF tmp = 0 THEN
      insert into flow_settings (flowid,name,description,value,isquery,mdate) values (aflowid,aname,adescription,avalue,aisquery,nowdate);
      process := 1;
      
    ELSIF asettingtype = 0 THEN
      update flow_settings set value=avalue,mdate=nowdate where flowid=aflowid and name=aname;
      
    ELSIF asettingtype = 1 AND avalue IS NOT NULL THEN
      update flow_settings set value=avalue,mdate=nowdate where flowid=aflowid and name=aname;
      process := 1;
      
    ELSIF asettingtype = 1 AND avalue IS NULL THEN
      delete from flow_settings where flowid=aflowid and name=aname;
      process := 1;
      
    ELSIF asettingtype = 2 THEN
      update flow_settings set description=adescription,mdate=nowdate where flowid=aflowid and name=aname;
      process := 1;
      
    ELSE
      process := 0;
      
    END IF;

    -- now update value and historify. process will only be 0 in the case of
    -- no prev value and null curr value
    IF process = 1 THEN
      insert into flow_settings_history (flowid,name,description,value,mdate,mid) values (aflowid,aname,adescription,avalue,nowdate,amid);
    END IF;
END;
/

-- gets next available pid (from counter table) and updates counter table for a given flow.
CREATE OR REPLACE PROCEDURE get_next_pid (retpid OUT NUMBER,
                                          retsubpid OUT NUMBER,
                                          aflowid IN NUMBER,
                                          acreatedate IN DATE,
                                          acreator IN VARCHAR2) IS
BEGIN
  BEGIN
    select value into retpid from counter where name='pid';
    retpid := retpid + 1;
    update counter set value=retpid where name='pid';
     retsubpid := 1;
    insert into process (flowid,pid,subpid,mid,created,creator,pnumber,currentuser) values
        (aflowid,retpid,retsubpid,1,acreatedate,acreator,retpid,acreator);
     insert into process_history (flowid,pid,subpid,mid,created,creator,pnumber,currentuser) values
        (aflowid,retpid,retsubpid,1,acreatedate,acreator,retpid,acreator);
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      retpid := 0;
      retsubpid := 0;
  END;
END;
/

-- gets next available pid (from counter table) and updates counter table for a given flow.
CREATE OR REPLACE PROCEDURE get_next_sub_pid (retsubpid OUT NUMBER,
                                          aflowid IN NUMBER,
                                          apid IN NUMBER,
                                          acreatedate IN DATE,
                                          acreator VARCHAR2,
							   	          acreatorsubpid INTEGER) IS
    midtmp number;
    tmp number;
    apnumber varchar2(128);
BEGIN
  midtmp := 1;
  select count(subpid) into tmp from process_history where flowid=aflowid and pid=apid;
  IF tmp > 0 THEN
    select max(subpid) into retsubpid from process_history where flowid=aflowid and pid=apid;
    select mid,pnumber into midtmp,apnumber from process where flowid=aflowid and pid=apid and subpid=acreatorsubpid;
    retsubpid := retsubpid + 1;
  ELSE
    retsubpid := 1;
    midtmp := 0;    
  END IF;
  insert into process (flowid,pid,subpid,mid,created,creator,currentuser,lastupdate,pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,acreator,acreator,acreatedate,apnumber);
  insert into process_history (flowid,pid,subpid,mid,created,creator,currentuser,lastupdate,pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,acreator,acreator,acreatedate,apnumber);
END;
/


-- gets next available mid for a given process and updates modification table.
CREATE OR REPLACE PROCEDURE GET_NEXT_MID (retmid OUT NUMBER,
                                          auserid IN VARCHAR2,
                                          aflowid IN NUMBER,
                                          apid IN NUMBER,
                                          asubpid IN NUMBER) IS
  tmp number;
BEGIN
  retmid := 1;
  select count(mid) into tmp from modification where subpid=asubpid and pid=apid and flowid=aflowid;
  IF tmp > 0 THEN
    select max(mid)+1 into retmid from modification where subpid=asubpid and pid=apid and flowid=aflowid;
  END IF;
  insert into modification (flowid,pid,subpid,mid,mdate,muser) values (aflowid,apid,asubpid,retmid,SYSDATE,auserid);
END;
/

-- deletes all associated data for the given process
CREATE OR REPLACE PROCEDURE deleteProc (aflowid IN NUMBER, apid IN NUMBER) IS
BEGIN

  DBMS_OUTPUT.ENABLE(100000);

  DBMS_OUTPUT.PUT('  START deleteProc: pid ' || apid || '.');

  delete from activity where flowid=aflowid and pid=apid;
  DBMS_OUTPUT.PUT('.');
  delete from activity_history where flowid=aflowid and pid=apid;
  DBMS_OUTPUT.PUT('.');

  delete from modification where flowid=aflowid and pid=apid;
  DBMS_OUTPUT.PUT('.');

  delete from process where flowid=aflowid and pid=apid;
  DBMS_OUTPUT.PUT('.');
  delete from process_history where flowid=aflowid and pid=apid;
  DBMS_OUTPUT.PUT('.');


  delete from flow_state where flowid=aflowid and pid=apid;
  DBMS_OUTPUT.PUT('.');
  delete from flow_state_history where flowid=aflowid and pid=apid;
  DBMS_OUTPUT.PUT('.');

  DBMS_OUTPUT.PUT_LINE('DONE deleteProc');
END;
/

-- move a process to archive tables
CREATE OR REPLACE PROCEDURE archiveProc (archiveResult OUT NUMBER, aflowid IN NUMBER,apid IN NUMBER,aarchivedate IN DATE) IS
    open number;
PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN
  archiveResult := 0;
  BEGIN
    -- check if process (or any of its subprocesses) is still open
    select count(1) into open from process where flowid=aflowid and pid=apid and closed=0;
    DBMS_OUTPUT.PUT_LINE('Is Process Open: '||to_char(open)) ;
    IF open = 0 THEN
      -- so far so nice, copy to archive
      insert into process_archive (flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,archived,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19) 
        select flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,aarchivedate,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19
              from process_history where flowid=aflowid and pid=apid;
      DBMS_OUTPUT.PUT_LINE('process history for '|| to_char(apid) || ' archived') ;

      insert into activity_archive (userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename)
        select userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename from activity_history where flowid=aflowid and pid=apid;
      DBMS_OUTPUT.PUT_LINE('activities archived') ;

      insert into modification_archive (flowid,pid,subpid,mid,mdate,muser)
        select flowid,pid,subpid,mid,mdate,muser from modification where flowid=aflowid and pid=apid;
      DBMS_OUTPUT.PUT_LINE('modification register archived') ;

      -- finally, delete from history
      delete from modification where flowid=aflowid and pid=apid;
      delete from activity_history where flowid=aflowid and pid=apid;
      delete from process_history where flowid=aflowid and pid=apid;
      delete from process where flowid=aflowid and pid=apid;
      DBMS_OUTPUT.PUT_LINE('Registers removed') ;

      archiveResult := 1;
    END IF;
    COMMIT;
  EXCEPTION WHEN OTHERS THEN
    ROLLBACK;
    archiveResult := -1;
    DBMS_OUTPUT.PUT_LINE('Exception caught archiving process '||to_char(apid));
  END;
END;
/

-- deletes all flow associated data.
-- if delprocs = 1, deletes everything (data, states, settings, flow..)
-- if delprocs = 0, only moves opened procs to historic tables (and keep them marked as not closed)
CREATE OR REPLACE PROCEDURE deleteFlow (auserid IN VARCHAR2,
                                        aflowid IN NUMBER,
                                        delprocs IN NUMBER) IS
  cursor COPEN is SELECT distinct(PID) as dpid, subpid FROM process where flowid=aflowid;
  cursor CHIST is SELECT distinct(PID) as dpid FROM process_history where flowid=aflowid;
  tmp number;
  error number := 0;
  exc EXCEPTION;
BEGIN

  DBMS_OUTPUT.ENABLE(100000);

  DBMS_OUTPUT.PUT_LINE('START deleteFlow');

  -- first handle procs
  if (delprocs = 1) then
    -- delete all flow procs (including historic ones)
    for rec in COPEN loop
      deleteProc(aflowid,rec.dpid);
    end loop;
    for rec in CHIST loop
      deleteProc(aflowid,rec.dpid);
    end loop;

  else
    -- move opened procs to historic tables
    -- move scheduled activities to historic table
    -- move history to archive
    for rec in COPEN loop
      GET_NEXT_MID (tmp,auserid,aflowid,rec.dpid,rec.subpid);
      update process set mid=tmp where subpid=rec.subpid and pid=rec.dpid and flowid=aflowid;
      update flow_state set mid=tmp where subpid=rec.subpid and pid=rec.dpid and flowid=aflowid;

        insert into process_history (flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19,undoflag) 
            (select flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19,0
                from process where flowid=aflowid and pid=rec.dpid and subpid=rec.subpid);
        update process set closed=1 where flowid=aflowid and pid=rec.dpid and subpid=rec.subpid;

        insert into activity_history (userid,flowid,pid,subpid,type,priority,created,started,archived,
            description,url,status,notify,delegated,delegateduserid)
            (SELECT userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,userid 
                from activity where flowid=aflowid and pid=rec.dpid and subpid=rec.subpid);
        delete from activity where flowid=aflowid and pid=rec.dpid and subpid=rec.subpid;

      DBMS_OUTPUT.PUT_LINE('MOVED PROC ' || to_char(rec.dpid));

    end loop;
    for rec in COPEN loop
      archiveProc(tmp,aflowid,rec.dpid,SYSDATE);
      DBMS_OUTPUT.PUT_LINE('    Process archived ' || to_char(rec.dpid));
    end loop;
  end if;

  -- delete all flow related stuff
  delete from flow_settings where flowid=aflowid;
  DBMS_OUTPUT.PUT_LINE('  deleted settings');
  delete from flow_settings_history where flowid=aflowid;
  DBMS_OUTPUT.PUT_LINE('  deleted settings history');
  delete from flow_roles where flowid=aflowid;
  DBMS_OUTPUT.PUT_LINE('  deleted roles');
  delete from flow where flowid=aflowid;
  DBMS_OUTPUT.PUT_LINE('  deleted flow');

  if (error != 0) then
    RAISE exc;
  end if;

  DBMS_OUTPUT.PUT_LINE('END deleteFlow');
END;
/

-- Extract a variable value from a process XML
CREATE OR REPLACE FUNCTION get_procdata_value (procdata IN CLOB, varname IN VARCHAR2)
  RETURN varchar2 IS
  xval XMLTYPE;
  xml XMLTYPE := XMLTYPE(procdata);
BEGIN
    xval := xml.extract('/processdata/a[@n="'||varname||'"]/text()', 'xmlns="http://www.iflow.pt/ProcessData"');
    RETURN CASE when xval is null then null else xval.getStringVal() end;
END;
/
insert into counter values ('pid',0,SYSDATE);
insert into counter values ('docid',1,SYSDATE);
insert into counter values ('emailid',1,SYSDATE);
insert into counter values ('cid',1,SYSDATE);
insert into counter values ('flowid',0,SYSDATE);

insert into profiles (name,description) values ('Admin','Administrador');
insert into profiles (name,description) values ('Manager','Organic Unit Manager');
insert into profiles (name,description) values ('Worker','Company Employee');

drop sequence seq_organization;
CREATE SEQUENCE SEQ_ORGANIZATION START WITH 1 INCREMENT BY 1 MINVALUE 1 NOMAXVALUE CACHE 20 NOCYCLE ORDER;
insert into organizations (name,description) values ('SYS','SYS ORG');

insert into organizational_units (organizationid,parent_id,name,description) 
values((select organizationid from organizations where name='SYS') ,-1,'SYS','SYS');


-- USERS

-- admin admin
-- <user> password
INSERT INTO SYSTEM_USERS (userid, username, userpassword, email_address)
VALUES (1, 'admin', 'F/KbuDOEofgjp7/9yUGnrw==', 'change@this.address');

--insert into userprofiles (userid,profileid) values ((select userid from users where username='admin'),(select profileid from profiles where name='Admin'));
--insert into unitmanagers values ((select userid from users where username='admin'),(select UNITID from organizational_units where NAME='SYS'));
--insert into organization_theme (organizationid, theme, style_url, logo_url) values ('SYS', 'default','iflow.css','/iFlow/images/iflow.gif');

-- Event info
insert into event_info (name,description) values ('AsyncWait','description=Processo fica bloqueado num bloco NOP ''a espera de de um evento externo');
insert into event_info (name,description) values ('Timer','description=Timer para reencaminhar para ...;workingdays=true;minutes=10');
insert into event_info (name,description) values ('Deadline', 'dateVar=date');

----------------------
--    START QRTZ    --
----------------------
CREATE TABLE qrtz_job_details
  (
    JOB_NAME  VARCHAR2(200) NOT NULL,
    JOB_GROUP VARCHAR2(200) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    JOB_CLASS_NAME   VARCHAR2(250) NOT NULL, 
    IS_DURABLE VARCHAR2(1) NOT NULL,
    IS_VOLATILE VARCHAR2(1) NOT NULL,
    IS_STATEFUL VARCHAR2(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP)
);
CREATE TABLE qrtz_job_listeners
  (
    JOB_NAME  VARCHAR2(200) NOT NULL, 
    JOB_GROUP VARCHAR2(200) NOT NULL,
    JOB_LISTENER VARCHAR2(200) NOT NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),
    FOREIGN KEY (JOB_NAME,JOB_GROUP) 
	REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);
CREATE TABLE qrtz_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    JOB_NAME  VARCHAR2(200) NOT NULL, 
    JOB_GROUP VARCHAR2(200) NOT NULL,
    IS_VOLATILE VARCHAR2(1) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    NEXT_FIRE_TIME NUMBER(13) NULL,
    PREV_FIRE_TIME NUMBER(13) NULL,
    PRIORITY NUMBER(13) NULL,
    TRIGGER_STATE VARCHAR2(16) NOT NULL,
    TRIGGER_TYPE VARCHAR2(8) NOT NULL,
    START_TIME NUMBER(13) NOT NULL,
    END_TIME NUMBER(13) NULL,
    CALENDAR_NAME VARCHAR2(200) NULL,
    MISFIRE_INSTR NUMBER(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (JOB_NAME,JOB_GROUP) 
	REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP) 
);
CREATE TABLE qrtz_simple_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    REPEAT_COUNT NUMBER(7) NOT NULL,
    REPEAT_INTERVAL NUMBER(12) NOT NULL,
    TIMES_TRIGGERED NUMBER(10) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_cron_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    CRON_EXPRESSION VARCHAR2(120) NOT NULL,
    TIME_ZONE_ID VARCHAR2(80),
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_blob_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
        REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_trigger_listeners
  (
    TRIGGER_NAME  VARCHAR2(200) NOT NULL, 
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    TRIGGER_LISTENER VARCHAR2(200) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_calendars
  (
    CALENDAR_NAME  VARCHAR2(200) NOT NULL, 
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (CALENDAR_NAME)
);
CREATE TABLE qrtz_paused_trigger_grps
  (
    TRIGGER_GROUP  VARCHAR2(200) NOT NULL, 
    PRIMARY KEY (TRIGGER_GROUP)
);
CREATE TABLE qrtz_fired_triggers 
  (
    ENTRY_ID VARCHAR2(95) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    IS_VOLATILE VARCHAR2(1) NOT NULL,
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    FIRED_TIME NUMBER(13) NOT NULL,
    PRIORITY NUMBER(13) NOT NULL,
    STATE VARCHAR2(16) NOT NULL,
    JOB_NAME VARCHAR2(200) NULL,
    JOB_GROUP VARCHAR2(200) NULL,
    IS_STATEFUL VARCHAR2(1) NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NULL,
    PRIMARY KEY (ENTRY_ID)
);
CREATE TABLE qrtz_scheduler_state 
  (
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    LAST_CHECKIN_TIME NUMBER(13) NOT NULL,
    CHECKIN_INTERVAL NUMBER(13) NOT NULL,
    PRIMARY KEY (INSTANCE_NAME)
);
CREATE TABLE qrtz_locks
  (
    LOCK_NAME  VARCHAR2(40) NOT NULL, 
    PRIMARY KEY (LOCK_NAME)
);
INSERT INTO qrtz_locks values('TRIGGER_ACCESS');
INSERT INTO qrtz_locks values('JOB_ACCESS');
INSERT INTO qrtz_locks values('CALENDAR_ACCESS');
INSERT INTO qrtz_locks values('STATE_ACCESS');
INSERT INTO qrtz_locks values('MISFIRE_ACCESS');
create index idx_qrtz_j_req_recovery on qrtz_job_details(REQUESTS_RECOVERY);
create index idx_qrtz_t_next_fire_time on qrtz_triggers(NEXT_FIRE_TIME);
create index idx_qrtz_t_state on qrtz_triggers(TRIGGER_STATE);
create index idx_qrtz_t_nft_st on qrtz_triggers(NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_qrtz_t_volatile on qrtz_triggers(IS_VOLATILE);
create index idx_qrtz_ft_trig_name on qrtz_fired_triggers(TRIGGER_NAME);
create index idx_qrtz_ft_trig_group on qrtz_fired_triggers(TRIGGER_GROUP);
create index idx_qrtz_ft_trig_nm_gp on qrtz_fired_triggers(TRIGGER_NAME,TRIGGER_GROUP);
create index idx_qrtz_ft_trig_volatile on qrtz_fired_triggers(IS_VOLATILE);
create index idx_qrtz_ft_trig_inst_name on qrtz_fired_triggers(INSTANCE_NAME);
create index idx_qrtz_ft_job_name on qrtz_fired_triggers(JOB_NAME);
create index idx_qrtz_ft_job_group on qrtz_fired_triggers(JOB_GROUP);
create index idx_qrtz_ft_job_stateful on qrtz_fired_triggers(IS_STATEFUL);
create index idx_qrtz_ft_job_req_recovery on qrtz_fired_triggers(REQUESTS_RECOVERY);
----------------------
--     END  QRTZ    --
----------------------

CREATE TABLE hotfolder_files (
	entryid int NOT NULL,
	path VARCHAR2(1000) NOT NULL,
	flowid int not null,
	in_user varchar2(100) not null,
  	entry_date timestamp not null,
  	processed_path varchar2(1000),
  	created_pid int,
  	PRIMARY KEY (entryid)
);
create sequence SEQ_HOTFOLDER increment by 1 start with 1 nocycle order;

CREATE OR REPLACE TRIGGER TRIG_INSERT_HOTFOLDER
BEFORE INSERT  ON hotfolder_files
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
SELECT SEQ_HOTFOLDER.NEXTVAL INTO :NEW.entryid FROM DUAL;
END;
/


-- Add pid index to flow_state_history
create index idx_fsh_pid on flow_state_history(pid);

create sequence SEQ_FLOW_STATE_LOG_ID increment by 1 start with 1 nocycle order;

create table user_passimage (
        passid                int,
        userid                int constraint userid_nn not null,
        passimage             blob,
        rubimage             blob,
        constraint passid_pk primary key (passid)
        constraint fk_userid foreign key (userid)
        references users (userid)
        on delete cascade,
);
create sequence seq_passid increment by 1 start with 1 nocycle order;

alter table activity_history add INDEX IND_ACTIVITY_HISTORY_PID (pid);

create view process_intervenients (userid, pid) as
    select distinct userid, pid from activity
    union 
    select distinct userid, pid from activity_history;
    
alter table users add column department varchar(50);
alter table users add column employeeid varchar(50);
alter table users add column manager varchar(50);
alter table users add column telephonenumber varchar(50);
alter table users add column title varchar(50);

alter table users add column orgadm_users NUMBER(1)  NOT NULL DEFAULT 1,
alter table users add column orgadm_flows NUMBER(1)  NOT NULL DEFAULT 1,
alter table users add column orgadm_processes NUMBER(1)  NOT NULL DEFAULT 1,
alter table users add column orgadm_resources NUMBER(1)  NOT NULL DEFAULT 1,
alter table users add column orgadm_org NUMBER(1)  NOT NULL DEFAULT 1;

commit;

