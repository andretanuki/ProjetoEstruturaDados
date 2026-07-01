package br.com.investigativo.datastructures;

import br.com.investigativo.model.Pista;
import java.util.ArrayList;
import java.util.List;

public class Arvore {

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

    // Percorre recursivamente a árvore: os filhos de um nó só entram na lista
    // se o próprio nó já estiver no histórico (a raiz sentinela sempre "está",
    // liberando seus filhos diretos sem pré-requisito)
    //
    // Uma pista com mais de um pré-requisito é cadastrada como mais de um
    // NoArvore com o mesmo id (um sob cada pai) — por isso a checagem de
    // "já está na lista" abaixo evita que ela apareça duplicada no menu
    // quando os dois pré-requisitos já estiverem no histórico.
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

    // Busca recursiva — retorna o nó com o id informado, ou null se não encontrar
    private NoArvore buscarNo(String id) {
        // ATENÇÃO — RISCO DE BUG: a busca começa nos filhos da raiz sentinela, não na raiz em si
        // (a raiz não tem pista, então comparar raiz.pista.id causaria NullPointerException).
        // Sempre delegar para buscarNoRecursivo a partir dos filhos de raiz.
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
        // ATENÇÃO — RISCO DE BUG: não use == para comparar Strings em Java.
        // Use atual.pista.id.equals(id), não atual.pista.id == id.
        // O == compara referência de objeto, não conteúdo — pode falhar silenciosamente.
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
