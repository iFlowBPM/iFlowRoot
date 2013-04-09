alter table users add column orgadm_users NUMBER(1)  NOT NULL DEFAULT 1,
alter table users add column orgadm_flows NUMBER(1)  NOT NULL DEFAULT 1,
alter table users add column orgadm_processes NUMBER(1)  NOT NULL DEFAULT 1,
alter table users add column orgadm_resources NUMBER(1)  NOT NULL DEFAULT 1,
alter table users add column orgadm_org NUMBER(1)  NOT NULL DEFAULT 1;