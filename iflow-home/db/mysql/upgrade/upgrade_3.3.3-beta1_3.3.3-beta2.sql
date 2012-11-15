DELIMITER //

-- deletes all associated data for the given process
drop procedure deleteProc;
//
CREATE PROCEDURE deleteProc(aflowid INTEGER,apid INTEGER)
BEGIN

  delete from data_numeric where flowid=aflowid and pid=apid;
  delete from data_numeric_history where flowid=aflowid and pid=apid;
  delete from data_string where flowid=aflowid and pid=apid;
  delete from data_string_history where flowid=aflowid and pid=apid;


  delete from activity where flowid=aflowid and pid=apid;
  delete from activity_history where flowid=aflowid and pid=apid;

  delete from modification where flowid=aflowid and pid=apid;

  delete from process where flowid=aflowid and pid=apid;
  delete from process_history where flowid=aflowid and pid=apid;

  delete from flow_state where flowid=aflowid and pid=apid;
  delete from flow_state_history where flowid=aflowid and pid=apid;
END;
//

-- trigger fix: also update if description changes
drop trigger trig_flow_state_update;//
create trigger trig_flow_state_update after update on flow_state for each row
begin

  declare tmp integer;
  declare toinsert integer;

  if (NEW.state <> OLD.state or NEW.result <> OLD.result) then

    set tmp = 0;
    set toinsert = 1;

    if (NEW.exit_flag = 0) then
      select count(1) into tmp from flow_state_history where flowid=NEW.flowid and pid=NEW.pid and subpid=NEW.subpid and mid=(select max(mid) from flow_state_history where flowid=NEW.flowid and pid=NEW.pid and subpid=NEW.subpid) and undoflag=1;
      if (tmp > 0) then
        set toinsert = 0;
      end if;
    end if;

    if (toinsert > 0) then
      select count(1) into tmp from flow_state_history where flowid=NEW.flowid and pid=NEW.pid and subpid=NEW.subpid;
      if (tmp > 0) then
        select max(mid) into tmp from flow_state_history where flowid=NEW.flowid and pid=NEW.pid and subpid=NEW.subpid;
      end if;
      set tmp = tmp+1;
      insert into flow_state_history (flowid,pid,subpid,state,result,mdate,mid,exit_flag,undoflag)
        values (NEW.flowid,NEW.pid,NEW.subpid,NEW.state,NEW.result,NOW(),tmp,NEW.exit_flag,0);
    end if;
  end if;
end;//

DELIMITER ;
