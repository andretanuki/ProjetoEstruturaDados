# Notas de Design — Jogo Investigativo (Caso Dr. Almeida)

> Racional de projeto extraído dos comentários do código na auditoria de
> 2026-07-01. O código-fonte mantém apenas os comentários estritamente
> necessários à compreensão local; o **porquê** de cada decisão vive aqui.
> Complementa `docs/Arquitetura_e_Tarefas.md` (arquitetura e API das classes).

## 1. Estrutura geral

- O jogo tem **5 cenas de texto fixo** — o roteiro não muda entre partidas.
  O que muda é apenas quais pistas o jogador escolhe destacar em cada cena.
- Fluxo: `Main` → `Terminal` (teclado ou script `test_inputs/*.txt`, uma
  entrada por linha) → `Jogo.iniciar()` (login, histórico da persistência,
  gabarito, loop de cenas).
- **Toda saída de tela passa por `Terminal.exibir()`.** Os `System.err` da
  `Persistencia` são canal de diagnóstico de erro de I/O (não fazem parte da
  interface do jogo) e por isso não passam pelo `Terminal`.

## 2. Papéis das pistas

Cada pista do gabarito tem um de três papéis:

- **(a) Caminho da vitória (3 coletas):** `cracha` → `camera` →
  `celular_esquecido` (título no menu: "Revirar Tudo"). A pista final só é
  oferecida na Cena 5 e só fica selecionável com cracha+camera no histórico
  (na árvore, é filha de `camera` — ter camera implica ter cracha).
- **(b) Auxiliares verdadeiras:** pistas reais que enriquecem a história mas
  não são necessárias para vencer. A dupla `registro_saida` +
  `testemunho_zelador` concede a badge "Venceu com Excelência" (ver §4);
  `extrato_bancario` é auxiliar de sabor, fora da badge.
- **(c) Distrações:** pistas cujo texto se auto-descarta (álibi,
  impossibilidade, irrelevância). Tom "fácil": o jogador atento elimina.

## 3. As três regras do menu

1. **PISO DE 4:** toda cena tem ≥ 4 pistas filhas diretas da raiz
   (distrações sem pré-requisito), garantindo que o menu nunca fique com
   menos de 4 opções.
2. **TRANSBORDO:** pistas-chave são repetidas nas listas das cenas seguintes
   até serem coletadas, para o jogador atrasado ainda encontrá-las
   (`cracha` até a C3, `camera` até a C4; `registro`/`testemunho` da C3 para
   a C4).
3. **MENU FILTRADO:** o menu real de cada cena é a lista fixa da cena
   filtrada pela árvore — só entra pista cujo pai já está no histórico
   (`Arvore.getPistasDisponiveis`), sem repetir coletadas nem ids duplicados.

**Corte matemático pela distribuição (não por cálculo dinâmico):** cada
pista-chave deixa de ser oferecida exatamente na cena em que pegá-la já não
levaria mais à vitória (`cracha` transborda só até a C3 porque
cracha C3 → camera C4 → celular C5 ainda fecha). Não existe — e não deve
existir — cálculo dinâmico de viabilidade.

## 4. Badge de excelência

A vitória trava 3 das 5 escolhas (cracha, camera, celular), sobrando **2
livres** — por isso a badge é uma **dupla**: `registro_saida` +
`testemunho_zelador` (decisão do usuário: o zelador tem personalidade e
merece ficar). As duas são oferecidas na C3 e transbordam para a C4, então a
dupla fecha em qualquer ordem. `extrato_bancario` é filha de `registro_saida`
na árvore (só alcançável via registro C3 → extrato C4) e fica fora da badge.

> Histórico: a regra original ("as 3 auxiliares") era matematicamente
> inatingível — 3 auxiliares não cabem em 2 escolhas livres, e
> registro/testemunho competiam na mesma cena. Corrigida em 2026-07-01.

## 5. Finais malucos

- **Eixo central do jogo = resistir à tentação do absurdo.** As trilhas
  malucas são tentações visíveis que competem com o fio sério; cair nelas é
  um final alternativo divertido e **celebrado**, nunca punição.
- Duas trilhas de 3 passos (pai → filho → neto na árvore):
  - **Abdução:** `janela_forcada` (C1, retake na C3) → `vidro_quebrado` (C4)
    → `luz_estranha` (C5).
  - **Loucura:** `copo_cafe` (C1, retake na C3) → `bilhete_manchado` (C4) →
    `mural_conspiracao` (C5).
- As duas iscas-pai reaparecem na C3 (quem mordeu uma pode pegar a outra e
  escolher qual trilha desenvolver), mas completar as duas é impossível
  (6 coletas em 5 cenas).
- Coletar a neta consome 3 escolhas da trilha, o que torna impossível ter
  completado também a vitória séria — a neta no histórico já define o
  desfecho alternativo (`verificarGameOver`).

## 6. Coreografia das listas por cena (`pistasPorCena`)

