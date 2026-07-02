package estruturadados;

import engine.Pista;
import java.util.ArrayList;
import java.util.List;

// ARVORE - a estrutura de dependências do caso: cada nó é uma pista e os
// filhos só ficam disponíveis depois que o pai entra no histórico do jogador.
public class Arvore {

    // A raiz é um nó sentinela(valor null) sem pista - seus filhos são as pistas iniciais do jogo
    private NoArvore raiz;

    // Cria a árvore apenas com a raiz sentinela.
    public Arvore() {
        this.raiz = new NoArvore(null);
    }

    // Localiza o nó com idPai (null = raiz) e adiciona filha como seu filho
    public void inserirDependencia(String idPai, Pista filha) {
        NoArvore pai = (idPai == null) ? raiz : buscarNo(raiz, idPai);
        if (pai != null) {
            pai.filhos.add(new NoArvore(filha));
        }
    }

    // Busca a Pista com o id informado - a árvore é a fonte única dos dados
    // das pistas (o Jogo consulta até título e descrição aqui).
    public Pista buscarPista(String id) {
        NoArvore no = (id == null) ? null : buscarNo(raiz, id);
        return (no != null) ? no.pista : null;
    }

    // Retorna os ids das pistas cujo pai já está no histórico do jogador
    public List<String> getPistasDisponiveis(ListaEncadeada historico) {
        List<String> disponiveis = new ArrayList<>();
        coletarPistasDisponiveis(raiz, historico, disponiveis);
        return disponiveis;
    }

    // Passo recursivo: filhos de nós já coletados entram na lista.
    private void coletarPistasDisponiveis(NoArvore atual, ListaEncadeada historico, List<String> disponiveis) {
        boolean noAtualColetado = (atual == raiz) || historico.contemPista(atual.pista.id);
        if (!noAtualColetado) {
            return;
        }
        for (NoArvore filho : atual.filhos) {
            if (!historico.contemPista(filho.pista.id)) {
                disponiveis.add(filho.pista.id);
            }
            coletarPistasDisponiveis(filho, historico, disponiveis);
        }
    }

    // Busca o nó com o id informado, ou null (busca em profundidade, DFS).
    private NoArvore buscarNo(NoArvore atual, String id) {
        if (atual != raiz && atual.pista.id.equals(id)) {
            return atual;
        }
        for (NoArvore filho : atual.filhos) {
            NoArvore encontrado = buscarNo(filho, id);
            if (encontrado != null) {
                return encontrado;
            }
        }
        return null;
    }

    // MÉTODOS VISUAIS E DE DESENHO (Para o relatório no Terminal)

    // Desenha a árvore inteira em ASCII, realçando o rastro do jogador.
    public String desenharAscii(ListaEncadeada historico) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raiz.filhos.size(); i++) {
            boolean ultimaRaiz = (i == raiz.filhos.size() - 1);
            desenharNo(raiz.filhos.get(i), "", ultimaRaiz, historico, sb);
        }
        return sb.toString();
    }

    // Passo recursivo do desenho. 'prefixo' acumula os traços verticais das
    // gerações anteriores; 'ultimo' indica se este nó é o último filho do pai
    // (muda o conector de ├─ para └─).
    private void desenharNo(NoArvore no, String prefixo, boolean ultimo,
                            ListaEncadeada historico, StringBuilder sb) {
        String conector = ultimo ? "└─ " : "├─ ";
        sb.append(prefixo).append(conector)
          .append(decorar(no.pista, historico))
          .append("\n");

        String prefixoFilhos = prefixo + (ultimo ? "   " : "│  ");
        for (int i = 0; i < no.filhos.size(); i++) {
            boolean ultimoFilho = (i == no.filhos.size() - 1);
            desenharNo(no.filhos.get(i), prefixoFilhos, ultimoFilho, historico, sb);
        }
    }

    // Aplica cor e símbolo ao rótulo da pista (regras de pintura na própria Pista)
    private String decorar(Pista pista, ListaEncadeada historico) {
        String rotulo = pista.titulo;
        if (!historico.contemPista(pista.id)) {
            return rotulo; // não coletada: sem cor, sem símbolo
        }
        if (!pista.simbolo.isEmpty()) {
            rotulo = rotulo + " " + pista.simbolo;
        }
        return pista.pintar(rotulo);
    }
}
