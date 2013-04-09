alter table activity_history drop foreign key activity_history_process_fk;
alter table external_dms_properties alter column value VARCHAR(1024);