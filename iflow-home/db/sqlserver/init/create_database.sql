CREATE TABLE system_users (
  userid INT NOT NULL IDENTITY(1,1),
  username VARCHAR(50) NOT NULL,
  userpassword VARCHAR(125) NOT NULL,
  email_address VARCHAR(100) NOT NULL,
  phone_number VARCHAR(20) NULL,
  mobile_number VARCHAR(20) NULL,
  first_name VARCHAR(50) NULL,
  last_name VARCHAR(50) NULL,
  sessionid VARCHAR(150) NULL,
  CONSTRAINT pk_system_users PRIMARY KEY (userid),
  --CONSTRAINT uk_system_users_sessionid UNIQUE (sessionid), -- UNIQUES com NULLS em SQLServer nï¿½o funcionam bem
  CONSTRAINT uk_system_users_username UNIQUE (username))
GO

CREATE TABLE flow (
  flowid INT NOT NULL IDENTITY(1,1),
  flowname VARCHAR(64) NOT NULL,
  flowfile VARCHAR(64) NOT NULL,
  enabled INT NULL DEFAULT 0,
  created DATETIME NULL,
  organizationid VARCHAR(50) NOT NULL,
  flowdata VARBINARY(max) not null,
  flowversion int,
  modified datetime not null,
  name_idx0 varchar(64),
  name_idx1 varchar(64),
  name_idx2 varchar(64),
  name_idx3 varchar(64),
  name_idx4 varchar(64),
  name_idx5 varchar(64),
  name_idx6 varchar(64),
  name_idx7 varchar(64),
  name_idx8 varchar(64),
  name_idx9 varchar(64),
  name_idx10 varchar(64),
  name_idx11 varchar(64),
  name_idx12 varchar(64),
  name_idx13 varchar(64),
  name_idx14 varchar(64),
  name_idx15 varchar(64),
  name_idx16 varchar(64),
  name_idx17 varchar(64),
  name_idx18 varchar(64),
  name_idx19 varchar(64),
  seriesid int,
  type_code varchar(1) default 'W',
  PRIMARY KEY (flowid))
GO
CREATE NONCLUSTERED INDEX ind_flow ON flow (enabled)
GO

