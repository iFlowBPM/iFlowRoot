alter table documents add flowid int not null default 0;
alter table documents add pid int not null default 0;
alter table documents add subpid int not null default 0;
update documents set flowid=0,pid=0,subpid=0;

-- Adaptar as queries seguintes para actualizar os ids de processo dos ficheiros.
-- update documents set flowid=?,pid=?,subpid=? where docid=?
