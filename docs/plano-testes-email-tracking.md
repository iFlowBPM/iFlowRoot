# Plano de Testes - Sistema de Email Tracking (v4.2.8)

## 1. Envio de Email e Registo Inicial

| # | Teste | Resultado Esperado |
|---|-------|--------------------|
| 1.1 | Enviar email para **um unico destinatario** | Registo criado em `email_request_log` com `smtp_code=150`, `locked=0`, `request_id` UUID valido |
| 1.2 | Enviar email para **multiplos destinatarios** (TO) | Um registo por destinatario, todos com o mesmo `request_id`, `to_address` diferente |
| 1.3 | Enviar email com **destinatario invalido** (SMTP rejeita) | Registo atualizado com codigo de erro SMTP (5xx), status `hard_failure` |
| 1.4 | Enviar email com **servidor SMTP indisponivel** | Registo atualizado com codigo de erro, sem crash da aplicacao |
| 1.5 | Verificar que `X-RequestId` header e adicionado ao email | Inspecionar headers do email recebido ou logs SMTP |

## 2. MailLogManager - Scan do Log Postfix

| # | Teste | Resultado Esperado |
|---|-------|--------------------|
| 2.1 | Email enviado com sucesso (log Postfix com `status=sent`, codigo 250) | Registo atualizado para `smtp_code=250`, `locked=1` |
| 2.2 | Email com bounce permanente (5xx) | Registo atualizado com codigo 5xx correspondente |
| 2.3 | Email com bounce temporario (4xx) | Registo atualizado com codigo 4xx correspondente |
| 2.4 | Email com `status=expired` no log | Registo atualizado com `smtp_code=902` |
| 2.5 | Linha de log com `queueId` mas **sem recipient** | Warning nos logs da aplicacao, sem crash |
| 2.6 | Linha de log com `queueId` e `recipient` mas **sem smtpCode** | Debug log com linha completa para investigacao |
| 2.7 | Verificar que o scan **respeita o checkpoint** (nao reprocessa linhas antigas) | Apos restart, apenas linhas novas sao processadas |
| 2.8 | **Ficheiro de log inexistente** | Warning no log, scanner continua sem crash |

## 3. Rotacao de Logs

| # | Teste | Resultado Esperado |
|---|-------|--------------------|
| 3.1 | Simular rotacao: reduzir tamanho do ficheiro (novo `maillog` menor que checkpoint) | Scanner deteta rotacao, processa `maillog.1` antes do `maillog` atual |
| 3.2 | Rotacao com **ficheiro comprimido** (`maillog.1.gz`) | Scanner descomprime e processa o `.gz` corretamente |
| 3.3 | Rotacao com **`maillog.1` em texto simples** | Scanner processa o ficheiro sem problemas |
| 3.4 | Rotacao quando **nem `maillog.1` nem `maillog.1.gz` existem** | Scanner ignora graciosamente, continua com o ficheiro atual |

## 4. Transicao de Ano

| # | Teste | Resultado Esperado |
|---|-------|--------------------|
| 4.1 | Em Janeiro, processar linha de log de **Dezembro** (ex: `Dec 31 23:59:59`) | Timestamp calculado como Dezembro do **ano anterior**, nao do ano atual |
| 4.2 | Em Janeiro, processar linha de log de **Janeiro** | Timestamp calculado corretamente com o ano atual |
| 4.3 | Em Dezembro, processar linha de Dezembro | Timestamp com ano atual (sem ajuste) |

## 5. Alertas de Registos Stale (codigo 150)

| # | Teste | Resultado Esperado |
|---|-------|--------------------|
| 5.1 | Criar >10 registos com `smtp_code=150` ha mais de 30 minutos | WARNING no log alertando sobre registos stale |
| 5.2 | Verificar **cooldown** do alerta (default 15 min) | Segundo alerta **nao** e emitido se cooldown nao expirou |
| 5.3 | Registos com `smtp_code=150` mas `locked=1` | **Nao** contam como stale (ja processados) |
| 5.4 | Menos que 10 registos stale | Nenhum alerta emitido |
| 5.5 | Alterar configuracoes (`STALE_THRESHOLD_MINUTES`, `ALERT_THRESHOLD`, `COOLDOWN_MINUTES`) | Comportamento ajustado conforme novos valores |

## 6. BlockEmailCheckStatus (Bloco BPMN)

