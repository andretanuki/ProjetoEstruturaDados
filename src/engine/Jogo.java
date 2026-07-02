package engine;

import estruturadados.Arvore;
import estruturadados.ListaEncadeada;
import java.util.ArrayList;
import java.util.List;

// JOGO - regras: fluxo das 5 cenas, menu atual filtrado pela Arvore, registro
// do caminho na ListaEncadeada, desfecho e relatório. 
// Os textos Narrativos ficam todos em Roteiro.java

public class Jogo {

    // [ListaEncadeada historico]
    // Armazena a sequência de pistas que o jogador escolheu durante a partida.
    private ListaEncadeada historico;

    // [Arvore dependencias]
    // Representa o mapa do jogo, onde cada nó é uma pista e seus filhos são as pistas que são desbloqueadas após coletá-la.
    private Arvore dependencias;
    
    private Terminal terminal;
    private Persistencia persistencia;
    private String nomeJogador;
    private List<ListaEncadeada> todasTentativas = new ArrayList<>();
    private String[] textosCenas;

    // Liga o jogo às suas dependências: terminal, persistência e estruturas vazias.
    public Jogo(Terminal terminal) {
        this.terminal = terminal;
        // O construtor da Persistencia cria o diretório dados/ se não existir.
        this.persistencia = new Persistencia("dados/partidas.txt");
        this.historico = new ListaEncadeada();
        this.dependencias = new Arvore();
    }

    // Ponto de entrada: login, carrega o histórico salvo, monta o gabarito
    // e inicia o loop de cenas [parte principal]
    public void iniciar() {
        terminal.limparTela();
        nomeJogador = terminal.loginUsuario();

        String historicoAnterior = persistencia.carregarHistorico(nomeJogador);
        if (!historicoAnterior.isEmpty()) {
            terminal.exibir("\n=== Bem-vindo de volta, " + nomeJogador + "! ===");
            terminal.exibir("Suas partidas anteriores:");
            terminal.exibir(historicoAnterior);
            todasTentativas = persistencia.carregarCaminhos(nomeJogador);
        } else {
            terminal.exibir("\n=== Bem-vindo, Detetive " + nomeJogador + "! ===");
            terminal.exibir("Primeira vez jogando. Boa sorte!");
        }

        montarGabarito();
        rodarCenas();
    }

    // Loop principal: a cada cena pausa, texto, menu filtrado pela árvore,
    // escolha do jogador e registro no histórico; ao fim das 5 cenas, decide
    // o desfecho, exibe o relatório e oferece a restart.

    private void rodarCenas() {
        for (int cena = 0; cena < textosCenas.length; cena++) {
            terminal.aguardarEnterELimpar();
            terminal.exibir("\n" + Roteiro.SEPARADOR_CENA);
            terminal.exibir(textosCenas[cena]);

            List<String> menu = montarMenuDaCena(cena);
            if (menu.isEmpty()) {
                continue;
            }

            terminal.exibir("\nPISTAS DISPONÍVEIS:");
            for (int i = 0; i < menu.size(); i++) {
                Pista p = dependencias.buscarPista(menu.get(i));
                terminal.exibir("  " + (i + 1) + ". " + p.titulo);
            }

            String idEscolhido = lerEscolha(menu);
            Pista escolhida = dependencias.buscarPista(idEscolhido);
            terminal.exibir("\n>> " + escolhida.titulo);
            // Descrição pintada com a cor do papel da pista.
            terminal.exibir("   " + escolhida.pintar(escolhida.descricao));
            // [ListaEncadeada historico]
            // A pista escolhida é inserida no final da lista encadeada.
            historico.inserirPista(idEscolhido);
        }
        terminal.aguardarEnterELimpar();
        // O desfecho é uma função da última pista coletada.
        int desfecho = Roteiro.desfechoDe(historico.getUltimaPista());
        imprimirRelatorio(desfecho);
        reiniciar();
    }

