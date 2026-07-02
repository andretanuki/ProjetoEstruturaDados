

import engine.Jogo;
import engine.Terminal;

// MAIN — liga tudo: cria o Terminal (teclado ou script) e inicia o Jogo.
public class Main {

    public static void main(String[] args) {
        // Se um arquivo de script for passado como argumento, usa ele no lugar do teclado
        String arquivoInput = (args.length > 0) ? args[0] : null;
        Terminal terminal = new Terminal(arquivoInput);
        Jogo jogo = new Jogo(terminal);
        jogo.iniciar();
    }
}
