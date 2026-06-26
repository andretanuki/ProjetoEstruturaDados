# Arquitetura do Sistema e Divisão de Tarefas

O projeto é um Jogo Investigativo em texto focado no uso de **Lista Encadeada** e **Árvore** em Java. Como solicitado, abaixo está o detalhamento da arquitetura e a divisão do desenvolvimento em etapas para uma equipe de 3 pessoas.

---

## 1. Arquitetura do Projeto

O sistema será construído usando uma arquitetura modular com separação de responsabilidades (baseado no padrão MVC simplificado para terminal):

### Camadas Principais

1. **`model` (Modelos de Dados)**
   - `Evidence` (Pista/Evidência): Classe base contendo `id`, `name`, `description`, `isCorrect` (se faz parte do caminho ideal).
   - `Scene` (Cena): Representa cada etapa do jogo (3 a 5 cenas). Contém uma narrativa inicial e um conjunto de evidências que podem ser coletadas nessa cena.
   - `User` (Usuário): Para o sistema de login (apenas nome e um controle simples de ID).

2. **`datastructures` (Estruturas de Dados Customizadas)**
   - **`Tree` e `TreeNode`**: Mapeia a hierarquia lógica. A Árvore determina se uma evidência (Nó filho) está disponível com base em o jogador ter ou não coletado as evidências anteriores (Nó pai). 
   - **`LinkedList` e `Node`**: Mantém o Histórico Investigativo. Toda vez que o jogador seleciona uma pista, ela é adicionada (append) na Lista. No final, iteramos por essa lista para cruzar com a Árvore e ver se o caminho foi o Correto.

3. **`engine` (Lógica e Regras do Jogo)**
   - `GameController`: Gerencia o fluxo principal. Inicializa as Cenas, popula a Árvore de dependências (o gabarito), e controla o Loop principal do jogo.
   - `InputManager`: Lê entradas (seja do `Scanner` do console ou de um Arquivo de script automatizado).
   - `ReportGenerator`: Avalia a `LinkedList` do jogador em comparação com a `Tree`. Se errar, renderiza no terminal visualmente onde o jogador desviou do caminho e reseta o jogo.

4. **`auth` (Autenticação)**
   - `LoginSystem`: Pede o nome/usuário no início e mantém o registro da sessão.

5. **`Main`**
   - O ponto de entrada. Invoca o `LoginSystem`, em seguida passa o controle para o `GameController`.

---

## 2. Divisão do Desenvolvimento para 3 Pessoas (Etapas)

Dividimos as responsabilidades em 3 Perfis: **Pessoa A (Estruturas de Dados)**, **Pessoa B (Lógica e Arquitetura do Jogo)** e **Pessoa C (Autenticação, Fluxo de IO e Relatórios)**.

### Etapa 1: Fundação (Semana 1)
- **Pessoa A:**
  - Implementar as classes genéricas `LinkedList` e `Node`.
  - Escrever pequenos testes (na `main` ou usando JUnit básico) para garantir `add()`, `remove()`, `print()`, `size()`.
- **Pessoa B:**
  - Criar os Modelos: `Evidence`, `Scene` e `User`.
  - Começar o esqueleto do `GameController` (ainda sem a lógica complexa).
- **Pessoa C:**
  - Implementar o `LoginSystem`.
  - Estruturar a leitura de dados do teclado (`Scanner`) no `InputManager` de forma que possa ser reaproveitada.

### Etapa 2: Core e Lógica Estrutural (Semana 2)
- **Pessoa A:**
  - Implementar as classes `Tree` e `TreeNode`.
  - Criar o método de busca na árvore (`find()`, `getChildren()`) que servirá para validar se uma pista pode ser revelada no momento.
- **Pessoa B:**
  - Integrar a `Tree` dentro do `GameController`: Criar a árvore de pistas "gabarito" do caso investigativo.
  - Criar o texto das 3 a 5 cenas.
- **Pessoa C:**
  - Criar a leitura de "Entradas Automatizadas". Um método no `InputManager` que lê de um arquivo `.txt` os passos simulando um jogador (ex: "1, 3, 2").
  - Testar o login automatizado.

### Etapa 3: Integração e Gameplay (Semana 3)
- **Pessoa A & B:**
  - Trabalhar juntas no Loop do Jogo no `GameController`: A cada cena, verificar na `Tree` quais pistas estão liberadas, mostrar pro jogador e adicionar a escolha na `LinkedList` do jogador.
- **Pessoa C:**
  - Começar a estruturar o `ReportGenerator`: Como imprimir de forma amigável no console o percurso que está armazenado na `LinkedList`.

### Etapa 4: Validação, Resets e Finalização (Semana 4)
- **Pessoa A:**
  - Otimizar a árvore e ajudar na lógica de validação do caminho percorrido.
- **Pessoa B:**
  - Finalizar as condições de vitória e derrota: Se a pista adicionada levar a uma contradição (nó folha errado na Árvore), acionar o "Game Over" e o reinício automático.
- **Pessoa C:**
  - Polir a Interface no Terminal (arte ASCII, limpar a tela).
  - Integrar o `ReportGenerator` na condição de derrota, renderizando visualmente o caminho do jogador até o ponto de falha.
  - Garantir que a apresentação do professor possa ser feita de forma 100% automatizada lendo um script de arquivo.
