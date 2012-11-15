create sequence seq_flow increment by 1 start with 1 nocycle order;

alter table flow add modified date;
update flow set modified = sysdate;
alter table flow modify modified not null;

alter table flow add flowdata BLOB;
update flow set flowdata = '20';
alter table flow modify flowdata not null;

alter table flow add flowversion int;

create table sub_flow (
        flowid                  int,
        flowname                varchar (64) constraint sub_flow_flowname_nn not null,
        flowfile                varchar (64) constraint sub_flow_flowfile_nn not null,
        created                 date default sysdate,
        organizationid          varchar(50) not null,
        flowdata                blob constraint sub_flow_flowdata_nn not null,
        flowversion             int,
        modified                date constraint sub_flow_modified_nn not null,
        constraint sub_flow_pk primary key (flowid)
);
create sequence seq_sub_flow increment by 1 start with 1 nocycle order;

create table flow_template (
        name                varchar (64),
        description         varchar (64) constraint flow_template_desc_nn not null,
        data                blob constraint flow_template_data_nn not null,
        constraint flow_template_pk primary key (name)
);

create table sub_flow_template (
        name                varchar (64),
        description         varchar (64) constraint sub_flow_template_desc_nn not null,
        data                blob constraint sub_flow_template_data_nn not null,
        constraint sub_flow_template_pk primary key (name)
);


-- User activation
alter table users add activated number(1);
update users set activated = 1;
alter table users modify activated not null;
alter table users add password_reset number(1) default 1;
update users set password_reset = 0;
alter table users modify password_reset not null;

create table user_activation (
        userid number (38) not null,
        organizationid number (38) not null,
        unitid number (38) not null,
        code varchar2 (64) not null,
        created timestamp default sysdate not null,
        primary key (userid,organizationid,unitid)
);

ALTER TABLE user_activation
  ADD CONSTRAINT fk_user_activation_org FOREIGN KEY (organizationid)
    REFERENCES organizations (organizationid) ENABLE
  ADD CONSTRAINT fk_user_activation_orgunit FOREIGN KEY (unitid)
    REFERENCES organizational_units (unitid) ENABLE
  ADD CONSTRAINT fk_user_activation_user FOREIGN KEY (userid)
    REFERENCES users (userid) ENABLE;

alter table users add orgadm number(1) default 0;
update users set orgadm = 0;
alter table users modify orgadm not null;

CREATE SEQUENCE SEQ_SYSTEM_USERS START WITH 1 INCREMENT BY 1 MINVALUE 1 NOMAXVALUE CACHE 20 NOCYCLE ORDER;
CREATE TABLE SYSTEM_USERS (
  userid number(10) NOT NULL,
  username VARCHAR2(10) NOT NULL,
  userpassword VARCHAR2(125) NOT NULL,
  email_address VARCHAR2(100) NOT NULL,
  phone_number VARCHAR2(20),
  mobile_number VARCHAR2(20),
  first_name VARCHAR2(50),
  last_name VARCHAR2(50),
  sessionid VARCHAR2(150),
  CONSTRAINT PK_SYSTEM_USERS PRIMARY KEY (USERID),
  CONSTRAINT UK_SYSTEM_USERS_USERNAME UNIQUE (USERNAME),
  CONSTRAINT UK_SYSTEM_USERS_SESSIONID UNIQUE (SESSIONID)
);

alter table new_features add organizationid varchar(50);
alter table flow add organizationid varchar(50) not null;

alter table profiles add organizationid varchar2(50);
update profiles set organizationid='1';
alter table profiles modify organizationid not null;

alter table links_flows add organizationid varchar2(50);
update links_flows set organizationid='1';
alter table links_flows modify organizationid not null;


--alter table users add organizationid number(38) null;
--update users set organizationid=(select organizationid from organizational_units where unitid=users.unitid);
--alter table users modify organizationid default 1 not null;
--
-- NOTE: Change to something else when migrating
--update users set email_address='oscar+'||username||'@iknow.pt';
--
--ALTER TABLE users
--  ADD CONSTRAINT fk_user_organization FOREIGN KEY (organizationid)
--    REFERENCES organizations (organizationid) ENABLE
--  ADD CONSTRAINT UK_USERS_EMAIL_ADDRESS UNIQUE (EMAIL_ADDRESS,ORGANIZATIONID) ENABLE;

-- please ignore any error if these columns already exist
alter table dirty_email add enext_send date default null;
alter table dirty_email add elast_send date default null;
alter table dirty_email add etls number(1) default 0 constraint dirtyemail_etls_nn not null;
alter table dirty_email add eauth number(1) default 0 constraint dirtyemail_eauth_nn not null;
alter table dirty_email add euser varchar2(256);
alter table dirty_email add epass varchar2(256);

-- Event info
create table event_info (
        name varchar2(64) not null,
        description varchar2 (255) not null,
        primary key (name)
);
insert into event_info (name,description) values ('AsyncWait','description=Processo fica bloqueado num bloco NOP ''a espera de de um evento externo');
insert into event_info (name,description) values ('Timer','description=Timer para reencaminhar para ...;workingdays=true;minutes=10');


-- iFlow 3.1.0rc1

alter table organizations add locked number(1) default 0;
update organizations set locked=0;
alter table organizations modify locked not null;


