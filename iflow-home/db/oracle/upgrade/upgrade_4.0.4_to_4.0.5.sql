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

insert into flow_settings (flowid,name,description,value) (select flowid, 'HOT_FOLDER_ONOFF' as name,'HotFolder' as description,null as value from flow_settings group by flowid);
insert into flow_settings (flowid,name,description,value) (select flowid, 'HOT_FOLDER_FOLDERS' as name,'Pastas onde pesquisar ficheiros novos' as description,null as value from flow_settings group by flowid);
insert into flow_settings (flowid,name,description,value) (select flowid, 'HOT_FOLDER_DEPTH' as name,'Profundidade da pesquisa' as description,null as value from flow_settings group by flowid);
insert into flow_settings (flowid,name,description,value) (select flowid, 'HOT_FOLDER_DOCVAR' as name,'Variável para ficheiro encontrado' as description,null as value from flow_settings group by flowid);
insert into flow_settings (flowid,name,description,value) (select flowid, 'HOT_FOLDER_IN_USER' as name,'O utilizador para criar o processo' as description,null as value from flow_settings group by flowid);
