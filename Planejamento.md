# Jogo Investigativo - Projeto de Estrutura de Dados

Este documento tem como objetivo explicar a arquitetura do projeto com base nas especificações, organizar os passos para desenvolvimento e servir como um plano de ação para a sua aprovação.

## 1. Entendimento do Projeto

O projeto é um **Jogo Investigativo em formato de texto**, executado exclusivamente no **terminal**. O foco principal é a demonstração do uso de estruturas de dados (Lista Encadeada e Árvore) integradas à lógica do jogo. 

**Requisitos e Mecânicas Principais:**
1. **Sistema de Login:** O usuário precisará se identificar antes de iniciar o jogo.
2. **Entradas (Inputs):** O jogo suportará entradas manuais (o jogador escolhendo as opções) ou de forma automatizada (por exemplo, um modo de demonstração lendo um arquivo de teste).
3. **Cenas:** O jogo será dividido em um número fixo de cenas (3 a 5).
4. **Relatório Final:** Ao final da execução, será exibido um relatório mostrando os dados, que no contexto do jogo representa o caminho percorrido e o resultado da investigação.

**Estruturas de Dados Utilizadas:**
- **Árvore (Tree):** Funcionará como o **mapa hierárquico das pistas**. Imagine uma árvore onde a raiz é a pista inicial. As pistas só ficam disponíveis para o jogador se as pistas "pai" na árvore já tiverem sido coletadas.
- **Lista Encadeada (Linked List):** Atuará como o **histórico da investigação**. Cada vez que o jogador coleta uma pista (evidência), ela é adicionada à lista em ordem cronológica. O sistema cruzará essa lista com a Árvore para verificar se o caminho investigativo faz sentido e qual é o desfecho. Se o caminho estiver errado, o jogo reinicia e imprime no terminal a sequência da lista encadeada, mostrando graficamente o caminho que falhou.

---

## 2. Proposta de Organização do Repositório

Sugiro estruturar o repositório da seguinte maneira para manter o código limpo, modular e fácil de demonstrar aos professores:

```
ProjetoEstruturaDados/
├── Especificacoes/        # (Já existente) Contém os requisitos
│   └── Specs.md
├── docs/                  # Documentação adicional e diagramas da Árvore/Lista
├── src/                   # Código fonte principal
│   ├── main.*             # Ponto de entrada do programa
│   ├── auth.*             # Sistema de login (simples)
│   ├── game_engine.*      # Lógica das cenas e do loop do jogo
│   ├── data_structures/   # Implementação das estruturas de dados do zero
│   │   ├── linked_list.*
│   │   └── tree.*
│   └── data/              # Dados das pistas e cenas ou testes automatizados
├── test_inputs/           # Arquivos para a entrada automatizada de dados
├── README.md              # Instruções de como rodar o jogo e compilar
└── .gitignore             # Arquivos ignorados pelo git
```

---

> [!IMPORTANT]
> ## User Review Required
> Por favor, revise o entendimento do projeto acima. Se estiver de acordo com o que os professores pediram, podemos prosseguir com o desenvolvimento. Além disso, veja a organização de pastas sugerida.

> [!WARNING]
> ## Open Questions
> Para que possamos configurar o repositório corretamente e iniciar o código, preciso que responda a uma pergunta fundamental:
> 
> **Qual é a linguagem de programação desejada ou obrigatória para este trabalho?** (ex: C, C++, Java, Python, C#). Isso definirá como vamos estruturar os arquivos no `src/` e o `.gitignore`. Java

---

## 3. Próximos Passos (Verification Plan)

Assim que você aprovar o plano e definir a linguagem:

1. **Configuração Git Inicial:** Criaremos os arquivos bases (`README.md`, `.gitignore`), faremos o primeiro commit e conectaremos o seu repositório remoto (`https://github.com/andretanuki/ProjetoEstruturaDados`) para dar o `git push`.
2. **Implementação Base (Estruturas):** Desenvolveremos as estruturas "puras" da Árvore e da Lista Encadeada no diretório correspondente.
3. **Mecânica do Jogo e Login:** Integração do sistema de login e do loop das 3 a 5 cenas.
4. **Validação Automática e Manual:** Criaremos uma opção no menu para rodar o jogo lendo de um arquivo (entradas automatizadas) e validaremos renderizando a árvore e a lista no terminal para o relatório final.
