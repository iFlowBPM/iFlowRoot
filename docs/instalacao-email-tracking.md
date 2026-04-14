# Guia de Instalacao - Email Tracking v4.2.8

## Indice

1. [Pre-requisitos](#1-pre-requisitos)
2. [Base de Dados](#2-base-de-dados)
3. [Configuracao do iFlow](#3-configuracao-do-iflow)
4. [Queries de Base de Dados](#4-queries-de-base-de-dados)
5. [Configuracao do Postfix](#5-configuracao-do-postfix)
6. [Verificacao da Instalacao](#6-verificacao-da-instalacao)
7. [Upgrade de versao anterior](#7-upgrade-de-versao-anterior)
8. [Resolucao de Problemas](#8-resolucao-de-problemas)

---

## 1. Pre-requisitos

- iFlow v4.2.8 ou superior
- MySQL ou SQL Server
- Servidor de email Postfix com logging activo
- Acesso de leitura ao ficheiro de log do Postfix (ex: `/var/log/maillog`)
- Directoria com permissao de escrita para o ficheiro de checkpoint (ex: `/var/lib/iflow/`)

---

## 2. Base de Dados

### 2.1 Instalacao nova

Executar o script completo:

```
iflow-home/db/mysql/upgrade/upgrade_4.2.7_to_4.2.8.sql
```

Este script cria duas tabelas:

**`smtp_error_codes`** - Tabela de referencia com codigos SMTP:

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| `error_code` | VARCHAR(3) PK | Codigo SMTP (ex: 250, 550) |
| `error_type` | VARCHAR(20) | Tipo: `pending`, `success`, `temporary_failure`, `permanent_failure`, `hard_failure` |
| `description` | TEXT | Descricao em portugues |

Codigos pre-carregados: 150 (pending), 211, 214, 220, 221, 250, 251, 252, 354 (success), 421, 450, 451, 452 (temporary), 400, 441, 500-504, 544, 550-554 (permanent), 901, 902, 999 (hard failure).

**`email_request_log`** - Registo de emails enviados:

| Coluna | Tipo | Descricao |
|--------|------|-----------|
| `request_id` | VARCHAR(36) NOT NULL | UUID gerado no envio |
| `to_address` | VARCHAR(255) NOT NULL DEFAULT '' | Endereco do destinatario |
| `status` | VARCHAR(10) | `pending`, `sent`, ou `failed` |
| `smtp_code` | VARCHAR(3) | Codigo SMTP (FK para `smtp_error_codes`) |
| `requested_at` | TIMESTAMP | Data/hora do pedido de envio |
| `processed_at` | TIMESTAMP | Data/hora da actualizacao de status |
| `queue_id` | VARCHAR(20) | ID da fila do Postfix |
| `locked` | INT(1) DEFAULT 0 | 1 = status final, 0 = actualizavel |

Chave primaria composta: `(request_id, to_address)`.

### 2.2 Verificacao

```sql
-- Confirmar que as tabelas foram criadas
SELECT COUNT(*) FROM smtp_error_codes;  -- Esperado: ~30 registos
SELECT * FROM email_request_log LIMIT 1;  -- Deve executar sem erro
```

---

## 3. Configuracao do iFlow

Adicionar as seguintes propriedades ao ficheiro `iflow.properties`:

```properties
# ==========================================
# Email Tracking - MailLogManager
# ==========================================

# Activar/desactivar o scanner de logs (default: false)
MAIL_LOG_SCANNER_ENABLED=true

# Formato da data nos logs do Postfix (default: MMM d HH:mm:ss yyyy)
MAIL_LOG_FORMAT=MMM d HH:mm:ss yyyy

# Timezone dos logs do Postfix (default: Europe/Lisbon)
MAIL_LOG_TIMEZONE=Europe/Lisbon

# Intervalo do ciclo de scan em milissegundos (default: 10000 = 10s)
MAIL_LOG_THREAD_CICLE=10000

# Caminho do ficheiro de log do Postfix
MAIL_LOG_FILE=/var/log/maillog

# Caminho do ficheiro de checkpoint (guarda progresso do scan)
MAIL_LOG_CHECKPOINT_FILE=/var/lib/iflow/maillog.checkpoint

# ==========================================
# Alertas de emails stale (presos em pending)
# ==========================================

# Minutos apos os quais um email em codigo 150 e considerado stale (default: 30)
MAIL_LOG_STALE_THRESHOLD_MINUTES=30

# Quantidade minima de emails stale para disparar alerta (default: 10)
MAIL_LOG_STALE_ALERT_THRESHOLD=10

# Intervalo minimo entre alertas repetidos em minutos (default: 15)
MAIL_LOG_STALE_ALERT_COOLDOWN_MINUTES=15
```

### Notas importantes

- **`MAIL_LOG_SCANNER_ENABLED`**: O default no codigo e `false`. Para activar o tracking e **obrigatorio** definir como `true`.
- **`MAIL_LOG_THREAD_CICLE`**: Atencao ao nome da propriedade (com "CICLE", nao "CYCLE").
- **`MAIL_LOG_CHECKPOINT_FILE`**: A directoria pai deve existir e o utilizador do Tomcat deve ter permissao de escrita.
- Todas as propriedades tem valores por defeito no codigo; se omitidas, usam os defaults indicados acima.

---

## 4. Queries de Base de Dados

Verificar que os ficheiros de queries contem as entradas de email tracking.

### MySQL (`queries_MYSQL.properties`)

```properties
EmailManager.UPDATE_EMAIL_REQUEST_LOG_BY_REQUESTID=UPDATE email_request_log SET status = ?, smtp_code = ?, processed_at = CURRENT_TIMESTAMP, locked = ? WHERE request_id = ? and to_address = ? and locked = 0

EmailManager.UPDATE_EMAIL_REQUEST_LOG_BY_QUEUEID=UPDATE email_request_log SET status = ?, smtp_code = ?, processed_at = CURRENT_TIMESTAMP, locked = ?, queue_id = ? WHERE to_address = ? AND locked = 0 AND queue_id IS NULL

EmailManager.INSERT_EMAIL_REQUEST_LOG=INSERT INTO email_request_log (request_id, status, smtp_code, queue_id, to_address, requested_at, locked) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 0)

EmailManager.COUNT_STALE_150=SELECT COUNT(*) FROM email_request_log WHERE smtp_code = '150' AND locked = 0 AND requested_at < ?
```

### SQL Server (`queries_SQLSERVER.properties`)

As queries sao identicas as de MySQL.

> **Atencao**: Verificar que as colunas usam `locked` (nao `lock`) e `queue_id` (nao `queue?id`). Versoes anteriores tinham estes erros corrigidos no commit `8c14f20`.

---

## 5. Configuracao do Postfix

### 5.1 Logging

Garantir que o Postfix escreve logs no caminho configurado em `MAIL_LOG_FILE`:

```bash
# Verificar onde o Postfix escreve os logs
postconf mail_log_path
# ou verificar no rsyslog/syslog
grep "mail" /etc/rsyslog.conf
```

### 5.2 Rotacao de logs (logrotate)

O sistema suporta rotacao de logs. Para funcionar correctamente:

- O logrotate deve manter o ficheiro `.1` (nao comprimido) pelo menos durante um ciclo de scan (10s por defeito)
- Ficheiros `.1.gz` (comprimidos com gzip) tambem sao suportados
- O scanner detecta a rotacao automaticamente comparando o tamanho do ficheiro com o checkpoint

Configuracao recomendada em `/etc/logrotate.d/maillog`:

```
/var/log/maillog {
    daily
    rotate 7
    compress
    delaycompress    # Manter .1 sem comprimir por 1 dia
    missingok
    notifempty
    postrotate
        /bin/kill -HUP `cat /var/run/syslogd.pid 2> /dev/null` 2> /dev/null || true
    endscript
}
```

A opcao `delaycompress` e importante: garante que `maillog.1` fica em texto simples durante um dia antes de ser comprimido para `maillog.1.gz`.

### 5.3 Permissoes

```bash
# O utilizador do Tomcat deve conseguir ler o ficheiro de log
ls -la /var/log/maillog

# E escrever no directorio do checkpoint
ls -la /var/lib/iflow/
# Se nao existir:
mkdir -p /var/lib/iflow
chown tomcat:tomcat /var/lib/iflow
```

---

## 6. Verificacao da Instalacao

### 6.1 Arranque

Apos restart do Tomcat, verificar nos logs da aplicacao:

```
# Deve aparecer mensagem de inicio do MailLogManager
grep -i "MailLogManager" catalina.out
```

Se `MAIL_LOG_SCANNER_ENABLED=false` ou a propriedade nao existir, o scanner **nao inicia** (comportamento esperado).

### 6.2 Teste funcional basico

1. Enviar um email atraves do iFlow
2. Verificar que foi criado registo na BD:
   ```sql
   SELECT * FROM email_request_log ORDER BY requested_at DESC LIMIT 5;
   ```
3. Aguardar ~10 segundos (um ciclo de scan)
4. Verificar que o `smtp_code` foi actualizado (de `150` para o codigo final):
   ```sql
   SELECT request_id, to_address, status, smtp_code, locked, queue_id
   FROM email_request_log
   WHERE smtp_code != '150'
   ORDER BY processed_at DESC LIMIT 5;
   ```

### 6.3 Verificar checkpoint

```bash
cat /var/lib/iflow/maillog.checkpoint
# Formato esperado: timestamp|filesize (ex: 1711900000000|524288)
```

---

## 7. Upgrade de versao anterior

Se ja tem as tabelas `smtp_error_codes` e `email_request_log` de uma versao anterior (pre-4.2.8):

### 7.1 Novo codigo SMTP

```sql
INSERT INTO smtp_error_codes VALUES ('902', 'hard_failure', 'Delivery expired - tempo de vida na fila excedido.');
```

### 7.2 Novas propriedades no `iflow.properties`

```properties
MAIL_LOG_STALE_THRESHOLD_MINUTES=30
MAIL_LOG_STALE_ALERT_THRESHOLD=10
MAIL_LOG_STALE_ALERT_COOLDOWN_MINUTES=15
```

### 7.3 Query actualizada

Substituir a query `UPDATE_EMAIL_REQUEST_LOG_BY_QUEUEID` (adicionada condicao `AND queue_id IS NULL`) e adicionar a nova query `COUNT_STALE_150` - ver seccao 4.

### 7.4 Checkpoint

O formato do ficheiro de checkpoint mudou de `timestamp` para `timestamp|filesize`. A migracao e **automatica** - o novo codigo le ambos os formatos.

---

## 8. Resolucao de Problemas

| Sintoma | Causa provavel | Solucao |
|---------|---------------|---------|
| Scanner nao inicia | `MAIL_LOG_SCANNER_ENABLED` nao esta `true` | Definir como `true` e reiniciar |
| Emails ficam sempre em 150 | Scanner nao encontra o ficheiro de log | Verificar `MAIL_LOG_FILE` e permissoes |
| Timestamps errados nos logs | Timezone mal configurado | Verificar `MAIL_LOG_TIMEZONE` (ex: `Europe/Lisbon`) |
| Alertas stale excessivos | Threshold muito baixo | Ajustar `MAIL_LOG_STALE_THRESHOLD_MINUTES` |
| Erros de BD no SQL Server | Colunas com nomes reservados | Verificar que as queries usam `locked` (nao `lock`) |
| Scanner nao detecta rotacao | Checkpoint corrompido | Apagar o ficheiro de checkpoint e reiniciar |
| WARNING "log file does not exist" | Caminho errado ou permissoes | Verificar `MAIL_LOG_FILE` e permissoes de leitura |
