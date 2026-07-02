import engine.Jogo;
import engine.Terminal;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

// MAIN - liga tudo: cria o Terminal (suporta teclado ou script) e inicia o Jogo.
public class Main {

    // Decide entre teclado e script, monta o Terminal e dá a partida no Jogo.
    public static void main(String[] args) {

        //Esse Bloco resolve um problema que estavamos tendo de formatação no terminal
        //Com a quantidade de texto massiva com caracteres especiais de Roteiro.java
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            System.setErr(new PrintStream(System.err, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }

        // os parametros -c/--color ligam cores e emojis (nem todo terminal suporta);
        // qualquer outro argumento é o arquivo de script usado no lugar do teclado
        String arquivoInput = null;
        for (String arg : args) {
            if (arg.equals("-c") || arg.equals("--color")) {
                Terminal.formatacaoAtiva = true;
            } else {
                arquivoInput = arg;
            }
        }
        Terminal terminal = new Terminal(arquivoInput);
        Jogo jogo = new Jogo(terminal);
        jogo.iniciar();
    }
}
