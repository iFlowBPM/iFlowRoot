-- gets next available pid (from counter table) and updates counter table for a given flow.
CREATE OR REPLACE PROCEDURE get_next_sub_pid (retsubpid OUT NUMBER,
                                          aflowid IN NUMBER,
                                          apid IN NUMBER,
                                                       acreatedate IN DATE) IS
    midtmp number;
    infotmp varchar2(1024);
    tmp number;
	acreator varchar2(32);
	apnumber varchar2(128);

BEGIN
  BEGIN
    midtmp := 1;
    select count(subpid) into tmp from process_history where flowid=aflowid and pid=apid;
     IF tmp > 0 THEN
      select max(subpid) into retsubpid from process_history where flowid=aflowid and pid=apid;
      select mid,info,creator,pnumber into midtmp,infotmp, acreator, apnumber from process where flowid=aflowid and pid=apid and subpid=retsubpid;
      retsubpid := retsubpid + 1;
     ELSE
       retsubpid := 1;
     END IF;
     insert into process (flowid,pid,subpid,mid,created,info, creator, pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,infotmp,acreator,apnumber);
     insert into process_history (flowid,pid,subpid,mid,created,info, creator, pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,infotmp,acreator,apnumber);
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      retsubpid := 0;
  END;
END;
/


create table activity_archive (
        userid          varchar(32),
        flowid          int constraint activityarch_flowid_nn not null,
        pid             int constraint activityarch_pid_nn not null,
        subpid          int default 1,
        type            int,
        priority        int,
        created         date default sysdate,
        started         date default sysdate,
        archived        date default sysdate,
        description     varchar(256),
        url             varchar(256),
        status          int,
        notify          number(1),
        delegated       number(1) default 0,
        delegateduserid varchar(32),
        profilename     VARCHAR(256)
);
create table data_numeric_archive (
    flowid                int,
    pid                   int,
    subpid                int default 1,
    mid                   int,
    name                  varchar(64),
    value                 float,
    closed                number(1) default 0,
    undoflag              number(1) default 0,
    constraint data_numeric_archive_pk primary key (flowid, pid, subpid, mid, name)
);
create table data_string_archive (
        flowid                int,
        pid                   int,
        subpid                int default 1,
        mid                   int,
        name                  varchar(64),
        value                 varchar(1024),
        closed                number(1) default 0,
        undoflag              number(1) default 0,
        constraint data_string_archive_pk primary key (flowid, pid, subpid, mid, name)
);
create table modification_archive (
    flowid          int,
    pid         int,
    subpid      int default 1,
    mid         int,
    mdate           date default sysdate,
    muser           varchar(256),
    constraint modification_archive_pk primary key (flowid, pid, subpid, mid)
);
create table process_archive (
    flowid  int,
    pid     int,
    subpid  int default 1,
    mid     int constraint processarch_mid_nn not null,
    created date default sysdate,
    info    varchar(1024),
	creator		varchar2(32) constraint processarch_cr_nn not null,
    closed  number(1) default 0 constraint process_archive_nn NOT NULL,
	idx0		varchar2(1024),
	idx1		varchar2(1024),
	idx2		varchar2(1024),
	idx3		varchar2(1024),
	idx4		varchar2(1024),
    pnumber		varchar2(128) constraint processarch_pnumber_nn not null,
    archived    date constraint processarch_archived_nn not null,
    constraint process_archive_pk primary key (flowid, pid, subpid)
);

-- move a process to archive tables
CREATE OR REPLACE PROCEDURE archiveProc (archiveResult OUT NUMBER, aflowid IN NUMBER,apid IN NUMBER,aarchivedate IN DATE) IS
    open number;
PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN
  archiveResult := 0;
  BEGIN
    -- check if process (or any of its subprocesses) is still open
    select count(1) into open from process where flowid=aflowid and pid=apid;
    -- DBMS_OUTPUT.PUT_LINE('Open Is : '||to_char(open)) ;
    IF open = 0 THEN
      -- so far so nice, copy to archive
      insert into process_archive (flowid,pid,subpid,mid,created,info,creator,closed,idx0,idx1,idx2,idx3,idx4,pnumber,archived) 
        select flowid,pid,subpid,mid,created,info,creator,closed,idx0,idx1,idx2,idx3,idx4,pnumber,aarchivedate from process_history where flowid=aflowid and pid=apid;
      -- DBMS_OUTPUT.PUT_LINE('insert 1') ;

      insert into data_string_archive (flowid,pid,subpid,mid,name,value,closed,undoflag)
        select flowid,pid,subpid,mid,name,value,closed,undoflag from data_string_history where flowid=aflowid and pid=apid;
      -- DBMS_OUTPUT.PUT_LINE('insert 2') ;

      insert into data_numeric_archive (flowid,pid,subpid,mid,name,value,closed,undoflag)
        select flowid,pid,subpid,mid,name,value,closed,undoflag from data_numeric_history where flowid=aflowid and pid=apid;
      -- DBMS_OUTPUT.PUT_LINE('insert 3') ;

      insert into activity_archive (userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename)
        select userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename from activity_history where flowid=aflowid and pid=apid;
      -- DBMS_OUTPUT.PUT_LINE('insert 4') ;

      insert into modification_archive (flowid,pid,subpid,mid,mdate,muser)
        select flowid,pid,subpid,mid,mdate,muser from modification where flowid=aflowid and pid=apid;
      -- DBMS_OUTPUT.PUT_LINE('insert 5') ;

      -- finally, delete from history
      delete from data_string_history where flowid=aflowid and pid=apid;
      -- DBMS_OUTPUT.PUT_LINE('delete 1') ;
      delete from data_numeric_history where flowid=aflowid and pid=apid;
      -- DBMS_OUTPUT.PUT_LINE('delete 2') ;
      delete from modification where flowid=aflowid and pid=apid;
      -- DBMS_OUTPUT.PUT_LINE('delete 3') ;
      delete from activity_history where flowid=aflowid and pid=apid;
      -- DBMS_OUTPUT.PUT_LINE('delete 4') ;
      delete from process_history where flowid=aflowid and pid=apid;
      -- DBMS_OUTPUT.PUT_LINE('delete 5') ;

      archiveResult := 1;
    END IF;
    COMMIT;
  EXCEPTION WHEN OTHERS THEN
    ROLLBACK;
    archiveResult := -1;
    -- DBMS_OUTPUT.PUT_LINE('excepcao');
  END;
END;
/

