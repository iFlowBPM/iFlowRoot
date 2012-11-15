-- APANHA LISTA DE PIDS dos processos a migrar
    SELECT pid AS pid
    FROM process
    WHERE flowid = <FLOWID_DO_FLUXO_ANTIGO> AND closed = 1;
												
-- Alterar os flowid's do historico de actividades
	-- UPDATE flow_state_history 
UPDATE flow_state_history 
SET flowid = <FLOWID_DO_FLUXO_NOVO>
WHERE pid = <PID_PARA_TESTE_UNITARIO> AND		  
	pid IN (<List_of_pids_to_close>);
									
	-- UPDATE flow_state 
UPDATE flow_state 
SET flowid = <FLOWID_DO_FLUXO_NOVO>
WHERE pid = <PID_PARA_TESTE_UNITARIO> AND		  
	pid IN (<List_of_pids_to_close>);

	-- UPDATE documents 
UPDATE documents 
SET flowid = <FLOWID_DO_FLUXO_NOVO>
WHERE pid = <PID_PARA_TESTE_UNITARIO> AND		  
	pid IN (<List_of_pids_to_close>);					
				
	-- UPDATE activity_history 
UPDATE activity_history 
SET flowid = <FLOWID_DO_FLUXO_NOVO>
WHERE pid = <PID_PARA_TESTE_UNITARIO> AND		  
	pid IN (<List_of_pids_to_close>);

	-- UPDATE process_history 
UPDATE process_history 
SET flowid = <FLOWID_DO_FLUXO_NOVO>
WHERE pid = <PID_PARA_TESTE_UNITARIO> AND		  
	pid IN (<List_of_pids_to_close>);

	-- UPDATE flowid dos links de documentos no procdata
UPDATE process set procdata = replace(procdata, 'fid="<FLOWID_DO_FLUXO_ANTIGO>"','fid="<FLOWID_DO_FLUXO_NOVO>"') where flowid = <FLOWID_DO_FLUXO_ANTIGO> AND closed = 1;


-- ULTIMO UPDATE
-- Alterar os flowid's dos processos fechados
UPDATE process 
SET flowid = <FLOWID_DO_FLUXO_NOVO> 
WHERE flowid = <FLOWID_DO_FLUXO_ANTIGO> AND closed = 1 AND		  
	pid IN (<List_of_pids_to_close>);		
			
		
