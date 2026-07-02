package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

// TERMINAL — toda a entrada e saída de tela passa por aqui: teclado ou
// arquivo de script (test_inputs/), pausas e limpeza de tela.
public class Terminal {

    private Scanner scanner;
    private BufferedReader leitorArquivo;

    // Com arquivo: entradas vêm do script (modo teste); sem: vêm do teclado.
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

    // Lê a próxima entrada — do script (ecoando na tela) ou do teclado.
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
        }
        return scanner.nextLine();
    }

    // Imprime uma linha na tela.
    public void exibir(String texto) {
        System.out.println(texto);
    }

    // Limpa a tela (código ANSI). No modo script não faz nada, para não
    // poluir a saída capturada pelos testes.
    public void limparTela() {
        if (leitorArquivo != null) {
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

    // Tela de identificação: exibe o cabeçalho e lê o nome do detetive.
    public String loginUsuario() {
        exibir("=======================================");
        exibir("       SISTEMA DE INVESTIGAÇÃO         ");
        exibir("=======================================");
        exibir("Por favor, identifique-se:");
        return lerEntrada();
    }
}