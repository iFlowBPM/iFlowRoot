# Investigação - Problema Agendamentos Quartz

## Data
2025-11-11

## Problema
Na instalação antiga (iFlowRoot), o JSP `Admin/flowSchedule/flow_schedule_list.jsp` não apresenta resultados, apesar das tabelas Quartz terem dados.

## Análise do Código

### Fluxo de Execução
1. **FlowScheduleServlet.java:73-76** - Endpoint `flow_schedule_list`
   - Chama `populateListData(userInfo, request)`

2. **FlowScheduleServlet.java:229-245** - Método `populateListData()`
   ```java
   String user = userInfo.getUserId();  // linha 231
   ArrayList<FlowScheduleDataInterface> listOfJobs = adminFlowScheduleBean.getScheduledFlowsJobs(userInfo, user);  // linha 237
   session.setAttribute("flow_events_list", listOfJobs);  // linha 244
   ```

3. **AdministrationFlowScheduleBean.java:67-74** - Delega para CronManager
   ```java
   ArrayList<FlowScheduleDataInterface> jobsList = cronManager.getScheduledFlowsJobs(userInfo, fromUser);
   ```

4. **CronManager.java:286-336** - Método `getScheduledFlowsJobs()`
   - Linha 288: `String[] listOfJobsNames = getJobNames(fromUser);`
   - **FILTRO POR UTILIZADOR na linha 203:**
   ```java
   if(null != userName && !userName.equalsIgnoreCase(jobTokens[1])) continue;
   ```

### Descoberta Chave
O código **filtra jobs por utilizador**. Jobs criados para "SRV_pari" só aparecem se:
- O utilizador logado for "SRV_pari" OU
- O utilizador for **admin** (o que deve passar null ou ter lógica especial)

## Comparação de Dados

### Instalação que NÃO FUNCIONA
**QRTZ_JOB_DETAILS:** 3 registos
- ArchiverJob (iFlowCore)
- 64-SRV_pari (ScheduledFlows)
- 195-SRV_pari (ScheduledFlows)

**QRTZ_TRIGGERS:** 3 registos
- ArchiverJob
- 64-SRV_pari
- 195-SRV_pari

**QRTZ_SIMPLE_TRIGGERS:** 3 registos
- 195-SRV_pari
- 64-SRV_pari
- **118-SRV_pari** ⚠️ ÓRFÃO (não existe em JOB_DETAILS nem TRIGGERS)

**QRTZ_CRON_TRIGGERS:** 1 registo
- ArchiverJob

### Instalação que FUNCIONA
**QRTZ_JOB_DETAILS:** 8 registos
- ArchiverJob (iFlowCore)
- 118-SRV_pari, 195-SRV_pari, 173-SRV_pari, 64-SRV_pari, 197-SRV_pari, 198-SRV_pari, 221-SRV_pari (todos ScheduledFlows)

**QRTZ_TRIGGERS:** 8 registos (todos consistentes)

**QRTZ_SIMPLE_TRIGGERS:** 7 registos (todos consistentes)

**QRTZ_CRON_TRIGGERS:** 1 registo
- ArchiverJob

## Problemas Identificados

### 1. Registo Órfão (menor)
O trigger **118-SRV_pari** existe em QRTZ_SIMPLE_TRIGGERS mas não tem correspondência em QRTZ_JOB_DETAILS nem QRTZ_TRIGGERS.

**Correção:**
```sql
DELETE FROM iflow.QRTZ_SIMPLE_TRIGGERS
WHERE TRIGGER_NAME = '118-SRV_pari'
AND TRIGGER_GROUP = 'ScheduledFlows';
```

### 2. Permissões de Admin (PRINCIPAL)
- Utilizador: **jcosta.infosistema**
- Na instalação que funciona: vê TODOS os 7 jobs do SRV_pari
- Na instalação que NÃO funciona: não vê nenhum job

**Conclusão:** O utilizador "jcosta.infosistema" na instalação que não funciona **não tem permissões de admin** corretas.

Quando é admin, o sistema deve permitir ver todos os jobs agendados, independentemente do utilizador dono do job.

## Próximos Passos

1. **Limpar registo órfão** (recomendado mas não resolve o problema principal)
2. **Verificar permissões/roles do utilizador "jcosta.infosistema"** na instalação que não funciona
3. **Comparar com a instalação que funciona:** que tabelas definem se um utilizador é admin?
   - USERS / UTILIZADORES
   - USER_ROLES / PROFILES
   - PERMISSIONS / PERMISSOES

## Arquivos Relevantes

- `/iflow-web/src/main/webapp/Admin/flowSchedule/flow_schedule_list.jsp`
- `/iflow-web/src/main/java/pt/iflow/servlets/FlowScheduleServlet.java`
- `/iflow-web/src/main/java/pt/iflow/core/AdministrationFlowScheduleBean.java`
- `/iflow-web/src/main/java/pt/iflow/scheduler/CronManager.java`
- `/iflow-home/config/quartz.properties`
- `/iflow-home/config/iflow.properties`

## Configuração Quartz

**quartz.properties:**
```properties
org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
org.quartz.jobStore.tablePrefix=QRTZ_
org.quartz.jobStore.dataSource=NAME
```

**iflow.properties:**
```properties
DB_TYPE=MYSQL
DB_POOL_NAME=java\:comp/env/jdbc/iFlowMyDS
```

## Notas
- Configuração é idêntica nas duas instalações (cópia)
- Datasources configurados corretamente
- Código é o mesmo
- **Diferença está nos DADOS de permissões/roles**
