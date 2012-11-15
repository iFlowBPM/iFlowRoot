
alter table flow_state_history add exit_port varchar2(64);

create table log (
  log_id        int not null,
  log           varchar2(1024) not null default '',
  username      varchar2(10),
  caller        varchar2(16),
  method        varchar2(16),
  creation_date date not null,
  constraint log_id_pk primary key (log_id)
);

create table flow_state_log (
  flowid int not null,
  pid    int not null,
  subpid int not null DEFAULT 1,
  state  int not null,
  log_id int not null,
  constraint fk_log_id foreign key (log_id)
    references log (log_id)
    on delete cascade
);

alter table activity_hierarchy add requested date default sysdate;
alter table activity_hierarchy add responded date default sysdate;
   

alter table process drop column procdata;
alter table process_history drop column procdata;
alter table process_archive drop column procdata;

alter table process add procdata CLOB;
alter table process_history add procdata CLOB;
alter table process_archive add procdata CLOB;

CREATE OR REPLACE FUNCTION get_procdata_value (procdata IN CLOB, varname IN VARCHAR2)
  RETURN varchar2 IS
  xval XMLTYPE;
  xml XMLTYPE := XMLTYPE(procdata);
BEGIN
    xval := xml.extract('/processdata/a[@n="'||varname||'"]/text()', 'xmlns="http://www.iflow.pt/ProcessData"');
    RETURN CASE when xval is null then null else xval.getStringVal() end;
END;
/


alter table flow_state add mid int default 0;
update flow_state f set f.mid=(select NVL(max(mid), 0) from flow_state_history fh where fh.flowid=f.flowid and fh.pid=f.pid and fh.subpid=f.subpid and fh.undoflag=0);

CREATE OR REPLACE TRIGGER TRIG_FLOW_STATE
AFTER DELETE ON FLOW_STATE
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
DECLARE
  tmp NUMBER;
  newmid NUMBER;
  miduser VARCHAR2(256);
BEGIN
    select count(1) into tmp from process where flowid=:old.FLOWID and pid=:old.PID and subpid=:old.SUBPID;
    -- when tmp=0 flow is being deleted... don't historify process.
    IF (tmp > 0) THEN
      -- update mid
        select mid+1,muser into newmid,miduser from modification 
          where flowid = :old.flowid and pid = :old.pid and subpid=:old.subpid and mid=:old.mid;
        
        -- insert mid in modification table
        insert into modification (flowid,pid,subpid,mid,mdate,muser) 
      	  values (:old.flowid, :old.pid, :old.subpid, newmid, sysdate, miduser);

        -- checks if is the last subprocess belonging to a specific process
        select count(1) into tmp from process where flowid=:old.FLOWID and pid=:old.PID;
        IF (tmp = 1) THEN
          -- delete from forkjoin_mines
          delete from forkjoin_mines where flowid=:old.FLOWID and pid=:old.PID;
        END IF;
        -- delete sub process
        delete from process where flowid=:old.FLOWID and pid=:old.PID and subpid=:old.SUBPID;
    END IF;
END;
/

CREATE OR REPLACE PROCEDURE get_next_sub_pid (retsubpid OUT NUMBER,
                                          aflowid IN NUMBER,
                                          apid IN NUMBER,
                                          acreatedate IN DATE,
                                          acreator VARCHAR2,
							   	          acreatorsubpid INTEGER) IS
    midtmp number;
    infotmp varchar2(1024);
    tmp number;
    apnumber varchar2(128);
BEGIN
  midtmp := 1;
  select count(subpid) into tmp from process_history where flowid=aflowid and pid=apid;
  IF tmp > 0 THEN
    select max(subpid) into retsubpid from process_history where flowid=aflowid and pid=apid;
    select mid,info,pnumber into midtmp,infotmp,apnumber from process where flowid=aflowid and pid=apid and subpid=acreatorsubpid;
    retsubpid := retsubpid + 1;
  ELSE
    retsubpid := 1;
    midtmp := 0;    
  END IF;
  insert into process (flowid,pid,subpid,mid,created,info,creator,currentuser,lastupdate,pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,infotmp,acreator,acreator,acreatedate,apnumber);
  insert into process_history (flowid,pid,subpid,mid,created,info,creator,currentuser,lastupdate,pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,infotmp,acreator,acreator,acreatedate,apnumber);
END;
/

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
    on delete cascade
);

create table external_dms_properties (
  dmsid int not null,
  name  varchar2(64),
  value varchar2(64),
  constraint fk_dmsid foreign key (dmsid)
    references external_dms (dmsid)
    on delete cascade
);

