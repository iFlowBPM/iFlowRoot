alter table user_settings add tutorial varchar(20) default 'none';
alter table user_settings add help_mode int(1) default 1;
alter table user_settings add tutorial_mode int(1) default 1;

alter table flow add name_idx5 varchar(64);
alter table flow add name_idx6 varchar(64);
alter table flow add name_idx7 varchar(64);
alter table flow add name_idx8 varchar(64);
alter table flow add name_idx9 varchar(64);
alter table flow add name_idx10 varchar(64);
alter table flow add name_idx11 varchar(64);
alter table flow add name_idx12 varchar(64);
alter table flow add name_idx13 varchar(64);
alter table flow add name_idx14 varchar(64);
alter table flow add name_idx15 varchar(64);
alter table flow add name_idx16 varchar(64);
alter table flow add name_idx17 varchar(64);
alter table flow add name_idx18 varchar(64);
alter table flow add name_idx19 varchar(64);


alter table process add currentuser VARCHAR(32);
alter table process add lastupdate DATETIME;
alter table process add procdata LONGTEXT CHARACTER SET utf8;
alter table process add closed INT(1) NULL DEFAULT 0;  
alter table process add idx5 varchar(1024);
alter table process add idx6 varchar(1024);
alter table process add idx7 varchar(1024);
alter table process add idx8 varchar(1024);
alter table process add idx9 varchar(1024);
alter table process add idx10 varchar(1024);
alter table process add idx11 varchar(1024);
alter table process add idx12 varchar(1024);
alter table process add idx13 varchar(1024);
alter table process add idx14 varchar(1024);
alter table process add idx15 varchar(1024);
alter table process add idx16 varchar(1024);
alter table process add idx17 varchar(1024);
alter table process add idx18 varchar(1024);
alter table process add idx19 varchar(1024);

alter table process drop INDEX IND_PROCESS_IDX5;
alter table process add INDEX IND_PROCESS_IDX0 (idx0);
alter table process add INDEX IND_PROCESS_IDX5 (idx5);
alter table process add INDEX IND_PROCESS_IDX6 (idx6);
alter table process add INDEX IND_PROCESS_IDX7 (idx7);
alter table process add INDEX IND_PROCESS_IDX8 (idx8);
alter table process add INDEX IND_PROCESS_IDX9 (idx9);
alter table process add INDEX IND_PROCESS_IDX10 (idx10);
alter table process add INDEX IND_PROCESS_IDX11 (idx11);
alter table process add INDEX IND_PROCESS_IDX12 (idx12);
alter table process add INDEX IND_PROCESS_IDX13 (idx13);
alter table process add INDEX IND_PROCESS_IDX14 (idx14);
alter table process add INDEX IND_PROCESS_IDX15 (idx15);
alter table process add INDEX IND_PROCESS_IDX16 (idx16);
alter table process add INDEX IND_PROCESS_IDX17 (idx17);
alter table process add INDEX IND_PROCESS_IDX18 (idx18);
alter table process add INDEX IND_PROCESS_IDX19 (idx19);



alter table process_history add currentuser VARCHAR(32);
alter table process_history add lastupdate DATETIME;
alter table process_history add procdata LONGTEXT CHARACTER SET utf8;
alter table process_history add undoflag INT(1) NULL DEFAULT 0;
alter table process_history add idx5 varchar(1024);
alter table process_history add idx6 varchar(1024);
alter table process_history add idx7 varchar(1024);
alter table process_history add idx8 varchar(1024);
alter table process_history add idx9 varchar(1024);
alter table process_history add idx10 varchar(1024);
alter table process_history add idx11 varchar(1024);
alter table process_history add idx12 varchar(1024);
alter table process_history add idx13 varchar(1024);
alter table process_history add idx14 varchar(1024);
alter table process_history add idx15 varchar(1024);
alter table process_history add idx16 varchar(1024);
alter table process_history add idx17 varchar(1024);
alter table process_history add idx18 varchar(1024);
alter table process_history add idx19 varchar(1024);

alter table process_history drop INDEX IND_PROCESS_HISTORY_IDX5;
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX0 (idx0);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX5 (idx5);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX6 (idx6);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX7 (idx7);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX8 (idx8);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX9 (idx9);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX10 (idx10);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX11 (idx11);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX12 (idx12);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX13 (idx13);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX14 (idx14);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX15 (idx15);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX16 (idx16);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX17 (idx17);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX18 (idx18);
alter table process_history add INDEX IND_PROCESS_HISTORY_IDX19 (idx19);

alter table modification drop foreign key modification_process_fk;
alter table activity_history drop foreign key activity_history_process_fk;
alter table process_history drop foreign key process_history_flow_fk;
alter table process_history drop primary key;
alter table process_history add primary key (flowid, pid, subpid, mid);
alter table process_history add CONSTRAINT `process_history_flow_fk` FOREIGN KEY `process_history_flow_fk` (`flowid`) REFERENCES `flow` (`flowid`) ON DELETE NO ACTION ON UPDATE NO ACTION;
alter table modification add CONSTRAINT `modification_process_fk` FOREIGN KEY `modification_process_fk` (`flowid`, `pid`, `subpid`) REFERENCES `process_history` (`flowid`, `pid`, `subpid`) ON DELETE NO ACTION ON UPDATE NO ACTION;
alter table activity_history add CONSTRAINT `activity_history_process_fk` FOREIGN KEY `activity_history_process_fk` (`flowid`, `pid`, `subpid`) REFERENCES `process_history` (`flowid`, `pid`, `subpid`) ON DELETE NO ACTION ON UPDATE NO ACTION;

