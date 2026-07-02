package estruturadados;

// NÓ DA LISTA - guarda o id de uma pista coletada e aponta para o próximo.
class NoLista {

    String idPista;
    NoLista proximo;

    NoLista(String idPista) {
        this.idPista = idPista;
        this.proximo = null;
    }
}
