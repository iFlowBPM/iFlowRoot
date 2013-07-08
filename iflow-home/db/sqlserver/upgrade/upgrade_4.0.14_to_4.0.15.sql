ALTER TABLE flow ADD max_block_id NUMERIC(11);

CREATE TABLE subflow_block_mapping (
  id NUMERIC(11) NOT NULL IDENTITY,
  created timestamp NOT NULL,
  flowname varchar(64) NOT NULL,
  sub_flowname varchar(64) NOT NULL,
  original_blockid NUMERIC(11) NOT NULL,
  mapped_blockid NUMERIC(11) NOT NULL,
  PRIMARY KEY (id) 
) ;

ALTER TABLE flow_history ADD max_block_id NUMERIC(11);