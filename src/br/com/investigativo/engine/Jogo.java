package br.com.investigativo.engine;

import br.com.investigativo.datastructures.Arvore;
import br.com.investigativo.datastructures.ListaEncadeada;
import br.com.investigativo.model.Pista;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Jogo {

    private ListaEncadeada historico;
    private Arvore dependencias;
    private Terminal terminal;
    private Persistencia persistencia;
    private String nomeJogador;
    private int tentativas;
    private List<String[]> todosCaminhos;

    public Jogo(Terminal terminal) {
        this.terminal = terminal;
        this.persistencia = new Persistencia("dados/partidas.txt");
        this.historico = new ListaEncadeada();
        this.dependencias = new Arvore();
        this.tentativas = 0;
        this.todosCaminhos = new ArrayList<>();
    }

    // Ponto de entrada: login, carrega histórico, monta gabarito, inicia loop
    public void iniciar() {
        nomeJogador = terminal.loginUsuario();

        String historicoAnterior = persistencia.carregarHistorico(nomeJogador);
        if (!historicoAnterior.isEmpty()) {
            terminal.exibir("\n=== Bem-vindo de volta, " + nomeJogador + "! ===");
            terminal.exibir("Suas partidas anteriores:");
            terminal.exibir(historicoAnterior);
        } else {
            terminal.exibir("\n=== Bem-vindo, Detetive " + nomeJogador + "! ===");
            terminal.exibir("Primeira vez jogando. Boa sorte!");
        }

        montarGabarito();
        rodarCenas();
    }

    // Monta a Arvore com o gabarito do caso — RESPONSABILIDADE DO DEV B
    private void montarGabarito() {
        // TODO: criar os objetos Pista e chamar dependencias.inserirDependencia()
        // Exemplo de estrutura esperada:
        //
        // dependencias.inserirDependencia(null, new Pista("faca", "Faca de cozinha", "Uma faca com manchas escuras."));
        // dependencias.inserirDependencia(null, new Pista("janela", "Janela Arrombada", "Vidro quebrado de dentro para fora."));
        // dependencias.inserirDependencia("faca", new Pista("impressao", "Impressão Digital", "Corresponde ao suspeito."));
        // dependencias.inserirDependencia("faca", new Pista("bota", "Marca de Bota", "Tamanho 42, sem correspondência."));
        // dependencias.inserirDependencia("impressao", new Pista("capturado", "Suspeito Capturado", "Caso encerrado!"));
        throw new UnsupportedOperationException("montarGabarito() não implementado ainda — Dev B deve preencher");
    }

    // Loop principal: exibe menu de cenas, lê escolhas e insere no histórico
    private void rodarCenas() {
        // TODO: para cada cena (3 a 5 rodadas):
        //   1. chamar dependencias.getPistasDisponiveis(historico) para obter o menu
        //   2. exibir as pistas disponíveis numeradas via terminal.exibir()
        //   3. ler a escolha do jogador via terminal.lerEntrada()
        //   4. inserir a pista escolhida no historico com historico.inserirPista(id)
        //   5. chamar verificarGameOver() — se true, chamar reiniciar() ou imprimirRelatorio(true)
        throw new UnsupportedOperationException("rodarCenas() não implementado ainda");
    }

    // Retorna true se a última pista coletada é uma folha da árvore (sem filhos disponíveis)
    private boolean verificarGameOver() {
        // ATENÇÃO — RISCO DE BUG: esta lógica usa "lista de disponíveis vazia = fim de jogo".
        // Isso só funciona corretamente se o gabarito montado em montarGabarito() garantir que
        // TODA folha da árvore é um terminal real (vitória ou derrota) — nunca um nó intermediário
        // que por acaso não tem filhos cadastrados ainda.
        // Antes de integrar: confirmar com o Dev B que o gabarito não tem folhas "acidentais"
        // no meio do caminho correto. Um nó intermediário sem filhos vai encerrar o jogo cedo.
        throw new UnsupportedOperationException("verificarGameOver() não implementado ainda");
    }

    // Salva o caminho atual, incrementa tentativas e reinicia o histórico
    private void reiniciar() {
        todosCaminhos.add(historico.toArray());
        tentativas++;
        // Cria uma nova lista do zero — os dados do caminho anterior já estão salvos em todosCaminhos
        historico = new ListaEncadeada();
        terminal.exibir("\n--- Caminho incorreto. Reiniciando investigação... ---\n");
        rodarCenas();
    }

    // Exibe relatório com todos os caminhos tentados e persiste em arquivo
    private void imprimirRelatorio(boolean venceu) {
        // O caminho da rodada vencedora (ou da última derrota) ainda não está em todosCaminhos —
        // reiniciar() só é chamado em derrotas. Aqui adicionamos o snapshot final manualmente.
        todosCaminhos.add(historico.toArray());
        tentativas++;

        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        terminal.exibir("============================================");
        terminal.exibir("         RELATÓRIO DE INVESTIGAÇÃO");
        terminal.exibir("============================================");
        terminal.exibir("Detetive  : " + nomeJogador);
        terminal.exibir("Tentativas: " + tentativas);
        terminal.exibir("Data/Hora : " + dataHora);
        terminal.exibir("");

        // TODO: para cada caminho em todosCaminhos, exibir no formato:
        //   "--- Tentativa N (FALHOU/SUCESSO) ---"
        //   "  [pista1] -> [pista2] -> FIM"
        // O último caminho corresponde ao resultado de 'venceu'; os anteriores são todos derrotas.

        String resultado = venceu ? "CASO RESOLVIDO — Suspeito identificado!" : "CASO NÃO RESOLVIDO.";
        terminal.exibir("Resultado : " + resultado);
        terminal.exibir("============================================");

        persistencia.salvar(nomeJogador, tentativas, todosCaminhos, venceu);
    }
}
