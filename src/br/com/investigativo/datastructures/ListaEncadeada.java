package br.com.investigativo.datastructures;

public class ListaEncadeada {

    private NoLista inicio;

    public ListaEncadeada() {
        this.inicio = null;
    }

    // Adiciona a pista no fim da lista
    public void inserirPista(String id) {
        NoLista novo = new NoLista(id);
        if (inicio == null) {
            inicio = novo;
            return;
        }
        NoLista atual = inicio;
        while (atual.proximo != null) {
            atual = atual.proximo;
        }
        atual.proximo = novo;
    }

    // Retorna true se a pista com esse id já foi coletada
    public boolean contemPista(String id) {
        NoLista atual = inicio;
        while (atual != null) {
            if (atual.idPista.equals(id)) {
                return true;
            }
            atual = atual.proximo;
        }
        return false;
    }

    // Retorna todas as pistas coletadas como array (usado pelo Jogo antes de reiniciar)
    public String[] toArray() {
        int tamanho = 0;
        NoLista atual = inicio;
        while (atual != null) {
            tamanho++;
            atual = atual.proximo;
        }

        String[] ids = new String[tamanho];
        atual = inicio;
        int indice = 0;
        while (atual != null) {
            ids[indice] = atual.idPista;
            indice++;
            atual = atual.proximo;
        }
        return ids;
    }

    // Imprime no formato: [faca] -> [impressao_digital] -> FIM
    public void imprimirHistorico() {
        StringBuilder sb = new StringBuilder();
        NoLista atual = inicio;
        while (atual != null) {
            sb.append("[").append(atual.idPista).append("] -> ");
            atual = atual.proximo;
        }
        sb.append("FIM");
        System.out.println(sb.toString());
    }
}