    // Exibe o epílogo do desfecho e o relatório unificado (todas as
    // tentativas do jogador + mapa colorido do caso) e grava na persistência.
    private void imprimirRelatorio(int desfecho) {
        todasTentativas.add(historico);

        terminal.exibir("\n" + Roteiro.epilogoDesfecho(desfecho, venceuComExcelencia(), historico));
        terminal.aguardarEnterELimpar();

        terminal.exibir("\n============================================");
        terminal.exibir("         RELATÓRIO DE INVESTIGAÇÃO");
        terminal.exibir("============================================");
        terminal.exibir("Detetive  : " + nomeJogador);
        terminal.exibir("Tentativas: " + todasTentativas.size());
        terminal.exibir("");

        for (int i = 0; i < todasTentativas.size(); i++) {
            ListaEncadeada caminho = todasTentativas.get(i);
            String ultimaPista = caminho.getUltimaPista();
            int d = Roteiro.desfechoDe(ultimaPista);
            String rotulo = (d == Roteiro.DESFECHO_VITORIA) ? "SUCESSO"
                          : (d == Roteiro.DESFECHO_DERROTA) ? "FALHOU"
                          : "FINAL ALTERNATIVO";
            terminal.exibir("--- Tentativa " + (i + 1) + " (" + rotulo + ") ---");
            terminal.exibir("  " + caminho.formatarHistorico());
            if (d == Roteiro.DESFECHO_DERROTA) {
                if (ultimaPista != null) {
                    Pista p = dependencias.buscarPista(ultimaPista);
                    String titulo = (p != null) ? p.titulo : ultimaPista;
                    terminal.exibir("  ✗ \"" + titulo + "\" levou a um beco sem saída.");
                } else {
                    terminal.exibir("  ✗ Nenhuma pista foi investigada.");
                }
            }
            terminal.exibir("");
        }

        terminal.exibir("MAPA DO CASO:");
        terminal.exibir("  Toda pista COLORIDA já foi investigada por você.");
        terminal.exibir("  verde: pista-chave | amarelo: excelência | azul: pista comum");
        terminal.exibir("");

        // [Arvore dependencias]
        // Percorre a árvore de dependências comparando com a lista encadeada do histórico 
        // para imprimir o mapa completo, colorindo os nós que estão presentes no histórico geral.
        terminal.exibir(dependencias.desenharAscii(historicoDeTentativas()));

        terminal.exibir("Resultado : " + Roteiro.textoDesfecho(desfecho, venceuComExcelencia()));
        terminal.exibir("============================================");

        persistencia.salvar(nomeJogador, todasTentativas, desfecho == Roteiro.DESFECHO_VITORIA);
    }

    // Oferece nova partida: zera o histórico atual e roda as 5 cenas de novo.
    private void reiniciar() {
        terminal.exibir("\nQuer investigar o caso de novo, por outro caminho? (s/n)");
        String resposta = terminal.lerEntrada();
        if (resposta != null && resposta.trim().toLowerCase().startsWith("s")) {
            historico = new ListaEncadeada();
            terminal.exibir("\n--- Reabrindo a investigação. Boa sorte, detetive! ---");
            rodarCenas();
        } else {
            terminal.exibir("\nCaso encerrado. Até a próxima, detetive.");
        }
    }

    // Popula a árvore a partir da tabela do Roteiro
    private void montarGabarito() {
        for (String[] linha : Roteiro.PISTAS) {
            Pista pista = new Pista(linha[1], linha[2], linha[3]);
            if (linha.length > 4) pista.cor = Integer.parseInt(linha[4]);
            if (linha.length > 5) pista.simbolo = linha[5];
            dependencias.inserirDependencia(linha[0], pista);
        }
        textosCenas = Roteiro.TEXTOS_CENAS;
    }

    // Menu da cena é uma lista fixa da cena que mantem só as pistas selecionáveis:
    // sem duplicatas, sem já coletadas e com o pré-requisito cumprido na árvore.
    private List<String> montarMenuDaCena(int cena) {
        List<String> disponiveis = dependencias.getPistasDisponiveis(historico);//<- metodo importante

        List<String> menu = new ArrayList<>();
        for (String id : Roteiro.PISTAS_POR_CENA[cena]) {
            if (menu.contains(id)) continue;        // sem duplicatas
            if (historico.contemPista(id)) continue;// já coletada
            if (!disponiveis.contains(id)) continue;// pré-requisito não cumprido
            menu.add(id);
        }
        return menu;
    }

    // Lê um número válido do menu; entrada inválida avisa e pede de novo,
    // sem lançar exceção (robusto também no modo script).
    private String lerEscolha(List<String> menu) {
        while (true) {
            terminal.exibir("\nDigite o número da pista que quer investigar:");
            String entrada = terminal.lerEntrada();
            if (entrada != null) {
                entrada = entrada.trim();
                try {
                    int indice = Integer.parseInt(entrada);
                    if (indice >= 1 && indice <= menu.size()) {
                        return menu.get(indice - 1);
                    }
                } catch (NumberFormatException e) {
                }
            }
            terminal.exibir("Isso não consta no caso, detetive. Digite um número entre 1 e " + menu.size() + ".");
        }
    }

    // Agrega as pistas de todas as tentativas (sem repetição) numa lista só,
    // para o mapa colorir tudo que o jogador já percorreu.
    private ListaEncadeada historicoDeTentativas() {
        ListaEncadeada agregado = new ListaEncadeada();
        for (ListaEncadeada caminho : todasTentativas) {
            agregado.adicionarSeNaoExistir(caminho);
        }
        return agregado;
    }

    // Excelência = ter coletado a dupla AUXILIARES na partida atual.
    private boolean venceuComExcelencia() {
        for (String aux : Roteiro.AUXILIARES) {
            if (!historico.contemPista(aux)) {
                return false;
            }
        }
        return true;
    }
}
