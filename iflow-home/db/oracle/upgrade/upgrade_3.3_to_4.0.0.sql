alter table user_settings add tutorial varchar2(20) default 'none';
alter table user_settings add help_mode number(1) default 1;
alter table user_settings add tutorial_mode number(1) default 1;
alter table user_settings add tutorial varchar2(20) default 'none';

alter table flow add name_idx5 varchar2(64);
alter table flow add name_idx6 varchar2(64);
alter table flow add name_idx7 varchar2(64);
alter table flow add name_idx8 varchar2(64);
alter table flow add name_idx9 varchar2(64);
alter table flow add name_idx10 varchar2(64);
alter table flow add name_idx11 varchar2(64);
alter table flow add name_idx12 varchar2(64);
alter table flow add name_idx13 varchar2(64);
alter table flow add name_idx14 varchar2(64);
alter table flow add name_idx15 varchar2(64);
alter table flow add name_idx16 varchar2(64);
alter table flow add name_idx17 varchar2(64);
alter table flow add name_idx18 varchar2(64);
alter table flow add name_idx19 varchar2(64);


alter table process add currentuser varchar2(32);
alter table process add lastupdate date default sysdate;
alter table process add procdata XMLTYPE;
alter table process add closed number(1) default 0;  
alter table process add idx5 varchar2(1024);
alter table process add idx6 varchar2(1024);
alter table process add idx7 varchar2(1024);
alter table process add idx8 varchar2(1024);
alter table process add idx9 varchar2(1024);
alter table process add idx10 varchar2(1024);
alter table process add idx11 varchar2(1024);
alter table process add idx12 varchar2(1024);
alter table process add idx13 varchar2(1024);
alter table process add idx14 varchar2(1024);
alter table process add idx15 varchar2(1024);
alter table process add idx16 varchar2(1024);
alter table process add idx17 varchar2(1024);
alter table process add idx18 varchar2(1024);
alter table process add idx19 varchar2(1024);

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


alter table process_history add currentuser varchar2(32);
alter table process_history add lastupdate date default sysdate;
alter table process_history add procdata XMLTYPE;
alter table process_history add undoflag number(1) default 0;  
alter table process_history add idx5 varchar2(1024);
alter table process_history add idx6 varchar2(1024);
alter table process_history add idx7 varchar2(1024);
alter table process_history add idx8 varchar2(1024);
alter table process_history add idx9 varchar2(1024);
alter table process_history add idx10 varchar2(1024);
alter table process_history add idx11 varchar2(1024);
alter table process_history add idx12 varchar2(1024);
alter table process_history add idx13 varchar2(1024);
alter table process_history add idx14 varchar2(1024);
alter table process_history add idx15 varchar2(1024);
alter table process_history add idx16 varchar2(1024);
alter table process_history add idx17 varchar2(1024);
alter table process_history add idx18 varchar2(1024);
alter table process_history add idx19 varchar2(1024);

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


alter table process_history add currentuser varchar2(32);
alter table process_history add lastupdate date default sysdate;
alter table process_history add procdata XMLTYPE;
alter table process_history add undoflag number(1) default 0;  
alter table process_archive add idx5 varchar2(1024);
alter table process_archive add idx6 varchar2(1024);
alter table process_archive add idx7 varchar2(1024);
alter table process_archive add idx8 varchar2(1024);
alter table process_archive add idx9 varchar2(1024);
alter table process_archive add idx10 varchar2(1024);
alter table process_archive add idx11 varchar2(1024);
alter table process_archive add idx12 varchar2(1024);
alter table process_archive add idx13 varchar2(1024);
alter table process_archive add idx14 varchar2(1024);
alter table process_archive add idx15 varchar2(1024);
alter table process_archive add idx16 varchar2(1024);
alter table process_archive add idx17 varchar2(1024);
alter table process_archive add idx18 varchar2(1024);
alter table process_archive add idx19 varchar2(1024);

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
  cod_reporting varchar2(16),
  start_reporting date,
  stop_reporting date,
  ttl date,
  notify number(1) default 0
);


alter table flow_roles add profileid int;
update flow_roles r, profiles p set r.profileid=p.profileid where r.perfil like p.name;
alter table flow_roles drop primary key;
alter table flow_roles add primary key (profileid, flowid);
alter table flow_roles drop column perfil;

UPDATE series set kind=replace(kind, 'pt.iknow.utils.series', 'pt.iflow.api.utils.series');