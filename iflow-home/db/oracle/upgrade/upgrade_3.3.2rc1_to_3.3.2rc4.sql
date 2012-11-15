alter table documents add flowid int default 0;
alter table documents add pid int default 0;
alter table documents add subpid int default 0;
update documents set flowid=0,pid=0,subpid=0;
alter table documents modify flowid not null;
alter table documents modify pid not null;
alter table documents modify subpid not null;

-- Adaptar as queries seguintes para actualizar os ids de processo dos ficheiros.
-- update documents set flowid=?,pid=?,subpid=? where docid=?
