# 🕵️‍♂️ O Caso Dr. Almeida

**O Caso Dr. Almeida** é um jogo de investigação criminal em formato de ficção interativa rodando via CLI (Terminal). O jogador assume o papel de um detetive que deve desvendar o desaparecimento de um cientista renomado, navegando por diversas cenas investigativas e tomando decisões críticas que alteram o final da história.

Este projeto foi desenvolvido como trabalho acadêmico para a disciplina de Estrutura de Dados

## 🧠 Arquitetura e Estruturas de Dados

O projeto adota princípios de separação de responsabilidades (MVC-like):
- **Árvore N-Ária (`Arvore.java`):** Gerencia o gabarito lógico das pistas. O jogador só desbloqueia opções filhas se já tiver descoberto a opção pai. A checagem de menu utiliza algoritmos recursivos de **Busca em Profundidade (DFS)**. Além disso, a classe mapeia a estrutura invisível para um **Diagrama Visual ASCII** gerado dinamicamente no terminal ao final do jogo.
- **Lista Encadeada (`ListaEncadeada.java`):** Mantém o histórico cronológico de decisões do jogador na sessão.
- **I/O Isolado (`Terminal.java`):** Toda a camada de Entradas e Saídas (`Scanner` e `System.out`) foi isolada da regra de negócio (`Jogo.java`), permitindo o controle da aplicação via scripts de texto autônomos.
- **Persistência (`Persistencia.java`):** Salva o progresso consolidado do jogador em `.txt`, agregando históricos de múltiplas sessões para revelar pistas já coletadas anteriormente.

## 🚀 Como Executar

Certifique-se de estar na **raiz do repositório** e que você possui o Java instalado.

### 🎮 Modo Interativo (Jogar Manualmente)
Para iniciar a investigação normalmente pelo seu teclado, execute:
```bash
java ./src/Main.java
```

### ⚡ Modo Automático (Testes de Múltiplos Finais)
Graças à injeção de I/O na classe `Terminal`, o jogo pode jogar a si próprio lendo arquivos pré-gravados. Isso é extremamente útil para testes contínuos ou para pular rapidamente para um dos finais possíveis da narrativa.

Basta passar o caminho de um arquivo `.txt` como argumento:

- 🟢 **Vitória Padrão:** `java ./src/Main.java test_inputs/vitoria.txt`
- ⭐ **Vitória com Excelência:** `java ./src/Main.java test_inputs/excelencia.txt`
- 🛸 **Final Secreto (Abdução Alienígena):** `java ./src/Main.java test_inputs/abducao.txt`
- 🌀 **Final Secreto (Loucura Total):** `java ./src/Main.java test_inputs/loucura.txt`
- 🔴 **Derrota Investigativa:** `java ./src/Main.java test_inputs/derrota.txt`
- 🔄 **Revanche (Falhar e tentar novamente):** `java ./src/Main.java test_inputs/revanche.txt`

## 📁 Estrutura de Diretórios

- `/src/engine`: Motor do jogo, regras, roteiro e abstração de Terminal.
- `/src/estruturadados`: Algoritmos e classes puras de dados (Árvore, Lista).
- `/test_inputs`: Arquivos TXT de mock para rodar o jogo no modo automático.
- `/docs`: Diagramas arquiteturais (Mermaid) e notas de design.

---
*Projeto acadêmico.*
