import engine.Jogo;
import engine.Terminal;

// MAIN - liga tudo: cria o Terminal (suporta teclado ou script) e inicia o Jogo.
public class Main {

    // Decide entre teclado e script, monta o Terminal e dá a partida no Jogo.
    public static void main(String[] args) {

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
