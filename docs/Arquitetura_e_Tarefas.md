# Arquitetura e Organização do Projeto

Considerando o prazo de um fim de semana e os conhecimentos esperados do 2º período de ADS, a arquitetura deve ser concisa e bem "fechada" (coesão forte e baixo acoplamento). A seguir está a especificação técnica de como o sistema deve ser estruturado.

---

## 1. Diagrama de Classes e Interações

O sistema é focado exclusivamente nas estruturas de dados solicitadas. 

```mermaid
classDiagram
    class Main {
        +main(String[] args)
    }

    class Jogo {
        -ListaEncadeada historico
        -Arvore dependencias
        -Scanner scanner
        +iniciar(String arquivoInput)
        +loginUsuario()
        +rodarCenas()
        +verificarGameOver()
        +imprimirRelatorio()
    }

    class ListaEncadeada {
        -NoLista inicio
        +inserirPista(String pista)
        +imprimirHistorico()
    }

    class NoLista {
        +String pista
        +NoLista proximo
    }

    class Arvore {
        -NoArvore raiz
        +inserirDependencia(String pai, String filha)
        +verificarAcesso(String pistaEscolhida, ListaEncadeada historico)
    }

    class NoArvore {
        +String idPista
        +List~NoArvore~ filhos
    }

    Main --> Jogo : Inicia
    Jogo --> ListaEncadeada : Mantém histórico
    Jogo --> Arvore : Valida regras lógicas
    ListaEncadeada *-- NoLista : Contém
    Arvore *-- NoArvore : Contém
```

### Como as Classes Interagem:
1. **`Main`** invoca `Jogo.iniciar()`. Se rodarmos o jogo via **script** passando um arquivo `.txt`, a `Main` repassa esse arquivo para o `Jogo`.
2. O **`Jogo`** inicializa a `Arvore` preenchendo as regras lógicas.
3. Durante as cenas do **`Jogo`**, o jogador escolhe uma evidência. O **`Jogo`** pergunta à **`Arvore`**: `verificarAcesso()`. A Árvore analisa a **`ListaEncadeada`** para ver se o jogador tem a pista anterior necessária.
4. Se sim, o **`Jogo`** faz um `inserirPista()` na **`ListaEncadeada`**.
5. Se errar a dedução, o sistema invoca `imprimirRelatorio()`, que renderiza a Lista Encadeada no console.

---

## 2. Divisão de Tarefas para o Final de Semana (3 Desenvolvedores)

### Módulo 1: Lista Encadeada (Desenvolvedor A)
- **Responsabilidade:** Criar do zero as classes `NoLista` e `ListaEncadeada`.
- **Regras:** A lista guarda Strings (pistas). A impressão no console deve ter uma interface visual amigável (ex: `Pista 1 -> Pista 2 -> Fim`).
- **Prazo Ideal:** Sábado de manhã.

### Módulo 2: Árvore Hierárquica (Desenvolvedor B)
- **Responsabilidade:** Criar do zero as classes `NoArvore` e `Arvore`.
- **Regras:** O `NoArvore` precisa ter uma lista de filhos genérica (não binária), pois uma pista pode liberar múltiplas outras evidências.
- **Prazo Ideal:** Sábado de manhã.

### Módulo 3: Jogo, Interface e Automação com Scripts (Desenvolvedor C)
- **Responsabilidade:** Classe `Jogo`, leitura do `Scanner` e Scripts de execução.
- **Regras:** Desenvolver a leitura do teclado (`System.in`) e a leitura de arquivos `.txt` **desde o início**. 
  - O `Scanner` deve ser configurado de forma que, se o jogo for executado via terminal com um arquivo (ex: `java Main input.txt`), ele leia o arquivo, permitindo **testes automatizados rápidos** para os Desenvolvedores A e B enquanto eles programam o restante das lógicas no Sábado.
- **Prazo Ideal:** Sábado de manhã.

### Integração e Regras de Negócio (Todos Juntos)
- **Responsabilidade:** Ligar a Lista, a Árvore e o Jogo.
- **Cronograma de Integração (Sábado de Tarde):** Instanciar a Árvore com as pistas e inserir a lógica das cenas. Como a Automação por `.txt` já estará pronta (feita pelo Desenvolvedor C), a equipe usará scripts para validar as jogadas rapidamente.
- **Testes e Polimento (Domingo):** Validar se o cenário de "Game Over" reseta o jogo corretamente e limpar bugs.
