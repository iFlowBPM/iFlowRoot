create table user_passimage (
        passid                int,
        userid                int constraint userid_nn not null,
        passimage             blob,
        constraint passid_pk primary key (passid)
        constraint fk_userid foreign key (userid)
        references users (userid)
        on delete cascade,
);
create sequence seq_passid increment by 1 start with 1 nocycle order;

alter table documents add numass int default 0 NOT NULL;

ALTER TABLE notifications ADD COLUMN link VARCHAR(45) NOT NULL DEFAULT 'false'  AFTER flowid;