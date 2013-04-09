CREATE VIEW process_intervenients AS
    SELECT DISTINCT userid, pid FROM activity
    UNION 
    SELECT DISTINCT userid, pid FROM activity_history;
    
ALTER TABLE user_passimage ADD COLUMN rubimage blob;

insert into flow_settings (flowid, name, description, value, isquery, mdate)
select flowid, 'FLOW_MENU_ACCESSIBLE', 'Permitir visualizar no menu.', 'Sim', 0, '2011-11-21 10:00:00' from flow;

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
