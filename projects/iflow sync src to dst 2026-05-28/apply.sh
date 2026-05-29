#!/bin/bash
#
# Sync cirurgico iFlow: 20.166.233.15 (SRC, P17006A-PRD) -> 52.155.163.178 (DST, P15045A-PRD)
# Gerado: 2026-05-28
#
# O que faz:
#   1. Verifica pre-requisitos (root, paths, ficheiros do pacote)
#   2. Cria backup tar.gz completo do iFlow actual em /userdata/backups/
#   3. Remove 13 ficheiros que so existem no DST (ver remove_list.txt)
#   4. Extrai 11 ficheiros novos/diferentes para o DST (ver payload_list.txt / payload.tar.gz)
#   5. Mostra resumo + comando de rollback
#
# NAO mexe no Tomcat. Para parar/arrancar usa systemctl manualmente.
#
# Uso: sudo ./apply.sh
#
set -euo pipefail

IFLOW=/userdata/tomcat/webapps/iFlow
BACKUP_DIR=/userdata/backups
TS=$(date +%Y%m%d-%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/iflow-pre-sync-${TS}.tar.gz"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PAYLOAD="${SCRIPT_DIR}/payload.tar.gz"
REMOVE_LIST="${SCRIPT_DIR}/remove_list.txt"
MANIFEST="${SCRIPT_DIR}/MANIFEST.txt"

red()    { printf '\033[31m%s\033[0m\n' "$*"; }
green()  { printf '\033[32m%s\033[0m\n' "$*"; }
yellow() { printf '\033[33m%s\033[0m\n' "$*"; }
bold()   { printf '\033[1m%s\033[0m\n' "$*"; }

bold "==> Pre-checks"
[[ $EUID -eq 0 ]]        || { red "ERRO: corre como root (sudo)"; exit 1; }
[[ -d "$IFLOW" ]]        || { red "ERRO: $IFLOW nao existe"; exit 1; }
[[ -f "$PAYLOAD" ]]      || { red "ERRO: payload.tar.gz nao encontrado em $SCRIPT_DIR"; exit 1; }
[[ -f "$REMOVE_LIST" ]]  || { red "ERRO: remove_list.txt nao encontrado em $SCRIPT_DIR"; exit 1; }

HOST=$(hostname)
if [[ "$HOST" != "P15045A-PRD" ]]; then
  yellow "AVISO: este pacote foi gerado para P15045A-PRD; hostname actual e $HOST"
  read -rp "Continuar mesmo assim? [s/N]: " ans
  [[ "$ans" =~ ^[sS]$ ]] || { red "Abortado pelo utilizador."; exit 1; }
fi

mkdir -p "$BACKUP_DIR"

bold "==> Backup completo de $IFLOW"
green "    -> $BACKUP_FILE"
tar -czf "$BACKUP_FILE" -C "$(dirname "$IFLOW")" "$(basename "$IFLOW")"
BACKUP_SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
green "    OK ($BACKUP_SIZE)"

bold "==> Remocao de ficheiros que so existem no DST"
removed=0
not_found=0
while IFS= read -r path; do
  [[ -z "$path" || "$path" =~ ^# ]] && continue
  full="$IFLOW/$path"
  if [[ -f "$full" ]]; then
    rm -f -- "$full"
    echo "    rm $path"
    removed=$((removed + 1))
  else
    yellow "    (nao existe) $path"
    not_found=$((not_found + 1))
  fi
done < "$REMOVE_LIST"
green "    Removidos: $removed   |   Ja nao existiam: $not_found"

bold "==> Extracao do payload (ficheiros novos/diferentes)"
tar -xzf "$PAYLOAD" -C "$IFLOW"
EXTRACTED=$(tar -tzf "$PAYLOAD" | grep -cv '/$' || true)
green "    Ficheiros extraidos: $EXTRACTED"

bold "==> Garantir ownership tomcat:tomcat nos ficheiros tocados"
# tar foi criado com --owner=tomcat --group=tomcat, por isso ownership ja deve estar correcto
# mas reforcamos por seguranca em ficheiros novos
while IFS= read -r path; do
  [[ -z "$path" || "$path" =~ ^# ]] && continue
  full="$IFLOW/$path"
  [[ -f "$full" ]] && chown tomcat:tomcat "$full"
done < "${SCRIPT_DIR}/payload_list.txt"
green "    OK"

echo ""
bold "==> Resumo"
echo "    Backup completo:   $BACKUP_FILE  ($BACKUP_SIZE)"
echo "    Ficheiros removidos: $removed"
echo "    Ficheiros extraidos: $EXTRACTED"
echo ""
bold "==> ROLLBACK (em caso de problema):"
echo "    systemctl stop tomcat"
echo "    rm -rf $IFLOW"
echo "    tar -xzf $BACKUP_FILE -C $(dirname "$IFLOW")"
echo "    systemctl start tomcat"
echo ""
bold "==> POS-SYNC manual:"
echo "    1. systemctl restart tomcat"
echo "    2. Validar acesso ao iFlow"
echo "    3. (Opcional) Remover sudoers temporario:  rm /etc/sudoers.d/zzz-uniksystem-iflow-compare"
echo ""
green "Sync concluido."