CREATE TABLE process (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  pnumber varchar(128) NOT NULL,
  mid INT NOT NULL,
  creator	VARCHAR(100) NOT NULL,
  created DATETIME NOT NULL,
  currentuser VARCHAR(100) NOT NULL,
  lastupdate DATETIME NOT NULL,
  procdata VARCHAR(MAX),
  closed INT NULL DEFAULT 0,
  canceled INT NULL DEFAULT 0,
  idx0 varchar(1024),
  idx1 varchar(1024),
  idx2 varchar(1024),
  idx3 varchar(1024),
  idx4 varchar(1024),
  idx5 varchar(1024),
  idx6 varchar(1024),
  idx7 varchar(1024),
  idx8 varchar(1024),
  idx9 varchar(1024),
  idx10 varchar(1024),
  idx11 varchar(1024),
  idx12 varchar(1024),
  idx13 varchar(1024),
  idx14 varchar(1024),
  idx15 varchar(1024),
  idx16 varchar(1024),
  idx17 varchar(1024),
  idx18 varchar(1024),
  idx19 varchar(1024),
  PRIMARY KEY (flowid, pid,subpid),
  FOREIGN KEY (flowid)
    REFERENCES flow (flowid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO
CREATE NONCLUSTERED INDEX ind_process ON process (created)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX0 ON process (idx0)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX1 ON process (idx1)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX2 ON process (idx2)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX3 ON process (idx3)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX4 ON process (idx4)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX5 ON process (idx5)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX6 ON process (idx6)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX7 ON process (idx7)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX8 ON process (idx8)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX9 ON process (idx9)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX10 ON process (idx10)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX11 ON process (idx11)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX12 ON process (idx12)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX13 ON process (idx13)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX14 ON process (idx14)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX15 ON process (idx15)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX16 ON process (idx16)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX17 ON process (idx17)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX18 ON process (idx18)
CREATE NONCLUSTERED INDEX IND_PROCESS_IDX19 ON process (idx19)
GO

CREATE TABLE process_history (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  pnumber varchar(128) NOT NULL,
  mid INT NOT NULL,
  creator VARCHAR(100) NOT NULL,
  created DATETIME NOT NULL,
  currentuser VARCHAR(100) NOT NULL,
  lastupdate DATETIME NOT NULL,
  procdata VARCHAR(MAX),
  procdatazip VARBINARY(MAX),
  closed INT NULL DEFAULT 0,
  canceled INT NULL DEFAULT 0,
  idx0 varchar(1024),
  idx1 varchar(1024),
  idx2 varchar(1024),
  idx3 varchar(1024),
  idx4 varchar(1024),
  idx5 varchar(1024),
  idx6 varchar(1024),
  idx7 varchar(1024),
  idx8 varchar(1024),
  idx9 varchar(1024),
  idx10 varchar(1024),
  idx11 varchar(1024),
  idx12 varchar(1024),
  idx13 varchar(1024),
  idx14 varchar(1024),
  idx15 varchar(1024),
  idx16 varchar(1024),
  idx17 varchar(1024),
  idx18 varchar(1024),
  idx19 varchar(1024),
  undoflag INT NULL DEFAULT 0,
  PRIMARY KEY (flowid, pid, subpid, mid),
  FOREIGN KEY (flowid)
    REFERENCES flow (flowid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX0 ON process_history (idx0)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX1 ON process_history (idx1)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX2 ON process_history (idx2)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX3 ON process_history (idx3)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX4 ON process_history (idx4)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX5 ON process_history (idx5)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX6 ON process_history (idx6)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX7 ON process_history (idx7)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX8 ON process_history (idx8)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX9 ON process_history (idx9)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX10 ON process_history (idx10)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX11 ON process_history (idx11)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX12 ON process_history (idx12)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX13 ON process_history (idx13)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX14 ON process_history (idx14)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX15 ON process_history (idx15)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX16 ON process_history (idx16)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX17 ON process_history (idx17)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX18 ON process_history (idx18)
CREATE NONCLUSTERED INDEX IND_PROCESS_HISTORY_IDX19 ON process_history (idx19)  
GO

CREATE TABLE modification (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  mid INT NOT NULL,
  mdate DATETIME NULL,
  muser VARCHAR(256) NULL,
  PRIMARY KEY (pid, subpid, mid, flowid))
GO

CREATE TABLE profiles (
  profileid INT NOT NULL IDENTITY(1,1),
  name VARCHAR(50) NOT NULL,
  description VARCHAR(1024) NULL,
  organizationid VARCHAR(50)  DEFAULT '1' NOT NULL,
  PRIMARY KEY (profileid))
GO

CREATE TABLE flow_roles (
  flowid INT NOT NULL,
  profileid INT NOT NULL,
  permissions VARCHAR(16) NULL,
  PRIMARY KEY (profileid, flowid),
  FOREIGN KEY (flowid)
    REFERENCES flow (flowid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (profileid) 
  	REFERENCES profiles (profileid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE flow_state (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  state INT NOT NULL,
  result VARCHAR(1024) NULL,
  mdate DATETIME NULL,
  exit_flag INT NOT NULL DEFAULT 0,
  mid INT NULL DEFAULT 0,
  closed INT NULL DEFAULT 0,
  canceled INT NULL DEFAULT 0,
  PRIMARY KEY (flowid, pid, subpid),
  FOREIGN KEY (flowid)
    REFERENCES flow (flowid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO
CREATE NONCLUSTERED INDEX ind_flow_state ON flow_state (state)
GO

CREATE TABLE flow_state_history (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NULL DEFAULT 1,
  state INT NOT NULL,
  result VARCHAR(1024) NULL,
  mdate DATETIME NULL,
  mid INT NULL DEFAULT 0,
  exit_flag INT NOT NULL DEFAULT 0,
  undoflag INT NULL DEFAULT 0,
  exit_port VARCHAR(64) NULL,
  FOREIGN KEY (flowid)
    REFERENCES flow (flowid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO
CREATE NONCLUSTERED INDEX ind_flow_state_history ON flow_state_history (mdate, result, mid, exit_flag)
CREATE NONCLUSTERED INDEX ind_flow_state_history2 ON flow_state_history (flowid, pid, subpid, mid)
GO

CREATE TABLE flow_settings (
  flowid INT NOT NULL,
  name VARCHAR(64) NOT NULL,
  description VARCHAR(1024) NULL,
  value VARCHAR(1024) NULL,
  isquery INT NULL DEFAULT 0,
  mdate DATETIME NULL,
  PRIMARY KEY (flowid, name),
  FOREIGN KEY (flowid)
    REFERENCES flow (flowid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE flow_settings_history (
  flowid INT NOT NULL,
  name VARCHAR(64) NULL,
  description VARCHAR(1024) NULL,
  value VARCHAR(1024) NULL,
  isquery INT NULL DEFAULT 0,
  mdate DATETIME NULL,
  mid INT NULL,
  FOREIGN KEY (flowid)
    REFERENCES flow (flowid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE activity (
  userid VARCHAR(100) NOT NULL,
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  type INT NULL,
  priority INT NULL,
  created DATETIME NULL,
  started DATETIME NULL,
  archived DATETIME NULL,
  description VARCHAR(256) NULL,
  url VARCHAR(256) NULL,
  status INT NULL,
  notify INT NULL DEFAULT 0,
  delegated INT NULL DEFAULT 0,
  profilename VARCHAR(256),
  read_flag INT NULL DEFAULT 1,
  mid INT NULL DEFAULT 0,
  folderid INT NULL,
  PRIMARY KEY (flowid, pid, subpid, userid),
  FOREIGN KEY (flowid, pid, subpid)
    REFERENCES process (flowid, pid, subpid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE sequences (
  sequence char(32) NOT NULL,
  seqval INT NOT NULL,
  PRIMARY KEY (sequence))
GO

CREATE TABLE activity_hierarchy (
  hierarchyid INT NOT NULL IDENTITY(1,1),
  parentid INT NOT NULL DEFAULT 0,
  userid VARCHAR(100) NOT NULL,
  ownerid VARCHAR(100) NOT NULL,
  flowid INT NOT NULL,
  slave INT NULL DEFAULT 1,
  expires DATETIME NULL,
  permissions VARCHAR(16) NULL DEFAULT null,
  pending INT NULL DEFAULT 1,
  acceptkey VARCHAR(32) NULL DEFAULT null,
  rejectkey VARCHAR(32) NULL DEFAULT null,
  requested DATETIME NULL,
  responded DATETIME NULL,
  PRIMARY KEY (hierarchyid),
  CONSTRAINT activity_hierarchy_uk UNIQUE (flowid, ownerid, userid))
GO

CREATE TABLE activity_hierarchy_history (
  hierarchyid INT NOT NULL,
  parentid INT NOT NULL DEFAULT 0,
  userid VARCHAR(100) NOT NULL,
  ownerid VARCHAR(100) NOT NULL,
  flowid INT NOT NULL,
  started DATETIME NULL,
  expires DATETIME NULL,
  permissions VARCHAR(16) NULL DEFAULT null,
  requested DATETIME NULL,
  responded DATETIME NULL,
  PRIMARY KEY (hierarchyid))
GO

CREATE TABLE activity_history (
  userid VARCHAR(100) NULL,
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NULL DEFAULT 1,
  type INT NULL,
  priority INT NULL,
  created DATETIME NULL,
  started DATETIME NULL,
  archived DATETIME NULL,
  description VARCHAR(256) NULL,
  url VARCHAR(256) NULL,
  status INT NULL,
  notify INT NULL,
  delegated INT NULL DEFAULT 0,
  delegateduserid VARCHAR(100) NULL,
  profilename VARCHAR(256),
  read_flag INT NULL DEFAULT 1,
  mid INT NULL DEFAULT 0,
  worker INT NULL DEFAULT 0,
  undoflag INT NULL DEFAULT 0,
  FOREIGN KEY (flowid, pid, subpid, mid)
    REFERENCES process_history (flowid, pid, subpid, mid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE profiles_tabs (
  profileid INT DEFAULT NULL,
  tabid INT DEFAULT NULL,
  FOREIGN KEY (profileid)
    REFERENCES profiles (profileid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE applications (
  appid INT NOT NULL IDENTITY(1,1),
  name VARCHAR(50) NOT NULL,
  description VARCHAR(125) NULL,
  PRIMARY KEY (appid),
  CONSTRAINT uk_application_name UNIQUE (name))
GO

CREATE TABLE counter (
  name VARCHAR(64) NOT NULL,
  value INT NULL,
  modification DATETIME NULL,
  PRIMARY KEY (name))
GO

CREATE TABLE dirty_email (
  eid INT NOT NULL IDENTITY(1,1),
  eserver VARCHAR(256) NOT NULL,
  eport INT DEFAULT -1,
  efrom VARCHAR(256) NOT NULL,
  eto VARBINARY(max) NOT NULL,
  ecc VARBINARY(max) NULL,
  esubject VARCHAR(1024) NULL,
  ebody VARBINARY(max) NULL,
  ehtml INT NOT NULL DEFAULT 0,
  ecreated DATETIME NULL,
  etries INT DEFAULT 0 NOT NULL,
  enext_send DATETIME NULL,
  elast_send DATETIME NULL,
  etls INT NOT NULL DEFAULT 0,
  eauth INT NOT NULL DEFAULT 0,
  euser VARCHAR(256),
  epass VARCHAR(256),
  PRIMARY KEY (eid))
GO

CREATE TABLE documents (
  docid INT NOT NULL  IDENTITY(1,1),
  filename VARCHAR(128) NOT NULL,
  datadoc VARBINARY(max) NULL,
  docurl VARCHAR(2000),
  updated DATETIME NULL,
  flowid int not null default 0,
  pid int not null default 0,
  subpid int not null default 0,
  tosign int NOT NULL DEFAULT 1,
  PRIMARY KEY (docid))
GO

CREATE TABLE email (
  eid INT NOT NULL IDENTITY(1,1),
  eserver VARCHAR(256) NOT NULL,
  eport INT DEFAULT -1,
  efrom VARCHAR(256) NOT NULL,
  eto VARBINARY(max) NOT NULL,
  ecc VARBINARY(max) NULL,
  esubject VARCHAR(1024) NULL,
  ebody VARBINARY(max) NULL,
  ehtml INT NOT NULL DEFAULT 0,
  ecreated DATETIME NULL,
  etries  INT DEFAULT 0 NOT NULL,
  enext_send DATETIME NULL,
  elast_send DATETIME NULL,
  etls INT NOT NULL DEFAULT 0,
  eauth INT NOT NULL DEFAULT 0,
  euser VARCHAR(256),
  epass VARCHAR(256),
  PRIMARY KEY (eid))
GO

CREATE TABLE event_data (
  eventid INT NOT NULL IDENTITY(1,1),
  fid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NULL DEFAULT 1,
  blockid INT NOT NULL,
  starttime VARCHAR(MAX) NULL,
  type VARCHAR(255) NULL,
  properties VARCHAR(1024) NULL,
  processed INT NULL DEFAULT 0,
  userid VARCHAR(1024) NULL,
  PRIMARY KEY (eventid),
  FOREIGN KEY (fid)
    REFERENCES flow (flowid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (fid, pid, subpid)
    REFERENCES process (flowid, pid, subpid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE forkjoin_blocks (
  flowid INT NOT NULL,
  blockid INT NOT NULL,
  type INT NULL,
  PRIMARY KEY (flowid, blockid),
  FOREIGN KEY (flowid)
    REFERENCES flow (flowid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE forkjoin_hierarchy (
  flowid INT NOT NULL,
  parentblockid INT NOT NULL,
  blockid INT NOT NULL,
  PRIMARY KEY (blockid, flowid, parentblockid),
  FOREIGN KEY (flowid, parentblockid)
    REFERENCES forkjoin_blocks (flowid, blockid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (flowid, blockid)
    REFERENCES forkjoin_blocks (flowid, blockid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE forkjoin_mines (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  blockid INT NOT NULL,
  mined INT NULL,
  locked INT NULL,
  PRIMARY KEY (pid, blockid, flowid),
  FOREIGN KEY (flowid)
    REFERENCES flow (flowid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE forkjoin_state_dep (
  flowid INT NOT NULL,
  parentblockid INT NOT NULL,
  blockid INT NOT NULL,
  PRIMARY KEY (flowid, parentblockid, blockid),
  FOREIGN KEY (flowid, parentblockid)
    REFERENCES forkjoin_blocks (flowid, blockid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE iflow_errors (
  errorid INT NOT NULL IDENTITY(1,1),
  userid VARCHAR(100) NOT NULL,
  created DATETIME NULL,
  method VARCHAR(128) NULL,
  object VARCHAR(128) NULL,
  flowid INT NULL,
  pid INT NULL,
  subpid INT NULL,
  errortype INT NULL,
  description VARCHAR(512) NOT NULL,
  PRIMARY KEY (errorid))
GO

CREATE TABLE links_flows (
  linkid INT NOT NULL IDENTITY(1,1),
  parentid INT NULL DEFAULT 0,
  flowid INT NULL DEFAULT 0,
  name VARCHAR(64) NULL DEFAULT null,
  url VARCHAR(256) NULL DEFAULT null,
  organizationid VARCHAR(50)  DEFAULT '1' NOT NULL,
  PRIMARY KEY (linkid))
GO

CREATE TABLE new_features (
  newfeaturesid INT NOT NULL IDENTITY(1,1),
  version VARCHAR(64) NOT NULL,
  feature VARCHAR(128) NOT NULL,
  description VARCHAR(1024) NOT NULL,
  created DATETIME NULL,
  organizationid VARCHAR(50),
  PRIMARY KEY (newfeaturesid),
  CONSTRAINT new_features_uk UNIQUE (version, feature))
GO

CREATE TABLE organizations (
  organizationid INT NOT NULL IDENTITY(1,1),
  name VARCHAR(50) NOT NULL,
  description VARCHAR(150) NULL,
  style_url VARCHAR(128) NULL,
  logo_url VARCHAR(128) NULL,
  locked INT default 0 not null,
  PRIMARY KEY (organizationid),
  CONSTRAINT un_organization_name UNIQUE (name))
GO

CREATE TABLE organizational_units (
  unitid INT NOT NULL IDENTITY(1,1),
  parent_id INT NOT NULL,
  organizationid INT NOT NULL,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(150) NULL,
  PRIMARY KEY (unitid),
  FOREIGN KEY (organizationid)
    REFERENCES organizations (organizationid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE organization_theme (
  organizationid VARCHAR(50) NOT NULL,
  theme VARCHAR(256) NOT NULL,
  style_url VARCHAR(256) NOT NULL,
  logo_url VARCHAR(256) NOT NULL,
  menu_location VARCHAR(256) default 'left',
  menu_style VARCHAR(256) default 'list', 
  proc_menu_visible INT default 1,
  PRIMARY KEY (organizationid))
GO

CREATE TABLE queue_proc (
  id INT NOT NULL IDENTITY(1,1),
  object INT NOT NULL,
  groupid VARCHAR(64) NULL,
  flowid INT NULL,
  pid INT NULL,
  properties VARCHAR(1024) NULL,
  creation_date DATETIME NOT NULL,
  PRIMARY KEY (id))
GO
CREATE NONCLUSTERED INDEX ind_queue_proc ON queue_proc (id, object, groupid)
GO

CREATE TABLE queue_data (
  queue_proc_id INT NOT NULL,
  name VARCHAR(64) NOT NULL,
  value VARCHAR(1024) NULL,
  PRIMARY KEY (name, queue_proc_id),
  FOREIGN KEY (queue_proc_id)
    REFERENCES queue_proc (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE users (
  userid INT NOT NULL IDENTITY(1,1),
  unitid INT NULL,
  username VARCHAR(100) NOT NULL,
  userpassword VARCHAR(125) NOT NULL,
  email_address VARCHAR(100) NOT NULL,
  gender CHAR(1) NOT NULL,
  first_name VARCHAR(50) NULL,
  last_name VARCHAR(50) NULL,
  phone_number VARCHAR(20) NULL,
  fax_number VARCHAR(20) NULL,
  mobile_number VARCHAR(20) NULL,
  company_phone VARCHAR(20) NULL,
  sessionid VARCHAR(150) NULL,
  activated INT NOT NULL,
  password_reset INT DEFAULT 1 NOT NULL,
  orgadm INT DEFAULT 0 NOT NULL,
  PRIMARY KEY (userid),
  --CONSTRAINT uk_users_sessionid UNIQUE (sessionid), -- UNIQUES com NULLS em SQLServer nï¿½o funcionam bem
  CONSTRAINT uk_users_username UNIQUE (username),
  FOREIGN KEY (unitid)
    REFERENCES organizational_units (unitid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE unitmanagers (
  userid INT NOT NULL,
  unitid INT NOT NULL,
  PRIMARY KEY (unitid, userid),
  FOREIGN KEY (unitid)
    REFERENCES organizational_units (unitid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (userid)
    REFERENCES users (userid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE userprofiles (
  userid INT NOT NULL,
  profileid INT NOT NULL,
  PRIMARY KEY (profileid, userid),
  FOREIGN KEY (profileid)
    REFERENCES profiles (profileid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (userid)
    REFERENCES users (userid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE sub_flow (
  flowid INT NOT NULL IDENTITY(1,1),
  flowname VARCHAR(64) NOT NULL,
  flowfile VARCHAR(64) NOT NULL,
  created DATETIME NOT NULL,
  organizationid VARCHAR(50) NOT NULL,
  flowdata VARBINARY(max) not null,
  flowversion int,
  modified DATETIME not null,
  PRIMARY KEY (flowid))
GO

CREATE TABLE flow_history (
  id int IDENTITY(1,1),
  flowid int not null,
  name VARCHAR(64) not null,
  description VARCHAR(64) not null,
  data VARBINARY(max) not null,
  flowversion int,
  modified DATETIME not null,
  comment varchar(512),
  primary key (id))
GO

CREATE TABLE sub_flow_history (
        id                  int IDENTITY(1,1),
        flowid              int not null,
        name                VARCHAR(64) not null,
        description         VARCHAR(64) not null,
        data                VARBINARY(max) not null,
        flowversion         int,
        modified            DATETIME not null,
        comment           varchar(512),
        primary key (id))
GO

CREATE TABLE flow_template (
        name                VARCHAR(64) not null,
        description         VARCHAR(64) not null,
        data                VARBINARY(max) not null,
        primary key (name))
GO

CREATE TABLE sub_flow_template (
        name                VARCHAR(64) not null,
        description         VARCHAR(64) not null,
        data                VARBINARY(max) not null,
        primary key (name)
)
GO

CREATE TABLE user_activation (
        userid int not null,
        organizationid int not null,
        unitid int not null,
        code VARCHAR(64) not null,
        created datetime default GETDATE() not null,
  PRIMARY KEY (userid,organizationid,unitid),
  FOREIGN KEY (organizationid)
    REFERENCES organizations (organizationid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (unitid)
    REFERENCES organizational_units (unitid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (userid)
    REFERENCES users (userid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE event_info (
        name varchar(64) not null,
        description VARCHAR(255) not null,
        primary key (name))
GO

CREATE TABLE seq_flow_settings (
        id int not null,
        primary key (id))
GO
-- initializes data
INSERT INTO seq_flow_settings VALUES (0)
GO

CREATE TABLE email_confirmation (
  userid int not null,
  organizationid int not null,
  email varchar(100) not null,
  code varchar(50) not null,
  primary key (userid,organizationid),
  constraint un_email_confirmation_code unique (code))
GO

CREATE TABLE user_settings (
  userid varchar(100) not null,
  lang varchar(2),
  region varchar(2),
  timezone varchar(64),
  tutorial varchar(20),
  help_mode INT default 1,
  tutorial_mode INT default 1,
  primary key (userid))
GO

CREATE TABLE organization_settings (
  organizationid varchar(32) not null,
  lang varchar(2),
  region varchar(2),
  timezone varchar(64),
  constraint organization_settings_pk primary key (organizationid))
GO

CREATE TABLE notifications (
  id int IDENTITY(1,1),
  created DATETIME,
  sender varchar(192),
  message varchar(500),
  flowid INT NULL DEFAULT 0,
  constraint notifications_pk primary key (id))
GO

CREATE TABLE user_notifications (
  userid varchar(100) not null,
  notificationid int not null,
  isread INT not null default 0,
  constraint user_notifications_pk primary key (userid,notificationid),
  FOREIGN KEY (notificationid)
    REFERENCES notifications (id)
    ON DELETE CASCADE)
GO

CREATE TABLE series (
	id int not null IDENTITY(1,1),
	organizationid VARCHAR(50)  DEFAULT '1' NOT NULL,
	created bigint not null,
	enabled INT not null default 1,
	state INT not null,
	name varchar(100) not null,
	kind varchar(100) not null,
	pattern varchar(200) not null,
	pattern_field_formats varchar(200),
	start_with varchar(200) not null,
	max_value varchar(200),
	current_value varchar(200),
	extra_options varchar(200),
	constraint series_pk primary key (id),
	CONSTRAINT series_name_uk UNIQUE (name, organizationid))
GO

-- process archive
CREATE TABLE process_archive (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  pnumber varchar(128) NOT NULL,
  mid INT NOT NULL,
  creator	VARCHAR(100) NOT NULL,
  created DATETIME NOT NULL,
  currentuser VARCHAR(100) NOT NULL,
  lastupdate DATETIME NOT NULL,
  procdata VARCHAR(MAX),
  closed INT NULL DEFAULT 0,
  archived DATETIME NOT NULL,
  idx0 varchar(1024),
  idx1 varchar(1024),
  idx2 varchar(1024),
  idx3 varchar(1024),
  idx4 varchar(1024),
  idx5 varchar(1024),
  idx6 varchar(1024),
  idx7 varchar(1024),
  idx8 varchar(1024),
  idx9 varchar(1024),
  idx10 varchar(1024),
  idx11 varchar(1024),
  idx12 varchar(1024),
  idx13 varchar(1024),
  idx14 varchar(1024),
  idx15 varchar(1024),
  idx16 varchar(1024),
  idx17 varchar(1024),
  idx18 varchar(1024),
  idx19 varchar(1024),
  PRIMARY KEY (flowid, pid,subpid))
GO
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX0 ON process_archive (idx0)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX1 ON process_archive (idx1)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX2 ON process_archive (idx2)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX3 ON process_archive (idx3)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX4 ON process_archive (idx4)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX5 ON process_archive (idx5)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX6 ON process_archive (idx6)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX7 ON process_archive (idx7)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX8 ON process_archive (idx8)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX9 ON process_archive (idx9)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX10 ON process_archive (idx10)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX11 ON process_archive (idx11)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX12 ON process_archive (idx12)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX13 ON process_archive (idx13)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX14 ON process_archive (idx14)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX15 ON process_archive (idx15)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX16 ON process_archive (idx16)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX17 ON process_archive (idx17)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX18 ON process_archive (idx18)
CREATE NONCLUSTERED INDEX IND_PROCESS_ARCH_IDX19 ON process_archive (idx19)
GO

CREATE TABLE activity_archive (
  userid VARCHAR(100) NULL,
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NULL DEFAULT 1,
  type INT NULL,
  priority INT NULL,
  created DATETIME NULL,
  started DATETIME NULL,
  archived DATETIME NULL,
  description VARCHAR(256) NULL,
  url VARCHAR(256) NULL,
  status INT NULL,
 NOTify INT NULL,
  delegated INT NULL DEFAULT 0,
  delegateduserid VARCHAR(100) NULL,
  profilename VARCHAR(256))
GO

CREATE TABLE modification_archive (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  mid INT NOT NULL,
  mdate DATETIME NULL,
  muser VARCHAR(256) NULL,
  PRIMARY KEY (pid, subpid, mid, flowid))
GO

CREATE TABLE migration_log (
  migrator VARCHAR(16) NULL,
  task VARCHAR(128) NOT NULL,
  finished DATETIME NULL,
  --CONSTRAINT uk_migration_log UNIQUE (migrator,task)) -- UNIQUES com NULLS em SQLServer nï¿½o funcionam bem
)
GO

CREATE TABLE reporting (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  cod_reporting VARCHAR(1024),
  start_reporting DATETIME,
  stop_reporting DATETIME,
  ttl DATETIME,
  active INT NOT NULL DEFAULT 0) 
GO

CREATE TABLE log (
  log_id INT NOT NULL,
  log VARCHAR(2048) NOT NULL DEFAULT '',
  username VARCHAR(50),
  caller VARCHAR(16),
  method VARCHAR(16),
  creation_date DATETIME NOT NULL,
  CONSTRAINT log_id_pk PRIMARY KEY (log_id)) 
GO

CREATE TABLE flow_state_log (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  state INT NOT NULL,
  log_id INT NOT NULL,
  FOREIGN KEY (log_id)
    REFERENCES log (log_id)
    ON DELETE CASCADE) 
GO

CREATE TABLE external_dms (
  dmsid INT NOT NULL,
  docid INT NOT NULL,
  uuid VARCHAR(64) NOT NULL,
  scheme VARCHAR(64),
  address VARCHAR(64),
  path VARCHAR(64),
  CONSTRAINT dmsid_pk PRIMARY KEY (dmsid),
  FOREIGN KEY (docid)
    REFERENCES documents (docid)
    ON DELETE CASCADE,
  CONSTRAINT un_external_dms_uuid UNIQUE (uuid)) 
GO

CREATE TABLE external_dms_properties (
  dmsid INT NOT NULL,
  name VARCHAR(64),
  value VARCHAR(1024),
  FOREIGN KEY (dmsid)
    REFERENCES external_dms (dmsid)
    ON DELETE CASCADE) 
GO

CREATE TABLE upgrade_log (
  signature	VARCHAR(125)	NOT NULL,
  executed	INT			NOT NULL DEFAULT 0,
  error		INT			NOT NULL DEFAULT 0,
  log_id	INT			NOT NULL,
  CONSTRAINT PK_UPGRADE_LOG PRIMARY KEY (signature),
  FOREIGN KEY (log_id)
    REFERENCES log (log_id)
    ON DELETE CASCADE) 
GO

CREATE TABLE organizations_tabs (
  organizationid INT DEFAULT NULL,
  tabid INT DEFAULT NULL,
  FOREIGN KEY (organizationid)
    REFERENCES organizations (organizationid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION) 
GO

CREATE TABLE application_flows (
  appid INT NOT NULL,
  flowid INT NOT NULL,
  PRIMARY KEY (appid, flowid),
  FOREIGN KEY (flowid)
    REFERENCES flow (flowid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (appid)
    REFERENCES applications (appid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE application_organization (
  organizationid INT NOT NULL,
  appid INT NOT NULL,
  PRIMARY KEY (organizationid, appid),
  FOREIGN KEY (organizationid)
    REFERENCES organizations (organizationid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (appid)
    REFERENCES applications (appid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE application_profiles (
  appID INT NOT NULL,
  profileID INT NOT NULL,
  organizationID INT NOT NULL,
  PRIMARY KEY (appID,profileID,organizationID),
  FOREIGN KEY (profileid) 
	REFERENCES profiles (profileid) 
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION,
  FOREIGN KEY (appid) 
	REFERENCES applications (appid) 
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION,
  FOREIGN KEY (organizationID)
  	REFERENCES organizations (organizationid)
  	ON DELETE NO ACTION
  	ON UPDATE NO ACTION)
GO

CREATE TABLE application_neededprofiles (
  appID INT NOT NULL, 
  profileID INT NOT NULL,
  PRIMARY KEY (appID, profileID),
  FOREIGN KEY (appID)
    REFERENCES applications (appid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (profileID)
    REFERENCES profiles (profileid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE application_transaction (
  transactionid INT NOT NULL IDENTITY(1,1),
  clientid varchar(100) DEFAULT NULL,
  ownerid varchar(100) DEFAULT NULL,
  applicationid INT DEFAULT NULL,
  state INT DEFAULT '0',
  blocked INT DEFAULT '0',
  requested datetime DEFAULT NULL,
  responded datetime DEFAULT NULL,
  acceptkey varchar(100) DEFAULT NULL,
  rejectkey varchar(100) DEFAULT NULL,
  PRIMARY KEY (transactionid))
GO

CREATE TABLE application_profilesmap (
  appid INT NOT NULL, 
  oldprofile INT NOT NULL, 
  newprofile INT NOT NULL,
  orgid INT NOT NULL,
  FOREIGN KEY (appid)
    REFERENCES applications (appid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (oldprofile)
    REFERENCES profiles (profileid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (newprofile)
    REFERENCES profiles (profileid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (orgid)
    REFERENCES organizations (organizationid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE tabledef (
  tabledefId INT NOT NULL IDENTITY(1,1),
  organizationid INT NOT NULL,
  name VARCHAR(15) NOT NULL,
  PRIMARY KEY (tabledefId),
  CONSTRAINT uk_tabledef_orgid_name UNIQUE (organizationid, name),
  FOREIGN KEY (organizationid)
    REFERENCES organizations (organizationid)
	ON DELETE NO ACTION
	ON UPDATE NO ACTION)
GO
	
CREATE TABLE columndef (
columnDefId INT NOT NULL IDENTITY(1,1),
tableDefId INT NOT NULL,
name VARCHAR(30) NOT NULL,
type INT NOT NULL,
defaultValue VARCHAR(100) NULL,
required BIT NOT NULL,
isUnique BIT NULL,
PRIMARY KEY (columnDefId),
CONSTRAINT uk_columndef_tabledefId_name UNIQUE (tableDefId, name),
FOREIGN KEY (tableDefId)
	REFERENCES tabledef (tabledefId)
	ON DELETE NO ACTION
	ON UPDATE NO ACTION)
GO
	
CREATE TABLE application_tabledef (
appTableDefId INT NOT NULL IDENTITY(1,1),
appId INT NOT NULL,
tableDefId INT NOT NULL,
PRIMARY KEY (appTableDefId),
CONSTRAINT uk_appTableDef_appIdTableDefId UNIQUE (appId, tableDefId),
FOREIGN KEY (appId)
	REFERENCES applications (appid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
FOREIGN KEY (tableDefId)
    REFERENCES tabledef (tabledefId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO
	
CREATE TABLE application_columndef (
  appColumnDefId INT NOT NULL IDENTITY(1,1),
  appTableDefId INT NOT NULL,
  columnDefId INT NOT NULL,
  PRIMARY KEY (appColumnDefId),
  CONSTRAINT uk_appCol_appTabId_colId UNIQUE (appTableDefId, columnDefId),
  FOREIGN KEY (appTableDefId)
    REFERENCES application_tabledef (appTableDefId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (columnDefId)
    REFERENCES columndef (columnDefId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE organization_tabledef (
  orgTableDefId INT NOT NULL IDENTITY(1,1),
  appId INT NOT NULL,
  organizationId INT NOT NULL,
  appTableDefId INT NOT NULL,
  PRIMARY KEY (orgTableDefId),
  CONSTRAINT uk_orgTab_appid_orgId_appTabId UNIQUE (appId, organizationId, appTableDefId),
  FOREIGN KEY (organizationId, appId )
    REFERENCES application_organization (organizationid, appid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (appTableDefId)
    REFERENCES application_tabledef (appTableDefId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO
	
CREATE TABLE organization_columndef (
  orgColumnDefId INT NOT NULL IDENTITY(1,1),
  orgTableDefId INT NOT NULL,
  appColumnDefId INT NOT NULL,
  PRIMARY KEY (orgColumnDefId),
  CONSTRAINT uk_orgCol_appTabId_appColId UNIQUE (orgTableDefId, appColumnDefId),
  FOREIGN KEY (appColumnDefId)
    REFERENCES application_columndef (appColumnDefId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (orgTableDefId)
    REFERENCES organization_tabledef (orgTableDefId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE application_organicalunits (
  appid INT NOT NULL,
  unitid INT NOT NULL,
  organizationid INT NOT NULL,
  PRIMARY KEY (appid,unitid,organizationid),
  FOREIGN KEY (unitid) 
	REFERENCES organizational_units (unitid) 
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION,
  FOREIGN KEY (appid) 
	REFERENCES applications (appid) 
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION,
  FOREIGN KEY (organizationID)
  	REFERENCES organizations (organizationid)
  	ON DELETE NO ACTION
  	ON UPDATE NO ACTION)
GO

CREATE TABLE application_organicalunitsmap (
  appid INT NOT NULL, 
  oldunit INT NOT NULL, 
  newunit INT NOT NULL,
  orgid INT NOT NULL,
  FOREIGN KEY (appid)
    REFERENCES applications (appid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (oldunit)
    REFERENCES organizational_units (unitid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (newunit)
    REFERENCES organizational_units (unitid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (orgid)
    REFERENCES organizations (organizationid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

CREATE TABLE menu (
  menuId INT NOT NULL IDENTITY(1,1),
  parentMenuId INT NULL,
  organizationId INT NOT NULL, 
  appId INT NULL, 
  flowId INT NULL, 
  description VARCHAR(45) NULL, 
  menuType INT NOT NULL, 
  link VARCHAR(256) NOT NULL, 
  norder INT NOT NULL,
  level INT NOT NULL,
  PRIMARY KEY (menuId),
  --CONSTRAINT parentMenuId_UNIQUE UNIQUE (parentMenuId, norder), -- UNIQUES com NULLS em SQLServer nï¿½o funcionam bem
  FOREIGN KEY (parentMenuId)
    REFERENCES menu (menuId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (organizationId)
    REFERENCES organizations (organizationid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION, 
  FOREIGN KEY (appId)
    REFERENCES applications (appid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (flowId)
    REFERENCES flow (flowid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
GO

--
-- @view  activity tabela que contem as actividades delegadas a outros
-- @field id identificador da hierarquia de delegacoes
-- @field userid identificador alfanumerico do utilizador
-- @field pid identificador numerico do processo
-- @field ownerid identificador alfanumerico do utilizador a quem a actividade pertence
-- @field flowid identificador numerico do fluxo
CREATE VIEW activity_delegated 
AS
  SELECT H.hierarchyid, H.userid, A.pid, A.subpid, A.userid as ownerid, A.flowid, A.created, 
         A.type, A.started, A.archived, A.status, A.notify, A.priority, A.description, A.url,
         A.profilename, H.requested, H.responded, A.read_flag, A.mid
  FROM activity A, activity_hierarchy H
  WHERE ((A.userid = H.ownerid AND H.slave=1) or (A.userid = H.userid AND slave=0)) 
        AND A.flowid = H.flowid AND H.pending=0 AND A.delegated <> 0
GO

-- deletes all associated data for the given process
CREATE PROCEDURE deleteProc
  @aflowid INT,
  @apid INT
AS
BEGIN
  DELETE FROM activity WHERE flowid=@aflowid AND pid=@apid
  DELETE FROM activity_history WHERE flowid=@aflowid AND pid=@apid

  DELETE FROM modification WHERE flowid=@aflowid AND pid=@apid

  DELETE FROM process WHERE flowid=@aflowid AND pid=@apid
  DELETE FROM process_history WHERE flowid=@aflowid AND pid=@apid

  DELETE FROM flow_state WHERE flowid=@aflowid AND pid=@apid
  DELETE FROM flow_state_history WHERE flowid=@aflowid AND pid=@apid
END
GO

CREATE PROCEDURE deleteFlow
  @auserid VARCHAR(256),
  @aflowid INT,
  @delprocs INT
AS
BEGIN
  DECLARE @tmp INTEGER
  DECLARE @error INTEGER
  DECLARE @apid INTEGER
  DECLARE @asubpid INTEGER
  DECLARE @aname VARCHAR(256)
  DECLARE @avaluestring VARCHAR(256)
  DECLARE @avaluenumber VARCHAR(256)

  DECLARE COPEN CURSOR FOR SELECT distinct(pid) as dpid, subpid FROM process WHERE flowid=@aflowid
  DECLARE CHIST CURSOR FOR SELECT distinct(pid) as dpid FROM process_history WHERE flowid=@aflowid

  SET @error = 1

  -- first handle procs
  IF (@delprocs = 1)
  BEGIN
    -- DELETE all flow procs (including historic ones)

    OPEN COPEN
    FETCH NEXT FROM COPEN INTO @apid, @asubpid
    WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC deleteProc @aflowid, @apid 
      FETCH NEXT FROM COPEN INTO @apid, @asubpid
    END
    CLOSE COPEN
    DEALLOCATE COPEN

    OPEN CHIST
    FETCH NEXT FROM CHIST INTO @apid
    WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC deleteProc @aflowid, @apid
      FETCH NEXT FROM CHIST INTO @apid
    END
    CLOSE CHIST
    DEALLOCATE CHIST
  END
  ELSE
  BEGIN
    -- move opened procs to historic tables
    -- move scheduled activities to historic table

    OPEN COPEN
    FETCH NEXT FROM COPEN INTO @apid, @asubpid
    WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC get_next_mid @tmp, @auserid, @aflowid, @apid, @asubpid
      UPDATE process SET mid=@tmp WHERE subpid=@asubpid AND pid=@apid AND flowid=@aflowid
      UPDATE flow_state SET mid=@tmp WHERE subpid=@asubpid AND pid=@apid AND flowid=@aflowid

      -- move data
      INSERT INTO process_history (flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19,undoflag) 
             SELECT flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19,0
             FROM process WHERE flowid=@aflowid AND pid=@apid AND subpid=@asubpid
      UPDATE process SET closed=1 WHERE flowid=@aflowid AND pid=@apid AND subpid=@asubpid

      INSERT INTO activity_history (userid,flowid,pid,subpid,type,priority,created,started,archived,
                                    description,url,status,notify,delegated,delegateduserid)
             (SELECT userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,
                     status,notify,delegated, userid as delegateduserid 
              FROM activity WHERE flowid=@aflowid AND pid=@apid AND subpid=@asubpid)
      DELETE FROM activity WHERE flowid=@aflowid AND pid=@apid AND subpid=@asubpid
      FETCH NEXT FROM COPEN INTO @apid, @asubpid
    END
    CLOSE COPEN
    DEALLOCATE COPEN

    -- archive all history
    OPEN CHIST
    FETCH NEXT FROM CHIST INTO @apid
    WHILE @@FETCH_STATUS = 0
    BEGIN
      DECLARE @dtnow datetime
      SET @dtnow = GETDATE()
      EXEC archiveProc @tmp, @aflowid, @apid, @dtnow
      FETCH NEXT FROM CHIST INTO @apid
    END
    CLOSE CHIST
    DEALLOCATE  CHIST
  END

  DELETE FROM flow_settings WHERE flowid=@aflowid
  DELETE FROM flow_settings_history WHERE flowid=@aflowid
  DELETE FROM flow_roles WHERE flowid=@aflowid
  DELETE FROM application_flows WHERE flowid=@aflowid
  DELETE FROM flow WHERE flowid=@aflowid
END
GO


-- updates a given flow settings.
-- IF setting does not exist, it creates it.
-- IF it exists, it updates it.
-- IF it exists AND settingtype=1 AND setting value is null, it deletes setting.
-- in all changing cases, the changing is saved in setting historic table.
CREATE PROCEDURE updateFlowSetting
  @aflowid INT,
  @amid INT,
  @aname VARCHAR(64),
  @adescription VARCHAR(1024),
  @avalue VARCHAR(1024),
  @aisquery INT,
  @settingtype INT
AS
BEGIN
  DECLARE @tmp INT
  DECLARE @process INT
  DECLARE @nowdate DATETIME

  SET @process = 0
  SET @nowdate = GETDATE()
  select @tmp = count(1) FROM flow_settings WHERE flowid=@aflowid AND name=@aname

  IF @tmp = 0
  BEGIN
    INSERT INTO flow_settings (flowid,name,description,value,isquery,mdate) 
                       VALUES (@aflowid,@aname,@adescription,@avalue,@aisquery,@nowdate)
    SET @process = 1
  END
  ELSE IF (@settingtype = 0)
    UPDATE flow_settings SET value=@avalue, mdate=@nowdate WHERE flowid=@aflowid AND name=@aname
  ELSE IF (@settingtype = 1 AND @avalue IS NOT NULL)
  BEGIN
    UPDATE flow_settings SET value=@avalue, mdate=@nowdate WHERE flowid=@aflowid AND name=@aname
    SET @process = 1
  END
  ELSE IF (@settingtype = 1 AND @avalue IS NULL)
  BEGIN
    DELETE FROM flow_settings WHERE flowid=@aflowid AND name=@aname
    SET @process = 1
  END
  ELSE IF (@settingtype = 2)
  BEGIN
    UPDATE flow_settings SET description=@adescription, mdate=@nowdate WHERE flowid=@aflowid AND name=@aname
    SET @process = 1
  END
  ELSE
    SET @process = 0

  -- now UPDATE value AND historify.process will only be 0 in the case of
  -- no prev value AND null curr value
  IF (@process = 1)
    INSERT INTO flow_settings_history (flowid, name, description, value, mdate, mid) 
                               VALUES (@aflowid, @aname, @adescription, @avalue, @nowdate, @amid)
END
GO

-- gets next available pid (from counter table) AND updates counter table for a given flow.
CREATE PROCEDURE get_next_pid 
  @retpid INT OUT,
  @retsubpid INT OUT,
  @aflowid INT,
  @acreatedate DATETIME,
  @acreator VARCHAR(100)
AS
BEGIN
  DECLARE @nowdate DATETIME
  SET @nowdate = GETDATE()
  SELECT @retpid = value FROM counter WHERE name='pid'
  SET @retpid = @retpid + 1
  UPDATE counter SET value=@retpid WHERE name='pid'
  SET @retsubpid = 1
  INSERT INTO process (flowid,pid,subpid,mid,created,creator,pnumber,currentuser,lastupdate) 
         VALUES (@aflowid,@retpid,@retsubpid,1,@acreatedate,@acreator,@retpid,@acreator,@nowdate)
  INSERT INTO process_history (flowid,pid,subpid,mid,created,creator,pnumber,currentuser,lastupdate)
         VALUES (@aflowid,@retpid,@retsubpid,1,@acreatedate,@acreator,@retpid,@acreator,@nowdate)
END
GO

-- gets next available sub process id
CREATE PROCEDURE get_next_sub_pid 
  @retsubpid INT OUT,
  @aflowid INT,
  @apid INT,
  @acreatedate DATETIME,
  @acreator VARCHAR(100),
  @acreatorsubpid INT
AS
BEGIN
  DECLARE @midtmp INT
  DECLARE @tmp INT
  DECLARE @apnumber VARCHAR(128)

  SET @midtmp = 1
  SELECT @tmp = count(subpid) FROM process_history WHERE flowid=@aflowid AND pid=@apid
  IF @tmp > 0
  BEGIN
    SELECT @retsubpid = max(subpid) FROM process_history WHERE flowid=@aflowid AND pid=@apid
    SELECT @midtmp=mid, @apnumber=pnumber FROM process WHERE flowid=@aflowid AND pid=@apid AND subpid=@acreatorsubpid
    SET @retsubpid = @retsubpid + 1
  END
  ELSE
  BEGIN
    SET @retsubpid = 1
    SET @midtmp = 0
  END
  INSERT INTO process (flowid,pid,subpid,mid,created,creator,currentuser,lastupdate, pnumber) 
         VALUES (@aflowid,@apid,@retsubpid,@midtmp,@acreatedate,@acreator,@acreator,@acreatedate, @apnumber)
  INSERT INTO process_history (flowid,pid,subpid,mid,created,creator,currentuser,lastupdate, pnumber)
         VALUES (@aflowid,@apid,@retsubpid,@midtmp,@acreatedate,@acreator,@acreator,@acreatedate, @apnumber)
END
GO

-- gets next available mid for a given process AND updates modification table.
CREATE PROCEDURE get_next_mid 
  @retmid INT OUT,
  @auserid VARCHAR(256),
  @aflowid INT,
  @apid INT,
  @asubpid INT
AS
BEGIN
  DECLARE @tmp INT
  SET @retmid = 1
  SELECT @tmp = count(mid) FROM modification WHERE subpid=@asubpid AND pid=@apid AND flowid=@aflowid
  IF @tmp > 0
    SELECT @retmid = max(mid)+1 FROM modification WHERE subpid=@asubpid AND pid=@apid AND flowid=@aflowid
  INSERT INTO modification (flowid,pid,subpid,mid,mdate,muser) VALUES (@aflowid,@apid,@asubpid,@retmid,GETDATE(),@auserid)
END
GO

--CREATE FUNCTION seq_flow_settings_nextval()
CREATE PROCEDURE seq_flow_settings_nextval
AS
BEGIN
 UPDATE seq_flow_settings SET id=id+1
 SELECT max(id) mid FROM seq_flow_settings
END
GO

-- Simulates a sequence in mysql using data in the table seq_flow_settings
-- This function must be created by a SUPER user (such as root)
CREATE FUNCTION get_procdata_value (@procdata VARCHAR(MAX), @varname VARCHAR(256))
  RETURNS VARCHAR(1024)
AS
BEGIN
  DECLARE @stripped VARCHAR(MAX)
  DECLARE @xmlTagPos INT
  DECLARE @xmlTag VARCHAR(264)
  SET @xmlTag = '<a n="'+@varname+'">'
  SET @xmlTagPos = patindex('%'+@xmlTag+'%', @procdata)
  IF (@xmlTagPos = 0)
    RETURN NULL
  SET @xmlTagPos = @xmlTagPos+len(@xmlTag)
  SET @stripped = substring(@procdata, @xmlTagPos, 50000)
  RETURN substring(@stripped, 1, patindex('%</a>%',@stripped)-1)
END
GO

--CREATE FUNCTION sequence(@name CHAR(32))
CREATE PROCEDURE sequence
  @name CHAR(32)
AS
BEGIN
    UPDATE sequences SET seqval = seqval + 1 WHERE sequence=@name
    SELECT max(seqval) FROM sequences
END
GO

-- move a process to archive tables
CREATE PROCEDURE archiveProc
  @archiveResult INT OUT,
  @aflowid INT,
  @apid INT,
  @aarchivedate DATETIME
AS
BEGIN
  DECLARE @open INT
 
  SET @archiveResult = 0

  BEGIN TRY
    BEGIN TRANSACTION

    -- check IF process (or any of its subprocesses) is still open
    SELECT @open = count(1) FROM process WHERE flowid=@aflowid AND pid=@apid AND closed=0
    
    IF @open = 0
	BEGIN

      -- so far so nice, copy to archive
      INSERT INTO process_archive (flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,archived,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19) 
        SELECT flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,@aarchivedate,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19
                FROM process_history WHERE flowid=@aflowid AND pid=@apid

      INSERT INTO activity_archive (userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename)
        SELECT userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename 
        FROM activity_history WHERE flowid=@aflowid AND pid=@apid
  
      INSERT INTO modification_archive (flowid,pid,subpid,mid,mdate,muser)
        SELECT flowid,pid,subpid,mid,mdate,muser FROM modification WHERE flowid=@aflowid AND pid=@apid

      -- finally, DELETE FROM history
      DELETE FROM modification WHERE flowid=@aflowid AND pid=@apid
      DELETE FROM activity_history WHERE flowid=@aflowid AND pid=@apid
      DELETE FROM process_history WHERE flowid=@aflowid AND pid=@apid
      DELETE FROM process WHERE flowid=@aflowid AND pid=@apid

      SET @archiveResult = 1
    END
  
    COMMIT
  END TRY
  BEGIN CATCH
    ROLLBACK
    SET @archiveResult = -1
  END CATCH
END
GO

INSERT INTO counter VALUES ('pid',0,GETDATE())
INSERT INTO counter VALUES ('docid',1,GETDATE())
INSERT INTO counter VALUES ('emailid',1,GETDATE())
INSERT INTO counter VALUES ('cid',1,GETDATE())
INSERT INTO counter VALUES ('flowid',0,GETDATE())
GO

SET IDENTITY_INSERT organizations ON
INSERT INTO organizations (organizationid,name,description) VALUES (1,'SYS','SYS ORG')
SET IDENTITY_INSERT organizations OFF
GO

INSERT INTO organizational_units (organizationid,parent_id,name,description) 
       SELECT organizationid,-1,'SYS','SYS' FROM organizations WHERE name='SYS'
GO

-- USERS

-- admin admin
-- <user> password
SET IDENTITY_INSERT system_users ON
INSERT INTO system_users (userid, username, userpassword, email_address,first_name,last_name)
       VALUES (1, 'admin', 'F/KbuDOEofgjp7/9yUGnrw==', 'change@this.address','','')
SET IDENTITY_INSERT system_users OFF

-- Event info
INSERT INTO event_info (name,description) VALUES ('AsyncWait','description=Processo fica bloqueado num bloco NOP ''a espera de de um evento externo')
INSERT INTO event_info (name,description) VALUES ('Timer','description=Timer para reencaminhar para ...;workingdays=true;minutes=10')
INSERT INTO event_info (name,description) VALUES ('Deadline', 'dateVar=date')
GO

-- ----------------
--    START QRTZ    --
-- -------------------

CREATE TABLE QRTZ_JOB_DETAILS (
  JOB_NAME  VARCHAR(200) NOT NULL,
  JOB_GROUP VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(250) NULL,
  JOB_CLASS_NAME VARCHAR(250) NOT NULL,
  IS_DURABLE VARCHAR(1) NOT NULL,
  IS_VOLATILE VARCHAR(1) NOT NULL,
  IS_STATEFUL VARCHAR(1) NOT NULL,
  REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
  JOB_DATA VARBINARY(max) NULL,
  PRIMARY KEY (JOB_NAME,JOB_GROUP))
GO

CREATE TABLE QRTZ_JOB_LISTENERS (
  JOB_NAME  VARCHAR(200) NOT NULL,
  JOB_GROUP VARCHAR(200) NOT NULL,
  JOB_LISTENER VARCHAR(200) NOT NULL,
  PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),
  FOREIGN KEY (JOB_NAME,JOB_GROUP)
    REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP))
GO

CREATE TABLE QRTZ_TRIGGERS (
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  JOB_NAME  VARCHAR(200) NOT NULL,
  JOB_GROUP VARCHAR(200) NOT NULL,
  IS_VOLATILE VARCHAR(1) NOT NULL,
  DESCRIPTION VARCHAR(250) NULL,
  NEXT_FIRE_TIME BIGINT NULL,
  PREV_FIRE_TIME BIGINT NULL,
  PRIORITY INT NULL,
  TRIGGER_STATE VARCHAR(16) NOT NULL,
  TRIGGER_TYPE VARCHAR(8) NOT NULL,
  START_TIME BIGINT NOT NULL,
  END_TIME BIGINT NULL,
  CALENDAR_NAME VARCHAR(200) NULL,
  MISFIRE_INSTR SMALLINT NULL,
  JOB_DATA VARBINARY(max) NULL,
  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (JOB_NAME,JOB_GROUP)
      REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP))
GO

CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  REPEAT_COUNT BIGINT NOT NULL,
  REPEAT_INTERVAL BIGINT NOT NULL,
  TIMES_TRIGGERED BIGINT NOT NULL,
  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
      REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP))
GO

CREATE TABLE QRTZ_CRON_TRIGGERS (
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  CRON_EXPRESSION VARCHAR(200) NOT NULL,
  TIME_ZONE_ID VARCHAR(80),
  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
      REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP))
GO

CREATE TABLE QRTZ_BLOB_TRIGGERS (
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  BLOB_DATA VARBINARY(max) NULL,
  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
      REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP))
GO

CREATE TABLE QRTZ_TRIGGER_LISTENERS (
  TRIGGER_NAME  VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  TRIGGER_LISTENER VARCHAR(200) NOT NULL,
  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),
  FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
      REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP))
GO

CREATE TABLE QRTZ_CALENDARS (
  CALENDAR_NAME  VARCHAR(200) NOT NULL,
  CALENDAR VARBINARY(max) NOT NULL,
  PRIMARY KEY (CALENDAR_NAME))
GO

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
  TRIGGER_GROUP  VARCHAR(200) NOT NULL, 
  PRIMARY KEY (TRIGGER_GROUP))
GO

CREATE TABLE QRTZ_FIRED_TRIGGERS (
  ENTRY_ID VARCHAR(95) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  IS_VOLATILE VARCHAR(1) NOT NULL,
  INSTANCE_NAME VARCHAR(200) NOT NULL,
  FIRED_TIME BIGINT NOT NULL,
  PRIORITY INT NOT NULL,
  STATE VARCHAR(16) NOT NULL,
  JOB_NAME VARCHAR(200) NULL,
  JOB_GROUP VARCHAR(200) NULL,
  IS_STATEFUL VARCHAR(1) NULL,
  REQUESTS_RECOVERY VARCHAR(1) NULL,
  PRIMARY KEY (ENTRY_ID))
GO

CREATE TABLE QRTZ_SCHEDULER_STATE (
  INSTANCE_NAME VARCHAR(200) NOT NULL,
  LAST_CHECKIN_TIME BIGINT NOT NULL,
  CHECKIN_INTERVAL BIGINT NOT NULL,
  PRIMARY KEY (INSTANCE_NAME))
GO

CREATE TABLE QRTZ_LOCKS (
  LOCK_NAME  VARCHAR(40) NOT NULL, 
  PRIMARY KEY (LOCK_NAME))
GO

CREATE TABLE user_passimage (
  passid INT NOT NULL IDENTITY(1,1),
  userid INT NOT NULL,
  passimage VARBINARY(max) ,
  rubimage VARBINARY(max),
  PRIMARY KEY (passid),
  FOREIGN KEY (userid)
  	REFERENCES users (userid)
) 

INSERT INTO QRTZ_LOCKS values('TRIGGER_ACCESS')
INSERT INTO QRTZ_LOCKS values('JOB_ACCESS')
INSERT INTO QRTZ_LOCKS values('CALENDAR_ACCESS')
INSERT INTO QRTZ_LOCKS values('STATE_ACCESS')
INSERT INTO QRTZ_LOCKS values('MISFIRE_ACCESS')
-- -------------------
--     END  QRTZ    --
-- -------------------

CREATE NONCLUSTERED INDEX idx_fsh_pid ON flow_state_history(pid)
GO
CREATE VIEW process_intervenients AS
    SELECT DISTINCT userid, pid FROM activity
    UNION 
    SELECT DISTINCT userid, pid FROM activity_history;

    -- 
GO
CREATE TABLE folder (
  id INT NOT NULL IDENTITY,
  name VARCHAR(50)  NOT NULL,
  color VARCHAR(10),
  userid VARCHAR(100) NOT NULL,
  PRIMARY KEY (id)
)
GO

ALTER TABLE activity
ADD FOREIGN KEY (folderid)
REFERENCES folder(id)

CREATE TABLE label (
  id INT NOT NULL IDENTITY,
  name VARCHAR(50)  NOT NULL,
  description VARCHAR(125),
  icon VARCHAR(50),
  PRIMARY KEY (id),
  CONSTRAINT uk_label_name UNIQUE (name),
  CONSTRAINT uk_label_icon UNIQUE (icon)
)
GO

CREATE TABLE process_label (
  id INT NOT NULL IDENTITY,
  labelid INT NOT NULL,
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  FOREIGN KEY (labelid)
    REFERENCES label (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (flowid, pid, subpid)
    REFERENCES process (flowid, pid, subpid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
GO

CREATE TABLE process_label_history (
  id INT NOT NULL IDENTITY,
  labelid INT  NOT NULL,
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  userid VARCHAR(100) NOT NULL, 
  date DATETIME NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (labelid)
    REFERENCES label (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (flowid, pid, subpid)
    REFERENCES process (flowid, pid, subpid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
GO

CREATE TABLE deadline (
  id INT NOT NULL IDENTITY,
  deadline VARCHAR(20),
  userid VARCHAR(100) NOT NULL,
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  FOREIGN KEY (flowid, pid, subpid)
    REFERENCES process (flowid, pid, subpid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
GO

CREATE TABLE deadline_history (
  id INT NOT NULL IDENTITY,
  deadline VARCHAR(20),
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  userid VARCHAR(100) NOT NULL,
  date DATETIME NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (flowid, pid, subpid)
    REFERENCES process (flowid, pid, subpid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
GO

CREATE TABLE user_session (
  id INT NOT NULL IDENTITY,
  userid VARCHAR(100) NOT NULL,
  session  VARBINARY(max) NOT NULL,
  PRIMARY KEY (id)
)
GO

CREATE TABLE comment (
  id INT NOT NULL identity,
  date DATETIME NOT NULL,
  userid VARCHAR(100) NOT NULL,
  comment VARCHAR(125) NOT NULL,
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  foreign key (flowid, pid, subpid) references process (flowid, pid, subpid)
)
go
CREATE TABLE comment_history (
  id INT NOT NULL IDENTITY (1,1),
  comment VARCHAR(125) NOT NULL,
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  userid VARCHAR(100) NOT NULL,
  date DATETIME NOT NULL,
  PRIMARY KEY (id),
  foreign key (flowid, pid, subpid) references process (flowid, pid, subpid)
)
go

create view all_process_annotation_icons (iconid, flowid, pid, subpid) as
select 0 iconid, flowid, pid, subpid from comment
union
select pl.labelid iconid, pl.flowid, pl.pid, pl.subpid from process_label pl
union
select 99999 iconid, flowid, pid, subpid from deadline;
GO

create view annotation_icons (iconid, icon) as
select 0 iconid, 'label_comment.png'
union
select id iconid, icon icon from label
union
select 99999 iconid, 'label_clock.png';
GO

create view process_annotation_icon_link (iconid, flowid, pid, subpid) as
select min(iconid), flowid, pid, subpid from all_process_annotation_icons
group by flowid, pid, subpid;
GO

CREATE VIEW process_annotation_icon (flowid, pid, subpid, icon, iconid) as
select flowid, pid, subpid, ai.icon, ai.iconid 
from process_annotation_icon_link pail 
inner join annotation_icons ai on pail.iconid = ai.iconid;
GO

ALTER TABLE flow ADD max_block_id INT;
GO

CREATE TABLE subflow_block_mapping (
  id INT NOT NULL IDENTITY,
  created timestamp NOT NULL,
  flowname varchar(64) NOT NULL,
  sub_flowname varchar(64) NOT NULL,
  original_blockid INT NOT NULL,
  mapped_blockid INT NOT NULL,
  PRIMARY KEY (id) 
)
GO

ALTER TABLE flow_history ADD max_block_id INT;
GO

INSERT INTO label (name, description, icon) values ('Urgente', 'Tarefas urgentes', 'label_urgent.png');
INSERT INTO label (name, description, icon) values ('Importante', 'Tarefas importantes', 'label_important.png');
INSERT INTO label (name, description, icon) values ('Nota', 'Tarefas anotadas', 'label_normal.png');

alter table users add department varchar(50);
GO
alter table users add employeeid varchar(50);
GO
alter table users add manager varchar(50);
GO
alter table users add telephonenumber varchar(50);
GO
alter table users add title varchar(50);
GO
CREATE INDEX IDX_REPORTING ON dbo.reporting(flowid , pid ,subpid );
GO
ALTER TABLE dbo.users ADD 
  orgadm_users NUMERIC(1)  NOT NULL DEFAULT 1,
  orgadm_flows NUMERIC(1)  NOT NULL DEFAULT 1,
  orgadm_processes NUMERIC(1)  NOT NULL DEFAULT 1,
  orgadm_resources NUMERIC(1)  NOT NULL DEFAULT 1,
  orgadm_org NUMERIC(1)  NOT NULL DEFAULT 1;
GO