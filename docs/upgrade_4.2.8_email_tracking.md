# Notas de Upgrade - Email Tracking v4.2.8

## 1. Base de Dados

**Para instalações novas**: o script `upgrade_4.2.7_to_4.2.8.sql` já inclui tudo.

**Para instalações que já tenham as tabelas `smtp_error_codes` e `email_request_log`** (upgrade incremental):

```sql
-- Novo código para emails expirados na fila do Postfix
INSERT INTO smtp_error_codes VALUES ('902', 'hard_failure', 'Delivery expired - tempo de vida na fila excedido.');
```

## 2. Configuração (`iflow.properties`)

Adicionar as seguintes propriedades:

```properties
# Alerta de emails presos em código 150 (pendente)
MAIL_LOG_STALE_THRESHOLD_MINUTES=30        # minutos após os quais um 150 é considerado stale
MAIL_LOG_STALE_ALERT_THRESHOLD=10          # quantidade mínima para disparar alerta no log
MAIL_LOG_STALE_ALERT_COOLDOWN_MINUTES=15   # intervalo mínimo entre alertas repetidos
```

> Se omitidas, usam os valores por defeito indicados acima.

## 3. Queries (`queries_MYSQL.properties` / `queries_SQLSERVER.properties`)

**Substituir** a query `UPDATE_EMAIL_REQUEST_LOG_BY_QUEUEID` (adicionado `AND queue_id IS NULL`):

```properties
EmailManager.UPDATE_EMAIL_REQUEST_LOG_BY_QUEUEID=UPDATE email_request_log SET status = ?, smtp_code = ?, processed_at = CURRENT_TIMESTAMP, locked = ?, queue_id = ? WHERE to_address = ? AND locked = 0 AND queue_id IS NULL
```

**Adicionar** nova query:

```properties
EmailManager.COUNT_STALE_150=SELECT COUNT(*) FROM email_request_log WHERE smtp_code = '150' AND locked = 0 AND requested_at < ?
```

## 4. Ficheiro de Checkpoint

O formato do checkpoint (`/var/lib/iflow/maillog.checkpoint`) mudou de `timestamp` para `timestamp|filesize`. A migração é **automática** - o novo código lê ambos os formatos.

## 5. Postfix / Operacional

Para a detecção de rotação de logs funcionar correctamente:
- O logrotate deve manter o ficheiro `.1` (não comprimido) pelo menos durante um ciclo de scan (10s por defeito)
- Ficheiros `.1.gz` também são suportados
- Verificar que o Postfix escreve logs no caminho configurado em `MAIL_LOG_FILE`
