package br.com.investigativo.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Terminal {

    private Scanner scanner;
    private BufferedReader leitorArquivo;

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

    public void exibir(String texto) {
        System.out.println(texto);
    }

    public String loginUsuario() {
        exibir("=======================================");
        exibir("       SISTEMA DE INVESTIGAÇÃO         ");
        exibir("=======================================");
        exibir("Por favor, identifique-se:");
        return lerEntrada();
    }
}