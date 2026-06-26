package br.com.investigativo;

import br.com.investigativo.engine.Jogo;
import br.com.investigativo.engine.Terminal;

public class Main {

    public static void main(String[] args) {
        // Se um arquivo de script for passado como argumento, usa ele no lugar do teclado
        // Exemplo: java br.com.investigativo.Main test_inputs/vitoria.txt
        String arquivoInput = (args.length > 0) ? args[0] : null;

        Terminal terminal = new Terminal(arquivoInput);
        Jogo jogo = new Jogo(terminal);
        jogo.iniciar();
    }
}
