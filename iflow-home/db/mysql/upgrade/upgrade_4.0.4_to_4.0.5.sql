-- agon 25/05/2010
CREATE TABLE hotfolder_files (
	entryid INT NOT NULL auto_increment,
	path VARCHAR(1000) NOT NULL,
	flowid int not null,
	in_user varchar(100) not null,
  	entry_date timestamp not null,
  	processed_path varchar(1000),
  	created_pid int,
  	CONSTRAINT PK_HOTF PRIMARY KEY (entryid)
) ENGINE = INNODB DEFAULT CHARSET=utf8;

insert into flow_settings (flowid,name,description,value) (select flowid, 'HOT_FOLDER_ONOFF' as name,'HotFolder' as description,null as value from flow_settings group by flowid);
insert into flow_settings (flowid,name,description,value) (select flowid, 'HOT_FOLDER_FOLDERS' as name,'Pastas onde pesquisar ficheiros novos' as description,null as value from flow_settings group by flowid);
insert into flow_settings (flowid,name,description,value) (select flowid, 'HOT_FOLDER_DEPTH' as name,'Profundidade da pesquisa' as description,null as value from flow_settings group by flowid);
insert into flow_settings (flowid,name,description,value) (select flowid, 'HOT_FOLDER_DOCVAR' as name,'Variável para ficheiro encontrado' as description,null as value from flow_settings group by flowid);
insert into flow_settings (flowid,name,description,value) (select flowid, 'HOT_FOLDER_IN_USER' as name,'O utilizador para criar o processo' as description,null as value from flow_settings group by flowid);
