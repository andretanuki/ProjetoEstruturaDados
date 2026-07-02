package estruturadados;

import engine.Pista;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Arvore {

    // Códigos ANSI para colorir o mapa de caminhos no relatório final.
    private static final String ANSI_RESET = "\033[0m";
    private static final String ANSI_VERDE = "\033[32m"; // pista importante coletada
    private static final String ANSI_AZUL  = "\033[34m"; // pista percorrida (não importante)
    private static final String ANSI_AMARELO = "\033[33m"; // auxiliar de excelência coletada

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

    // Retorna todas as pistas cujo nó pai já está no histórico do jogador (busca em largura)
    public List<Pista> getPistasDisponiveis(ListaEncadeada historico) {
        List<Pista> disponiveis = new ArrayList<>();
        Queue<NoArvore> fila = new LinkedList<>();
        fila.add(raiz);

        while (!fila.isEmpty()) {
            NoArvore atual = fila.poll();
            boolean noAtualColetado = (atual == raiz) || historico.contemPista(atual.pista.id);
            
            if (noAtualColetado) {
                for (NoArvore filho : atual.filhos) {
                    if (!historico.contemPista(filho.pista.id) && !contemId(disponiveis, filho.pista.id)) {
                        disponiveis.add(filho.pista);
                    }
                    fila.add(filho);
                }
            }
        }
        return disponiveis;
    }

    // Busca o nó com o id informado, ou null. Usa busca em largura (BFS).
    private NoArvore buscarNo(String id) {
        Queue<NoArvore> fila = new LinkedList<>(raiz.filhos);
        
        while (!fila.isEmpty()) {
            NoArvore atual = fila.poll();
            if (atual.pista.id.equals(id)) {
                return atual;
            }
            fila.addAll(atual.filhos);
        }
        return null;
    }

    private boolean contemId(List<Pista> pistas, String id) {
        for (Pista p : pistas) {
            if (p.id.equals(id)) {
                return true;
            }
        }
        return false;
    }

    // =========================================================================
    // MÉTODOS VISUAIS E DE DESENHO (Para o relatório no Terminal)
    // =========================================================================

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
}
