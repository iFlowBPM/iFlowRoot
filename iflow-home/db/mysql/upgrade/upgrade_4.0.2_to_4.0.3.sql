-- agon 27/08/2009
insert into event_info (name,description) values ('Deadline', 'dateVar=date');
-- agon 01/09/2009
alter table reporting modify cod_reporting varchar(1024);
-- agon 03/09/2009
alter table series add extra_options varchar(200);
alter table series add organizationid VARCHAR(50);
alter table series drop index series_name_uk;
alter table series add UNIQUE INDEX `series_name_uk` (`name`(100), `organizationid`(50));
-- agon 07/09/2009
alter table system_users modify username VARCHAR(50) NOT NULL;
alter table system_users drop index uk_users_username;
alter table system_users add UNIQUE INDEX uk_users_username (username(50));
alter table users modify username VARCHAR(50) NOT NULL;
alter table users drop index uk_users_username;
alter table users add UNIQUE INDEX uk_users_username (username(50));
alter table log modify username VARCHAR(50);



