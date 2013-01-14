-- CREATE VIEW dos pids dos processos a migrar
CREATE OR REPLACE VIEW `pids_of_ended_proc_view` AS 
    SELECT pid AS pid
    FROM process
    WHERE flowid = 86 and closed = 1;
												
-- Alterar os flowid's do historico de actividades
	-- UPDATE flow_state_history 
UPDATE flow_state_history 
SET flowid = 87
WHERE pid IN (SELECT pid 
				FROM pids_of_ended_proc_view )
	  and pid = 202840;	
									
	-- UPDATE flow_state 
UPDATE flow_state 
SET flowid = 87
WHERE pid IN (SELECT pid 
				FROM pids_of_ended_proc_view )
	  and pid = 202840;		

	-- UPDATE documents 
UPDATE documents 
SET flowid = 87
WHERE pid IN (SELECT pid 
				FROM pids_of_ended_proc_view )
	  and pid = 202840;						
				
	-- UPDATE activity_history 
UPDATE activity_history 
SET flowid = 87
WHERE pid in (SELECT pid 
				FROM pids_of_ended_proc_view )
	  and pid = 202840;

	-- UPDATE process_history 
UPDATE process_history 
SET flowid = 87
WHERE pid IN (SELECT pid 
				FROM pids_of_ended_proc_view )
	  and pid = 202840;		  
	  
-- ULTIMO UPDATE
-- Alterar os flowid's dos processos fechados
UPDATE process 
SET flowid = 87 
WHERE pid IN (SELECT * 
				FROM ( SELECT pid AS pid
						FROM process
						WHERE flowid = 86 and closed = 1		
					) pidsFromProces
			)
	  and pid = 202840;		
			
		
