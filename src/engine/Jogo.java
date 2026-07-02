package engine;

import estruturadados.Arvore;
import estruturadados.ListaEncadeada;
import java.util.ArrayList;
import java.util.List;

// JOGO — as regras: fluxo das 5 cenas, menu filtrado pela Arvore, registro
// do caminho na ListaEncadeada, desfecho e relatório. Nenhum texto narrativo
// vive aqui — conteúdo é papel do Roteiro.
public class Jogo {

    // [ListaEncadeada historico]
    // Armazena a sequência de pistas que o jogador escolheu durante a partida.
    // É uma lista encadeada simples onde cada nó aponta para a próxima pista coletada.
    private ListaEncadeada historico;

    // [Arvore dependencias]
    // Representa o mapa do jogo, onde cada nó é uma pista e seus filhos são 
    // as pistas que são desbloqueadas após coletá-la.
    private Arvore dependencias;
    
    private Terminal terminal;
    private Persistencia persistencia;
    
    private String nomeJogador;
    private List<ListaEncadeada> todasTentativas = new ArrayList<>();
    
    private Pista[] textosPistas;
    private String[] textosCenas;

    public Jogo(Terminal terminal) {
        this.terminal = terminal;
        // O construtor da Persistencia cria o diretório dados/ se não existir.
        this.persistencia = new Persistencia("dados/partidas.txt");
        this.historico = new ListaEncadeada();
        this.dependencias = new Arvore();
    }

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
                Pista p = buscarPistaPorId(menu.get(i));
                terminal.exibir("  " + (i + 1) + ". " + p.titulo);
            }

            String idEscolhido = lerEscolha(menu);
            Pista escolhida = buscarPistaPorId(idEscolhido);
            terminal.exibir("\n>> " + escolhida.titulo);
            // Descrição pintada com a cor do papel da pista.
            terminal.exibir("   " + escolhida.pintar(escolhida.descricao));

            // [ListaEncadeada historico]
            // A pista escolhida é inserida no final da lista encadeada.
            historico.inserirPista(idEscolhido);
        }

        terminal.aguardarEnterELimpar();
        int desfecho = verificarGameOver();
        imprimirRelatorio(desfecho);
        reiniciar();
    }

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
            boolean sucesso = Roteiro.PISTA_FINAL.equals(ultimaPista);
            boolean alternativo = Roteiro.NETA_ABDUCAO.equals(ultimaPista) || Roteiro.NETA_LOUCURA.equals(ultimaPista);
            String rotulo = sucesso ? "SUCESSO" : (alternativo ? "FINAL ALTERNATIVO" : "FALHOU");
            terminal.exibir("--- Tentativa " + (i + 1) + " (" + rotulo + ") ---");
            terminal.exibir("  " + caminho.formatarHistorico());
            if (!sucesso && !alternativo) {
                if (ultimaPista != null) {
                    Pista p = buscarPistaPorId(ultimaPista);
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
        // Percorre a árvore de dependências comparando com a lista encadeada do histórico da sessão
        // para imprimir o mapa completo, colorindo os nós que estão presentes no histórico.
        terminal.exibir(dependencias.desenharAscii(historicoDaSessao()));

        terminal.exibir("Resultado : " + Roteiro.textoDesfecho(desfecho, venceuComExcelencia()));
        terminal.exibir("============================================");

        persistencia.salvar(nomeJogador, todasTentativas, desfecho == Roteiro.DESFECHO_VITORIA);
    }

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

    private void montarGabarito() {
        // [Arvore dependencias]
        // Popula a árvore a partir da tabela do Roteiro: cada linha diz quem é
        // o pai, e a MESMA Pista vai para a árvore e para o índice de textos
        // (cor e símbolo também vêm da tabela — colunas opcionais 5 e 6).
        textosPistas = new Pista[Roteiro.PISTAS.length];

        for (int i = 0; i < Roteiro.PISTAS.length; i++) {
            String[] linha = Roteiro.PISTAS[i];
            Pista pista = new Pista(linha[1], linha[2], linha[3]);
            if (linha.length > 4) pista.cor = Integer.parseInt(linha[4]);
            if (linha.length > 5) pista.simbolo = linha[5];

            dependencias.inserirDependencia(linha[0], pista);
            textosPistas[i] = pista;
        }
        textosCenas = Roteiro.TEXTOS_CENAS;
    }

    private Pista buscarPistaPorId(String id) {
        if (id == null) return null;
        for (int i = 0; i < textosPistas.length; i++) {
            if (textosPistas[i] != null && textosPistas[i].id.equals(id)) {
                return textosPistas[i];
            }
        }
        return null;
    }

    private List<String> montarMenuDaCena(int cena) {
        List<String> disponiveis = dependencias.getPistasDisponiveis(historico);

        List<String> menu = new ArrayList<>();
        for (String id : Roteiro.PISTAS_POR_CENA[cena]) {
            if (menu.contains(id)) continue;                 // sem duplicatas
            if (historico.contemPista(id)) continue;         // já coletada
            if (!disponiveis.contains(id)) continue;         // pré-requisito não cumprido
            menu.add(id);
        }
        return menu;
    }

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

    private int verificarGameOver() {
        if (historico.contemPista(Roteiro.PISTA_FINAL)) {
            return Roteiro.DESFECHO_VITORIA;
        }
        if (historico.contemPista(Roteiro.NETA_ABDUCAO)) {
            return Roteiro.DESFECHO_ABDUCAO;
        }
        if (historico.contemPista(Roteiro.NETA_LOUCURA)) {
            return Roteiro.DESFECHO_LOUCURA;
        }
        return Roteiro.DESFECHO_DERROTA;
    }

    private ListaEncadeada historicoDaSessao() {
        ListaEncadeada agregado = new ListaEncadeada();
        for (ListaEncadeada caminho : todasTentativas) {
            agregado.adicionarSeNaoExistir(caminho);
        }
        return agregado;
    }

    private boolean venceuComExcelencia() {
        for (String aux : Roteiro.AUXILIARES) {
            if (!historico.contemPista(aux)) {
                return false;
            }
        }
        return true;
    }
}
