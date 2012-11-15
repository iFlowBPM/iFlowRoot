CREATE SEQUENCE SEQ_SERIES START WITH 1 INCREMENT BY 1 MINVALUE 1 NOMAXVALUE CACHE 20 NOCYCLE ORDER;
create table series (
	id int,
	created number constraint series_created_nn not null,
	enabled number(1) default 1 constraint series_enabled_nn not null,
	state number(1) constraint series_state_nn not null,
	name varchar2(100) constraint series_name_nn not null,
	kind varchar2(100) constraint series_kind_nn not null,
	pattern varchar2(200) constraint series_pattern_nn not null,
	pattern_field_formats varchar2(200),
	start_with varchar2(200) constraint series_start_nn not null,
	max_value varchar2(200),
	current_value varchar2(200),
	constraint pk_series primary key (id),
	constraint uk_series_name unique (name)
);

alter table flow add seriesid number;

alter table process add pnumber varchar2(128);
update process set pnumber=pid;
alter table process modify pnumber  constraint process_pnumber_nn not null;


alter table process_history add pnumber varchar2(128);
update process_history set pnumber=pid;
alter table process_history modify pnumber  constraint process_hist_pnumber_nn not null;

CREATE OR REPLACE TRIGGER TRIG_INSERT_SERIES
BEFORE INSERT ON SERIES
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
SELECT SEQ_SERIES.NEXTVAL INTO :NEW.ID FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER TRIG_UPDATE_SERIES
BEFORE UPDATE ON SERIES
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
IF :NEW.STATE < :OLD.STATE THEN
	raise_application_error (-20999,'Invalid state');
END IF;
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
    insert into process         (flowid,pid,subpid,mid,created,info,creator,pnumber) values
        (aflowid,retpid,retsubpid,0,acreatedate,'',acreator, retpid);
     insert into process_history (flowid,pid,subpid,mid,created,info,creator,pnumber) values
        (aflowid,retpid,retsubpid,0,acreatedate,'',acreator,retpid);
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      retpid := 0;
      retsubpid := 0;
  END;
END;
/

commit;

