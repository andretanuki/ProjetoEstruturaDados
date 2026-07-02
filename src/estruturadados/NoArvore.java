package estruturadados;

import engine.Pista;
import java.util.ArrayList;
import java.util.List;

class NoArvore {

    Pista pista;
    List<NoArvore> filhos;

    NoArvore(Pista pista) {
        this.pista = pista;
        this.filhos = new ArrayList<>();
    }
}
