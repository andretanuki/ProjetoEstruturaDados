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
1. **`Main`** invoca `Jogo.iniciar()`. Se rodarmos o jogo via script passando um arquivo `.txt`, a `Main` repassa para o `Jogo`.
2. O **`Jogo`** inicializa a `Arvore` preenchendo as regras lógicas.
3. Durante as cenas do **`Jogo`**, o jogador escolhe uma evidência. O **`Jogo`** pergunta à **`Arvore`**: `verificarAcesso()`. A Árvore analisa a **`ListaEncadeada`** para ver se o jogador tem a pista anterior necessária.
4. Se sim, o **`Jogo`** faz um `inserirPista()` na **`ListaEncadeada`**.
5. Se errar a dedução, o sistema invoca `imprimirRelatorio()`, que renderiza a Lista Encadeada no console mostrando o caminho incorreto.

---

## 2. Divisão de Tarefas para o Final de Semana (3 Desenvolvedores)

Como a implementação de código padrão é extremamente rápida com o uso de IA, as duas Estruturas de Dados foram concentradas em um único desenvolvedor, abrindo espaço para um foco maior na Lógica e na Interface.

### Módulo 1: Estruturas de Dados (Desenvolvedor A)
- **Responsabilidade:** Criar do zero as classes `NoLista`, `ListaEncadeada`, `NoArvore` e `Arvore`.
- **Regras da Lista:** Guarda Strings (pistas) e possui um método visual de impressão (`Pista 1 -> Pista 2 -> Fim`).
- **Regras da Árvore:** Cada `NoArvore` tem uma lista de filhos genérica (não binária), pois uma pista pode liberar múltiplas evidências.
- **Prazo Ideal:** Sábado de manhã.

### Módulo 2: Motor do Jogo e Narrativa (Desenvolvedor B)
- **Responsabilidade:** Classe `Jogo` (Parte 1: Loop, Narrativa e Regras).
- **Regras:** Desenvolver o "roteiro" (textos das 3 a 5 cenas) e "chumbar" (hardcode) a árvore do gabarito (qual pista libera qual pista).
- **Prazo Ideal:** Sábado de manhã.

### Módulo 3: Interface, Inputs e Scripts (Desenvolvedor C)
- **Responsabilidade:** Classe `Jogo` (Parte 2: IO), `Main` e Automação.
- **Regras:** Cuidar do sistema de login e da leitura de entradas (`Scanner`). Desenvolver a leitura mista: aceitar digitação manual via `System.in` ou ler de um arquivo de script `.txt` via argumentos da `Main`.
- **Prazo Ideal:** Sábado de manhã (para que os Desenvolvedores A e B usem os scripts `.txt` na etapa de testes à tarde).

### Integração e Testes (Todos Juntos)
- **Responsabilidade:** Conectar Módulos 1, 2 e 3.
- **Cronograma (Sábado à Tarde):** Injetar as instâncias de Árvore e Lista dentro do loop do Jogo. Usar os scripts em `.txt` criados pelo Desenvolvedor C para rodar o jogo do início ao fim em segundos e mapear bugs.
- **Polimento (Domingo):** Garantir a estabilidade do reset do jogo em caso de "Game Over" e preparar a apresentação.
