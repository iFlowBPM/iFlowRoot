# iFlow Sync Package — 2026-05-28

Sync cirurgico do `/userdata/tomcat/webapps/iFlow`:
- **SRC**: 20.166.233.15 (P17006A-PRD) — produção mais recente
- **DST**: 52.155.163.178 (P15045A-PRD) — produção a actualizar

## Conteúdo do pacote

| Ficheiro | Função |
|---|---|
| `apply.sh` | Script a correr **no destino** como root |
| `payload.tar.gz` | Ficheiros a copiar/substituir (11) — extracção relativa ao iFlow/ |
| `remove_list.txt` | Caminhos relativos dos 13 ficheiros a apagar no destino |
| `payload_list.txt` | Lista interna usada pelo `apply.sh` para chown final |
| `MANIFEST.txt` | Sumário + hashes SHA256 da SRC para auditoria |
| `diffs/` | Diffs unified dos ficheiros texto (CSS, JSP) + conteúdo do teste.html removido |

### Conteúdo de `diffs/`

| Ficheiro | O que mostra |
|---|---|
| `iflow_main.css.diff` | Mudança de cor laranja (`#f76600`) → verde (`#01B21B`) em 2 selectores — coerente com rebranding bankinter → universo |
| `process_load.jsp.diff` | **Hardening de segurança**: sanitiza `process_url` removendo `http://`/`https://`/`//` antes do redirect (prevenção de open redirect) |
| `teste.html.removed` | Ficheiro vazio (0 bytes) — a remover sem perda |

## Como aplicar (no destino)

```bash
# 1. Copiar o pacote para a 52.155.163.178 (ex: scp do bastion ou outra via)
scp iflow-sync-2026-05-28.tar.gz uniksystem@52.155.163.178:/tmp/

# 2. Na 52.155.163.178
cd /tmp
tar -xzf iflow-sync-2026-05-28.tar.gz
cd iflow-sync-2026-05-28

# 3. (Recomendado) Parar Tomcat antes
sudo systemctl stop tomcat

# 4. Aplicar
sudo ./apply.sh

# 5. Arrancar Tomcat
sudo systemctl start tomcat

# 6. Validar acesso ao iFlow e funcionalidade base
```

## Rollback

O `apply.sh` cria um backup completo em `/userdata/backups/iflow-pre-sync-<TS>.tar.gz` antes de mexer em nada. Em caso de problema:

```bash
sudo systemctl stop tomcat
sudo rm -rf /userdata/tomcat/webapps/iFlow
sudo tar -xzf /userdata/backups/iflow-pre-sync-<TS>.tar.gz -C /userdata/tomcat/webapps/
sudo systemctl start tomcat
```

## Notas operacionais

- Hostname check: o `apply.sh` avisa se o hostname ≠ `P15045A-PRD` (pede confirmação manual).
- Path.properties é **excluído** porque legitimamente difere entre máquinas (hostnames, paths específicos do ambiente).
- Logs/cache/work/temp são excluídos do diff (ruído operacional).
- Lixo de editor (`*.css~`, `*.bck`, `*_OLD.css`) ignorado por decisão do operador.
- O sudoers temporário (`/etc/sudoers.d/zzz-uniksystem-iflow-compare`) instalado para o discovery deve ser removido manualmente no fim em **ambas as máquinas**:
  ```bash
  sudo rm /etc/sudoers.d/zzz-uniksystem-iflow-compare
  ```

## Análise resumida (do diff)

| Categoria | # | Detalhe |
|---|---|---|
| Só na SRC (copiados) | 6 | 4 JARs novos (Jersey 1.17, batik-js, crypto-api, curvesapi), 2 PNGs (bkcf, universo) |
| Só na DST (removidos) | 13 | 10 .class soltos (já dentro do iflow-blocks JAR novo), Jersey 1.14 antigo, bankinter.png, teste.html |
| Diferentes (substituídos) | 5 | iflow_main.css, 3 JARs core (api/blocks/web), process_load.jsp |
| Iguais | 1392 | |

Todas as 10 .class removidas foram verificadas e estão presentes no `iflow-blocks-4.2.6-R20180704.jar` actualizado (datadas 2025-09-11).
