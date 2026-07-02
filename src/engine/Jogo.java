package engine;

import estruturadados.Arvore;
import estruturadados.ListaEncadeada;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Jogo {

    private ListaEncadeada historico;
    private Arvore dependencias;
    private Terminal terminal;
    private Persistencia persistencia;
    
    private String nomeJogador;
    private List<String[]> todosCaminhos;
    // Caminhos de sessões ANTERIORES, carregados no login.
    // Usados APENAS para colorir o mapa — não entram no relatório da sessão atual.
    private List<String[]> caminhosPreviaosSessao = new ArrayList<>();
    
    // id -> Pista com título e descrição (preenchido a partir de Roteiro.PISTAS).
    private Map<String, Pista> textosPistas = new HashMap<>();
    private String[] textosCenas;

    public Jogo(Terminal terminal) {
        this.terminal = terminal;
        // Resolve o caminho dependendo de onde o jogo é executado (IDE vs cd src)
        String caminhoDados = new java.io.File("dados").isDirectory() 
            ? "dados/partidas.txt" 
            : "src/dados/partidas.txt";
        this.persistencia = new Persistencia(caminhoDados);
        this.historico = new ListaEncadeada();
        this.dependencias = new Arvore();
        this.todosCaminhos = new ArrayList<>();
    }

    // =========================================================================
    // FLUXO PRINCIPAL DO JOGO (Top-Down)
    // =========================================================================

    // Ponto de entrada: login, carrega histórico, monta gabarito, inicia loop
    public void iniciar() {
        terminal.limparTela();
        nomeJogador = terminal.loginUsuario();

        String historicoAnterior = persistencia.carregarHistorico(nomeJogador);
        if (!historicoAnterior.isEmpty()) {
            terminal.exibir("\n=== Bem-vindo de volta, " + nomeJogador + "! ===");
            terminal.exibir("Suas partidas anteriores:");
            terminal.exibir(historicoAnterior);
            // Carrega os caminhos anteriores para o mapa de cores incluir
            // o histórico completo do jogador, não só a sessão atual.
            caminhosPreviaosSessao = persistencia.carregarCaminhos(nomeJogador);
        } else {
            terminal.exibir("\n=== Bem-vindo, Detetive " + nomeJogador + "! ===");
            terminal.exibir("Primeira vez jogando. Boa sorte!");
        }

        montarGabarito();
        rodarCenas();
    }

    // Loop principal: a cada cena exibe o texto, monta o menu filtrado, lê a
    // escolha e registra no histórico; ao fim decide o desfecho e fecha.
    private void rodarCenas() {
        for (int cena = 0; cena < textosCenas.length; cena++) {
            terminal.aguardarEnterELimpar();
            terminal.exibir("\n" + Roteiro.SEPARADOR_CENA);
            terminal.exibir(textosCenas[cena]);

            List<String> menu = montarMenuDaCena(cena);
            // Salvaguarda: menu vazio pula a cena sem travar.
            if (menu.isEmpty()) {
                continue;
            }

            terminal.exibir("\nPISTAS DISPONÍVEIS:");
            for (int i = 0; i < menu.size(); i++) {
                Pista p = textosPistas.get(menu.get(i));
                terminal.exibir("  " + (i + 1) + ". " + p.titulo);
            }

            String idEscolhido = lerEscolha(menu);
            Pista escolhida = textosPistas.get(idEscolhido);
            terminal.exibir("\n>> " + escolhida.titulo);
            // Descrição pintada com a cor do papel da pista (código de cores do mapa).
            terminal.exibir("   " + Arvore.pintar(idEscolhido, escolhida.descricao, estiloDoMapa()));
            historico.inserirPista(idEscolhido);
        }

        terminal.aguardarEnterELimpar();
        int desfecho = verificarGameOver();
        imprimirRelatorio(desfecho);
        reiniciar();
    }

    // Exibe epílogo + relatório da sessão e persiste. 'desfecho' é um DESFECHO_*.
    private void imprimirRelatorio(int desfecho) {
        todosCaminhos.add(historico.toArray());

        // Epílogo antes do relatório: a história fecha colada no clímax.
        terminal.exibir("\n" + Roteiro.epilogoDesfecho(desfecho, venceuComExcelencia(), historico));
        terminal.aguardarEnterELimpar();

        List<String[]> todasTentativasDoJogador = new ArrayList<>();
        todasTentativasDoJogador.addAll(caminhosPreviaosSessao);
        todasTentativasDoJogador.addAll(todosCaminhos);

        terminal.exibir("\n============================================");
        terminal.exibir("         RELATÓRIO DE INVESTIGAÇÃO");
        terminal.exibir("============================================");
        terminal.exibir("Detetive  : " + nomeJogador);
        terminal.exibir("Tentativas: " + todasTentativasDoJogador.size());
        terminal.exibir("");

        // Todas as tentativas do jogador (sessões anteriores e atual unidas).
        // O rótulo deriva da última pista do caminho; finais malucos não
        // ganham a linha de beco sem saída.
        for (int i = 0; i < todasTentativasDoJogador.size(); i++) {
            String[] caminho = todasTentativasDoJogador.get(i);
            String ultimaPista = (caminho.length > 0) ? caminho[caminho.length - 1] : null;
            boolean sucesso = Roteiro.PISTA_FINAL.equals(ultimaPista);
            boolean alternativo = Roteiro.NETA_ABDUCAO.equals(ultimaPista) || Roteiro.NETA_LOUCURA.equals(ultimaPista);
            String rotulo = sucesso ? "SUCESSO" : (alternativo ? "FINAL ALTERNATIVO" : "FALHOU");
            terminal.exibir("--- Tentativa " + (i + 1) + " (" + rotulo + ") ---");
            terminal.exibir("  " + formatarCaminho(caminho));
            if (!sucesso && !alternativo) {
                if (ultimaPista != null) {
                    Pista p = textosPistas.get(ultimaPista);
                    String titulo = (p != null) ? p.titulo : ultimaPista;
                    terminal.exibir("  ✗ \"" + titulo + "\" levou a um beco sem saída.");
                } else {
                    terminal.exibir("  ✗ Nenhuma pista foi investigada.");
                }
            }
            terminal.exibir("");
        }

        // Árvore inteira do caso com o rastro da sessão colorido por cima.
        terminal.exibir("MAPA DO CASO:");
        terminal.exibir("  Toda pista COLORIDA já foi investigada por você.");
        terminal.exibir("  verde: pista-chave | amarelo: excelência | azul: pista comum");
        terminal.exibir("");
        terminal.exibir(dependencias.desenharAscii(historicoDaSessao(), estiloDoMapa()));

        terminal.exibir("Resultado : " + textoDesfecho(desfecho));
        terminal.exibir("============================================");

        persistencia.salvar(nomeJogador, todasTentativasDoJogador.size(), todosCaminhos, desfecho == Roteiro.DESFECHO_VITORIA);
    }

    // Oferece nova partida: zera o histórico e roda as cenas de novo (o
    // snapshot do caminho já foi guardado em todosCaminhos).
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

    // =========================================================================
    // MÉTODOS AUXILIARES E LÓGICA DE DADOS
    // =========================================================================

    // Monta o roteiro do caso a partir da classe Roteiro
    private void montarGabarito() {
        for (String[] p : Roteiro.PISTAS) {
            dependencias.inserirDependencia(p[0], new Pista(p[1], p[2], ""));
            textosPistas.put(p[1], new Pista(p[1], p[2], p[3]));
        }
        textosCenas = Roteiro.getTextosCenas();
    }

    // Menu da cena = lista fixa da cena mantendo só as pistas selecionáveis:
    // pai já no histórico (árvore), não coletadas e sem ids duplicados.
    private List<String> montarMenuDaCena(int cena) {
        List<Pista> disponiveis = dependencias.getPistasDisponiveis(historico);
        List<String> idsDisponiveis = new ArrayList<>();
        for (Pista p : disponiveis) {
            idsDisponiveis.add(p.id);
        }

        List<String> menu = new ArrayList<>();
        for (String id : Roteiro.PISTAS_POR_CENA[cena]) {
            if (menu.contains(id)) continue;                 // sem duplicatas
            if (historico.contemPista(id)) continue;         // já coletada
            if (!idsDisponiveis.contains(id)) continue;      // pré-requisito não cumprido
            menu.add(id);
        }
        return menu;
    }

    // Lê a escolha do jogador validando contra o menu
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
                    // ignora e avisa abaixo
                }
            }
            terminal.exibir("Isso não consta no caso, detetive. Digite um número entre 1 e " + menu.size() + ".");
        }
    }

    // Decide o desfecho pelo histórico
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

    // Agrega todas as pistas coletadas na sessão atual + sessões anteriores
    // numa única ListaEncadeada, para o mapa colorir o histórico completo.
    private ListaEncadeada historicoDaSessao() {
        ListaEncadeada agregado = new ListaEncadeada();
        for (String[] caminho : caminhosPreviaosSessao) {
            for (String id : caminho) {
                if (!agregado.contemPista(id)) agregado.inserirPista(id);
            }
        }
        for (String[] caminho : todosCaminhos) {
            for (String id : caminho) {
                if (!agregado.contemPista(id)) agregado.inserirPista(id);
            }
        }
        return agregado;
    }

    // Estilo do mapa ASCII: importantes (verde), auxiliares da badge, símbolos
    private Arvore.EstiloMapa estiloDoMapa() {
        Arvore.EstiloMapa estilo = new Arvore.EstiloMapa();

        Set<String> importantes = new HashSet<>();
        for (String id : Roteiro.PRE_REQUISITOS) importantes.add(id);
        importantes.add(Roteiro.PISTA_FINAL);
        for (String id : Roteiro.AUXILIARES) importantes.add(id);
        importantes.add("janela_forcada");
        importantes.add("vidro_quebrado");
        importantes.add(Roteiro.NETA_ABDUCAO);
        importantes.add("copo_cafe");
        importantes.add("bilhete_manchado");
        importantes.add(Roteiro.NETA_LOUCURA);
        estilo.importantes = importantes;

        estilo.auxiliares = new HashSet<>(Arrays.asList(Roteiro.AUXILIARES));

        Map<String, String> simbolos = new HashMap<>();
        simbolos.put(Roteiro.PISTA_FINAL, "★");
        simbolos.put(Roteiro.NETA_ABDUCAO, "🛸");
        simbolos.put(Roteiro.NETA_LOUCURA, "🌀");
        estilo.simbolos = simbolos;

        estilo.titulos = titulosDasPistas();
        return estilo;
    }

    // id -> título legível
    private Map<String, String> titulosDasPistas() {
        Map<String, String> titulos = new HashMap<>();
        for (Map.Entry<String, Pista> e : textosPistas.entrySet()) {
            titulos.put(e.getKey(), e.getValue().titulo);
        }
        return titulos;
    }

    // Formata um snapshot de caminho
    private String formatarCaminho(String[] caminho) {
        StringBuilder sb = new StringBuilder();
        for (String id : caminho) {
            sb.append("[").append(id).append("] -> ");
        }
        sb.append("FIM");
        return sb.toString();
    }

    // Linha de resultado exibida no relatório
    private String textoDesfecho(int desfecho) {
        switch (desfecho) {
            case Roteiro.DESFECHO_VITORIA:
                return venceuComExcelencia()
                    ? "CASO RESOLVIDO COM EXCELÊNCIA — nenhuma pista escapou!"
                    : "CASO RESOLVIDO — o Dr. Almeida forjou o próprio sumiço.";
            case Roteiro.DESFECHO_ABDUCAO:
                return "FINAL ALTERNATIVO — Você foi ABDUZIDO investigando o inexplicável!";
            case Roteiro.DESFECHO_LOUCURA:
                return "FINAL ALTERNATIVO — Você MERGULHOU NA LOUCURA da conspiração!";
            default:
                return "CASO NÃO RESOLVIDO — as pistas certas escaparam desta vez.";
        }
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
