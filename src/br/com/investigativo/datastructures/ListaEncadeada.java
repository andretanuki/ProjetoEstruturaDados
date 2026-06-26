package br.com.investigativo.datastructures;

public class ListaEncadeada {

    private NoLista inicio;

    public ListaEncadeada() {
        this.inicio = null;
    }

    // Adiciona a pista no fim da lista
    public void inserirPista(String id) {
        // TODO: criar um NoLista com o id e encadear no final
        throw new UnsupportedOperationException("inserirPista() não implementado ainda");
    }

    // Retorna true se a pista com esse id já foi coletada
    public boolean contemPista(String id) {
        // TODO: percorrer a lista a partir de 'inicio' e comparar ids
        //
        // ATENÇÃO — RISCO DE BUG: não use == para comparar Strings em Java.
        // Use atual.idPista.equals(id), não atual.idPista == id.
        // O == compara referência de objeto — pode retornar false mesmo quando os
        // valores são idênticos, e o bug não aparece nenhuma mensagem de erro.
        throw new UnsupportedOperationException("contemPista() não implementado ainda");
    }

    // Retorna todas as pistas coletadas como array (usado pelo Jogo antes de reiniciar)
    public String[] toArray() {
        // TODO: primeiro percorrer a lista contando os nós para saber o tamanho do array,
        //       depois percorrer de novo copiando os ids para o array
        //       (Java não permite criar array com tamanho desconhecido em tempo de compilação)
        throw new UnsupportedOperationException("toArray() não implementado ainda");
    }

    // Imprime no formato: [faca] -> [impressao_digital] -> FIM
    public void imprimirHistorico() {
        // TODO: percorrer a lista e imprimir cada idPista no formato acima
        throw new UnsupportedOperationException("imprimirHistorico() não implementado ainda");
    }
}
