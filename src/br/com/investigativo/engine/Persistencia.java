package br.com.investigativo.engine;

import java.io.*;
import java.util.List;

public class Persistencia {

    private String caminhoArquivo;

    public Persistencia(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
        File arquivo = new File(caminhoArquivo);
        
        try {
            File diretorioPai = arquivo.getParentFile();
            if (diretorioPai != null && !diretorioPai.exists()) {
                diretorioPai.mkdirs();
            }
            
            if (!arquivo.exists()) {
                arquivo.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar persistência: " + e.getMessage());
        }
    }

    public void salvar(String nomeJogador, int tentativas, List<String[]> caminhos, boolean venceu) {
        try (FileWriter fw = new FileWriter(caminhoArquivo, true);
             PrintWriter pw = new PrintWriter(fw)) {
             
            pw.println(">>> SESSÃO DE: " + nomeJogador);
            pw.println("Tentativas: " + tentativas);
            pw.println("Resultado Final: " + (venceu ? "Caso Solucionado" : "Investigação Mal Sucedida"));
            
            for (int i = 0; i < caminhos.size(); i++) {
                String[] caminho = caminhos.get(i);
                pw.print("  Caminho " + (i + 1) + ": ");
                for (String pista : caminho) {
                    pw.print("[" + pista + "] -> ");
                }
                pw.println("FIM");
            }
            pw.println("----------------------------------------");
            
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados no arquivo: " + e.getMessage());
        }
    }

    public String carregarHistorico(String nomeJogador) {
        File arquivo = new File(caminhoArquivo);
        
        if (!arquivo.exists()) {
            return ""; 
        }

        StringBuilder historico = new StringBuilder();
        boolean copiandoBloco = false;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            
            while ((linha = br.readLine()) != null) {
                
                if (linha.equals(">>> SESSÃO DE: " + nomeJogador)) {
                    copiandoBloco = true;
                } else if (linha.startsWith(">>> SESSÃO DE: ")) {
                    copiandoBloco = false;
                }

                if (copiandoBloco) {
                    historico.append(linha).append("\n");
                    
                    if (linha.equals("----------------------------------------")) {
                        copiandoBloco = false;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar histórico: " + e.getMessage());
        }

        return historico.toString();
    }
}