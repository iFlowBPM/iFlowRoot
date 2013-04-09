-- SQL SERVER
ALTER TABLE documents ADD tosign int NOT NULL DEFAULT 1;

CREATE TABLE folder (
  id NUMBER(11) NOT NULL IDENTITY,
  name VARCHAR(50)  NOT NULL,
  color VARCHAR(10),
  userid VARCHAR(100) NOT NULL,
  PRIMARY KEY (id)
)
GO

ALTER TABLE activity ADD folderid INT NULL;

ALTER TABLE activity
ADD FOREIGN KEY (folderid)
REFERENCES folder(id)

CREATE TABLE comment (
  id NUMBER(11) NOT NULL IDENTITY,
  date DATETIME NOT NULL,
  userid VARCHAR(100) NOT NULL,
  comment VARCHAR(125) NOT NULL,
  flowid NUMBER NOT NULL,
  pid NUMBER NOT NULL,
  subpid NUMBER NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  FOREIGN KEY (flowid, pid, subpid)
    REFERENCES process (flowid, pid, subpid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
GO

CREATE TABLE comment_history (
  id NUMBER(11) NOT NULL IDENTITY,
  comment VARCHAR(125) NOT NULL,
  flowid NUMBER NOT NULL,
  pid NUMBER NOT NULL,
  subpid NUMBER NOT NULL DEFAULT 1,
  userid VARCHAR(100) NOT NULL,
  date DATETIME NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (flowid, pid, subpid)
    REFERENCES process (flowid, pid, subpid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
GO

CREATE TABLE label (
  id NUMBER(11) NOT NULL IDENTITY,
  name VARCHAR(50)  NOT NULL,
  description VARCHAR(125),
  icon VARCHAR(50),
  PRIMARY KEY (id),
  CONSTRAINT uk_label_name UNIQUE (name)
  CONSTRAINT uk_label_icon UNIQUE (icon)
)
GO

CREATE TABLE process_label (
  id NUMBER(11) NOT NULL IDENTITY,
  labelid NUMBER NOT NULL,
  flowid NUMBER NOT NULL,
  pid NUMBER NOT NULL,
  subpid NUMBER NOT NULL DEFAULT 1,
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
  id NUMBER(11) NOT NULL IDENTITY,
  labelid NUMBER  NOT NULL,
  flowid NUMBER NOT NULL,
  pid NUMBER NOT NULL,
  subpid NUMBER NOT NULL DEFAULT 1,
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
  id NUMBER(11) NOT NULL IDENTITY,
  deadline VARCHAR(20),
  userid VARCHAR(100) NOT NULL,
  flowid NUMBER NOT NULL,
  pid NUMBER NOT NULL,
  subpid NUMBER NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  FOREIGN KEY (flowid, pid, subpid)
    REFERENCES process (flowid, pid, subpid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
GO

CREATE TABLE deadline_history (
  id NUMBER(11) NOT NULL IDENTITY,
  deadline VARCHAR(20),
  flowid NUMBER NOT NULL,
  pid NUMBER NOT NULL,
  subpid NUMBER NOT NULL DEFAULT 1,
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
  id NUMBER(11) NOT NULL IDENTITY,
  userid VARCHAR(100) NOT NULL,
  session  VARBINARY(max) NOT NULL,
  PRIMARY KEY (id)
)

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

insert into flow_settings (flowid, name, description, value, isquery, mdate)
select flowid, 'FLOW_MENU_ACCESSIBLE', 'Permitir visualizar no menu.', 'Sim', 0, '2011-11-21 10:00:00' from flow;
GO

ALTER TABLE flow ADD max_block_id NUMBER(11);
GO

CREATE TABLE subflow_block_mapping (
  id NUMBER(11) NOT NULL IDENTITY,
  created timestamp NOT NULL,
  flowname varchar(64) NOT NULL,
  sub_flowname varchar(64) NOT NULL,
  original_blockid NUMBER(11) NOT NULL,
  mapped_blockid NUMBER(11) NOT NULL,
  PRIMARY KEY (id) 
)
GO

ALTER TABLE flow_history ADD max_block_id NUMBER(11);

ALTER TABLE process DROP PRIMARY KEY,
 ADD PRIMARY KEY  USING BTREE(flowid, pid, subpid, pnumber);