| # | Teste | Resultado Esperado |
|---|-------|--------------------|
| 6.1 | Consultar status de email **entregue** (250) | Bloco retorna status correto, fluxo segue pelo caminho de sucesso |
| 6.2 | Consultar status de email **falhado** (5xx) | Bloco retorna erro, fluxo segue pelo caminho de falha |
| 6.3 | Consultar status de email **pendente** (150) | Bloco retorna pending, fluxo pode ser configurado para esperar |
| 6.4 | Consultar status com codigo SMTP **nao existente** na tabela `smtp_error_codes` | LEFT JOIN garante que o registo e retornado (com `description=NULL`) |
| 6.5 | Consultar email com **multiplos destinatarios** | Status correto por destinatario |

## 7. Base de Dados

| # | Teste | Resultado Esperado |
|---|-------|--------------------|
| 7.1 | Executar script `upgrade_4.2.7_to_4.2.8.sql` em BD limpa | Tabelas criadas com colunas corretas, codigos SMTP inseridos |
| 7.2 | Executar script upgrade em BD **ja com tabelas anteriores** | Upgrade idempotente ou com mensagem clara |
| 7.3 | Verificar que `to_address` e `request_id` sao `NOT NULL` | INSERT com NULL deve falhar |
| 7.4 | Verificar PK composta `(request_id, to_address)` | Inserir duplicado deve falhar |
| 7.5 | Verificar codigos SMTP: 150, 250, 400, 441, 544, 901, 902, 999 | Todos presentes com descricoes correctas |
| 7.6 | Testar queries **SQL Server** (alem de MySQL) | Queries `queries_SQLSERVER.properties` funcionam (verificar `locked`, `queue_id`) |

## 8. Ciclo de Vida do Thread

| # | Teste | Resultado Esperado |
|---|-------|--------------------|
| 8.1 | Startup com `MAIL_LOG_SCANNER_ENABLED=true` | MailLogManager inicia como daemon thread |
| 8.2 | Startup com `MAIL_LOG_SCANNER_ENABLED=false` | MailLogManager **nao** inicia |
| 8.3 | Shutdown da aplicacao (undeploy/stop) | `stopManager()` chamado no `destroy()`, thread termina limpo |
| 8.4 | Verificar que thread e **daemon** | Nao impede JVM shutdown |
| 8.5 | Stop e re-start (sem restart da aplicacao) | Thread reinicia corretamente (Runnable, nao Thread) |

## 9. Thread Safety e Concorrencia

| # | Teste | Resultado Esperado |
|---|-------|--------------------|
| 9.1 | Multiplos emails enviados em paralelo | Cada um obtem registos independentes sem conflitos |
| 9.2 | Scanner a processar enquanto emails estao a ser enviados | Sem deadlocks, sem registos corrompidos |
| 9.3 | Verificar que `SimpleDateFormat` nao causa erros concorrentes | Instancia criada por invocacao (nao partilhada) |
| 9.4 | Verificar `volatile` em `keepRunning` | Thread para quando `stopManager()` e chamado |

## 10. Configuracao

| # | Teste | Resultado Esperado |
|---|-------|--------------------|
| 10.1 | Verificar timezone `Europe/Lisbon` (corrigido de `Europ/Lisbon`) | Timestamps calculados corretamente |
| 10.2 | Alterar `MAIL_LOG_THREAD_CICLE` (intervalo de scan) | Scanner respeita novo intervalo |
| 10.3 | Alterar `MAIL_LOG_FILE` para caminho diferente | Scanner le do caminho correto |
| 10.4 | `getPropertyBoolean()` com valor default | Retorna o default quando propriedade nao existe (corrigido no Setup.java) |

## 11. Testes de Regressao

| # | Teste | Resultado Esperado |
|---|-------|--------------------|
| 11.1 | Envio de email **sem** tracking (path `withLog=false`) | Email enviado normalmente, sem registo em `email_request_log` |
| 11.2 | Funcionalidades de email existentes (templates, anexos, CC/BCC) | Nenhuma regressao |
| 11.3 | Export XLSX (alterado no mesmo commit) | BlockData export funciona em formato XLSX |

---

## Prioridades de Execucao

1. **P0 (Critico)**: Testes 1.1-1.4, 2.1-2.4, 6.1-6.3, 7.1, 7.6, 8.1-8.3
2. **P1 (Importante)**: Testes 3.1-3.4, 4.1-4.2, 5.1-5.2, 9.1-9.2, 10.1, 10.4
3. **P2 (Normal)**: Restantes testes

## Pre-requisitos

- Acesso a servidor com Postfix configurado (ou mock de logs)
- Base de dados MySQL **e** SQL Server para testar ambas as queries
- Configuracao de `iflow.properties` com caminhos validos de log
- Acesso a `email_request_log` para verificar registos directamente
