package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

// TERMINAL - toda a entrada e saída de tela passa por aqui: teclado ou
// arquivo de script (test_inputs/), pausas e limpeza de tela.
public class Terminal {

    // Cores e emojis são ativaveis pela flag -c/--color
    // Para lidar com incompatibilidade de terminais
    public static boolean formatacaoAtiva = false;

    private Scanner scanner;
    private BufferedReader leitorArquivo;

    // Com arquivo: entradas vêm do script (modo teste); sem: vêm do teclado via Scanner.
    public Terminal(String arquivoInput) {
        if (arquivoInput != null) {
            try {
                leitorArquivo = new BufferedReader(new FileReader(arquivoInput));
            } catch (IOException e) {
                System.err.println("Erro ao abrir arquivo de script: " + e.getMessage());
                System.exit(1);
            }
        } else {
            scanner = new Scanner(System.in);
        }
    }

    // Lê a próxima entrada - Linha do Arquivo ou Scanner.nextLine
    // Informa se o script acabar antes do fim do jogo
    public String lerEntrada() {
        if (leitorArquivo != null) {
            try {
                String linha = leitorArquivo.readLine();
                if (linha != null) {
                    System.out.println(linha);
                    return linha;
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler do arquivo: " + e.getMessage());
            }
            System.err.println("Script de entrada terminou antes do fim do jogo.");
            System.exit(1);
        }
        return scanner.nextLine();
    }

    // Imprime uma linha na tela. Alias para PrintLn Basicamente
    public void exibir(String texto) {
        System.out.println(formatacaoAtiva ? texto : semEmojis(texto));
    }

    // Sem formatação, troca os símbolos decorativos por equivalentes ASCII
    // (nem toda fonte de terminal desenha emoji).
    private String semEmojis(String texto) {
        return texto.replace("☎", "#")
                    .replace("★", "*")
                    .replace("✗", "x")
                    .replace("🛸", "(ovni)")
                    .replace("🌀", "(espiral)");
    }

    // Limpa a tela. No modo script não faz nada; sem formatação só separa
    // com uma linha em branco (o escape de limpeza também é código ANSI).
    public void limparTela() {
        if (leitorArquivo != null) {
            return;
        }
        if (!formatacaoAtiva) {
            System.out.println();
            return;
        }
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Pausa até o jogador pressionar ENTER e limpa a tela. No modo script
    // não faz nada: não consome linhas do arquivo de teste.
    public void aguardarEnterELimpar() {
        if (leitorArquivo != null) {
            return;
        }
        System.out.println("\n[ Pressione ENTER para continuar ]");
        scanner.nextLine();
        limparTela();
    }

    // Tela de identificação: Exibe o cabeçalho do Jogo e lê o nome do detetive.
    public String loginUsuario() {
        exibir("=======================================");
        exibir("       SISTEMA DE INVESTIGAÇÃO         ");
        exibir("=======================================");
        exibir("Por favor, identifique-se:");
        return lerEntrada();
    }
}
