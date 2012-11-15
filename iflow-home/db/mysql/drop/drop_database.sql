-- store current charset and set it to UTF-8
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;

drop procedure IF EXISTS deleteProc;

drop procedure IF EXISTS deleteFlow;

drop procedure IF EXISTS updateFlowSetting;

DROP PROCEDURE IF EXISTS get_next_pid;

DROP PROCEDURE IF EXISTS get_next_sub_pid;

DROP PROCEDURE IF EXISTS get_next_mid;

DROP PROCEDURE IF EXISTS forward_proc_to_user;

DROP FUNCTION IF EXISTS seq_flow_settings_nextval;

DROP FUNCTION IF EXISTS get_procdata_value;

DROP PROCEDURE IF EXISTS archiveProc;

drop view IF EXISTS activity_delegated;

drop trigger IF EXISTS trig_flow_state_insert;
drop trigger IF EXISTS trig_flow_state_update;
drop trigger IF EXISTS trig_flow_state_delete;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `system_users`;

DROP TABLE IF EXISTS `flow`;

DROP TABLE IF EXISTS `process`;

DROP TABLE IF EXISTS `process_history`;

DROP TABLE IF EXISTS `modification`;

DROP TABLE IF EXISTS `flow_roles`;

DROP TABLE IF EXISTS `flow_state`;

DROP TABLE IF EXISTS `flow_state_history`;

DROP TABLE IF EXISTS `flow_settings`;

DROP TABLE IF EXISTS `flow_settings_history`;

DROP TABLE IF EXISTS `activity`;

DROP TABLE IF EXISTS `activity_hierarchy`;

DROP TABLE IF EXISTS `activity_hierarchy_history`;

DROP TABLE IF EXISTS `activity_history`;

DROP TABLE IF EXISTS profiles_tabs;

DROP TABLE IF EXISTS organizations_tabs;

DROP TABLE IF EXISTS `profiles`;

DROP TABLE IF EXISTS `applications`;

DROP TABLE IF EXISTS `applicationprofiles`;

DROP TABLE IF EXISTS `counter`;

DROP TABLE IF EXISTS `dirty_email`;

DROP TABLE IF EXISTS `documents`;

DROP TABLE IF EXISTS `email`;

DROP TABLE IF EXISTS `event_data`;

DROP TABLE IF EXISTS `forkjoin_blocks`;

DROP TABLE IF EXISTS `forkjoin_hierarchy`;

DROP TABLE IF EXISTS `forkjoin_mines`;

DROP TABLE IF EXISTS `forkjoin_state_dep`;

DROP TABLE IF EXISTS `iflow_errors`;

DROP TABLE IF EXISTS `links_flows`;

DROP TABLE IF EXISTS `new_features`;

DROP TABLE IF EXISTS `organizations`;

DROP TABLE IF EXISTS `organizational_units`;

DROP TABLE IF EXISTS `organization_theme`;

DROP TABLE IF EXISTS `queue_proc`;

DROP TABLE IF EXISTS `queue_data`;

DROP TABLE IF EXISTS `users`;

DROP TABLE IF EXISTS `unitmanagers`;

DROP TABLE IF EXISTS `userprofiles`;

DROP TABLE IF EXISTS `sub_flow`;

DROP TABLE IF EXISTS `flow_history`;

DROP TABLE IF EXISTS `sub_flow_history`;

DROP TABLE IF EXISTS flow_template;

DROP TABLE IF EXISTS sub_flow_template;

DROP TABLE IF EXISTS user_activation;

DROP TABLE IF EXISTS event_info;

DROP TABLE IF EXISTS seq_flow_settings;

DROP TABLE IF EXISTS email_confirmation;

DROP TABLE IF EXISTS user_settings;

DROP TABLE IF EXISTS organization_settings;

DROP TABLE IF EXISTS `notifications`;

DROP TABLE IF EXISTS user_notifications;

DROP TABLE IF EXISTS series;

DROP TABLE IF EXISTS process_archive;

DROP TABLE IF EXISTS data_string_archive;

DROP TABLE IF EXISTS data_numeric_archive;

DROP TABLE IF EXISTS activity_archive;

DROP TABLE IF EXISTS modification_archive;

DROP TABLE IF EXISTS reporting;

DROP TABLE IF EXISTS flow_state_log;

DROP TABLE IF EXISTS log;

DROP TABLE IF EXISTS external_dms;

DROP TABLE IF EXISTS external_dms_properties;

DROP TABLE IF EXISTS migration_log;

DROP TABLE IF EXISTS QRTZ_JOB_LISTENERS;

DROP TABLE IF EXISTS QRTZ_TRIGGER_LISTENERS;

DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;

DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;

DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;

DROP TABLE IF EXISTS QRTZ_LOCKS;

DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;

DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;

DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;

DROP TABLE IF EXISTS QRTZ_TRIGGERS;

DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;

DROP TABLE IF EXISTS QRTZ_CALENDARS;

DROP TABLE IF EXISTS hotfolder_files;

DROP TABLE IF EXISTS user_passimage;

DROP TABLE IF EXISTS `folder`;

DROP TABLE IF EXISTS `comment`;

DROP TABLE IF EXISTS `process_label_history`;

DROP TABLE IF EXISTS `process_label`;

DROP TABLE IF EXISTS `label`;

DROP TABLE IF EXISTS `deadline_history`;

DROP TABLE IF EXISTS `deadline`;

DROP TABLE IF EXISTS user_session;

SET FOREIGN_KEY_CHECKS = 1;

-- restore previous charset
SET character_set_client = @saved_cs_client;
