
ALTER TABLE flow_state_history ADD exit_port VARCHAR(64) NULL;

CREATE TABLE log (
  log_id INT NOT NULL,
  log VARCHAR(1024) NOT NULL DEFAULT '',
  username VARCHAR(10),
  caller VARCHAR(16),
  method VARCHAR(16),
  creation_date DATETIME NOT NULL,
  CONSTRAINT log_id_pk PRIMARY KEY (log_id)
) 
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE flow_state_log (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  state INT NOT NULL,
  log_id INT NOT NULL,
  CONSTRAINT fk_log_id FOREIGN KEY (log_id)
    REFERENCES log (log_id)
    ON DELETE CASCADE
) 
ENGINE = INNODB DEFAULT CHARSET=utf8;

ALTER TABLE activity_hierarchy ADD requested DATETIME NULL;
ALTER TABLE activity_hierarchy ADD responded DATETIME NULL;    

create index ind_flow_state_history2 on flow_state_history(flowid,pid,subpid,mid);

alter table flow_state add column mid int null default 0;

update flow_state f set f.mid=(select IF(max(mid) is null, 0, max(mid)) from flow_state_history fh where 
fh.flowid=f.flowid and fh.pid=f.pid and fh.subpid=f.subpid and fh.undoflag=0);

drop trigger if exists trig_flow_state_insert;
drop trigger if exists trig_flow_state_update;
drop trigger if exists trig_flow_state_delete;

delimiter //

create trigger trig_flow_state_delete after delete on flow_state for each row
begin
    declare tmp integer;
    declare miduser VARCHAR(256);
    declare newmid integer;

    select count(1) into tmp from process where flowid=old.flowid and pid=old.pid and subpid=old.subpid;
    -- when tmp=0 flow is being deleted... don't historify process.
    if (tmp > 0) then
      -- update mid
      	select mid+1,muser into newmid,miduser from modification 
        		where flowid = old.flowid and pid = old.pid and subpid=old.subpid and mid=old.mid;
   
        -- insert mid in modification table
        insert into modification (flowid,pid,subpid,mid,mdate,muser) 
        	values (old.flowid, old.pid, old.subpid, newmid, now(), miduser);

        -- checks if is the last subprocess belonging to a specific process
        select count(1) into tmp from process where flowid=old.flowid and pid=old.pid;
        if (tmp = 1) then
          -- delete from forkjoin_mines
          delete from forkjoin_mines where flowid=old.flowid and pid=old.pid;
        end if;
        -- delete sub process
        delete from process where flowid=old.flowid and pid=old.pid and subpid=old.subpid;
    end if;
end;//

DROP PROCEDURE if exists get_next_sub_pid;
//
CREATE PROCEDURE get_next_sub_pid (OUT retsubpid  INTEGER,
                                   aflowid INTEGER,
                                   apid INTEGER,
                                   acreatedate DATETIME,
								   acreator VARCHAR(32),
							   	   acreatorsubpid INTEGER)
BEGIN
    DECLARE midtmp integer;
    DECLARE infotmp varchar(1024);
    DECLARE tmp integer;
	DECLARE apnumber VARCHAR(128);

    set midtmp = 1;
    select count(subpid) into tmp from process_history where flowid=aflowid and pid=apid;
    IF tmp > 0 THEN
      select max(subpid) into retsubpid from process_history where flowid=aflowid and pid=apid;
      select mid, info, pnumber into midtmp, infotmp, apnumber from process where flowid=aflowid and pid=apid and subpid=acreatorsubpid;
      set retsubpid = retsubpid + 1;
    ELSE
       set retsubpid = 1;
       set midtmp = 0;
    END IF;
    insert into process (flowid,pid,subpid,mid,created,info,creator,currentuser,lastupdate, pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,infotmp,acreator,acreator,acreatedate, apnumber);
    insert into process_history (flowid,pid,subpid,mid,created,info,creator,currentuser,lastupdate, pnumber) values (aflowid,apid,retsubpid,midtmp,acreatedate,infotmp,acreator,acreator,acreatedate, apnumber);
END;
//

delimiter ;

