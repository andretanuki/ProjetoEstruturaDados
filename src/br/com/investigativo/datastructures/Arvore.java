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
        // TODO: usar buscarNo(idPai) para encontrar o nó pai e adicionar filha à lista de filhos
        // Caso especial: idPai == null significa que filha é filha direta da raiz
        throw new UnsupportedOperationException("inserirDependencia() não implementado ainda");
    }

    // Retorna todas as pistas cujo nó pai já está no histórico do jogador
    public List<Pista> getPistasDisponiveis(ListaEncadeada historico) {
        // TODO: percorrer todos os nós da árvore; para cada nó, verificar se seu pai está
        //       no histórico via historico.contemPista(idPai) — se sim, adicionar à lista
        // Filhos diretos da raiz ficam sempre disponíveis (sem pré-requisito)
        throw new UnsupportedOperationException("getPistasDisponiveis() não implementado ainda");
    }

    // Busca recursiva — retorna o nó com o id informado, ou null se não encontrar
    private NoArvore buscarNo(String id) {
        // ATENÇÃO — RISCO DE BUG: a busca começa nos filhos da raiz sentinela, não na raiz em si
        // (a raiz não tem pista, então comparar raiz.pista.id causaria NullPointerException).
        // Sempre delegar para buscarNoRecursivo a partir dos filhos de raiz.
        throw new UnsupportedOperationException("buscarNo() não implementado ainda");
    }

    // Auxiliar recursivo para buscarNo
    private NoArvore buscarNoRecursivo(NoArvore atual, String id) {
        // TODO: comparar atual.pista.id com id — se igual, retornar atual
        //       senão, iterar sobre atual.filhos e chamar buscarNoRecursivo em cada um
        //       se algum retorno não for null, é o resultado — retornar ele
        //       se nenhum filho encontrou, retornar null
        //
        // ATENÇÃO — RISCO DE BUG: não use == para comparar Strings em Java.
        // Use atual.pista.id.equals(id), não atual.pista.id == id.
        // O == compara referência de objeto, não conteúdo — pode falhar silenciosamente.
        throw new UnsupportedOperationException("buscarNoRecursivo() não implementado ainda");
    }
}