create table flow_history (
        id                  int,
        flowid              int constraint flow_history_flowid_nn not null,
        name                varchar (64) constraint flow_history_name_nn not null,
        description         varchar (64) constraint flow_history_desc_nn not null,
        data                blob constraint flow_history_data_nn not null,
        flowversion         int,
        modified            date constraint flow_history_mod_nn not null,
        constraint flow_history_pk primary key (id)
);
create sequence seq_flow_history increment by 1 start with 1 nocycle order;

create table sub_flow_history (
        id                  int,
        flowid              int constraint sub_flow_history_flowid_nn not null,
        name                varchar (64) constraint sub_flow_history_name_nn not null,
        description         varchar (64) constraint sub_flow_history_desc_nn not null,
        data                blob constraint sub_flow_history_data_nn not null,
        flowversion         int,
        modified            date constraint sub_flow_history_mod_nn not null,
        constraint sub_flow_history_pk primary key (id)
);
create sequence seq_sub_flow_history increment by 1 start with 1 nocycle order;

-- iFlow 3.1.1rc1

-- email modification confirmations
create table email_confirmation (
  userid number (38) not null,
  organizationid number (38) not null,
  email varchar2(100) not null,
  code varchar2(50) not null,
  constraint email_confirmation_pk primary key (userid,organizationid),
  constraint un_email_confirmation_code unique (code)
);

-- iFlow 3.1.2rc1

-- iFlow 3.2.1

-- User settings table
create table user_settings (
  userid varchar2(32) not null,
  lang varchar2(2),
  region varchar2(2),
  timezone varchar2(64),
  constraint user_settings_pk primary key (userid)
);

-- Organization settings table
create table organization_settings (
  organizationid varchar2(32) not null,
  lang varchar2(2),
  region varchar2(2),
  constraint organization_settings_pk primary key (organizationid)
);

alter table organization_settings add timezone varchar2(64);
update organization_settings set timezone='UTC';

-- Notifications table
create sequence seq_notifications;
create table notifications (
  id int,
  created timestamp,
  sender varchar2(192),
  message varchar2(500),
  constraint notifications_pk primary key (id)
);


create table user_notifications (
  userid varchar2(32) not null,
  notificationid int not null,
  isread number(1) default 0 not null,
  constraint user_notifications_pk primary key (userid,notificationid)
);

-- user notification table constraints
ALTER TABLE user_notifications
  ADD CONSTRAINT fk_user_notifications FOREIGN KEY (notificationid)
    REFERENCES notifications (id)
    ON DELETE CASCADE
  ENABLE;

-- support for process creator in process tables
alter table process add creator varchar2(32);
alter table process_history add creator varchar2(32);

update process set creator=(select value from data_string where name='PROCESS_CREATOR' and data_string.pid=process.pid) where exists (select value from data_string where name='PROCESS_CREATOR' and data_string.pid=process.pid);
update process_history set creator=(select value from data_string_history where name='PROCESS_CREATOR' and data_string_history.pid=process_history.pid and mid=1) where exists (select value from data_string_history where name='PROCESS_CREATOR' and data_string_history.pid=process_history.pid and mid=1);

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
    insert into process         (flowid,pid,subpid,mid,created,info,creator) values
        (aflowid,retpid,retsubpid,0,acreatedate,'',acreator);
     insert into process_history (flowid,pid,subpid,mid,created,info,creator) values
        (aflowid,retpid,retsubpid,0,acreatedate,'',acreator);
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      retpid := 0;
      retsubpid := 0;
  END;
END;
/

alter table flow_history add "comment" varchar2(512);
update flow_history set "comment" = '(no comment)';
alter table sub_flow_history add "comment" varchar2(512);
update sub_flow_history set "comment" = '(no comment)';

alter table process add idx0 varchar2(1024);
alter table process add idx1 varchar2(1024);
alter table process add idx2 varchar2(1024);
alter table process add idx3 varchar2(1024);
alter table process add idx4 varchar2(1024);

create index IND_PROCESS_IDX0 on process(IDX0);
create index IND_PROCESS_IDX1 on process(IDX1);
create index IND_PROCESS_IDX2 on process(IDX2);
create index IND_PROCESS_IDX3 on process(IDX3);
create index IND_PROCESS_IDX4 on process(IDX4);

alter table process_history add idx0 varchar2(1024);
alter table process_history add idx1 varchar2(1024);
alter table process_history add idx2 varchar2(1024);
alter table process_history add idx3 varchar2(1024);
alter table process_history add idx4 varchar2(1024);

create index IND_PROCESS_HISTORY_IDX0 on process_history(IDX0);
create index IND_PROCESS_HISTORY_IDX1 on process_history(IDX1);
create index IND_PROCESS_HISTORY_IDX2 on process_history(IDX2);
create index IND_PROCESS_HISTORY_IDX3 on process_history(IDX3);
create index IND_PROCESS_HISTORY_IDX4 on process_history(IDX4);

alter table flow add name_idx0 varchar2(64);
alter table flow add name_idx1 varchar2(64);
alter table flow add name_idx2 varchar2(64);
alter table flow add name_idx3 varchar2(64);
alter table flow add name_idx4 varchar2(64);

alter table process_history add closed number(1) default 0 constraint process_history_nn NOT NULL;


