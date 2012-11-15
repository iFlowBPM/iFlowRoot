DROP PROCEDURE DELETEPROC;

DROP PROCEDURE DELETEFLOW;

DROP PROCEDURE UPDATEFLOWSETTING;

DROP PROCEDURE GET_NEXT_PID;

DROP PROCEDURE GET_NEXT_SUB_PID;

DROP PROCEDURE GET_NEXT_MID;

DROP FUNCTION GET_PROCDATA_VALUE;

DROP PROCEDURE ARCHIVEPROC;
drop view activity_delegated;
drop trigger TRIG_FLOW_STATE;
drop trigger TRIG_INSERT_ORGANIZATION;
drop trigger TRIG_INSERT_ORGANIZATIONALUNIT;
drop trigger TRIG_INSERT_PROFILE;
drop trigger TRIG_INSERT_USER;
drop trigger TRIG_INSERT_SERIES;
drop trigger TRIG_UPDATE_SERIES;
DROP TABLE system_users;

DROP TABLE flow;

DROP TABLE process;

DROP TABLE process_history;

DROP TABLE modification;

DROP TABLE flow_roles;

DROP TABLE flow_state;

DROP TABLE flow_state_history;

DROP TABLE flow_settings;

DROP TABLE flow_settings_history;

DROP TABLE activity;

DROP TABLE activity_hierarchy;

DROP TABLE activity_hierarchy_history;

DROP TABLE activity_history;

DROP TABLE profiles_tabs;

DROP TABLE organizations_tabs;

DROP TABLE profiles;

DROP TABLE applications;

DROP TABLE applicationprofiles;

DROP TABLE counter;

DROP TABLE dirty_email;

DROP TABLE documents;

DROP TABLE email;

DROP TABLE event_data;

DROP TABLE forkjoin_blocks;

DROP TABLE forkjoin_hierarchy;

DROP TABLE forkjoin_mines;

DROP TABLE forkjoin_state_dep;

DROP TABLE iflow_errors;

DROP TABLE links_flows;

DROP TABLE new_features;

DROP TABLE organizations;

DROP TABLE organizational_units;

DROP TABLE organization_theme;

DROP TABLE queue_proc;

DROP TABLE queue_data;

DROP TABLE users;

DROP TABLE unitmanagers;

DROP TABLE userprofiles;

DROP TABLE sub_flow;

DROP TABLE flow_history;

DROP TABLE sub_flow_history;

DROP TABLE flow_template;

DROP TABLE sub_flow_template;

DROP TABLE user_activation;

DROP TABLE event_info;

DROP TABLE seq_flow_settings;

DROP TABLE email_confirmation;

DROP TABLE user_settings;

DROP TABLE organization_settings;

DROP TABLE notifications;

DROP TABLE user_notifications;

DROP TABLE series;

DROP TABLE process_archive;

DROP TABLE activity_archive;

DROP TABLE modification_archive;

DROP TABLE reporting;

DROP TABLE flow_state_log;

DROP TABLE log;

DROP TABLE migration_log;

DROP TABLE external_dms;

DROP TABLE external_dms_properties;

DROP SEQUENCE SEQ_FLOW;

DROP SEQUENCE SEQ_FLOW_SETTINGS;

DROP SEQUENCE SEQ_ACTIVITY_HIERARCHY;

DROP SEQUENCE SEQ_APPLICATIONS;

DROP SEQUENCE SEQ_DOCUMENTS_ID;

DROP SEQUENCE SEQ_EMAIL;

DROP SEQUENCE SEQ_EVENT_DATA;

DROP SEQUENCE SEQ_IFLOW_ERRORS;

DROP SEQUENCE SEQ_LINKS_FLOWS;

DROP SEQUENCE SEQ_NEW_FEATURES;

DROP SEQUENCE SEQ_ORGANIZATIONAL_UNIT;

DROP SEQUENCE SEQ_ORGANIZATION;

DROP SEQUENCE SEQ_PROFILES;

DROP SEQUENCE SEQ_QUEUE_PROC;

DROP SEQUENCE SEQ_USERS;

DROP SEQUENCE SEQ_SUB_FLOW;

DROP SEQUENCE SEQ_FLOW_HISTORY;

DROP SEQUENCE SEQ_SUB_FLOW_HISTORY;

DROP SEQUENCE SEQ_NOTIFICATIONS;

DROP SEQUENCE SEQ_SYSTEM_USERS;

DROP SEQUENCE SEQ_SERIES;

DELETE FROM qrtz_job_listeners;
DELETE FROM qrtz_trigger_listeners;
DELETE FROM qrtz_fired_triggers;
DELETE FROM qrtz_simple_triggers;
DELETE FROM qrtz_cron_triggers;
DELETE FROM qrtz_blob_triggers;
DELETE FROM qrtz_triggers;
DELETE FROM qrtz_job_details;
DELETE FROM qrtz_calendars;
DELETE FROM qrtz_paused_trigger_grps;
DELETE FROM qrtz_locks;
DELETE FROM qrtz_scheduler_state;

DROP TABLE qrtz_calendars;
DROP TABLE qrtz_fired_triggers;
DROP TABLE qrtz_trigger_listeners;
DROP TABLE qrtz_blob_triggers;
DROP TABLE qrtz_cron_triggers;
DROP TABLE qrtz_simple_triggers;
DROP TABLE qrtz_triggers;
DROP TABLE qrtz_job_listeners;
DROP TABLE qrtz_job_details;
DROP TABLE qrtz_paused_trigger_grps;
DROP TABLE qrtz_locks;
DROP TABLE qrtz_scheduler_state;

DROP TABLE hotfolder_files;

DROP TABLE user_passimage;