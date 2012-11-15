-- lcabral 06/11/2009
CREATE TABLE QRTZ_JOB_DETAILS
  (
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_VOLATILE VARCHAR(1) NOT NULL,
    IS_STATEFUL VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_JOB_LISTENERS
  (
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    JOB_LISTENER VARCHAR(200) NOT NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),
    FOREIGN KEY (JOB_NAME,JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    IS_VOLATILE VARCHAR(1) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT(13) NULL,
    PREV_FIRE_TIME BIGINT(13) NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT(13) NOT NULL,
    END_TIME BIGINT(13) NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (JOB_NAME,JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT(7) NOT NULL,
    REPEAT_INTERVAL BIGINT(12) NOT NULL,
    TIMES_TRIGGERED BIGINT(10) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(200) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_TRIGGER_LISTENERS
  (
    TRIGGER_NAME  VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    TRIGGER_LISTENER VARCHAR(200) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);


CREATE TABLE QRTZ_CALENDARS
  (
    CALENDAR_NAME  VARCHAR(200) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (CALENDAR_NAME)
);



CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
  (
    TRIGGER_GROUP  VARCHAR(200) NOT NULL, 
    PRIMARY KEY (TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
  (
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    IS_VOLATILE VARCHAR(1) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT(13) NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_STATEFUL VARCHAR(1) NULL,
    REQUESTS_RECOVERY VARCHAR(1) NULL,
    PRIMARY KEY (ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
  (
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
    CHECKIN_INTERVAL BIGINT(13) NOT NULL,
    PRIMARY KEY (INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
  (
    LOCK_NAME  VARCHAR(40) NOT NULL, 
    PRIMARY KEY (LOCK_NAME)
);

INSERT INTO QRTZ_LOCKS values('TRIGGER_ACCESS');
INSERT INTO QRTZ_LOCKS values('JOB_ACCESS');
INSERT INTO QRTZ_LOCKS values('CALENDAR_ACCESS');
INSERT INTO QRTZ_LOCKS values('STATE_ACCESS');
INSERT INTO QRTZ_LOCKS values('MISFIRE_ACCESS');

-- lcabral 07/12/2009
CREATE TABLE upgrade_log (
  signature	VARCHAR(125)	NOT NULL,
  executed	INT(1)			NOT NULL DEFAULT 0,
  error		INT(1)			NOT NULL DEFAULT 0,
  log_id	INT(11)			NOT NULL,
  CONSTRAINT PK_UPGRADE_LOG PRIMARY KEY (signature),
  CONSTRAINT FK_UPGRADE_LOG_LOG_ID FOREIGN KEY (log_id)
    REFERENCES log (log_id)
    ON DELETE CASCADE
) ENGINE = INNODB DEFAULT CHARSET=utf8;

-- olopes 09/12/2009
-- Add pid index to flow_state_history
create index idx_fsh_pid on flow_state_history(pid);

-- Flow stuff
alter table flow add type_code varchar(1) default 'W';
update flow set type_code='W' where type_code is null;

-- agon 09/12/2009
alter table organization_theme add proc_menu_visible INT(1) default 1;

-- agon 17/12/2009
CREATE TABLE `sequences` (
  `sequence` char(32) NOT NULL,
  `seqval` int(11) NOT NULL,
  PRIMARY KEY  (`sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

delimiter //
CREATE FUNCTION `sequence`(name CHAR(32))
    RETURNS int(11)
    MODIFIES SQL DATA
    DETERMINISTIC
    SQL SECURITY INVOKER
BEGIN
    INSERT INTO sequences (sequence, seqval)
    VALUES (name, LAST_INSERT_ID(1))
    ON DUPLICATE KEY UPDATE seqval = LAST_INSERT_ID(seqval + 1);
    RETURN LAST_INSERT_ID();
END;
//
delimiter ;

insert into sequences (sequence, seqval) select 'activity_hierarchy', max(hierarchyid) from activity_hierarchy_history;

-- AS ROOT!!
CREATE TRIGGER trigger_activity_hierarchy
BEFORE INSERT ON `activity_hierarchy` FOR EACH ROW
SET NEW.`hierarchyid` = sequence('activity_hierarchy');

-- lcabral 04/01/2010
-- AS ROOT!
DROP TRIGGER IF EXISTS trig_flow_state_delete;

ALTER TABLE process ADD canceled INT(1) DEFAULT 0;

-- lcabral 05/01/2010
ALTER TABLE flow_state ADD closed INT(1) DEFAULT 0;
ALTER TABLE flow_state ADD canceled INT(1) DEFAULT 0;

-- lcabral 07/01/2010
CREATE TABLE TMP_external_dms (
  dmsid INT NOT NULL,
  docid INT NOT NULL,
  uuid VARCHAR(64) NOT NULL,
  scheme VARCHAR(64),
  address VARCHAR(64),
  path VARCHAR(64),
  CONSTRAINT TMP_dmsid_pk PRIMARY KEY (dmsid),
  CONSTRAINT TMP_fk_docid FOREIGN KEY (docid)
    REFERENCES documents (docid)
    ON DELETE CASCADE,
  CONSTRAINT TMP_un_external_dms_uuid UNIQUE (uuid)
) ENGINE = INNODB DEFAULT CHARSET=utf8;
CREATE TABLE TMP_external_dms_properties (
  dmsid INT NOT NULL,
  name VARCHAR(64),
  value VARCHAR(64),
  CONSTRAINT TMP_fk_dmsid FOREIGN KEY (dmsid)
    REFERENCES TMP_external_dms (dmsid)
    ON DELETE CASCADE
) ENGINE = INNODB DEFAULT CHARSET=utf8;
INSERT INTO TMP_external_dms (SELECT dmsid, docid, uuid, scheme, address, path from external_dms group by uuid);
INSERT INTO TMP_external_dms_properties (SELECT dmsid, name, value from external_dms_properties where dmsid in (SELECT dmsid from TMP_external_dms));
DROP TABLE external_dms_properties;
DROP TABLE external_dms;
CREATE TABLE external_dms (
  dmsid INT NOT NULL,
  docid INT NOT NULL,
  uuid VARCHAR(64) NOT NULL,
  scheme VARCHAR(64),
  address VARCHAR(64),
  path VARCHAR(64),
  CONSTRAINT dmsid_pk PRIMARY KEY (dmsid),
  CONSTRAINT fk_docid FOREIGN KEY (docid)
    REFERENCES documents (docid)
    ON DELETE CASCADE,
  CONSTRAINT un_external_dms_uuid UNIQUE (uuid)
) ENGINE = INNODB DEFAULT CHARSET=utf8;
CREATE TABLE external_dms_properties (
  dmsid INT NOT NULL,
  name VARCHAR(64),
  value VARCHAR(64),
  CONSTRAINT fk_dmsid FOREIGN KEY (dmsid)
    REFERENCES external_dms (dmsid)
    ON DELETE CASCADE
) ENGINE = INNODB DEFAULT CHARSET=utf8;
INSERT INTO external_dms (SELECT dmsid, docid, uuid, scheme, address, path from TMP_external_dms);
INSERT INTO external_dms_properties (SELECT dmsid, name, value from TMP_external_dms_properties);
DROP TABLE TMP_external_dms_properties;
DROP TABLE TMP_external_dms;


-- agon 20/01/2010
ALTER TABLE process_history ADD canceled INT(1) DEFAULT 0;


DELIMITER $$

DROP PROCEDURE IF EXISTS `deleteFlow` $$
CREATE PROCEDURE deleteFlow(auserid VARCHAR(256),aflowid INTEGER,delprocs INTEGER)
BEGIN
  DECLARE tmp INTEGER;
  DECLARE error INTEGER;
  DECLARE apid INTEGER;
  DECLARE asubpid INTEGER;
  DECLARE done INT DEFAULT 0;
  DECLARE aname VARCHAR(256);
  DECLARE avaluestring VARCHAR(256);
  DECLARE avaluenumber VARCHAR(256);

  DECLARE COPEN CURSOR FOR SELECT distinct(pid) as dpid, subpid FROM process where flowid=aflowid;
  DECLARE CHIST CURSOR FOR SELECT distinct(pid) as dpid FROM process_history where flowid=aflowid;

  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

  set error = 1;

  -- first handle procs
  if (delprocs = 1) then
    -- delete all flow procs (including historic ones)

    open COPEN;
    REPEAT
      FETCH COPEN INTO apid, asubpid;
      IF NOT done THEN
        call deleteProc(aflowid,apid);
      END IF;
    UNTIL done END REPEAT;
    close COPEN;
    set done = 0;
    
    open CHIST;
    REPEAT
      FETCH CHIST INTO apid;
      IF NOT done THEN
        call deleteProc(aflowid,apid);
      END IF;
    UNTIL done END REPEAT;
    close CHIST;
    set done = 0;

  else
    -- move opened procs to historic tables
    -- move scheduled activities to historic table

    open COPEN;
    REPEAT
      FETCH COPEN INTO apid, asubpid;
      IF NOT done THEN
        call get_next_mid(tmp,auserid,aflowid,apid,asubpid);
	    update process set mid=tmp where subpid=asubpid and pid=apid and flowid=aflowid;
    	update flow_state set mid=tmp where subpid=asubpid and pid=apid and flowid=aflowid;

        -- move data
        insert into process_history (flowid,pid,subpid,pnumber,mid,info,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19,undoflag) 
            select flowid,pid,subpid,pnumber,mid,info,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19,0
                from process where flowid=aflowid and pid=apid and subpid=asubpid;
        update process set closed=1 where flowid=aflowid and pid=apid and subpid=asubpid;

        insert into activity_history (userid,flowid,pid,subpid,type,priority,created,started,archived,
            description,url,status,notify,delegated,delegateduserid)
            (SELECT userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,
            status,notify,delegated, userid as delegateduserid from activity where flowid=aflowid and pid=apid and subpid=asubpid);
        delete from activity where flowid=aflowid and pid=apid and subpid=asubpid;
      END IF;
    UNTIL done END REPEAT;
    close COPEN;
    set done = 0;

    -- archive all history
    open CHIST;
    REPEAT
      FETCH CHIST INTO apid;
      IF NOT done THEN
        call archiveProc(tmp,aflowid,apid,NOW());
      END IF;
    UNTIL done END REPEAT;
    close CHIST;
    set done = 0;

  end if;

  -- delete all flow related stuff
  delete from flow_settings where flowid=aflowid;
  delete from flow_settings_history where flowid=aflowid;
  delete from flow_roles where flowid=aflowid;
  delete from flow where flowid=aflowid;
END;
$$

DROP PROCEDURE IF EXISTS `archiveProc` $$
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
  select count(1) into open from process where flowid=aflowid and pid=apid and closed=0;
  IF open = 0 THEN

    START TRANSACTION;

    -- so far so nice, copy to archive
    insert into process_archive (flowid,pid,subpid,pnumber,mid,info,creator,created,currentuser,lastupdate,procdata,closed,archived,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19) 
      select flowid,pid,subpid,pnumber,mid,info,creator,created,currentuser,lastupdate,procdata,closed,aarchivedate,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19
              from process_history where flowid=aflowid and pid=apid;

    insert into activity_archive (userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename)
      select userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename 
              from activity_history where flowid=aflowid and pid=apid;
  
    insert into modification_archive (flowid,pid,subpid,mid,mdate,muser)
      select flowid,pid,subpid,mid,mdate,muser from modification where flowid=aflowid and pid=apid;

    -- finally, delete from history
    delete from modification where flowid=aflowid and pid=apid;
    delete from activity_history where flowid=aflowid and pid=apid;
    delete from process_history where flowid=aflowid and pid=apid;
    delete from process where flowid=aflowid and pid=apid;

    set archiveResult = 1;
  END IF;

  COMMIT;
END;
$$

delimiter ;

-- agon 25/01/2010
alter table users modify username VARCHAR(100);
alter table activity modify userid VARCHAR(100);
alter table activity_hierarchy modify userid VARCHAR(100);
alter table activity_hierarchy modify ownerid VARCHAR(100);
alter table activity_hierarchy_history modify userid VARCHAR(100);
alter table activity_hierarchy_history modify ownerid VARCHAR(100);
alter table activity_history modify userid VARCHAR(100);
alter table activity_history modify delegateduserid VARCHAR(100);
alter table iflow_errors modify userid VARCHAR(100);
alter table user_settings modify userid VARCHAR(100);
alter table user_notifications modify userid VARCHAR(100);
alter table activity_archive modify userid VARCHAR(100);
alter table activity_archive modify delegateduserid VARCHAR(100);

alter table process drop column info;
alter table process modify creator VARCHAR(100);
alter table process modify currentuser VARCHAR(100);
alter table process_history drop column info;
alter table process_history modify creator VARCHAR(100);
alter table process_history modify currentuser VARCHAR(100);
alter table process_archive drop column info;
alter table process_archive modify creator VARCHAR(100);
alter table process_archive modify currentuser VARCHAR(100);

alter table activity_hierarchy drop INDEX activity_hierarchy_uk;
alter table activity_hierarchy add UNIQUE INDEX `activity_hierarchy_uk` (`flowid`, `ownerid`(100), `userid`(100));

alter table users drop INDEX uk_users_username;
alter table users add UNIQUE INDEX `uk_users_username` (`username`(100));


delimiter //
DROP PROCEDURE IF EXISTS `get_next_pid` //
CREATE PROCEDURE get_next_pid (OUT retpid INTEGER,
                               OUT retsubpid INTEGER,
                               aflowid INTEGER,
                               acreatedate DATETIME,
							   acreator VARCHAR(100))
BEGIN
DECLARE nowdate DATETIME;
    select value into retpid from counter where name='pid';
    set retpid = retpid + 1;
    update counter set value=retpid where name='pid';
    set retsubpid = 1;
	set nowdate = NOW();
    insert into process (flowid,pid,subpid,mid,created,creator,pnumber,currentuser,lastupdate) values 
		(aflowid,retpid,retsubpid,1,acreatedate,acreator,retpid,acreator,nowdate);
    insert into process_history (flowid,pid,subpid,mid,created,creator,pnumber,currentuser,lastupdate) values 
		(aflowid,retpid,retsubpid,1,acreatedate,acreator,retpid,acreator,nowdate);
END;
//

DROP PROCEDURE IF EXISTS `get_next_sub_pid` //
CREATE PROCEDURE get_next_sub_pid (OUT retsubpid  INTEGER,
                                   aflowid INTEGER,
                                   apid INTEGER,
                                   acreatedate DATETIME,
								   acreator VARCHAR(100),
							   	   acreatorsubpid INTEGER)
BEGIN
    DECLARE midtmp integer;
    DECLARE tmp integer;
	DECLARE apnumber VARCHAR(128);

    set midtmp = 1;
    select count(subpid) into tmp from process_history where flowid=aflowid and pid=apid;
    IF tmp > 0 THEN
      select max(subpid) into retsubpid from process_history where flowid=aflowid and pid=apid;
      select mid, info, pnumber into midtmp, apnumber from process where flowid=aflowid and pid=apid and subpid=acreatorsubpid;
      set retsubpid = retsubpid + 1;
    ELSE
       set retsubpid = 1;
       set midtmp = 0;
    END IF;
    insert into process (flowid,pid,subpid,mid,created,creator,currentuser,lastupdate, pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,acreator,acreator,acreatedate, apnumber);
    insert into process_history (flowid,pid,subpid,mid,created,creator,currentuser,lastupdate, pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,acreator,acreator,acreatedate, apnumber);
END;
//

delimiter ;

-- agon 01/02/2010

delimiter //

DROP PROCEDURE IF EXISTS `deleteFlow` //
CREATE PROCEDURE deleteFlow(auserid VARCHAR(256),aflowid INTEGER,delprocs INTEGER)
BEGIN
  DECLARE tmp INTEGER;
  DECLARE error INTEGER;
  DECLARE apid INTEGER;
  DECLARE asubpid INTEGER;
  DECLARE done INT DEFAULT 0;
  DECLARE aname VARCHAR(256);
  DECLARE avaluestring VARCHAR(256);
  DECLARE avaluenumber VARCHAR(256);

  DECLARE COPEN CURSOR FOR SELECT distinct(pid) as dpid, subpid FROM process where flowid=aflowid;
  DECLARE CHIST CURSOR FOR SELECT distinct(pid) as dpid FROM process_history where flowid=aflowid;

  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

  set error = 1;

  -- first handle procs
  if (delprocs = 1) then
    -- delete all flow procs (including historic ones)

    open COPEN;
    REPEAT
      FETCH COPEN INTO apid, asubpid;
      IF NOT done THEN
        call deleteProc(aflowid,apid);
      END IF;
    UNTIL done END REPEAT;
    close COPEN;
    set done = 0;
    
    open CHIST;
    REPEAT
      FETCH CHIST INTO apid;
      IF NOT done THEN
        call deleteProc(aflowid,apid);
      END IF;
    UNTIL done END REPEAT;
    close CHIST;
    set done = 0;

  else
    -- move opened procs to historic tables
    -- move scheduled activities to historic table

    open COPEN;
    REPEAT
      FETCH COPEN INTO apid, asubpid;
      IF NOT done THEN
        call get_next_mid(tmp,auserid,aflowid,apid,asubpid);
	    update process set mid=tmp where subpid=asubpid and pid=apid and flowid=aflowid;
    	update flow_state set mid=tmp where subpid=asubpid and pid=apid and flowid=aflowid;

        -- move data
        insert into process_history (flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19,undoflag) 
            select flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19,0
                from process where flowid=aflowid and pid=apid and subpid=asubpid;
        update process set closed=1 where flowid=aflowid and pid=apid and subpid=asubpid;

        insert into activity_history (userid,flowid,pid,subpid,type,priority,created,started,archived,
            description,url,status,notify,delegated,delegateduserid)
            (SELECT userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,
            status,notify,delegated, userid as delegateduserid from activity where flowid=aflowid and pid=apid and subpid=asubpid);
        delete from activity where flowid=aflowid and pid=apid and subpid=asubpid;
      END IF;
    UNTIL done END REPEAT;
    close COPEN;
    set done = 0;

    -- archive all history
    open CHIST;
    REPEAT
      FETCH CHIST INTO apid;
      IF NOT done THEN
        call archiveProc(tmp,aflowid,apid,NOW());
      END IF;
    UNTIL done END REPEAT;
    close CHIST;
    set done = 0;

  end if;

  -- delete all flow related stuff
  delete from flow_settings where flowid=aflowid;
  delete from flow_settings_history where flowid=aflowid;
  delete from flow_roles where flowid=aflowid;
  delete from flow where flowid=aflowid;
END;
//

DROP PROCEDURE IF EXISTS `archiveProc` //
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
  select count(1) into open from process where flowid=aflowid and pid=apid and closed=0;
  IF open = 0 THEN

    START TRANSACTION;

    -- so far so nice, copy to archive
    insert into process_archive (flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,archived,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19) 
      select flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,aarchivedate,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19
              from process_history where flowid=aflowid and pid=apid;

    insert into activity_archive (userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename)
      select userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename 
              from activity_history where flowid=aflowid and pid=apid;
  
    insert into modification_archive (flowid,pid,subpid,mid,mdate,muser)
      select flowid,pid,subpid,mid,mdate,muser from modification where flowid=aflowid and pid=apid;

    -- finally, delete from history
    delete from modification where flowid=aflowid and pid=apid;
    delete from activity_history where flowid=aflowid and pid=apid;
    delete from process_history where flowid=aflowid and pid=apid;
    delete from process where flowid=aflowid and pid=apid;

    set archiveResult = 1;
  END IF;

  COMMIT;
END;
//

delimiter ;

-- agon 08/02/2010
alter table log modify log varchar(2048);

-- hmartins 10/02/2010
CREATE TABLE `organizations_tabs` (
  `organizationid` int(11) DEFAULT NULL,
  `tabid` int(11) DEFAULT NULL,
  CONSTRAINT `fk_organization` FOREIGN KEY `fk_organization` (`organizationid`)
    REFERENCES `organizations` (`organizationid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `profiles_tabs` (
  `profileid` int(11) DEFAULT NULL,
  `tabid` int(11) DEFAULT NULL,
  CONSTRAINT `fk_profiles` FOREIGN KEY `fk_profiles` (`profileid`)
    REFERENCES `profiles` (`profileid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = INNODB DEFAULT CHARSET=utf8;

-- agon 22/02/2010
alter table email add eport int default -1;
alter table dirty_email add eport int default -1;

-- agon 01/04/2010 (as root)
CREATE TRIGGER trigger_email
BEFORE INSERT ON `email` FOR EACH ROW
SET NEW.`eid` = sequence('email');

insert into sequences (sequence, seqval) select 'email', max(eid) from email;

-- agon 22/04/2010
insert into sequences (sequence, seqval) select 'flow_state_log_id', max(log_id) from log;
