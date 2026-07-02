package estruturadados;

import engine.Pista;
import java.util.ArrayList;
import java.util.List;

// NÓ DA ÁRVORE - guarda uma Pista e a lista de filhos (as pistas que ela desbloqueia).
class NoArvore {

    Pista pista;
    List<NoArvore> filhos;

    NoArvore(Pista pista) {
        this.pista = pista;
        this.filhos = new ArrayList<>();
    }
}
