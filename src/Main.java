import engine.Jogo;
import engine.Terminal;

// MAIN - liga tudo: cria o Terminal (suporta teclado ou script) e inicia o Jogo.
public class Main {

    // Decide entre teclado e script, monta o Terminal e dá a partida no Jogo.
    public static void main(String[] args) {

        // os parametros -e/--emoji ligam os emojis (nem todo terminal desenha);
        // qualquer outro argumento é o arquivo de script usado no lugar do teclado
        String arquivoInput = null;
        for (String arg : args) {
            if (arg.equals("-e") || arg.equals("--emoji")) {
                Terminal.emojisAtivos = true;
            } else {
                arquivoInput = arg;
            }
        }
        Terminal terminal = new Terminal(arquivoInput);
        Jogo jogo = new Jogo(terminal);
        jogo.iniciar();
    }
}
