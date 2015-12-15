CREATE SEQUENCE SEQ_NODEKEY INCREMENT BY 1 MINVALUE 1 CACHE 20;

CREATE TABLE ACTIVE_NODE 
(
  NODEKEY VARCHAR2(50) NOT NULL , 
  EXPIRATION DATE NOT NULL , 
  CONSTRAINT ACTIVE_NODE_PK PRIMARY KEY ( NODEKEY )  ENABLE 
);

CREATE OR REPLACE 
PROCEDURE GET_NEXT_NODEKEY 
(
  RETNODEKEY OUT NUMBER  
) IS 
BEGIN
  SELECT SEQ_NODEKEY.NEXTVAL INTO RETNODEKEY FROM DUAL;
END;

CREATE SEQUENCE sqe_sorefresh INCREMENT BY 1 MINVALUE 1 CACHE 20;

CREATE TABLE  sharedobjectrefresh (
  id number,
  flowid number ,
  CONSTRAINT sorefresh_pk PRIMARY KEY ( id )  ENABLE 
);

col maxpid new_value _maxpid
select max(pid) maxpid from process;
create sequence SEQ_NEXT_PID start with &&_maxpid increment by 1;

-- gets next available pid (from counter table) and updates counter table for a given flow.
CREATE OR REPLACE PROCEDURE get_next_pid (retpid OUT NUMBER,
                                          retsubpid OUT NUMBER,
                                          aflowid IN NUMBER,
                                          acreatedate IN DATE,
                                          acreator IN VARCHAR2) IS
BEGIN
  BEGIN
    select SEQ_NEX_PID.nextval into retpid from dual;
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