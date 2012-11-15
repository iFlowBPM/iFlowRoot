-- lcabral 06/11/2009
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

-- lcabral 07/12/2009
CREATE TABLE UPGRADE_LOG (
  signature	VARCHAR2(125)	NOT NULL,
  executed	NUMBER(1)		NOT NULL DEFAULT 0,
  error		NUMBER(1)		NOT NULL DEFAULT 0,
  log_id	NUMBER(11)		NOT NULL,
  PRIMARY KEY (signature),
  FOREIGN KEY (log_id) REFERENCES log(log_id)
);

-- olopes 09/12/2009
-- Add pid index to flow_state_history
create index idx_fsh_pid on flow_state_history(pid);

-- Flow stuff
alter table flow add type_code varchar2(1) default 'W';
update flow set type_code='W' where type_code is null;

-- agon 09/12/2009
alter table organization_theme add proc_menu_visible NUMBER(1);
update organization_theme set proc_menu_visible=1;
alter table organization_theme modify proc_menu_visible NUMBER(1) default 1;

-- lcabral 04/01/2010
drop trigger TRIG_FLOW_STATE;
alter table process add canceled number(1) default 0;

-- lcabral 05/01/2010
alter table flow_state add closed number(1) default 0;
alter table flow_state add canceled number(1) default 0;

-- lcabral 07/01/2010
CREATE TABLE TMP_external_dms (
  dmsid int not null,
  docid int not null,
  uuid varchar2(64) not null,
  scheme varchar2(64),
  address varchar2(64),
  path varchar2(64),
  CONSTRAINT TMP_dmsid_pk PRIMARY KEY (dmsid),
  CONSTRAINT TMP_fk_docid FOREIGN KEY (docid)
    REFERENCES documents (docid)
    ON DELETE CASCADE,
  CONSTRAINT TMP_un_external_dms_uuid UNIQUE (uuid)
) ENGINE = INNODB DEFAULT CHARSET=utf8;
CREATE TABLE TMP_external_dms_properties (
  dmsid int not null,
  name varchar2(64),
  value varchar2(64),
  CONSTRAINT TMP_fk_dmsid FOREIGN KEY (dmsid)
    REFERENCES TMP_external_dms (dmsid)
    ON DELETE CASCADE
) ENGINE = INNODB DEFAULT CHARSET=utf8;
INSERT INTO TMP_external_dms (SELECT dmsid, docid, uuid, scheme, address, path from external_dms group by uuid);
INSERT INTO TMP_external_dms_properties (SELECT dmsid, name, value from external_dms_properties where dmsid in (SELECT dmsid from TMP_external_dms));
DROP TABLE external_dms_properties;
DROP TABLE external_dms;
CREATE TABLE external_dms (
  dmsid int not null,
  docid int not null,
  uuid varchar2(64) not null,
  scheme varchar2(64),
  address varchar2(64),
  path varchar2(64),
  CONSTRAINT dmsid_pk PRIMARY KEY (dmsid),
  CONSTRAINT fk_docid FOREIGN KEY (docid)
    REFERENCES documents (docid)
    ON DELETE CASCADE,
  CONSTRAINT un_external_dms_uuid UNIQUE (uuid)
) ENGINE = INNODB DEFAULT CHARSET=utf8;
CREATE TABLE external_dms_properties (
  dmsid int not null,
  name varchar2(64),
  value varchar2(64),
  CONSTRAINT fk_dmsid FOREIGN KEY (dmsid)
    REFERENCES external_dms (dmsid)
    ON DELETE CASCADE
) ENGINE = INNODB DEFAULT CHARSET=utf8;
INSERT INTO external_dms (SELECT dmsid, docid, uuid, scheme, address, path from TMP_external_dms);
INSERT INTO external_dms_properties (SELECT dmsid, name, value from TMP_external_dms_properties);
DROP TABLE TMP_external_dms_properties;
DROP TABLE TMP_external_dms;

commit;

-- agon 20/01/2010
alter table process_history add canceled number(1) default 0;

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

        insert into process_history (flowid,pid,subpid,pnumber,mid,info,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19,undoflag) 
            (select flowid,pid,subpid,pnumber,mid,info,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19,0
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
      insert into process_archive (flowid,pid,subpid,pnumber,mid,info,creator,created,currentuser,lastupdate,procdata,closed,archived,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19) 
        select flowid,pid,subpid,pnumber,mid,info,creator,created,currentuser,lastupdate,procdata,closed,aarchivedate,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19
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

-- agon 25/01/2010
alter table activity modify userid varchar2(100);
alter table activity_hierarchy modify userid varchar2(100);
alter table activity_hierarchy modify ownerid varchar2(100);
alter table activity_hierarchy_history modify userid varchar2(100);
alter table activity_hierarchy_history modify ownerid varchar2(100);
alter table activity_history modify userid varchar2(100);
alter table activity_history modify delegateduserid varchar2(100);
alter table event_data modify userid varchar2(100);
alter table iflow_errors modify userid varchar2(100);
alter table process modify creator varchar2(100);
alter table process modify currentuser varchar2(100);
alter table process_history modify creator varchar2(100);
alter table process_history modify currentuser varchar2(100);
alter table user_settings modify userid varchar2(100);
alter table user_notifications modify userid varchar2(100);
alter table activity_archive modify userid varchar2(100);
alter table activity_archive modify delegateduserid varchar2(100);
alter table process_archive modify creator varchar2(100);
alter table process_archive modify currentuser varchar2(100);

-- agon 01/02/2010

alter table process drop column info;
alter table process_history drop column info;
alter table process_archive drop column info;

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

-- agon 08/02/2010
alter table log modify log varchar2(2048);

-- hmartins 10/02/2010
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

-- agon 22/02/2010
alter table email add eport number;
alter table dirty_email add eport number;
update email set eport=-1;
update dirty_email set eport=-1;
alter table email modify eport number default -1;
alter table dirty_email modify eport number default -1;

-- agon 22/04/2010
SELECT 'CREATE SEQUENCE SEQ_FLOW_STATE_LOG_ID MINVALUE 0 START WITH '||MAX(trans_seq_no)+1||' INCREMENT BY 1 CACHE 20'
  INTO v_sql
  FROM log:
  
EXECUTE IMMEDIATE v_sql;
