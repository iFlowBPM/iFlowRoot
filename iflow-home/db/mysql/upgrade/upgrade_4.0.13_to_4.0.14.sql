drop procedure if exists get_next_pid;
DELIMITER //
CREATE PROCEDURE get_next_pid (OUT retpid INTEGER,
                               OUT retsubpid INTEGER,
                               aflowid INTEGER,
                               acreatedate DATETIME,
							   acreator VARCHAR(100))
BEGIN
    DECLARE nowdate DATETIME;
    DECLARE modificationdate DATETIME;
    DECLARE count INT DEfAULT 1000;
    DECLARE afectedrows INT DEFAULT 0;
    WHILE count > 0 AND afectedrows <= 0 DO
        SET nowdate = NOW();
        SET count = count - 1;
        SELECT value, modification INTO retpid, modificationdate FROM counter WHERE name = 'pid';
        set retpid = retpid + 1;
        update counter set value=retpid, modification=nowdate where name='pid' and modification=modificationdate;
        SET afectedrows = ROW_COUNT();
    END WHILE;
    IF count > 0 THEN 
        set retsubpid = 1;
        insert into process (flowid,pid,subpid,mid,created,creator,pnumber,currentuser,lastupdate) values 
            (aflowid,retpid,retsubpid,1,acreatedate,acreator,retpid,acreator,nowdate);
        insert into process_history (flowid,pid,subpid,mid,created,creator,pnumber,currentuser,lastupdate) values
            (aflowid,retpid,retsubpid,1,acreatedate,acreator,retpid,acreator,nowdate);
    END IF;
END 
// DELIMITER ;

DELIMITER ;
create view process_intervenients (userid, pid) as
    select distinct userid, pid from activity
    union 
    select distinct userid, pid from activity_history;
DELIMITER //

alter table `user_passimage` add `rubimage` LONGBLOB;


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
      select mid, pnumber into midtmp, apnumber from process where flowid=aflowid and pid=apid and subpid=acreatorsubpid;
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

ALTER TABLE flow ADD COLUMN `max_block_id` INT;

DROP TABLE IF EXISTS subflow_block_mapping;
CREATE TABLE  subflow_block_mapping (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `flowname` varchar(64) NOT NULL,
  `sub_flowname` varchar(64) NOT NULL,
  `original_blockid` int NOT NULL,
  `mapped_blockid` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

ALTER TABLE flow_history ADD COLUMN `max_block_id` INT;