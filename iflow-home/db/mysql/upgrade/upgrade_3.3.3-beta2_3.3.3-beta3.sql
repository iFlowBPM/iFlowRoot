DELIMITER //
-- updates a data value in table table_name.
-- if data does not exist, it creates it. otherwise, updates existing one.
-- if value is null and data exists, it deletes it from table_name.
-- when data changes (creation, update or deletion), value is also stored in respective historic table.
drop procedure updateValue;//

CREATE PROCEDURE updateValue(atablename VARCHAR(64),
                             is_numeric INTEGER,
                             amid INTEGER,
                             aflowid INTEGER,
                             apid INTEGER,
                             asubpid INTEGER,
                              aname VARCHAR(64),
                              valstring VARCHAR(1024),
                              valnum DOUBLE,
                              historifyflag INTEGER)
BEGIN

DECLARE tmp integer;
DECLARE process integer default 0;
-- DECLARE value varchar(1024);
    

    if(is_numeric = 1) then -- NUMBER DATA
              select count(1) into tmp from data_numeric 
              where flowid=aflowid and pid=apid and subpid=asubpid and name=aname;
              IF tmp=0 AND valnum is not null THEN -- NEW VALUE
                   insert into data_numeric (flowid,pid,subpid,name,value) 
             values (aflowid,apid,asubpid,aname,valnum);
             set process = 1;
              ELSEIF valnum is not null THEN -- UPADTE VALUE
                       update data_numeric set value=valnum
                 where flowid=aflowid and pid=apid and subpid=asubpid and name=aname;
                 set process = 1;
              ELSE -- REMOVE VALUE
                     delete from data_numeric
               where flowid=aflowid and pid=apid and subpid=asubpid and name=aname;
               set process = 1;
              END IF;
              
                  -- now update value and historify. process will only be 0 in the case of
                  -- no prev value and null curr value
                  IF (process = 1 AND historifyflag = 0 AND valnum is not null) THEN
                   insert into data_numeric_history (flowid,pid,subpid,mid,name,value) 
             values (aflowid,apid,asubpid,amid,aname,valnum);                   
              END iF;
    else -- STRING DATA
              select count(1) into tmp from data_string 
              where flowid=aflowid and pid=apid and subpid=asubpid and name=aname;
              IF tmp=0 AND valstring is not null THEN -- NEW VALUE
                   insert into data_string (flowid,pid,subpid,name,value) 
             values (aflowid,apid,asubpid,aname,valstring);
             set process = 1;
              ELSEIF valstring is not null THEN -- UPADTE VALUE
                       update data_string set value=valstring
                 where flowid=aflowid and pid=apid and subpid=asubpid and name=aname;
                 set process = 1;
              ELSE -- REMOVE VALUE
                     delete from data_string
               where flowid=aflowid and pid=apid and subpid=asubpid and name=aname;
               set process = 1;
              END IF;
              
                  -- now update value and historify. process will only be 0 in the case of
                  -- no prev value and null curr value
                  IF (process = 1 AND historifyflag = 0 AND valstring is not null) THEN
                   insert into data_string_history (flowid,pid,subpid,mid,name,value) 
             values (aflowid,apid,asubpid,amid,aname,valstring);                   
              END iF;
    end if;
END;
//

DELIMITER ;