- **C1:** 4 filhas da raiz — `cracha` (sério) + 2 iscas malucas (`janela`,
  `copo`) + 1 distração (`luvas`). O jogador morde (ou não) a primeira isca.
- **C2:** "só jogo sério", sem tentação maluca (o texto reforça: "tudo aqui
  parece sóbrio e concreto"). 4 distrações + transbordo(cracha) + `camera`.
- **C3:** encruzilhada — 4 distrações + transbordo(cracha, camera) +
  auxiliares (registro, testemunho) + retake das 2 iscas malucas. Os filhos
  malucos ainda NÃO aparecem.
- **C4:** 4 distrações + transbordo(camera, registro, testemunho) + extrato +
  filhos malucos (`vidro_quebrado`, `bilhete_manchado`).
- **C5:** 4 distrações + os TRÊS desfechos competindo (final séria + 2 netas).

Os textos das cenas mencionam em prosa, com destaque `[ENTRE COLCHETES]`,
todas as pistas selecionáveis da cena (o highlight simula a palavra
destacada). A C3 menciona crachá/câmera de forma fixa: soa como confirmação
de rotina para quem já os coletou e alerta real para quem não.

## 7. Relatório, epílogos e mapa do caso

- **Ordem de exibição: epílogo ANTES do relatório.** A história fecha colada
  no clímax da última pista; o relatório burocrático (tentativas, mapa,
  resultado) vem depois, como apêndice. Motivo: nos finais malucos, a
  revelação ("você foi abduzido") não pode ficar 40 linhas depois do gancho.
- **Rótulo por tentativa derivado do próprio caminho** (última pista do
  snapshot — pistas de desfecho só podem ser a 5ª coleta):
  celular = `SUCESSO`; netas malucas = `FINAL ALTERNATIVO` (sem a linha de
  "beco sem saída", pois são desfechos celebrados); resto = `FALHOU` + linha
  de beco. Motivo: derivar do parâmetro "última tentativa venceu?" rotulava
  errado a vitória de quem vence e joga de novo.
- **Mapa ASCII:** a árvore INTEIRA do gabarito é desenhada; o realce vale só
  para pistas coletadas na sessão (agregado de todos os caminhos):
  amarelo = auxiliar da badge, verde = importante (trilhas de desfecho),
  azul = comum, sem cor = não coletada (mesmo que importante). Símbolos
  ★/🛸/🌀 só aparecem quando a pista foi coletada, e a legenda **não** os
  revela — são surpresa de quem os atinge.
- Epílogo da derrota dá feedback dinâmico: aponta o primeiro elo faltante da
  corrente cracha → camera → celular (`feedbackDerrota`).

## 8. Persistência (`dados/partidas.txt`)

- **Um bloco por detetive**, com todas as rotas acumuladas entre partidas e
  sessões (decisão do usuário — o formato antigo, um bloco cumulativo por
  partida, duplicava rotas no histórico do login):

  ```
  >>> DETETIVE: nome
  Tentativas: N
  Último Resultado: Caso Solucionado | Investigação Mal Sucedida
    Caminho 1: [a] -> [b] -> FIM
  ----------------------------------------
  ```

- No login, `carregarCaminhos()` reconstrói as ListaEncadeada das rotas já
  salvas e semeia `todasTentativas` — a sessão nasce com o histórico
  completo. Por isso `salvar()` não precisa de merge nem cache: é chamado ao
  fim de cada partida (crash-safe) e **reescreve o arquivo inteiro** com
  `todasTentativas` (blocos dos outros jogadores preservados intactos).
  A reescrita sem modo append é intencional — o aviso original "nunca
  FileWriter sem true" valia para o formato append antigo.
- Consequência: o RELATÓRIO é unificado entre sessões (Tentativa 1..N
  acumula tudo que o jogador já jogou) e, por decisão do usuário, NÃO tem
  linha de Data/Hora — um carimbo único não faz sentido num acumulado.
- Arquivos no formato antigo (`>>> SESSÃO DE:`) não são migrados.

## 9. Avisos de implementação (estruturas de dados)

Avisos de risco que estavam como comentários `ATENÇÃO — RISCO DE BUG` no
esqueleto original (o código já os respeita):

- **Strings sempre com `.equals()`, nunca `==`** (comparação de referência
  falha silenciosamente).
- **A raiz da `Arvore` é um nó sentinela sem pista** (`raiz.pista == null`):
  buscas devem partir dos filhos da raiz, nunca comparar a raiz em si.
- Pista com mais de um pré-requisito é cadastrada como mais de um `NoArvore`
  com o mesmo id (um sob cada pai) — `getPistasDisponiveis` deduplica para o
  menu não repetir a pista.
- Toda folha da árvore deve ser um terminal intencional (vitória/derrota),
  nunca um nó intermediário "pela metade".
- A lista formata (`formatarHistorico()`), mas nunca imprime: a exibição é
  sempre via `Terminal.exibir()` (saída centralizada, ver §1). Os métodos
  legados `imprimirHistorico()`/`toArray()` foram removidos por não terem
  chamadores após a reorganização de 2026-07-02.
