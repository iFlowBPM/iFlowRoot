create table series (
	id int not null auto_increment,
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
	constraint series_pk primary key (id),
	constraint series_name_uk unique (name)
)
ENGINE = INNODB;

alter table flow add seriesid int;

alter table process add pnumber varchar(128);
update process set pnumber=pid;
alter table process modify pnumber varchar(128) not null;

alter table process_history add pnumber varchar(128);
update process_history set pnumber=pid;
alter table process_history modify pnumber varchar(128) not null;


DELIMITER //
DROP PROCEDURE get_next_pid;
//
CREATE PROCEDURE get_next_pid (OUT retpid INTEGER,
 OUT retsubpid INTEGER,
 aflowid INTEGER,
 acreatedate DATETIME,
 acreator VARCHAR(32))
BEGIN
 select value into retpid from counter where name='pid';
 set retpid = retpid + 1;
 update counter set value=retpid where name='pid';
 set retsubpid = 1;
 insert into process (flowid,pid,subpid,mid,created,info,creator,pnumber) values
  (aflowid,retpid,retsubpid,0,acreatedate,'',acreator,retpid);
 insert into process_history (flowid,pid,subpid,mid,created,info,creator,pnumber) values
  (aflowid,retpid,retsubpid,0,acreatedate,'',acreator,retpid);
END;
//
DELIMITER ;

