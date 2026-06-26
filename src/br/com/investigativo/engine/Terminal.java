package br.com.investigativo.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Terminal {

    private Scanner scanner;
    private BufferedReader leitorArquivo;

    // Se arquivoInput for null, lê do teclado; senão, lê do arquivo linha a linha
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

    // Retorna a próxima linha — do teclado ou do arquivo, de forma transparente
    public String lerEntrada() {
        // TODO: se leitorArquivo != null, ler do arquivo; senão, ler do scanner
        throw new UnsupportedOperationException("lerEntrada() não implementado ainda");
    }

    // Imprime texto na tela
    public void exibir(String texto) {
        System.out.println(texto);
    }

    // Pede o nome do jogador e retorna como String
    public String loginUsuario() {
        // TODO: exibir mensagem pedindo o nome e chamar lerEntrada()
        throw new UnsupportedOperationException("loginUsuario() não implementado ainda");
    }
}
