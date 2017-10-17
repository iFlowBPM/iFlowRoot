-- store current charset and set it to UTF-8
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;


SET FOREIGN_KEY_CHECKS = 0;
CREATE TABLE `system_users` (
  `userid` INT NOT NULL auto_increment,
  `username` VARCHAR(50)  NOT NULL,
  `userpassword` VARCHAR(125)  NOT NULL,
  `email_address` VARCHAR(100)  NOT NULL,
  `phone_number` VARCHAR(20)  NULL,
  `mobile_number` VARCHAR(20)  NULL,
  `first_name` VARCHAR(50)  NULL,
  `last_name` VARCHAR(50)  NULL,
  `sessionid` VARCHAR(150)  NULL,
  PRIMARY KEY (`userid`),
  UNIQUE INDEX `uk_users_sessionid` (`sessionid`(150)),
  UNIQUE INDEX `uk_users_username` (`username`(50))
)
ENGINE = INNODB DEFAULT CHARSET=utf8;


CREATE TABLE `flow` (
  `flowid` INT NOT NULL AUTO_INCREMENT,
  `flowname` VARCHAR(64)  NOT NULL,
  `flowfile` VARCHAR(64)  NOT NULL,
  `enabled` INT(1) NULL DEFAULT 0,
  `created` DATETIME NULL,
  `organizationid` VARCHAR(50) NOT NULL,
  `flowdata` LONGBLOB not null,
  `flowversion` int,
  `modified` timestamp not null,
  `name_idx0` varchar(64),
  `name_idx1` varchar(64),
  `name_idx2` varchar(64),
  `name_idx3` varchar(64),
  `name_idx4` varchar(64),
  `name_idx5` varchar(64),
  `name_idx6` varchar(64),
  `name_idx7` varchar(64),
  `name_idx8` varchar(64),
  `name_idx9` varchar(64),
  `name_idx10` varchar(64),
  `name_idx11` varchar(64),
  `name_idx12` varchar(64),
  `name_idx13` varchar(64),
  `name_idx14` varchar(64),
  `name_idx15` varchar(64),
  `name_idx16` varchar(64),
  `name_idx17` varchar(64),
  `name_idx18` varchar(64),
  `name_idx19` varchar(64),
  `seriesid` int,
  `max_block_id` INT,
  type_code varchar(1) default 'W',
  PRIMARY KEY (`flowid`),
  INDEX `ind_flow` (`enabled`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `process` (
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `pnumber` varchar(128) NOT NULL,
  `mid` INT NOT NULL,
  `creator`	VARCHAR(100) NOT NULL,
  `created` DATETIME NOT NULL,
  `currentuser` VARCHAR(100) NOT NULL,
  `lastupdate` DATETIME NOT NULL,
  `procdata` LONGTEXT CHARACTER SET utf8,
  `closed` INT(1) NULL DEFAULT 0,
  `canceled` INT(1) NULL DEFAULT 0,
  `idx0` varchar(1024),
  `idx1` varchar(1024),
  `idx2` varchar(1024),
  `idx3` varchar(1024),
  `idx4` varchar(1024),
  `idx5` varchar(1024),
  `idx6` varchar(1024),
  `idx7` varchar(1024),
  `idx8` varchar(1024),
  `idx9` varchar(1024),
  `idx10` varchar(1024),
  `idx11` varchar(1024),
  `idx12` varchar(1024),
  `idx13` varchar(1024),
  `idx14` varchar(1024),
  `idx15` varchar(1024),
  `idx16` varchar(1024),
  `idx17` varchar(1024),
  `idx18` varchar(1024),
  `idx19` varchar(1024),
  PRIMARY KEY (`flowid`, `pid`,`subpid`),
  INDEX `ind_process` (`created`),
  INDEX IND_PROCESS_IDX0 (idx0),
  INDEX IND_PROCESS_IDX1 (idx1),
  INDEX IND_PROCESS_IDX2 (idx2),
  INDEX IND_PROCESS_IDX3 (idx3),
  INDEX IND_PROCESS_IDX4 (idx4),
  INDEX IND_PROCESS_IDX5 (idx5),
  INDEX IND_PROCESS_IDX6 (idx6),
  INDEX IND_PROCESS_IDX7 (idx7),
  INDEX IND_PROCESS_IDX8 (idx8),
  INDEX IND_PROCESS_IDX9 (idx9),
  INDEX IND_PROCESS_IDX10 (idx10),
  INDEX IND_PROCESS_IDX11 (idx11),
  INDEX IND_PROCESS_IDX12 (idx12),
  INDEX IND_PROCESS_IDX13 (idx13),
  INDEX IND_PROCESS_IDX14 (idx14),
  INDEX IND_PROCESS_IDX15 (idx15),
  INDEX IND_PROCESS_IDX16 (idx16),
  INDEX IND_PROCESS_IDX17 (idx17),
  INDEX IND_PROCESS_IDX18 (idx18),
  INDEX IND_PROCESS_IDX19 (idx19),
  CONSTRAINT `process_flow_fk` FOREIGN KEY `process_flow_fk` (`flowid`)
    REFERENCES `flow` (`flowid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `process_history` (
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `pnumber` varchar(128) NOT NULL,
  `mid` INT NOT NULL,
  `creator`	VARCHAR(100) NOT NULL,
  `created` DATETIME NOT NULL,
  `currentuser` VARCHAR(100) NOT NULL,
  `lastupdate` DATETIME NOT NULL,
  `procdata` LONGTEXT CHARACTER SET utf8,
  `procdatazip` LONGBLOB NULL,
  `closed` INT(1) NULL DEFAULT 0,
  `canceled` INT(1) NULL DEFAULT 0,
  `idx0` varchar(1024),
  `idx1` varchar(1024),
  `idx2` varchar(1024),
  `idx3` varchar(1024),
  `idx4` varchar(1024),
  `idx5` varchar(1024),
  `idx6` varchar(1024),
  `idx7` varchar(1024),
  `idx8` varchar(1024),
  `idx9` varchar(1024),
  `idx10` varchar(1024),
  `idx11` varchar(1024),
  `idx12` varchar(1024),
  `idx13` varchar(1024),
  `idx14` varchar(1024),
  `idx15` varchar(1024),
  `idx16` varchar(1024),
  `idx17` varchar(1024),
  `idx18` varchar(1024),
  `idx19` varchar(1024),
  `undoflag` INT(1) NULL DEFAULT 0,
  PRIMARY KEY (`flowid`, `pid`,`subpid`, `mid`),
  INDEX IND_PROCESS_HISTORY_IDX0 (idx0),
  INDEX IND_PROCESS_HISTORY_IDX1 (idx1),
  INDEX IND_PROCESS_HISTORY_IDX2 (idx2),
  INDEX IND_PROCESS_HISTORY_IDX3 (idx3),
  INDEX IND_PROCESS_HISTORY_IDX4 (idx4),
  INDEX IND_PROCESS_HISTORY_IDX5 (idx5),
  INDEX IND_PROCESS_HISTORY_IDX6 (idx6),
  INDEX IND_PROCESS_HISTORY_IDX7 (idx7),
  INDEX IND_PROCESS_HISTORY_IDX8 (idx8),
  INDEX IND_PROCESS_HISTORY_IDX9 (idx9),
  INDEX IND_PROCESS_HISTORY_IDX10 (idx10),
  INDEX IND_PROCESS_HISTORY_IDX11 (idx11),
  INDEX IND_PROCESS_HISTORY_IDX12 (idx12),
  INDEX IND_PROCESS_HISTORY_IDX13 (idx13),
  INDEX IND_PROCESS_HISTORY_IDX14 (idx14),
  INDEX IND_PROCESS_HISTORY_IDX15 (idx15),
  INDEX IND_PROCESS_HISTORY_IDX16 (idx16),
  INDEX IND_PROCESS_HISTORY_IDX17 (idx17),
  INDEX IND_PROCESS_HISTORY_IDX18 (idx18),
  INDEX IND_PROCESS_HISTORY_IDX19 (idx19),  
  CONSTRAINT `process_history_flow_fk` FOREIGN KEY `process_history_flow_fk` (`flowid`)
    REFERENCES `flow` (`flowid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `modification` (
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `mid` INT NOT NULL,
  `mdate` DATETIME NULL,
  `muser` VARCHAR(256)  NULL,
  PRIMARY KEY (`pid`, `subpid`, `mid`, `flowid`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `flow_roles` (
  `flowid` INT NOT NULL,
  `profileid` INT NOT NULL,
  `permissions` VARCHAR(16)  NULL,
  PRIMARY KEY (`profileid`, `flowid`),
  CONSTRAINT `flow_roles_flow_fk` FOREIGN KEY `flow_roles_flow_fk` (`flowid`)
    REFERENCES `flow` (`flowid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `flow_roles_profiles_fk` FOREIGN KEY `flow_roles_profiles_fk` (`profileid`) 
  	REFERENCES `profiles` (`profileid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `flow_state` (
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `state` INT NOT NULL,
  `result` VARCHAR(1024)  NULL,
  `mdate` DATETIME NULL,
  `exit_flag` INT NOT NULL DEFAULT 0,
  `mid` INT NULL DEFAULT 0,
  `closed` INT(1) NULL DEFAULT 0,
  `canceled` INT(1) NULL DEFAULT 0,
  PRIMARY KEY (`flowid`, `pid`, `subpid`),
  INDEX `ind_flow_state` (`state`),
  CONSTRAINT `flow_state_flow_fk` FOREIGN KEY `flow_state_flow_fk` (`flowid`)
    REFERENCES `flow` (`flowid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `flow_state_history` (
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NULL DEFAULT 1,
  `state` INT NOT NULL,
  `result` VARCHAR(1024)  NULL,
  `mdate` DATETIME NULL,
  `mid` INT NULL DEFAULT 0,
  `exit_flag` INT NOT NULL DEFAULT 0,
  `undoflag` INT(1) NULL DEFAULT 0,
  `exit_port` VARCHAR(64) NULL,
  INDEX `ind_flow_state_history` (`mdate`, `result`(500), `mid`, `exit_flag`),
  INDEX `ind_flow_state_history2` (`flowid`, `pid`, `subpid`, `mid`),
  CONSTRAINT `flow_state_history_flow_fk` FOREIGN KEY `flow_state_history_flow_fk` (`flowid`)
    REFERENCES `flow` (`flowid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `flow_settings` (
  `flowid` INT NOT NULL,
  `name` VARCHAR(64)  NOT NULL,
  `description` VARCHAR(1024)  NULL,
  `value` VARCHAR(1024)  NULL,
  `isquery` INT(1) NULL DEFAULT 0,
  `mdate` DATETIME NULL,
  PRIMARY KEY (`flowid`, `name`),
  CONSTRAINT `flow_settings_flow_fk` FOREIGN KEY `flow_settings_flow_fk` (`flowid`)
    REFERENCES `flow` (`flowid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `flow_settings_history` (
  `flowid` INT NOT NULL,
  `name` VARCHAR(64)  NULL,
  `description` VARCHAR(1024)  NULL,
  `value` VARCHAR(1024)  NULL,
  `isquery` INT(1) NULL DEFAULT 0,
  `mdate` DATETIME NULL,
  `mid` INT NULL,
  CONSTRAINT `flow_settings_history_flow_fk` FOREIGN KEY `flow_settings_history_flow_fk` (`flowid`)
    REFERENCES `flow` (`flowid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `activity` (
  `userid` VARCHAR(100)  NOT NULL,
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `type` INT NULL,
  `priority` INT NULL,
  `created` DATETIME NULL,
  `started` DATETIME NULL,
  `archived` DATETIME NULL,
  `description` VARCHAR(256)  NULL,
  `url` VARCHAR(256)  NULL,
  `status` INT NULL,
  `notify` INT(1) NULL DEFAULT 0,
  `delegated` INT(1) NULL DEFAULT 0,
  `profilename` VARCHAR(256),
  `read_flag` INT(1) NULL DEFAULT 1,
  `mid` INT NULL DEFAULT 0,
  `folderid` INT NULL,
  PRIMARY KEY (`flowid`, `pid`, `subpid`, `userid`),
  CONSTRAINT `activity_process_fk` FOREIGN KEY `activity_process_fk` (`flowid`, `pid`, `subpid`)
    REFERENCES `process` (`flowid`, `pid`, `subpid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `sequences` (
  `sequence` char(32) NOT NULL,
  `seqval` int(11) NOT NULL,
  PRIMARY KEY  (`sequence`)
) 
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `activity_hierarchy` (
  `hierarchyid` INT NOT NULL AUTO_INCREMENT,
  `parentid` INT NOT NULL DEFAULT 0,
  `userid` VARCHAR(100)  NOT NULL,
  `ownerid` VARCHAR(100)  NOT NULL,
  `flowid` INT NOT NULL,
  `slave` INT(1) NULL DEFAULT 1,
  `expires` DATETIME NULL,
  `permissions` VARCHAR(16)  NULL DEFAULT null,
  `pending` INT(1) NULL DEFAULT 1,
  `acceptkey` VARCHAR(32)  NULL DEFAULT null,
  `rejectkey` VARCHAR(32)  NULL DEFAULT null,
  `requested` DATETIME NULL,
  `responded` DATETIME NULL,
  PRIMARY KEY (`hierarchyid`),
  UNIQUE INDEX `activity_hierarchy_uk` (`flowid`, `ownerid`(100), `userid`(100))
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `activity_hierarchy_history` (
  `hierarchyid` INT NOT NULL,
  `parentid` INT NOT NULL DEFAULT 0,
  `userid` VARCHAR(100) BINARY NOT NULL,
  `ownerid` VARCHAR(100) BINARY NOT NULL,
  `flowid` INT NOT NULL,
  `started` DATETIME NULL,
  `expires` DATETIME NULL,
  `permissions` VARCHAR(16) BINARY NULL DEFAULT null,
  `requested` DATETIME NULL,
  `responded` DATETIME NULL,
  PRIMARY KEY (`hierarchyid`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `activity_history` (
  `userid` VARCHAR(100)  NULL,
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NULL DEFAULT 1,
  `type` INT NULL,
  `priority` INT NULL,
  `created` DATETIME NULL,
  `started` DATETIME NULL,
  `archived` DATETIME NULL,
  `description` VARCHAR(256)  NULL,
  `url` VARCHAR(256)  NULL,
  `status` INT NULL,
  `notify` INT(1) NULL,
  `delegated` INT(1) NULL DEFAULT 0,
  `delegateduserid` VARCHAR(100)  NULL,
  `profilename` VARCHAR(256),
  `read_flag` INT(1) NULL DEFAULT 1,
  `mid` INT NULL DEFAULT 0,
  `worker` INT(1) NULL DEFAULT 0,
  `undoflag` INT(1) NULL DEFAULT 0,
  CONSTRAINT `activity_history_process_fk` FOREIGN KEY `activity_history_process_fk` (`flowid`, `pid`, `subpid`)
    REFERENCES `process_history` (`flowid`, `pid`, `subpid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `profiles` (
  `profileid` INT NOT NULL auto_increment,
  `name` VARCHAR(50)  NOT NULL,
  `description` VARCHAR(125)  NULL,
  `organizationid` VARCHAR(50)  DEFAULT '1' NOT NULL,
  PRIMARY KEY (`profileid`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `applications` (
  `appid` INT NOT NULL auto_increment,
  `name` VARCHAR(50)  NOT NULL,
  `description` VARCHAR(125)  NULL,
  PRIMARY KEY (`appid`),
  UNIQUE INDEX `uk_application_name` (`name`(50))
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `applicationprofiles` (
  `appid` INT NOT NULL auto_increment,
  `profileid` INT NOT NULL,
  PRIMARY KEY (`appid`, `profileid`),
  CONSTRAINT `fk_appprofiles_application` FOREIGN KEY `fk_appprofiles_application` (`profileid`)
    REFERENCES `profiles` (`profileid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_appprofiles_profile` FOREIGN KEY `fk_appprofiles_profile` (`appid`)
    REFERENCES `applications` (`appid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `counter` (
  `name` VARCHAR(64) NOT NULL,
  `value` INT NULL,
  `modification` DATETIME NULL,
  PRIMARY KEY (`name`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `dirty_email` (
  `eid`             INT NOT NULL auto_increment,
  `eserver`         VARCHAR(256) NOT NULL,
  `eport`			INT DEFAULT -1,
  `efrom`           VARCHAR(256) NOT NULL,
  `eto`             LONGBLOB NOT NULL,
  `ecc`             LONGBLOB NULL,
  `esubject`        VARCHAR(1024) NULL,
  `ebody`           LONGBLOB NULL,
  `ehtml`           INT(1) NOT NULL DEFAULT 0,
  `ecreated`        DATETIME NULL,
  `etries`          INT DEFAULT 0 NOT NULL,
  `enext_send`      DATETIME NULL,
  `elast_send`      DATETIME NULL,
  `etls`            INT(1) NOT NULL DEFAULT 0,
  `eauth`           INT(1) NOT NULL DEFAULT 0,
  `euser`           VARCHAR(256),
  `epass`           VARCHAR(256),
  PRIMARY KEY (`eid`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `documents` (
  `docid` INT NOT NULL  auto_increment,
  `filename` VARCHAR(128)  NOT NULL,
  `datadoc` LONGBLOB NULL,
  `docurl` VARCHAR(2000),
  `updated` DATETIME NULL,
  `flowid` int not null default 0,
  `pid` int not null default 0,
  `subpid` int not null default 0,
  `numass` int NOT NULL DEFAULT 0,
  `tosign` int NOT NULL DEFAULT 1,
  PRIMARY KEY (`docid`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `email` (
  `eid` INT NOT NULL auto_increment,
  `eserver` VARCHAR(256) NOT NULL,
  `eport` INT DEFAULT -1,
  `efrom` VARCHAR(256) NOT NULL,
  `eto` LONGBLOB NOT NULL,
  `ecc` LONGBLOB NULL,
  `esubject` VARCHAR(1024) NULL,
  `ebody` LONGBLOB NULL,
  `ehtml` INT(1) NOT NULL DEFAULT 0,
  `ecreated` DATETIME NULL,
  `etries`  INT DEFAULT 0 NOT NULL,
  `enext_send` DATETIME NULL,
  `elast_send` DATETIME NULL,
  `etls` INT(1) NOT NULL DEFAULT 0,
  `eauth` INT(1) NOT NULL DEFAULT 0,
  `euser` VARCHAR(256),
  `epass` VARCHAR(256),
  PRIMARY KEY (`eid`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `event_data` (
  `eventid` INT NOT NULL auto_increment,
  `fid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NULL DEFAULT 1,
  `blockid` INT NOT NULL,
  `starttime` LONGTEXT  NULL,
  `type` VARCHAR(255)  NULL,
  `properties` VARCHAR(1024)  NULL,
  `processed` INT NULL DEFAULT 0,
  `userid` VARCHAR(1024) NULL,
  PRIMARY KEY (`eventid`),
  CONSTRAINT `event_data_flow_fk` FOREIGN KEY `event_data_flow_fk` (`fid`)
    REFERENCES `flow` (`flowid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `event_data_process_fk` FOREIGN KEY `event_data_process_fk` (`fid`, `pid`, `subpid`)
    REFERENCES `process` (`flowid`, `pid`, `subpid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `forkjoin_blocks` (
  `flowid` INT NOT NULL,
  `blockid` INT NOT NULL,
  `type` INT(1) NULL,
  PRIMARY KEY (`flowid`, `blockid`),
  CONSTRAINT `fj_blocks_flow_fk` FOREIGN KEY `fj_blocks_flow_fk` (`flowid`)
    REFERENCES `flow` (`flowid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `forkjoin_hierarchy` (
  `flowid` INT NOT NULL,
  `parentblockid` INT NOT NULL,
  `blockid` INT NOT NULL,
  PRIMARY KEY (`blockid`, `flowid`, `parentblockid`),
  CONSTRAINT `fj_hierarchy_blocks1_fk` FOREIGN KEY `fj_hierarchy_blocks1_fk` (`flowid`, `parentblockid`)
    REFERENCES `forkjoin_blocks` (`flowid`, `blockid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fj_hierarchy_blocks2_fk` FOREIGN KEY `fj_hierarchy_blocks2_fk` (`flowid`, `blockid`)
    REFERENCES `forkjoin_blocks` (`flowid`, `blockid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `forkjoin_mines` (
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `blockid` INT NOT NULL,
  `mined` INT NULL,
  `locked` INT NULL,
  PRIMARY KEY (`pid`, `blockid`, `flowid`),
  CONSTRAINT `fj_mines_flow_fk` FOREIGN KEY `fj_mines_flow_fk` (`flowid`)
    REFERENCES `flow` (`flowid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `forkjoin_state_dep` (
  `flowid` INT NOT NULL,
  `parentblockid` INT NOT NULL,
  `blockid` INT NOT NULL,
  PRIMARY KEY (`flowid`, `parentblockid`, `blockid`),
  CONSTRAINT `fj_state_dep_blocks_fk` FOREIGN KEY `fj_state_dep_blocks_fk` (`flowid`, `parentblockid`)
    REFERENCES `forkjoin_blocks` (`flowid`, `blockid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `iflow_errors` (
  `errorid` INT NOT NULL auto_increment,
  `userid` VARCHAR(100) NOT NULL,
  `created` DATETIME NULL,
  `method` VARCHAR(128) NULL,
  `object` VARCHAR(128) NULL,
  `flowid` INT NULL,
  `pid` INT NULL,
  `subpid` INT NULL,
  `errortype` INT NULL,
  `description` VARCHAR(512)  NOT NULL,
  PRIMARY KEY (`errorid`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `links_flows` (
  `linkid` INT NOT NULL auto_increment,
  `parentid` INT NULL DEFAULT 0,
  `flowid` INT NULL DEFAULT 0,
  `name` VARCHAR(64)  NULL DEFAULT null,
  `url` VARCHAR(256)  NULL DEFAULT null,
  `organizationid` VARCHAR(50)  DEFAULT '1' NOT NULL,
  PRIMARY KEY (`linkid`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `new_features` (
  `newfeaturesid` INT NOT NULL auto_increment,
  `version` VARCHAR(64)  NOT NULL,
  `feature` VARCHAR(128)  NOT NULL,
  `description` VARCHAR(1024)  NOT NULL,
  `created` DATETIME NULL,
  `organizationid` VARCHAR(50),
  PRIMARY KEY (`newfeaturesid`),
  UNIQUE INDEX `new_features_uk` (`version`(64), `feature`(128))
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `organizations` (
  `organizationid` INT NOT NULL auto_increment,
  `name` VARCHAR(50)  NOT NULL,
  `description` VARCHAR(150)  NULL,
  `style_url` VARCHAR(128)  NULL,
  `logo_url` VARCHAR(128)  NULL,
  `locked` INT(1) default 0 not null,
  PRIMARY KEY (`organizationid`),
  UNIQUE INDEX `un_organization_name` (`name`(50))
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `organizational_units` (
  `unitid` INT NOT NULL auto_increment,
  `parent_id` INT NOT NULL,
  `organizationid` INT NOT NULL,
  `name` VARCHAR(50)  NOT NULL,
  `description` VARCHAR(150)  NULL,
  UNIQUE INDEX `un_organizational_units_name` (`name`(50)),
  PRIMARY KEY (`unitid`),
  CONSTRAINT `fk_unit_organization` FOREIGN KEY `fk_unit_organization` (`organizationid`)
    REFERENCES `organizations` (`organizationid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `organization_theme` (
  `organizationid` VARCHAR(50)  NOT NULL,
  `theme` VARCHAR(256)  NOT NULL,
  `style_url` VARCHAR(256)  NOT NULL,
  `logo_url` VARCHAR(256)  NOT NULL,
  `menu_location` VARCHAR(256) default 'left',
  `menu_style` VARCHAR(256) default 'list', 
  `proc_menu_visible` INT(1) default 1,
  PRIMARY KEY (`organizationid`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `queue_proc` (
  `id` INT NOT NULL auto_increment,
  `object` INT NOT NULL,
  `groupid` VARCHAR(64)  NULL,
  `flowid` INT NULL,
  `pid` INT NULL,
  `properties` VARCHAR(1024)  NULL,
  `creation_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ind_queue_proc` (`id`, `object`, `groupid`(64))
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `queue_data` (
  `queue_proc_id` INT NOT NULL,
  `name` VARCHAR(64)  NOT NULL,
  `value` VARCHAR(1024)  NULL,
  PRIMARY KEY (`name`, `queue_proc_id`),
  CONSTRAINT `queue_data_queue_proc_fk` FOREIGN KEY `queue_data_queue_proc_fk` (`queue_proc_id`)
    REFERENCES `queue_proc` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `users` (
  `userid` INT NOT NULL auto_increment,
  `unitid` INT NULL,
  `username` VARCHAR(100)  NOT NULL,
  `userpassword` VARCHAR(125)  NOT NULL,
  `email_address` VARCHAR(100)  NOT NULL,
  `gender` CHAR(1)  NOT NULL,
  `first_name` VARCHAR(50)  NULL,
  `last_name` VARCHAR(50)  NULL,
  `phone_number` VARCHAR(20)  NULL,
  `fax_number` VARCHAR(20)  NULL,
  `mobile_number` VARCHAR(20)  NULL,
  `company_phone` VARCHAR(20)  NULL,
  `sessionid` VARCHAR(150)  NULL,
  `activated` INTEGER (1) NOT NULL,
  `password_reset` INTEGER (1) DEFAULT 1 NOT NULL,
  `department` varchar(50),
  `employee_number` varchar(50),
  `employeeid` varchar(50),
  `manager` varchar(50),
  `telephonenumber` varchar(50),
  `title` varchar(50),
  `orgadm` INTEGER (1) DEFAULT 0 NOT NULL,
  `orgadm_users` INT(1) UNSIGNED NOT NULL DEFAULT 1,
  `orgadm_flows` INT(1) UNSIGNED NOT NULL DEFAULT 1,
  `orgadm_processes` INT(1) UNSIGNED NOT NULL DEFAULT 1,
  `orgadm_resources` INT(1) UNSIGNED NOT NULL DEFAULT 1,
  `orgadm_org` INT(1) UNSIGNED NOT NULL DEFAULT 1,
  PRIMARY KEY (`userid`),
  UNIQUE INDEX `uk_users_sessionid` (`sessionid`(150)),
  UNIQUE INDEX `uk_users_username` (`username`(100)),
  CONSTRAINT `fk_users_org_unit` FOREIGN KEY `fk_users_org_unit` (`unitid`)
    REFERENCES `organizational_units` (`unitid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `unitmanagers` (
  `userid` INT NOT NULL,
  `unitid` INT NOT NULL,
  PRIMARY KEY (`unitid`, `userid`),
  CONSTRAINT `fk_unitmanagers_unit` FOREIGN KEY `fk_unitmanagers_unit` (`unitid`)
    REFERENCES `organizational_units` (`unitid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_unitmanagers_user` FOREIGN KEY `fk_unitmanagers_user` (`userid`)
    REFERENCES `users` (`userid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `userprofiles` (
  `userid` INT NOT NULL,
  `profileid` INT NOT NULL,
  PRIMARY KEY (`profileid`, `userid`),
  CONSTRAINT `fk_userprofiles_profile` FOREIGN KEY `fk_userprofiles_profile` (`profileid`)
    REFERENCES `profiles` (`profileid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_userprofiles_user` FOREIGN KEY `fk_userprofiles_user` (`userid`)
    REFERENCES `users` (`userid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `sub_flow` (
  `flowid` INT NOT NULL AUTO_INCREMENT,
  `flowname` VARCHAR(64)  NOT NULL,
  `flowfile` VARCHAR(64)  NOT NULL,
  `created` DATETIME NOT NULL,
  `organizationid` VARCHAR(50) NOT NULL,
  `flowdata` LONGBLOB not null,
  `flowversion` int,
  `modified` DATETIME not null,
  PRIMARY KEY (`flowid`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

create table flow_history (
        id                  int auto_increment,
        flowid              int not null,
        name                varchar (64) not null,
        description         varchar (64) not null,
        data                longblob not null,
        flowversion         int,
        modified            DATETIME not null,
        `comment`           varchar(512),
		`max_block_id` 		int,
        primary key (id)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

create table sub_flow_history (
        id                  int auto_increment,
        flowid              int not null,
        name                varchar (64) not null,
        description         varchar (64) not null,
        data                longblob not null,
        flowversion         int,
        modified            DATETIME not null,
        `comment`           varchar(512),
        primary key (id)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

create table flow_template (
        name                varchar (64) not null,
        description         varchar (64) not null,
        data                longblob not null,
        primary key (name)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

create table sub_flow_template (
        name                varchar (64) not null,
        description         varchar (64) not null,
        data                longblob not null,
        primary key (name)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

create table user_activation (
        userid int not null,
        organizationid int not null,
        unitid int not null,
        code varchar (64) not null,
        created timestamp default now() not null,
        primary key (userid,organizationid,unitid),
  CONSTRAINT fk_user_activation_organization FOREIGN KEY fk_user_activation_organization (organizationid)
    REFERENCES organizations (organizationid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_user_activation_orgunit FOREIGN KEY fk_user_activation_orgunit (unitid)
    REFERENCES organizational_units (unitid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_user_activation_user FOREIGN KEY fk_user_activation_user (userid)
    REFERENCES users (userid)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

create table event_info (
        name varchar(64) not null,
        description varchar (255) not null,
        primary key (name)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

-- this table along function seq_flow_settings_nextval simulate a sequence in mysql
create table seq_flow_settings (
        id int not null,
        primary key (id)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;
-- initializes data
insert into seq_flow_settings values (0);

-- email modification confirmations
create table email_confirmation (
  userid int not null,
  organizationid int not null,
  email varchar(100) not null,
  code varchar(50) not null,
  primary key (userid,organizationid),
  constraint un_email_confirmation_code unique (code)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

-- User settings table
create table user_settings (
  userid varchar(100) not null,
  lang varchar(2),
  region varchar(2),
  timezone varchar(64),
  tutorial varchar(20),
  help_mode int(1) default 1,
  tutorial_mode int(1) default 1,
  primary key (userid)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

-- Organization settings table
create table organization_settings (
  organizationid varchar(32) not null,
  lang varchar(2),
  region varchar(2),
  timezone varchar(64),
  constraint organization_settings_pk primary key (organizationid)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

-- Notifications table
create table `notifications` (
  `id` int auto_increment,
  `created` DATETIME,
  `sender` varchar(192),
  `message` varchar(500),
  `link` VARCHAR(45) not NULL DEFAULT 'false',
  constraint notifications_pk primary key (id)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

create table user_notifications (
  userid varchar(100) not null,
  notificationid int not null,
  isread int(1) not null default 0,
  constraint user_notifications_pk primary key (userid,notificationid),
  CONSTRAINT fk_user_notifications FOREIGN KEY (notificationid)
    REFERENCES notifications (id)
    ON DELETE CASCADE
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

create table series (
	id int not null auto_increment,
	`organizationid` VARCHAR(50)  DEFAULT '1' NOT NULL,
	created bigint not null,
	enabled int(1) not null default 1,
	state int(1) not null,
	name varchar(100) not null,
	kind varchar(100) not null,
	pattern varchar(200) not null,
	pattern_field_formats varchar(200),
	start_with varchar(200) not null,
	max_value varchar(200),
	current_value varchar(200),
	extra_options varchar(200),
	constraint series_pk primary key (id),
	UNIQUE INDEX `series_name_uk` (`name`(100), `organizationid`(50))	
)
ENGINE = INNODB DEFAULT CHARSET=utf8;


-- process archive
CREATE TABLE `process_archive` (
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `pnumber` varchar(128) NOT NULL,
  `mid` INT NOT NULL,
  `creator`	VARCHAR(100) NOT NULL,
  `created` DATETIME NOT NULL,
  `currentuser` VARCHAR(100) NOT NULL,
  `lastupdate` DATETIME NOT NULL,
  `procdata` LONGTEXT CHARACTER SET utf8,
  `closed` INT(1) NULL DEFAULT 0,
  `archived` DATETIME NOT NULL,
  `idx0` varchar(1024),
  `idx1` varchar(1024),
  `idx2` varchar(1024),
  `idx3` varchar(1024),
  `idx4` varchar(1024),
  `idx5` varchar(1024),
  `idx6` varchar(1024),
  `idx7` varchar(1024),
  `idx8` varchar(1024),
  `idx9` varchar(1024),
  `idx10` varchar(1024),
  `idx11` varchar(1024),
  `idx12` varchar(1024),
  `idx13` varchar(1024),
  `idx14` varchar(1024),
  `idx15` varchar(1024),
  `idx16` varchar(1024),
  `idx17` varchar(1024),
  `idx18` varchar(1024),
  `idx19` varchar(1024),
  PRIMARY KEY (`flowid`, `pid`,`subpid`),
  INDEX IND_PROCESS_ARCH_IDX0 (idx0),
  INDEX IND_PROCESS_ARCH_IDX1 (idx1),
  INDEX IND_PROCESS_ARCH_IDX2 (idx2),
  INDEX IND_PROCESS_ARCH_IDX3 (idx3),
  INDEX IND_PROCESS_ARCH_IDX4 (idx4),
  INDEX IND_PROCESS_ARCH_IDX5 (idx5),
  INDEX IND_PROCESS_ARCH_IDX6 (idx6),
  INDEX IND_PROCESS_ARCH_IDX7 (idx7),
  INDEX IND_PROCESS_ARCH_IDX8 (idx8),
  INDEX IND_PROCESS_ARCH_IDX9 (idx9),
  INDEX IND_PROCESS_ARCH_IDX10 (idx10),
  INDEX IND_PROCESS_ARCH_IDX11 (idx11),
  INDEX IND_PROCESS_ARCH_IDX12 (idx12),
  INDEX IND_PROCESS_ARCH_IDX13 (idx13),
  INDEX IND_PROCESS_ARCH_IDX14 (idx14),
  INDEX IND_PROCESS_ARCH_IDX15 (idx15),
  INDEX IND_PROCESS_ARCH_IDX16 (idx16),
  INDEX IND_PROCESS_ARCH_IDX17 (idx17),
  INDEX IND_PROCESS_ARCH_IDX18 (idx18),
  INDEX IND_PROCESS_ARCH_IDX19 (idx19)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `activity_archive` (
  `userid` VARCHAR(100)  NULL,
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NULL DEFAULT 1,
  `type` INT NULL,
  `priority` INT NULL,
  `created` DATETIME NULL,
  `started` DATETIME NULL,
  `archived` DATETIME NULL,
  `description` VARCHAR(256)  NULL,
  `url` VARCHAR(256)  NULL,
  `status` INT NULL,
  `notify` INT(1) NULL,
  `delegated` INT(1) NULL DEFAULT 0,
  `delegateduserid` VARCHAR(100)  NULL,
  `profilename` VARCHAR(256)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `modification_archive` (
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  `mid` INT NOT NULL,
  `mdate` DATETIME NULL,
  `muser` VARCHAR(256)  NULL,
  PRIMARY KEY (`pid`, `subpid`, `mid`, `flowid`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;


CREATE TABLE `migration_log` (
  `migrator` VARCHAR(16) NULL,
  `task` VARCHAR(128) NOT NULL,
  `finished` DATETIME NULL,
  UNIQUE INDEX `uk_migration_log` (`migrator`,`task`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE reporting (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  cod_reporting VARCHAR(1024),
  start_reporting DATETIME,
  stop_reporting DATETIME,
  ttl DATETIME,
  active INT(1) NOT NULL DEFAULT 0
) 
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE log (
  log_id INT NOT NULL,
  log VARCHAR(2048) NOT NULL DEFAULT '',
  username VARCHAR(50),
  caller VARCHAR(16),
  method VARCHAR(16),
  creation_date DATETIME NOT NULL,
  CONSTRAINT log_id_pk PRIMARY KEY (log_id)
) 
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE flow_state_log (
  flowid INT NOT NULL,
  pid INT NOT NULL,
  subpid INT NOT NULL DEFAULT 1,
  state INT NOT NULL,
  log_id INT NOT NULL,
  CONSTRAINT fk_log_id FOREIGN KEY (log_id)
    REFERENCES log (log_id)
    ON DELETE CASCADE
) 
ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE external_dms (
  dmsid INT NOT NULL,
  docid INT NOT NULL,
  uuid VARCHAR(64) NOT NULL,
  scheme VARCHAR(64),
  address VARCHAR(64),
  path VARCHAR(64),
  CONSTRAINT dmsid_pk PRIMARY KEY (dmsid),
  CONSTRAINT fk_docid FOREIGN KEY (docid)
    REFERENCES documents (docid)
    ON DELETE CASCADE,
  CONSTRAINT un_external_dms_uuid UNIQUE (uuid)
) ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE external_dms_properties (
  dmsid INT NOT NULL,
  name VARCHAR(64),
  value VARCHAR(1024),
  CONSTRAINT fk_dmsid FOREIGN KEY (dmsid)
    REFERENCES external_dms (dmsid)
    ON DELETE CASCADE
) ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE upgrade_log (
  signature	VARCHAR(125)	NOT NULL,
  executed	INT(1)			NOT NULL DEFAULT 0,
  error		INT(1)			NOT NULL DEFAULT 0,
  log_id	INT(11)			NOT NULL,
  CONSTRAINT PK_UPGRADE_LOG PRIMARY KEY (signature),
  CONSTRAINT FK_UPGRADE_LOG_LOG_ID FOREIGN KEY (log_id)
    REFERENCES log (log_id)
    ON DELETE CASCADE
) ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `organizations_tabs` (
  `organizationid` int(11) DEFAULT NULL,
  `tabid` int(11) DEFAULT NULL,
  CONSTRAINT `fk_organization` FOREIGN KEY `fk_organization` (`organizationid`)
    REFERENCES `organizations` (`organizationid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `profiles_tabs` (
  `profileid` int(11) DEFAULT NULL,
  `tabid` int(11) DEFAULT NULL,
  CONSTRAINT `fk_profiles` FOREIGN KEY `fk_profiles` (`profileid`)
    REFERENCES `profiles` (`profileid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = INNODB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;

DELIMITER ;

-- sequences
CREATE TRIGGER trigger_activity_hierarchy
BEFORE INSERT ON `activity_hierarchy` FOR EACH ROW
SET NEW.`hierarchyid` = sequence('activity_hierarchy');

CREATE TRIGGER trigger_email
BEFORE INSERT ON `email` FOR EACH ROW
SET NEW.`eid` = sequence('email');

--
-- @view  activity tabela que contem as actividades delegadas a outros
-- @field id identificador da hierarquia de delegacoes
-- @field userid identificador alfanumerico do utilizador
-- @field pid identificador numerico do processo
-- @field ownerid identificador alfanumerico do utilizador a quem a actividade pertence
-- @field flowid identificador numerico do fluxo
create view activity_delegated
    (hierarchyid, userid, pid, subpid, ownerid, flowid, created, type, started, archived, 
    status, notify, priority, description, url,profilename, requested, responded, read_flag,mid) as
    select H.hierarchyid, H.userid, A.pid, A.subpid, A.userid as ownerid, A.flowid, A.created, 
    A.type, A.started, A.archived, A.status, A.notify, A.priority, A.description, A.url, A.profilename,
    H.requested, H.responded, A.read_flag, A.mid
    from activity A, activity_hierarchy H
    where ((A.userid = H.ownerid and H.slave=1) or (A.userid = H.userid and slave=0)) 
    and A.flowid = H.flowid and H.pending=0 and A.delegated <> 0;
-- Tarefas no topo da hierarquia --
-- WHERE A.userid = H.ownerid and H.slave=1 and A.flowid = H.flowid and H.pending=0 and A.delegated = 1;
-- Tarefas na base da hierarquia -- Nao e preciso estar delegado...
-- Tarefas no meio da hierarquia --
-- WHERE  and H.slave=1 and A.flowid = H.flowid and H.pending=0 and A.delegated = 1;

DELIMITER //
    
-- deletes all associated data for the given process
CREATE PROCEDURE deleteProc(aflowid INTEGER,apid INTEGER)
BEGIN

  delete from activity where flowid=aflowid and pid=apid;
  delete from activity_history where flowid=aflowid and pid=apid;

  delete from modification where flowid=aflowid and pid=apid;

  delete from process where flowid=aflowid and pid=apid;
  delete from process_history where flowid=aflowid and pid=apid;

  delete from flow_state where flowid=aflowid and pid=apid;
  delete from flow_state_history where flowid=aflowid and pid=apid;

END;
//

-- deletes all flow associated data.
-- if delprocs = 1, deletes everything (data, states, settings, flow..)
-- if delprocs = 0, only moves opened procs to archive tables (and keep them marked as not closed)
CREATE PROCEDURE deleteFlow(auserid VARCHAR(256),aflowid INTEGER,delprocs INTEGER)
BEGIN
  DECLARE tmp INTEGER;
  DECLARE error INTEGER;
  DECLARE apid INTEGER;
  DECLARE asubpid INTEGER;
  DECLARE done INT DEFAULT 0;
  DECLARE aname VARCHAR(256);
  DECLARE avaluestring VARCHAR(256);
  DECLARE avaluenumber VARCHAR(256);

  DECLARE COPEN CURSOR FOR SELECT distinct(pid) as dpid, subpid FROM process where flowid=aflowid;
  DECLARE CHIST CURSOR FOR SELECT distinct(pid) as dpid FROM process_history where flowid=aflowid;

  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

  set error = 1;

  -- first handle procs
  if (delprocs = 1) then
    -- delete all flow procs (including historic ones)

    open COPEN;
    REPEAT
      FETCH COPEN INTO apid, asubpid;
      IF NOT done THEN
        call deleteProc(aflowid,apid);
      END IF;
    UNTIL done END REPEAT;
    close COPEN;
    set done = 0;
    
    open CHIST;
    REPEAT
      FETCH CHIST INTO apid;
      IF NOT done THEN
        call deleteProc(aflowid,apid);
      END IF;
    UNTIL done END REPEAT;
    close CHIST;
    set done = 0;

  else
    -- move opened procs to historic tables
    -- move scheduled activities to historic table

    open COPEN;
    REPEAT
      FETCH COPEN INTO apid, asubpid;
      IF NOT done THEN
        call get_next_mid(tmp,auserid,aflowid,apid,asubpid);
	    update process set mid=tmp where subpid=asubpid and pid=apid and flowid=aflowid;
    	update flow_state set mid=tmp where subpid=asubpid and pid=apid and flowid=aflowid;

        -- move data
        insert into process_history (flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19,undoflag) 
            select flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19,0
                from process where flowid=aflowid and pid=apid and subpid=asubpid;
        update process set closed=1 where flowid=aflowid and pid=apid and subpid=asubpid;

        insert into activity_history (userid,flowid,pid,subpid,type,priority,created,started,archived,
            description,url,status,notify,delegated,delegateduserid)
            (SELECT userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,
            status,notify,delegated, userid as delegateduserid from activity where flowid=aflowid and pid=apid and subpid=asubpid);
        delete from activity where flowid=aflowid and pid=apid and subpid=asubpid;
      END IF;
    UNTIL done END REPEAT;
    close COPEN;
    set done = 0;

    -- archive all history
    open CHIST;
    REPEAT
      FETCH CHIST INTO apid;
      IF NOT done THEN
        call archiveProc(tmp,aflowid,apid,NOW());
      END IF;
    UNTIL done END REPEAT;
    close CHIST;
    set done = 0;

  end if;

  -- delete all flow related stuff
  delete from flow_settings where flowid=aflowid;
  delete from flow_settings_history where flowid=aflowid;
  delete from flow_roles where flowid=aflowid;
  delete from flow where flowid=aflowid;
END;
//


-- updates a given flow settings.
-- if setting does not exist, it creates it.
-- if it exists, it updates it.
-- if it exists and settingtype=1 and setting value is null, then it deletes setting.
-- in all changing cases, the changing is saved in setting historic table.
CREATE PROCEDURE updateFlowSetting (aflowid      INTEGER,
                                    amid         INTEGER,
                                    aname        VARCHAR(64),
                                    adescription VARCHAR(1024),
                                    avalue       VARCHAR(1024),
                                    aisquery     INTEGER,
                                    settingtype  INTEGER)
  BEGIN
  DECLARE tmp integer;
  DECLARE process integer default 0;
  DECLARE nowdate DATETIME;

    set nowdate = NOW();
    select count(1) into tmp from flow_settings where flowid=aflowid and name=aname;

    IF tmp = 0 THEN
      insert into flow_settings (flowid,name,description,value,isquery,mdate) 
        values (aflowid,aname,adescription,avalue,aisquery,nowdate);
      set process = 1;
    ELSEIF (settingtype = 0) THEN
      update flow_settings set value=avalue,mdate=nowdate where flowid=aflowid and name=aname;
    ELSEIF (settingtype = 1 AND avalue IS NOT NULL) THEN
      update flow_settings set value=avalue,mdate=nowdate where flowid=aflowid and name=aname;
      set process = 1;
    ELSEIF (settingtype = 1 AND avalue IS NULL) THEN
      delete from flow_settings where flowid=aflowid and name=aname;
      set process = 1;
    ELSEIF (settingtype = 2) THEN
      update flow_settings set description=adescription,mdate=nowdate where flowid=aflowid and name=aname;
      set process = 1;
    ELSE
      set process = 0;
    END IF;

    -- now update value and historify. process will only be 0 in the case of
    -- no prev value and null curr value
    IF (process = 1) THEN
      insert into flow_settings_history (flowid,name,description,value,mdate,mid) 
        values (aflowid,aname,adescription,avalue,nowdate,amid);
    END IF;

  END;
//

-- gets next available pid (from counter table) and updates counter table for a given flow.
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
//

-- gets next available sub process id
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


-- gets next available mid for a given process and updates modification table.
CREATE PROCEDURE get_next_mid (OUT retmid INTEGER,
                               auserid VARCHAR(256),
                               aflowid INTEGER,
                               apid INTEGER,
                               asubpid INTEGER)
  
BEGIN
    DECLARE tmp integer;
    set retmid = 1;
    select count(mid) into tmp from modification where subpid=asubpid and pid=apid and flowid=aflowid;
    IF tmp > 0 THEN
       select max(mid)+1 into retmid from modification where subpid=asubpid and pid=apid and flowid=aflowid;
    END IF;
    insert into modification (flowid,pid,subpid,mid,mdate,muser) values (aflowid,apid,asubpid,retmid,NOW(),auserid);
END;
//

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

-- Simulates a sequence in mysql using data in the table seq_flow_settings
-- This function must be created by a user with SUPER privileges (such as root)
CREATE FUNCTION seq_flow_settings_nextval ()
  RETURNS INT
  DETERMINISTIC
    BEGIN
     update seq_flow_settings set id=LAST_INSERT_ID(id+1);
     RETURN LAST_INSERT_ID();
    END
//

-- Simulates a sequence in mysql using data in the table seq_flow_settings
-- This function must be created by a SUPER user (such as root)
CREATE FUNCTION get_procdata_value (procdata LONGTEXT, varname VARCHAR(256))
  RETURNS VARCHAR(1024)
  DETERMINISTIC
    BEGIN
      declare stripped LONGTEXT;
      declare xmlTagPos INT;
      declare xmlTag VARCHAR(264); -- 256 (varname)+ 8 ('<a n="">')
      set xmlTag = concat('<a n="',varname,'">');
      set xmlTagPos = locate(xmlTag, procdata);
      if (xmlTagPos = 0) then
        RETURN NULL;
      end if;
      set xmlTagPos = xmlTagPos+length(xmlTag);
      set stripped = substring(procdata, xmlTagPos);
      return left(stripped,locate('</a>',stripped)-1);
    END
//

CREATE FUNCTION `sequence`(name CHAR(32))
    RETURNS int(11)
    MODIFIES SQL DATA
    DETERMINISTIC
    SQL SECURITY INVOKER
BEGIN
    INSERT INTO sequences (sequence, seqval)
    VALUES (name, LAST_INSERT_ID(1))
    ON DUPLICATE KEY UPDATE seqval = LAST_INSERT_ID(seqval + 1);
    RETURN LAST_INSERT_ID();
END;
//

/*
 Se usarmos mysql 5.1.5 ou superior podemos recorrer a funcao ExtractValue com XPath
 Como o mysql nao permite modificar uma funcao, e necessario fazer drop seguido de create
 
 Nota: Estes comentarios sao especiais do mysql. O delimiter tem que ficar de fora do comentario
 caso contrario o mysql considera que o codigo do comentario ainda nao terminou.
*/

/*!50105 DROP FUNCTION get_procdata_value */ 
//

/*!50105 CREATE FUNCTION get_procdata_value (procdata LONGTEXT, varname VARCHAR(256)) */
/*!50105   RETURNS VARCHAR(1024) */
/*!50105   DETERMINISTIC */
/*!50105     BEGIN */
/*!50105 	RETURN ExtractValue(procdata, concat('/process/a[@n="',varname,'"]')); */
/*!50105     END */ 
//

-- move a process to archive tables
CREATE PROCEDURE archiveProc(OUT archiveResult INTEGER, aflowid INTEGER,apid INTEGER,aarchivedate DATETIME)
BEGIN
  DECLARE open INTEGER;
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    set archiveResult = -1;
  END;
 
  set archiveResult = 0;

  START TRANSACTION;

  -- check if process (or any of its subprocesses) is still open
  select count(1) into open from process where flowid=aflowid and pid=apid and closed=0;
  IF open = 0 THEN

    START TRANSACTION;

    -- so far so nice, copy to archive
    insert into process_archive (flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,archived,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19) 
      select flowid,pid,subpid,pnumber,mid,creator,created,currentuser,lastupdate,procdata,closed,aarchivedate,idx0,idx1,idx2,idx3,idx4,idx5,idx6,idx7,idx8,idx9,idx10,idx11,idx12,idx13,idx14,idx15,idx16,idx17,idx18,idx19
              from process_history where flowid=aflowid and pid=apid;

    insert into activity_archive (userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename)
      select userid,flowid,pid,subpid,type,priority,created,started,archived,description,url,status,notify,delegated,delegateduserid,profilename 
              from activity_history where flowid=aflowid and pid=apid;
  
    insert into modification_archive (flowid,pid,subpid,mid,mdate,muser)
      select flowid,pid,subpid,mid,mdate,muser from modification where flowid=aflowid and pid=apid;

    -- finally, delete from history
    delete from modification where flowid=aflowid and pid=apid;
    delete from activity_history where flowid=aflowid and pid=apid;
    delete from process_history where flowid=aflowid and pid=apid;
    delete from process where flowid=aflowid and pid=apid;

    set archiveResult = 1;
  END IF;

  COMMIT;
END;
//

-- Restore delimiter
DELIMITER ;

insert into counter values ('pid',0,NOW());
insert into counter values ('docid',1,NOW());
insert into counter values ('emailid',1,NOW());
insert into counter values ('cid',1,NOW());
insert into counter values ('flowid',0,NOW());
insert into counter values ('nodekey',0,NOW());

insert into organizations (organizationid,name,description) values (1,'SYS','SYS ORG');

insert into organizational_units (organizationid,parent_id,name,description) 
values((select organizationid from organizations where name='SYS') ,-1,'SYS','SYS');


-- USERS

-- admin admin
-- <user> password
insert into system_users (userid, username, userpassword, email_address,first_name,last_name)
values (1, 'admin', 'F/KbuDOEofgjp7/9yUGnrw==', 'change@this.address','','');

-- Event info
insert into event_info (name,description) values ('AsyncWait','description=Processo fica bloqueado num bloco NOP ''a espera de de um evento externo');
insert into event_info (name,description) values ('Timer','description=Timer para reencaminhar para ...;workingdays=true;minutes=10');
insert into event_info (name,description) values ('Deadline', 'dateVar=date');

commit;

-- restore previous charset
SET character_set_client = @saved_cs_client;

-- -------------------
--    START QRTZ    --
-- -------------------
CREATE TABLE QRTZ_JOB_DETAILS
  (
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_VOLATILE VARCHAR(1) NOT NULL,
    IS_STATEFUL VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_JOB_LISTENERS
  (
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    JOB_LISTENER VARCHAR(200) NOT NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),
    FOREIGN KEY (JOB_NAME,JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    IS_VOLATILE VARCHAR(1) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT(13) NULL,
    PREV_FIRE_TIME BIGINT(13) NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT(13) NOT NULL,
    END_TIME BIGINT(13) NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (JOB_NAME,JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT(7) NOT NULL,
    REPEAT_INTERVAL BIGINT(12) NOT NULL,
    TIMES_TRIGGERED BIGINT(10) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(200) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_TRIGGER_LISTENERS
  (
    TRIGGER_NAME  VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    TRIGGER_LISTENER VARCHAR(200) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);


CREATE TABLE QRTZ_CALENDARS
  (
    CALENDAR_NAME  VARCHAR(200) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (CALENDAR_NAME)
);



CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
  (
    TRIGGER_GROUP  VARCHAR(200) NOT NULL, 
    PRIMARY KEY (TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
  (
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    IS_VOLATILE VARCHAR(1) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT(13) NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_STATEFUL VARCHAR(1) NULL,
    REQUESTS_RECOVERY VARCHAR(1) NULL,
    PRIMARY KEY (ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
  (
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
    CHECKIN_INTERVAL BIGINT(13) NOT NULL,
    PRIMARY KEY (INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
  (
    LOCK_NAME  VARCHAR(40) NOT NULL, 
    PRIMARY KEY (LOCK_NAME)
);


INSERT INTO QRTZ_LOCKS values('TRIGGER_ACCESS');
INSERT INTO QRTZ_LOCKS values('JOB_ACCESS');
INSERT INTO QRTZ_LOCKS values('CALENDAR_ACCESS');
INSERT INTO QRTZ_LOCKS values('STATE_ACCESS');
INSERT INTO QRTZ_LOCKS values('MISFIRE_ACCESS');
-- -------------------
--     END  QRTZ    --
-- -------------------


CREATE TABLE hotfolder_files (
	entryid INT NOT NULL auto_increment,
	path VARCHAR(1000) NOT NULL,
	flowid int not null,
	in_user varchar(100) not null,
  	entry_date timestamp not null,
  	processed_path varchar(1000),
  	created_pid int,
  	CONSTRAINT PK_HOTF PRIMARY KEY (entryid)
) ENGINE = INNODB DEFAULT CHARSET=utf8;

-- Add pid index to flow_state_history
create index idx_fsh_pid on flow_state_history(pid);

CREATE TABLE `user_passimage` (
  `passid` INT NOT NULL AUTO_INCREMENT,
  `userid` INT NOT NULL,
  `passimage` LONGBLOB,
  `rubimage` LONGBLOB,
  PRIMARY KEY (passid),
  CONSTRAINT `user_passimage_fk` FOREIGN KEY `user_passimage_fk` (`userid`)
  REFERENCES `users` (`userid`)
) 
ENGINE = INNODB DEFAULT CHARSET=utf8;

alter table activity_history add INDEX IND_ACTIVITY_HISTORY_PID (pid);

CREATE TABLE `folder` (
  `id` INT NOT NULL auto_increment,
  `name` VARCHAR(50)  NOT NULL,
  `color` VARCHAR(10),
  `userid` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

ALTER TABLE `activity` 
  ADD CONSTRAINT `activity_folder_fk`
  FOREIGN KEY (`folderid` )
  REFERENCES `folder` (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;


CREATE TABLE `comment` (
  `id` INT NOT NULL auto_increment,
  `date` DATETIME NOT NULL,
  `userid` VARCHAR(100) NOT NULL,
  `comment` VARCHAR(125) NOT NULL,
  `flowid` INT NOT NULL,
  `pid` INT NOT NULL,
  `subpid` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  CONSTRAINT `coment_process_fk` FOREIGN KEY `coment_process_fk` (`flowid`, `pid`, `subpid`)
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

drop view if exists all_process_annotation_icons;
create view all_process_annotation_icons (iconid, flowid, pid, subpid) as
select 0 iconid, flowid, pid, subpid from comment
union
select pl.labelid iconid, pl.flowid, pl.pid, pl.subpid from process_label pl
union
select 99999 iconid, flowid, pid, subpid from deadline;

drop view if exists annotation_icons;
create view annotation_icons (iconid, icon) as
select 0 iconid, 'label_comment.png'
union
select id iconid, icon icon from label
union
select 99999 iconid, 'label_clock.png';

drop view if exists process_annotation_icon_link;
create view process_annotation_icon_link (iconid, flowid, pid, subpid) as
select min(iconid), flowid, pid, subpid from all_process_annotation_icons
group by flowid, pid, subpid;

DROP VIEW IF EXISTS process_annotation_icon;
CREATE VIEW process_annotation_icon (flowid, pid, subpid, icon, iconid) as
select flowid, pid, subpid, ai.icon, ai.iconid 
from process_annotation_icon_link pail 
inner join annotation_icons ai on pail.iconid = ai.iconid;

DROP TABLE IF EXISTS user_session;

CREATE TABLE `user_session` (
  `id` INT NOT NULL auto_increment,
  `userid` VARCHAR(100) NOT NULL,
  `session`  BLOB NOT NULL,
  PRIMARY KEY (`id`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS serial_code_templates;

CREATE TABLE `serial_code_templates` (
  `template` VARCHAR(50) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(500),
  `callback` VARCHAR(50),
  `flag` VARCHAR(50),
  `organization` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`template`, `name`)
)
ENGINE = INNODB DEFAULT CHARSET=utf8;

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

DELIMITER ;
create view process_intervenients (userid, pid) as
    select distinct userid, pid from activity
    union 
    select distinct userid, pid from activity_history;
DELIMITER ;

DROP TABLE IF EXISTS serial_code_templates;

CREATE TABLE `serial_code_templates` (
  `template` VARCHAR(50) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(500),
  `callback` VARCHAR(50),
  `flag` VARCHAR(50),
  `organization` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`template`, `name`, `organization`)
);

ALTER TABLE `iflow`.`reporting` ADD INDEX `IDX_REPORTING`(`flowid`, `pid`, `subpid`);

-- insert into counter values ('nodekey',0,NOW());

DROP TABLE IF EXISTS `iflow`.`active_node`;
CREATE TABLE  `iflow`.`active_node` (
  `nodekey` varchar(50) NOT NULL,
  `expiration` datetime NOT NULL,
  PRIMARY KEY (`nodekey`)
);

DELIMITER $$
DROP PROCEDURE IF EXISTS `get_next_nodekey` $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_next_nodekey`(OUT retnodekey INTEGER)
BEGIN
    DECLARE tmp integer;
    SELECT GET_LOCK('iflow.get_next_nodekey',-1);
    set retnodekey = 1;
    select value into tmp from counter where name='nodekey';
    update counter set value=(tmp +1) where  name='nodekey';
    select value into retnodekey from counter where name='nodekey';
    SELECT RELEASE_LOCK('iflow.get_next_nodekey');
END $$
DELIMITER ;

DROP TABLE IF EXISTS `iflow`.`sharedobjectrefresh`;
CREATE TABLE  `iflow`.`sharedobjectrefresh` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `flowid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;