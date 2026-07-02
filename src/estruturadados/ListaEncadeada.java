package estruturadados;

// LISTA ENCADEADA — registra, em ordem, o caminho de pistas do jogador:
// cada nó aponta para a próxima pista coletada.
public class ListaEncadeada {

    private NoLista inicio;

    // Cria a lista vazia.
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

    // Formata o caminho no padrão "[a] -> [b] -> FIM"
    public String formatarHistorico() {
        StringBuilder sb = new StringBuilder();
        NoLista atual = inicio;
        while (atual != null) {
            sb.append("[").append(atual.idPista).append("] -> ");
            atual = atual.proximo;
        }
        sb.append("FIM");
        return sb.toString();
    }

    // Devolve o id da última pista coletada (null se a lista está vazia).
    public String getUltimaPista() {
        if (inicio == null) return null;
        NoLista atual = inicio;
        while (atual.proximo != null) {
            atual = atual.proximo;
        }
        return atual.idPista;
    }

    // Copia para esta lista as pistas de 'outra lista' que ainda não estão aqui
    public void adicionarSeNaoExistir(ListaEncadeada outra) {
        if (outra == null) return;
        NoLista atual = outra.inicio;
        while (atual != null) {
            if (!this.contemPista(atual.idPista)) {
                this.inserirPista(atual.idPista);
            }
            atual = atual.proximo;
        }
    }
}
