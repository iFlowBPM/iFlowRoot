-- agon 27/08/2009
insert into event_info (name,description) values ('Deadline', 'dateVar=date');
-- agon 01/09/2009
alter table reporting modify cod_reporting varchar2(1024);
-- agon 03/09/2009
alter table series add extra_options varchar2(200);
alter table series add organizationid varchar2(50);
alter table series drop constraint uk_series_name;
alter table series add constraint uk_series_name unique (name, organizationid);
-- agon 07/09/2009
alter table users modify username VARCHAR2(50);
alter table system_users modify username VARCHAR2(50);
alter table log modify username VARCHAR2(50);