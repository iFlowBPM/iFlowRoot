ALTER TABLE flow ADD max_block_id NUMBER(11);

CREATE TABLE subflow_block_mapping (
  id NUMBER(11) NOT NULL IDENTITY,
  created timestamp NOT NULL,
  flowname varchar(64) NOT NULL,
  sub_flowname varchar(64) NOT NULL,
  original_blockid NUMBER(11) NOT NULL,
  mapped_blockid NUMBER(11) NOT NULL,
  PRIMARY KEY (id) 
) ;

ALTER TABLE flow_history ADD max_block_id NUMBER(11);