alter table process_archive add currentuser VARCHAR(32);
alter table process_archive add lastupdate DATETIME;
alter table process_archive add procdata LONGTEXT CHARACTER SET utf8;
alter table process_archive add undoflag INT(1) NULL DEFAULT 0;
alter table process_archive add idx5 varchar(1024);
alter table process_archive add idx6 varchar(1024);
alter table process_archive add idx7 varchar(1024);
alter table process_archive add idx8 varchar(1024);
alter table process_archive add idx9 varchar(1024);
alter table process_archive add idx10 varchar(1024);
alter table process_archive add idx11 varchar(1024);
alter table process_archive add idx12 varchar(1024);
alter table process_archive add idx13 varchar(1024);
alter table process_archive add idx14 varchar(1024);
alter table process_archive add idx15 varchar(1024);
alter table process_archive add idx16 varchar(1024);
alter table process_archive add idx17 varchar(1024);
alter table process_archive add idx18 varchar(1024);
alter table process_archive add idx19 varchar(1024);

alter table process_archive add INDEX IND_PROCESS_ARCH_IDX5 (idx5);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX6 (idx6);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX7 (idx7);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX8 (idx8);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX9 (idx9);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX10 (idx10);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX11 (idx11);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX12 (idx12);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX13 (idx13);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX14 (idx14);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX15 (idx15);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX16 (idx16);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX17 (idx17);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX18 (idx18);
alter table process_archive add INDEX IND_PROCESS_ARCH_IDX19 (idx19);


CREATE TABLE `migration_log` (
  `migrator` VARCHAR(16) NULL,
  `task` VARCHAR(128) NOT NULL,
  `finished` DATETIME NULL,
  UNIQUE INDEX `uk_migration_log` (`migrator`,`task`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;


CREATE TABLE reporting (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  cod_reporting VARCHAR(16),
  start_reporting DATETIME,
  stop_reporting DATETIME,
  ttl DATETIME,
  active INT(1) NOT NULL DEFAULT 0
) 
ENGINE = INNODB DEFAULT CHARSET=utf8;

ALTER TABLE flow_roles ADD profileid INT NOT NULL;
UPDATE flow_roles r, profiles p SET r.profileid=p.profileid WHERE r.perfil LIKE p.name;
ALTER TABLE flow_roles DROP PRIMARY KEY;
ALTER TABLE flow_roles ADD PRIMARY KEY (profileid, flowid);
ALTER TABLE flow_roles ADD CONSTRAINT flow_roles_profiles_fk
	FOREIGN KEY flow_roles_profiles_fk(profileid) 
  	REFERENCES profiles(profileid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;
ALTER TABLE flow_roles DROP COLUMN perfil;


DELIMITER //
-- Simulates a sequence in mysql using data in the table seq_flow_settings
-- This function must be created by a SUPER user (such as root)
CREATE FUNCTION get_procdata_value (procdata LONGTEXT, varname VARCHAR(256))
  RETURNS VARCHAR(1024)
  DETERMINISTIC
    BEGIN
      declare stripped LONGTEXT;
      declare xmlTagPos INT;
      declare xmlTag VARCHAR(264); -- 256 (varname)+ 8 ('<a n="">')
      set xmlTag = concat('<a n="',varname,'">');
      set xmlTagPos = locate(xmlTag, procdata);
      if (xmlTagPos = 0) then
        RETURN NULL;
      end if;
      set xmlTagPos = xmlTagPos+length(xmlTag);
      set stripped = substring(procdata, xmlTagPos);
      return left(stripped,locate('</a>',stripped)-1);
    END
//

DELIMITER ;

drop procedure get_next_sub_pid;

DELIMITER //
CREATE PROCEDURE get_next_sub_pid (OUT retsubpid  INTEGER,
                                   aflowid INTEGER,
                                   apid INTEGER,
                                   acreatedate DATETIME,
								   acreator VARCHAR(32))
BEGIN
    DECLARE midtmp integer;
    DECLARE infotmp varchar(1024);
    DECLARE tmp integer;
	DECLARE apnumber VARCHAR(128);

    set midtmp = 1;
    select count(subpid) into tmp from process_history where flowid=aflowid and pid=apid;
    IF tmp > 0 THEN
      select max(subpid) into retsubpid from process_history where flowid=aflowid and pid=apid;
      select mid, info, pnumber into midtmp, infotmp, apnumber from process where flowid=aflowid and pid=apid and subpid=retsubpid;
      set retsubpid = retsubpid + 1;
    ELSE
       set retsubpid = 1;
    END IF;
    insert into process (flowid,pid,subpid,mid,created,info,creator,currentuser,lastupdate, pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,infotmp,acreator,acreator,acreatedate, apnumber);
    insert into process_history (flowid,pid,subpid,mid,created,info,creator,currentuser,lastupdate, pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,infotmp,acreator,acreator,acreatedate, apnumber);
END;
//

DELIMITER ;

UPDATE series set kind=replace(kind, 'pt.iknow.utils.series', 'pt.iflow.api.utils.series');