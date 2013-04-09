DROP TABLE IF EXISTS process_label;
DROP TABLE IF EXISTS process_label_history;
DROP TABLE IF EXISTS label;
DROP TABLE IF EXISTS folder_history;
DROP TABLE IF EXISTS comment_history;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS label_history;
DROP TABLE IF EXISTS deadline;
DROP TABLE IF EXISTS deadline_history;
DROP TABLE IF EXISTS folder;

CREATE TABLE `folder` (
  `id` INT NOT NULL auto_increment,
  `name` VARCHAR(50)  NOT NULL,
  `color` VARCHAR(10),
  `userid` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `comment` (
  `id` INT NOT NULL auto_increment,
  `date` DATETIME NOT NULL,
  `userid` VARCHAR(100) NOT NULL,
  `comment` VARCHAR(125) NOT NULL,
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  CONSTRAINT `comment_process_fk` FOREIGN KEY `comment_process_fk` (`flowid`, `pid`, `subpid`)
    REFERENCES `process` (`flowid`, `pid`, `subpid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `comment_history` (
  `id` INT NOT NULL auto_increment,
  `comment` VARCHAR(125) NOT NULL,
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `userid` VARCHAR(100) NOT NULL,
  `date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `comment_history_process_fk` FOREIGN KEY `comment_history_process_fk` (`flowid`, `pid`, `subpid`)
    REFERENCES `process` (`flowid`, `pid`, `subpid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

 CREATE TABLE `label` (
  `id` INT NOT NULL auto_increment,
  `name` VARCHAR(50)  NOT NULL,
  `description` VARCHAR(125),
  `icon` VARCHAR(50),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_label_name` (`name`(50)),
  UNIQUE INDEX `uk_label_icon` (`icon`(50))
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `process_label` (
  `id` INT NOT NULL auto_increment,
  `labelid` INT NOT NULL,
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  CONSTRAINT `process_label_label_fk` FOREIGN KEY `process_label_label_fk` (`labelid`)
    REFERENCES `label` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `process_label_process_fk` FOREIGN KEY `process_label_process_fk` (`flowid`, `pid`, `subpid`)
    REFERENCES `process` (`flowid`, `pid`, `subpid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `process_label_history` (
  `id` INT NOT NULL auto_increment,
  `labelid` INT  NOT NULL,
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `userid` VARCHAR(100) NOT NULL, 
  `date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `process_label_history_label_fk` FOREIGN KEY `process_label_history_label_fk` (`labelid`)
    REFERENCES `label` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `process_label_history_process_fk` FOREIGN KEY `process_label_history_process_fk` (`flowid`, `pid`, `subpid`)
    REFERENCES `process` (`flowid`, `pid`, `subpid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `deadline` (
  `id` INT NOT NULL auto_increment,
  `deadline` VARCHAR(20),
  `userid` VARCHAR(100) NOT NULL,
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
    CONSTRAINT `deadline_process_fk` FOREIGN KEY `deadline_process_fk` (`flowid`, `pid`, `subpid`)
    REFERENCES `process` (`flowid`, `pid`, `subpid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `deadline_history` (
  `id` INT NOT NULL auto_increment,
  `deadline` VARCHAR(20),
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `userid` VARCHAR(100) NOT NULL,
  `date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `deadline_history_process_fk` FOREIGN KEY `deadline_history_process_fk` (`flowid`, `pid`, `subpid`)
    REFERENCES `process` (`flowid`, `pid`, `subpid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

DROP VIEW IF EXISTS all_process_annotation_icons;
CREATE VIEW all_process_annotation_icons (iconid, flowid, pid, subpid) as
select 0 iconid, flowid, pid, subpid from deadline d where d.deadline is not null and d.deadline<>''
union
select pl.labelid iconid, pl.flowid, pl.pid, pl.subpid from process_label pl
union
select 99999 iconid, flowid, pid, subpid from comment c where c.comment is not null and c.comment <>'';

DROP VIEW IF EXISTS annotation_icons;
CREATE VIEW annotation_icons (iconid, icon) as
select 0 iconid, 'label_clock.png'
union
select id iconid, icon icon from label
union
select 99999 iconid, 'label_comment_blue.png';

DROP VIEW IF EXISTS process_annotation_icon_link;
CREATE VIEW process_annotation_icon_link (iconid, flowid, pid, subpid) as
select min(iconid), flowid, pid, subpid from all_process_annotation_icons
group by flowid, pid, subpid;

DROP VIEW IF EXISTS process_annotation_icon;
CREATE VIEW process_annotation_icon (flowid, pid, subpid, icon, iconid) as
select flowid, pid, subpid, ai.icon, ai.iconid 
from process_annotation_icon_link pail 
inner join annotation_icons ai on pail.iconid = ai.iconid;

ALTER TABLE `activity` ADD COLUMN `folderid` INT NULL;

ALTER TABLE `activity` 
  ADD CONSTRAINT `activity_folder_fk`
  FOREIGN KEY (`folderid` )
  REFERENCES `folder` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

INSERT INTO label (id, name, description, icon) values (1, 'Urgente', 'tarefas urgentes', 'label_urgent.png');
INSERT INTO label (id, name, description, icon) values (2, 'Importante', 'tarefas importantes', 'label_important.png');
INSERT INTO label (id, name, description, icon) values (3, 'Normal', 'tarefas padrão', 'label_normal.png');

DROP TABLE IF EXISTS user_session;
CREATE TABLE `user_session` (
  `id` INT NOT NULL auto_increment,
  `userid` VARCHAR(100) NOT NULL,
  `session`  BLOB NOT NULL,
  PRIMARY KEY (`id`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;