CREATE TABLE `activity_hierarchy_history` (
  `hierarchyid` INT NOT NULL,
  `parentid` INT NOT NULL DEFAULT 0,
  `userid` VARCHAR(32) BINARY NOT NULL,
  `ownerid` VARCHAR(32) BINARY NOT NULL,
  `flowid` INT NOT NULL,
  `started` DATETIME NULL,
  `expires` DATETIME NULL,
  `permissions` VARCHAR(16) BINARY NULL DEFAULT null,
  `requested` DATETIME NULL,
  `responded` DATETIME NULL,
  PRIMARY KEY (`hierarchyid`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

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
    ON DELETE CASCADE
) ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE external_dms_properties (
  dmsid INT NOT NULL,
  name VARCHAR(64),
  value VARCHAR(64),
  CONSTRAINT fk_dmsid FOREIGN KEY (dmsid)
    REFERENCES external_dms (dmsid)
    ON DELETE CASCADE
) ENGINE = INNODB DEFAULT CHARSET=utf8;

alter table activity add read_flag INT(1) DEFAULT 1;
alter table activity_history add read_flag INT(1) DEFAULT 1;

alter table activity add mid INT DEFAULT 0;
alter table activity_history add mid INT DEFAULT 0;
alter table activity_history add worker INT(1) DEFAULT 0;
alter table activity_history add undoflag INT(1) DEFAULT 0;

update activity set mid=-10;
update activity_history set mid=-10;
update activity_history set worker=1;

DROP VIEW IF EXISTS activity_delegated;
CREATE VIEW activity_delegated
    (hierarchyid, userid, pid, subpid, ownerid, flowid, created, type, started, archived, 
    status, notify, priority, description, url, profilename, requested, responded, read_flag, mid) AS
    SELECT H.hierarchyid, H.userid, A.pid, A.subpid, A.userid AS ownerid, A.flowid, A.created, 
    A.type, A.started, A.archived, A.status, A.notify, A.priority, A.description, A.url, A.profilename,
    H.requested, H.responded, A.read_flag, A.mid
    FROM activity A, activity_hierarchy H
    WHERE ((A.userid = H.ownerid AND H.slave=1) OR (A.userid = H.userid AND slave=0)) 
    AND A.flowid = H.flowid AND H.pending=0 AND A.delegated <> 0;
    
    
alter table modification drop foreign key modification_process_fk;

-- jcosta 17/07/2009
alter table organization_theme add menu_location varchar(256) default 'left';
alter table organization_theme add menu_style varchar(256) default 'list';

-- agon 27/07/2009
delimiter //
DROP PROCEDURE if exists deleteFlow;
//
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

    open CHIST;
    REPEAT
      FETCH CHIST INTO apid;
      IF NOT done THEN
        call deleteProc(aflowid,apid);
      END IF;
    UNTIL done END REPEAT;
    close CHIST;

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
        insert into process_history (flowid,pid,subpid,pnumber,mid,info,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,undoflag) 
            select flowid,pid,subpid,pnumber,mid,info,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,0
                from process where flowid=aflowid and pid=apid and subpid=asubpid;
        delete from process where flowid=aflowid and pid=apid and subpid=asubpid;

        insert into activity_history (userid,flowid,pid,subpid,type,priority,created,started,archived,
            description,url,status,notify,delegated,delegateduserid)
            (SELECT userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,
            status,notify,delegated, userid as delegateduserid from activity where flowid=aflowid and pid=apid and subpid=asubpid);
        delete from activity where flowid=aflowid and pid=apid and subpid=asubpid;
      END IF;
    UNTIL done END REPEAT;
    close COPEN;
    
    -- archive all history
    open CHIST;
    REPEAT
      FETCH CHIST INTO apid;
      IF NOT done THEN
        call archiveProc(tmp,aflowid,apid,NOW());
      END IF;
    UNTIL done END REPEAT;
    close CHIST;
    
  end if;

  -- delete all flow related stuff
  delete from flow_settings where flowid=aflowid;
  delete from flow_settings_history where flowid=aflowid;
  delete from flow_roles where flowid=aflowid;
  delete from flow where flowid=aflowid;
END;
//

DROP PROCEDURE if exists get_next_pid;
//
CREATE PROCEDURE get_next_pid (OUT retpid INTEGER,
                               OUT retsubpid INTEGER,
                               aflowid INTEGER,
                               acreatedate DATETIME,
							   acreator VARCHAR(32))
BEGIN
DECLARE nowdate DATETIME;
    select value into retpid from counter where name='pid';
    set retpid = retpid + 1;
    update counter set value=retpid where name='pid';
    set retsubpid = 1;
	set nowdate = NOW();
    insert into process (flowid,pid,subpid,mid,created,info,creator,pnumber,currentuser,lastupdate) values 
		(aflowid,retpid,retsubpid,1,acreatedate,'',acreator,retpid,acreator,nowdate);
    insert into process_history (flowid,pid,subpid,mid,created,info,creator,pnumber,currentuser,lastupdate) values 
		(aflowid,retpid,retsubpid,1,acreatedate,'',acreator,retpid,acreator,nowdate);
END;
//

DROP PROCEDURE if exists get_next_mid;
//
CREATE PROCEDURE get_next_mid (OUT retmid INTEGER,
                               auserid VARCHAR(256),
                               aflowid INTEGER,
                               apid INTEGER,
                               asubpid INTEGER)
  
BEGIN
    DECLARE tmp integer;
    set retmid = 1;
    select count(mid) into tmp from modification where subpid=asubpid and pid=apid and flowid=aflowid;
    IF tmp > 0 THEN
       select max(mid)+1 into retmid from modification where subpid=asubpid and pid=apid and flowid=aflowid;
    END IF;
    insert into modification (flowid,pid,subpid,mid,mdate,muser) values (aflowid,apid,asubpid,retmid,NOW(),auserid);
END;
//

-- lcabral 29/07/2009
insert into flow_settings (flowid,name,description,value) (select flowid, 'ENABLE_HISTORY' as name,'Permitir Visualizacao de Historicos' as description,'flow_settings.showHist.no' as value from flow_settings group by flowid);