alter table activity add read_flag number(1);
alter table activity_history add read_flag number(1);
update activity set read_flag=1;
update activity_history set read_flag=1;
alter table activity modify read_flag number(1) default 1;
alter table activity_history modify read_flag number(1) default 1;

alter table activity add mid int;
alter table activity_history add mid int;
alter table activity_history add worker number(1);
alter table activity_history add undoflag number(1);
update activity set mid=-10;
update activity_history set mid=-10;
update activity_history set worker=1;
alter table activity add mid int default 0;
alter table activity_history add mid int default 0;
alter table activity_history add worker number(1) default 0;
alter table activity_history add undoflag number(1) default 0;



drop view activity_delegated;
create view activity_delegated
    (hierarchyid, userid, pid, subpid, ownerid, flowid, created, type, started, archived, 
    status, notify, priority, description, url, profilename, requested, responded, read_flag,mid) as
    select H.hierarchyid, H.userid, A.pid, A.subpid, A.userid as ownerid, A.flowid, A.created, 
    A.type, A.started, A.archived, A.status, A.notify, A.priority, A.description, A.url, A.profilename,
    H.requested, H.responded, A.read_flag, A.mid
    from activity A, activity_hierarchy H
    where ((A.userid = H.ownerid and H.slave=1) or (A.userid = H.userid and slave=0)) 
    and A.flowid = H.flowid and H.pending=0 and A.delegated <> 0;
 
    
CREATE OR REPLACE PROCEDURE GET_NEXT_MID (retmid OUT NUMBER,
                                          auserid IN VARCHAR2,
                                          aflowid IN NUMBER,
                                          apid IN NUMBER,
                                          asubpid IN NUMBER,
                               			  aupdateproc IN NUMBER) IS
  tmp number;
BEGIN
  retmid := 1;
  select count(mid) into tmp from modification where subpid=asubpid and pid=apid and flowid=aflowid;

  IF tmp > 0 THEN
    select max(mid)+1 into retmid from modification where subpid=asubpid and pid=apid and flowid=aflowid;
  ELSE
	BEGIN
  		select mid into retmid from process where subpid=asubpid and pid=apid and flowid=aflowid and mid > 0;
    EXCEPTION
    WHEN NO_DATA_FOUND THEN
      retmid := 1;
  	END;
  END IF;
  IF aupdateproc = 1 THEN
    update process set mid=retmid where subpid=asubpid and pid=apid and flowid=aflowid;
    update flow_state set mid=retmid where subpid=asubpid and pid=apid and flowid=aflowid;
  END IF;
  insert into modification (flowid,pid,subpid,mid,mdate,muser) values (aflowid,apid,asubpid,retmid,SYSDATE,auserid);
END;
/

alter table users drop CONSTRAINT NL_EMAIL_ADDRESS;
alter table modification drop constraint modification_process_fk;

-- jcosta 17/07/2009
alter table organization_theme add menu_location varchar2(256) default 'left';
alter table organization_theme add menu_style varchar2(256) default 'list';


-- agon 27/07/2009
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
      GET_NEXT_MID (tmp,auserid,aflowid,rec.dpid,rec.subpid,1);
      update process set mid=tmp where subpid=rec.subpid and pid=rec.dpid and flowid=aflowid;
      update flow_state set mid=tmp where subpid=rec.subpid and pid=rec.dpid and flowid=aflowid;

        insert into process_history (flowid,pid,subpid,pnumber,mid,info,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,undoflag) 
            (select flowid,pid,subpid,pnumber,mid,info,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,0
                from process where flowid=aflowid and pid=rec.dpid and subpid=rec.subpid);
        delete from process where flowid=aflowid and pid=rec.dpid and subpid=rec.subpid;

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
    insert into process (flowid,pid,subpid,mid,created,info,creator,pnumber,currentuser) values
        (aflowid,retpid,retsubpid,1,acreatedate,'',acreator,retpid,acreator);
     insert into process_history (flowid,pid,subpid,mid,created,info,creator,pnumber,currentuser) values
        (aflowid,retpid,retsubpid,1,acreatedate,'',acreator,retpid,acreator);
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      retpid := 0;
      retsubpid := 0;
  END;
END;
/

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

-- lcabral 29/07/2009
insert into flow_settings (flowid,name,description,value) (select flowid, 'ENABLE_HISTORY' as name,'Permitir Visualizacao de Historicos' as description,'flow_settings.showHist.no' as value from flow_settings group by flowid);
