# bkcf - botoes nao respondem

Cliente em `bkcf.ai.uniksystem.com` reporta que botoes do iFlow nao respondem.
Mesmo utilizador noutra maquina funciona. Logo: problema da maquina/browser, nao do servidor.

## Estado da investigacao

- HAR analisado: `bkcf.ai.uniksystem.com.har` (em Downloads, 102 entries, 4 fluxos: 115, 70, 44, 73)
- Padrao: form carrega 200 OK, mas zero POSTs de submit apos cada form. Cliques nao geram trafego.
- Observacao em sessao remota: clique fisico no botao, **nada** na consola nem na Network.
- UA: Edge 148 / Windows 10/11.
- Confirmado nao ser servidor, sessao, nem conta do user.

## Diagnostico tecnico

O `onclick` nem chega a correr (sem alert do `CheckEmptyFields`, sem erro JS, sem request).
Causa esta na maquina: extensao do Edge, AV com web protection (Trend Micro / ESET / Kaspersky / Bitdefender), GPO corporativa, ou overlay invisivel a apanhar o clique.

## diag.html

Pagina de auto-diagnostico para o cliente (nao-tecnico) abrir sozinho. Testa:

1. `onclick` inline
2. `addEventListener('click')`
3. Form submit identico ao iFlow (botao type=submit + iframe sink)
4. Link `javascript:`
5. Click programatico (`button.click()`) automatico
6. `dispatchEvent(MouseEvent)` automatico

Mais: recolhe UA, plataforma, storage, cookies, `window.chrome.runtime` (sinal de extensao),
CSP, e tenta carregar `mootools.js`, `FormFunctions.js`, `iflow_main.js`, YUI e `ajax_processing.js`
para confirmar que `CheckEmptyFields`, `disableForm`, `YAHOO`, `$defined` ficam definidos.

### Layout

Topo da pagina = **caixa azul "RESULTADOS"** com tudo o que e preciso ver:
- linhas-resumo de cada teste (PASSOU/FALHOU + msg)
- tabela compacta com browser, UA, inline JS, cookies, CSP, recursos, globals
- botao **"Copiar resultados"** que poe um relatorio de texto na clipboard

O cliente faz UMA das duas coisas:
- carrega em "Copiar resultados" e cola no email (Ctrl+V), OU
- tira foto apenas da caixa azul "RESULTADOS"

Nao precisa de tirar foto da pagina inteira.

### Deploy

Ficheiro original em `iflow-web/src/main/webapp/diag.html`. Para nao esperar pelo build:
copiar este `diag.html` directamente para `$TOMCAT/webapps/iFlow/diag.html` no servidor.

URL para enviar ao cliente:
```
https://bkcf.ai.uniksystem.com/iFlow/diag.html
```

### Mensagem ao cliente (exemplo)

> "Abra este link no Edge: https://bkcf.ai.uniksystem.com/iFlow/diag.html
> Espere 15 segundos sem mexer em nada. Depois carregue no botao verde
> 'Copiar resultados', cole num email com Ctrl+V e envie."

## Interpretacao dos resultados (cheatsheet)

| Sintoma no relatorio | Causa raiz |
|---|---|
| Teste 3 falha com "onclick chegou mas form NAO submeteu" | submit interceptado (password manager, AV) |
| Testes 1-4 falham com "sem clique recebido em 15s" | cliques de rato nao chegam (overlay, driver, AV input) |
| Testes 1-4 OK mas Teste 5/6 falha | cliques sinteticos bloqueados (raro) |
| Recursos do iFlow falham a carregar | AV/proxy bloqueia scripts |
| `Inline JS: BLOQUEADO` | CSP ou extensao bloqueia inline JS = causa raiz |
| Tudo OK mas iFlow continua partido | especifico ao iframe/form do iFlow (proxima fase) |
| `chrome.runtime: PRESENTE` | extensao a injectar codigo em todas as paginas |

## Proximos passos

1. Cliente envia relatorio (texto ou foto da caixa azul).
2. Confirmar causa pela tabela acima.
3. Se for extensao/AV: pedir teste em InPrivate (Ctrl+Shift+N) para confirmar.
4. Se for tudo verde mas iFlow falha: criar uma segunda pagina que carrega o form do iFlow num iframe e replica o setup, para isolar o contexto.

## Ficheiros nesta pasta

- `diag.html` - copia da pagina de diagnostico (a viva esta em iflow-web/src/main/webapp/)
- `README.md` - este ficheiro
