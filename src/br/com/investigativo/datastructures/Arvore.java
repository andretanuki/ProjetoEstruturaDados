package br.com.investigativo.datastructures;

import br.com.investigativo.model.Pista;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Arvore {

    // Códigos ANSI para colorir o mapa de caminhos no relatório final.
    private static final String ANSI_RESET = "[0m";
    private static final String ANSI_VERDE = "[32m"; // pista importante coletada
    private static final String ANSI_AZUL  = "[34m"; // pista percorrida (não importante)
    private static final String ANSI_AMARELO = "[33m"; // auxiliar de excelência coletada

    // A raiz é um nó sentinela sem pista — seus filhos são as pistas iniciais do jogo
    private NoArvore raiz;

    public Arvore() {
        this.raiz = new NoArvore(null);
    }

    // Localiza o nó com idPai e adiciona filha como seu filho
    public void inserirDependencia(String idPai, Pista filha) {
        if (idPai == null) {
            raiz.filhos.add(new NoArvore(filha));
            return;
        }
        NoArvore pai = buscarNo(idPai);
        pai.filhos.add(new NoArvore(filha));
    }

    // Retorna todas as pistas cujo nó pai já está no histórico do jogador
    public List<Pista> getPistasDisponiveis(ListaEncadeada historico) {
        List<Pista> disponiveis = new ArrayList<>();
        coletarDisponiveis(raiz, historico, disponiveis);
        return disponiveis;
    }

    // Filhos de um nó só entram se o nó estiver no histórico (a raiz sentinela
    // sempre "está"). O contemId deduplica pistas com mais de um pré-requisito
    // (cadastradas como mais de um nó com o mesmo id).
    private void coletarDisponiveis(NoArvore no, ListaEncadeada historico, List<Pista> disponiveis) {
        boolean noAtualColetado = (no == raiz) || historico.contemPista(no.pista.id);
        if (!noAtualColetado) {
            return;
        }
        for (NoArvore filho : no.filhos) {
            if (!historico.contemPista(filho.pista.id) && !contemId(disponiveis, filho.pista.id)) {
                disponiveis.add(filho.pista);
            }
            coletarDisponiveis(filho, historico, disponiveis);
        }
    }

    private boolean contemId(List<Pista> pistas, String id) {
        for (Pista p : pistas) {
            if (p.id.equals(id)) {
                return true;
            }
        }
        return false;
    }

    // Reúne os dados de estilo do mapa para não carregar meia dúzia de
    // parâmetros pela recursão. Todos os realces só valem para pistas COLETADAS.
    public static class EstiloMapa {
        public Set<String> importantes;   // trilhas de desfecho -> verde
        public Set<String> auxiliares;    // badge de excelência -> amarelo
        public Map<String, String> simbolos; // id -> símbolo anexado (★, 🛸, 🌀)
        public Map<String, String> titulos;  // id -> título legível
    }

    // Desenha a árvore inteira em ASCII, realçando o rastro do jogador.
    // Cores e símbolos valem só para pistas coletadas (regras em decorar()).
    public String desenharAscii(ListaEncadeada historico, EstiloMapa estilo) {
        StringBuilder sb = new StringBuilder();
        List<NoArvore> raizes = raiz.filhos;
        for (int i = 0; i < raizes.size(); i++) {
            boolean ultimaRaiz = (i == raizes.size() - 1);
            desenharNo(raizes.get(i), "", ultimaRaiz, historico, estilo, sb);
        }
        return sb.toString();
    }

    // Passo recursivo do desenho. 'prefixo' acumula os traços verticais das
    // gerações anteriores; 'ultimo' indica se este nó é o último filho do pai
    // (muda o conector de ├─ para └─).
    private void desenharNo(NoArvore no, String prefixo, boolean ultimo,
                            ListaEncadeada historico, EstiloMapa estilo, StringBuilder sb) {
        String conector = ultimo ? "└─ " : "├─ ";
        sb.append(prefixo).append(conector)
          .append(decorar(no.pista, historico, estilo))
          .append("\n");

        String prefixoFilhos = prefixo + (ultimo ? "   " : "│  ");
        for (int i = 0; i < no.filhos.size(); i++) {
            boolean ultimoFilho = (i == no.filhos.size() - 1);
            desenharNo(no.filhos.get(i), prefixoFilhos, ultimoFilho, historico, estilo, sb);
        }
    }

    // Aplica cor e símbolo ao rótulo da pista conforme as regras do mapa.
    private String decorar(Pista pista, ListaEncadeada historico, EstiloMapa estilo) {
        String rotulo = estilo.titulos.getOrDefault(pista.id, pista.titulo);
        boolean coletada = historico.contemPista(pista.id);
        if (!coletada) {
            return rotulo; // não coletada: sem cor, sem símbolo
        }
        // Símbolo (★/🛸/🌀) só aparece quando a pista foi coletada.
        String simbolo = estilo.simbolos.get(pista.id);
        if (simbolo != null) {
            rotulo = rotulo + " " + simbolo;
        }
        return pintar(pista.id, rotulo, estilo);
    }

    // Pinta um texto com a cor do papel da pista, seguindo o código de cores
    // do jogo: amarelo (excelência) > verde (importante) > azul (comum).
    public static String pintar(String idPista, String texto, EstiloMapa estilo) {
        String cor;
        if (estilo.auxiliares.contains(idPista)) {
            cor = ANSI_AMARELO;
        } else if (estilo.importantes.contains(idPista)) {
            cor = ANSI_VERDE;
        } else {
            cor = ANSI_AZUL;
        }
        return cor + texto + ANSI_RESET;
    }

    // Busca o nó com o id informado, ou null. Parte dos FILHOS da raiz: a
    // raiz sentinela não tem pista (raiz.pista == null).
    private NoArvore buscarNo(String id) {
        for (NoArvore filho : raiz.filhos) {
            NoArvore encontrado = buscarNoRecursivo(filho, id);
            if (encontrado != null) {
                return encontrado;
            }
        }
        return null;
    }

    // Auxiliar recursivo para buscarNo
    private NoArvore buscarNoRecursivo(NoArvore atual, String id) {
        if (atual.pista.id.equals(id)) {
            return atual;
        }
        for (NoArvore filho : atual.filhos) {
            NoArvore encontrado = buscarNoRecursivo(filho, id);
            if (encontrado != null) {
                return encontrado;
            }
        }
        return null;
    }
}
