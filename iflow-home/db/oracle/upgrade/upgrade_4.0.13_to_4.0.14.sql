create view process_intervenients (userid, pid) as
    select distinct userid, pid from activity
    union 
    select distinct userid, pid from activity_history;

alter table user_passimage add rubimage blob;

commit;

ALTER TABLE flow ADD max_block_id NUMBER(11);

CREATE SEQUENCE "seq_subflow_block_mapping" MINVALUE 1 INCREMENT BY 1 START WITH 1 NOMAXVALUE;

CREATE TABLE subflow_block_mapping (
  id NUMBER(11) NOT NULL,
  created timestamp NOT NULL,
  flowname varchar(64) NOT NULL,
  sub_flowname varchar(64) NOT NULL,
  original_blockid NUMBER(11) NOT NULL,
  mapped_blockid NUMBER(11) NOT NULL,
  PRIMARY KEY (id) 
) ;

CREATE OR REPLACE TRIGGER subflow_block_mapping_t
before insert on subflow_block_mapping
for each row
begin
select "seq_subflow_block_mapping".nextval into :new.id from dual;
END;

ALTER TABLE flow_history ADD max_block_id NUMBER(11);
