# Arquitetura e Plano de Ação: Hackathon de Fim de Semana

Como o prazo é um único final de semana, a equipe tem conhecimentos básicos (2º período de ADS) e fará **uso intenso de Inteligência Artificial**, precisamos de um plano extremamente pragmático. Não podemos gastar tempo com over-engineering (arquitetura muito complexa). O foco é fazer funcionar e atender aos requisitos da matéria (Lista Encadeada, Árvore, Sistema no Terminal).

---

## 1. Arquitetura Simplificada (Para IA entender fácil)

O sistema terá poucas classes, todas diretas e fáceis de pedir para a IA gerar.

1. **`Estruturas` (Obrigatório para a nota)**
   - `ListaHistorico`: Uma classe de Lista Encadeada Simples com os métodos `adicionar(String pista)` e `imprimirHistorico()`.
   - `ArvoreGabarito`: Uma classe de Árvore (onde cada nó tem N filhos) com os métodos `adicionarPista()` e `verificarSePistaEstaLiberada()`.

2. **`Jogo` (Onde tudo acontece)**
   - `Pista`: Objeto simples (id, textoDaPista).
   - `SistemaTexto`: Classe responsável por dar os prints na tela, ler dados (`Scanner`) e fazer o "Login" fake.
   - `Main`: Classe principal que amarra as estruturas e roda um `while (jogoRodando)` (Loop do Jogo).

---

## 2. Roteiro do Final de Semana para 3 Pessoas (A, B e C)

A chave para programar com IA em grupo é a **divisão clara de arquivos** para evitar conflitos no Git e problemas de integração de código. Cada um será "dono" de uma parte.

### Sábado - Manhã: Fundação (Prompting Inicial)

- **Pessoa A (Dona das Estruturas 1 - Lista):**
  - **Tarefa:** Gerar a Lista Encadeada do zero.
  - **Uso de IA:** Pedir para a IA: *"Crie uma classe em Java de Lista Encadeada simples chamada ListaHistorico que armazena Strings. Preciso dos métodos adicionar e imprimir a lista toda formatada bonitinha para um jogo no terminal."*
- **Pessoa B (Dona das Estruturas 2 - Árvore):**
  - **Tarefa:** Gerar a Árvore Genérica.
  - **Uso de IA:** Pedir para a IA: *"Crie uma Árvore em Java chamada ArvoreGabarito onde cada Nó armazena o ID de uma pista (String ou int) e pode ter múltiplos filhos (List<No>). Faça um método para inserir um nó e outro para verificar se um nó específico existe."*
- **Pessoa C (Dona da Interface e IO):**
  - **Tarefa:** Fazer o sistema de Login, os Menus no terminal e leitura de texto.
  - **Uso de IA:** Pedir para a IA: *"Crie uma classe Java com Scanner para um jogo de terminal. Ela deve pedir o nome de usuário (login fake), dar boas vindas e ter um método que permite ler um arquivo .txt e simular os inputs para automatizar testes."*

### Sábado - Tarde: Integração (O "Frankenstein")

Neste momento, vocês juntam o código. A Pessoa C (ou quem tiver mais facilidade com o Git) puxa o código de A e B.

- **Trabalho em Conjunto:**
  - Criar a classe `Main`.
  - Construir o cenário do jogo "hardcoded" (chumbado no código). Exemplo: A pista "faca" só libera a pista "impressao_digital". 
  - **Uso de IA:** Mandar as 3 classes (Lista, Arvore, Scanner) para a IA e pedir: *"Integre esses 3 códigos. O jogador começa no menu, depois entra no loop de 3 cenas. A cada cena ele escolhe uma pista, nós adicionamos na ListaHistorico e verificamos na ArvoreGabarito se ele podia pegar aquela pista."*

### Domingo - Manhã: A Lógica de Falha e Reset (Regras de Negócio)

- **Pessoa A:** 
  - Trabalhar na lógica visual (prints ASCII) de quando o jogador perde. Usar a IA para gerar os desenhos.
- **Pessoa B & C:** 
  - Consertar os bugs do loop do jogo de sábado. Garantir que, se o cara escolher a evidência na ordem errada (que não bate com a Árvore), o jogo dê "Game Over", mostre a Lista Encadeada de onde ele errou, e reinicie.

### Domingo - Tarde: O Gran Finale (Modo de Apresentação)

- **Todos Juntos:** 
  - Finalizar o requisito de *"dados de entrada de forma automatizada"*.
  - Usar a IA para criar 2 arquivos `.txt`: um com a sequência VENCEDORA e outro com a sequência PERDEDORA.
  - Redirecionar o Scanner para ler esses arquivos. Assim, na hora de apresentar pro professor, vocês só apertam 1 botão e o jogo joga sozinho, provando que a Lista e a Árvore funcionam!
  - Revisar se o código não tem coisas estranhas criadas pela IA (nomes de variáveis em inglês e português misturados, coisas muito complexas do Java avançado que o professor desconfiaria).

---

## 3. Dica de Ouro para o Grupo

Sempre que a IA der um código que vocês não entenderem 100%, peçam: *"Me explique esse método como se eu estivesse no 2º período da faculdade de programação, para eu saber explicar para o meu professor."*
