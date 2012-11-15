DELIMITER //
-- gets next available pid (from counter table) and updates counter table for a given flow.
DROP PROCEDURE get_next_sub_pid;
//
CREATE PROCEDURE get_next_sub_pid (OUT retsubpid  INTEGER,
                                   aflowid INTEGER,
                                   apid INTEGER,
                                   acreatedate DATETIME)
BEGIN
    DECLARE midtmp integer;
    DECLARE infotmp varchar(1024);
    DECLARE tmp integer;
	DECLARE acreator VARCHAR(32);
	DECLARE apnumber VARCHAR(128);

    set midtmp = 1;
    select count(subpid) into tmp from process_history where flowid=aflowid and pid=apid;
    IF tmp > 0 THEN
      select max(subpid) into retsubpid from process_history where flowid=aflowid and pid=apid;
      select mid,info,creator,pnumber into midtmp,infotmp,acreator,apnumber from process where flowid=aflowid and pid=apid and subpid=retsubpid;
      set retsubpid = retsubpid + 1;
    ELSE
       set retsubpid = 1;
    END IF;
    insert into process (flowid,pid,subpid,mid,created,info, creator, pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,infotmp,acreator,apnumber);
    insert into process_history (flowid,pid,subpid,mid,created,info, creator, pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,infotmp,acreator,apnumber);
END;
//

DELIMITER ;

CREATE TABLE `process_archive` (
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `mid` INT NOT NULL,
  `created` DATETIME NULL,
  `info` VARCHAR(1024) BINARY NULL,
  `creator`	VARCHAR(32) NOT NULL,
  `closed` INT(1) NOT NULL DEFAULT 0,
  `idx0` varchar(1024),
  `idx1` varchar(1024),
  `idx2` varchar(1024),
  `idx3` varchar(1024),
  `idx4` varchar(1024),
  `pnumber` varchar(128) NOT NULL,
  `archived` DATETIME NOT NULL,
  PRIMARY KEY (`flowid`, `pid`,`subpid`),
  INDEX IND_PROCESS_ARCH_IDX0 (idx0),
  INDEX IND_PROCESS_ARCH_IDX1 (idx1),
  INDEX IND_PROCESS_ARCH_IDX2 (idx2),
  INDEX IND_PROCESS_ARCH_IDX3 (idx3),
  INDEX IND_PROCESS_ARCH_IDX4 (idx4)
)
ENGINE = INNODB;

CREATE TABLE `data_numeric_archive` (
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `mid` INT NOT NULL,
  `name` VARCHAR(64) BINARY NOT NULL,
  `value` DOUBLE NULL,
  `closed` INT(1) NULL DEFAULT 0,
  `undoflag` INT(1) NULL DEFAULT 0,
  PRIMARY KEY (`name`, `pid`, `subpid`, `mid`, `flowid`),
  INDEX `ind_data_numeric_archive` (`flowid`, `pid`, `closed`)
)
ENGINE = INNODB;

CREATE TABLE `data_string_archive` (
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `mid` INT NOT NULL,
  `name` VARCHAR(64) BINARY NOT NULL,
  `value` VARCHAR(1024) BINARY NULL,
  `closed` INT(1) NULL DEFAULT 0,
  `undoflag` INT(1) NULL DEFAULT 0,
  PRIMARY KEY (`flowid`, `pid`, `mid`, `subpid`, `name`),
  INDEX `ind_data_string_archive` (`flowid`, `pid`, `closed`)
)
ENGINE = INNODB;

CREATE TABLE `activity_archive` (
  `userid` VARCHAR(32) BINARY NULL,
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NULL DEFAULT 1,
  `type` INT NULL,
  `priority` INT NULL,
  `created` DATETIME NULL,
  `started` DATETIME NULL,
  `archived` DATETIME NULL,
  `description` VARCHAR(256) BINARY NULL,
  `url` VARCHAR(256) BINARY NULL,
  `status` INT NULL,
  `notify` INT(1) NULL,
  `delegated` INT(1) NULL DEFAULT 0,
  `delegateduserid` VARCHAR(32) BINARY NULL,
  `profilename` VARCHAR(256)
)
ENGINE = INNODB;

CREATE TABLE `modification_archive` (
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `mid` INT NOT NULL,
  `mdate` DATETIME NULL,
  `muser` VARCHAR(256) BINARY NULL,
  PRIMARY KEY (`pid`, `subpid`, `mid`, `flowid`)
)
ENGINE = INNODB;

DELIMITER //
-- move a process to archive tables
drop procedure archiveProc;//
CREATE PROCEDURE archiveProc(OUT archiveResult INTEGER, aflowid INTEGER,apid INTEGER,aarchivedate DATETIME)
BEGIN
  DECLARE open INTEGER;
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    set archiveResult = -1;
  END;
 
  set archiveResult = 0;

  START TRANSACTION;

  -- check if process (or any of its subprocesses) is still open
  select count(1) into open from process where flowid=aflowid and pid=apid;
  IF open = 0 THEN

    START TRANSACTION;

    -- so far so nice, copy to archive
    insert into process_archive (flowid,pid,subpid,mid,created,info,creator,closed,idx0,idx1,idx2,idx3,idx4,pnumber,archived) 
      select flowid,pid,subpid,mid,created,info,creator,closed,idx0,idx1,idx2,idx3,idx4,pnumber,aarchivedate from process_history where flowid=aflowid and pid=apid;

    insert into data_string_archive (flowid,pid,subpid,mid,name,value,closed,undoflag)
      select flowid,pid,subpid,mid,name,value,closed,undoflag from data_string_history where flowid=aflowid and pid=apid;
  
    insert into data_numeric_archive (flowid,pid,subpid,mid,name,value,closed,undoflag)
      select flowid,pid,subpid,mid,name,value,closed,undoflag from data_numeric_history where flowid=aflowid and pid=apid;
  
    insert into activity_archive (userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename)
      select userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename from activity_history where flowid=aflowid and pid=apid;
  
    insert into modification_archive (flowid,pid,subpid,mid,mdate,muser)
      select flowid,pid,subpid,mid,mdate,muser from modification where flowid=aflowid and pid=apid;

    -- finally, delete from history
    delete from data_string_history where flowid=aflowid and pid=apid;
    delete from data_numeric_history where flowid=aflowid and pid=apid;
    delete from modification where flowid=aflowid and pid=apid;
    delete from activity_history where flowid=aflowid and pid=apid;
    delete from process_history where flowid=aflowid and pid=apid;

    set archiveResult = 1;
  END IF;

  COMMIT;
END;
//

DELIMITER ;