Para o processo Ad-hoc

- Na base de dados, schema cma, correr script update_db.sql

- Criar perfis:
	* Aprov/AssistenteTecnico  Assistente Técnico
	* Aprov/CoordenadorTecnico	Coordenador Técnico
	* Aprov/AssistenteOperacional	Assistente Operacional
	* Aprov/EncarregadoOperacional Encarregado Operacional
	* Aprov/TecnicoSuperior	Técnico Superior
	* Aprov/ChefeDivisao	Chefe de Divisão
	* Aprov/Vereador	Vereador
	* Aprov/Presidente	Presidente

Todos os utilizadores que participarem no processo de aprovação terão que estar num destes perfis:
- das 4 UO's 
    -> DivAdministrativaJuridica
    	* chefeDivisao - Aprov/ChefeDivisao
    	* atendimento1 - Coordenador Técnico
    	* operadorAtendimento -	Assistente Operacional
    	* chefeAtendimento - Aprov/ChefeDivisao
    	* expediente1 - Assistente Operacional
		* actas1 - Assistente Operacional
		* aprovisionamento1 - Assistente Operacional
		* oficialPublico1 - Aprov/TecnicoSuperior
    -> DivFinanceira
    	* chefeFinanceiro - Aprov/ChefeDivisao
    	* financeiro1 - Aprov/AssistenteTecnico
    	* contabilidade1 - Aprov/AssistenteTecnico
    -> DivRH
    	* chefeRH - Aprov/ChefeDivisao
    -> DivOrdenamentoGestUrbanistica
    	* chefeObra - Aprov/ChefeDivisao
    	* obra1 - Aprov/AssistenteTecnico
    	* obra2 - Aprov/AssistenteTecnico
    	* obra3 - Aprov/AssistenteTecnico
    	* chefeProjEmpreitadas - Aprov/Encarregado Operacional
	-> Executivo
		* vereador1 - Aprov/Vereador
		* presidente - Aprov/Presidente
