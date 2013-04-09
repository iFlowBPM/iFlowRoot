ALTER TABLE `documents` ADD COLUMN `tosign` INT NOT NULL DEFAULT 1;

DELIMITER //
CREATE PROCEDURE forward_proc_to_user(aflowid INTEGER,apid INTEGER,auserid VARCHAR(256))
begin
  declare mydate timestamp;
  declare olduser varchar(150);
  declare oldmid int;

  set mydate := now();
  select userid, mid into olduser, oldmid from activity where pid = apid and flowid = aflowid and status = 0 group by pid, flowid;



  update activity set userid = auserid, created = mydate, started = mydate, archived = mydate, profilename = auserid
  where pid = apid and flowid = aflowid and userid = olduser and mid = oldmid;
END
//