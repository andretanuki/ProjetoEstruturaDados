package engine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import estruturadados.ListaEncadeada;

public class Persistencia {

    // Formato: UM bloco por detetive, com todas as rotas acumuladas
    // (decisões e detalhes em docs/Notas_de_Design.md, §8):
    //
    //   >>> DETETIVE: nome
    //   Tentativas: N
    //   Último Resultado: Caso Solucionado | Investigação Mal Sucedida
    //     Caminho 1: [a] -> [b] -> FIM
    //     ...
    //   ----------------------------------------
    private static final String MARCADOR = ">>> DETETIVE: ";
    private static final String SEPARADOR = "----------------------------------------";

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

    public void salvar(String nomeJogador, List<ListaEncadeada> todasTentativas, boolean venceu) {

        String blocosDosOutros = lerBlocosExceto(nomeJogador);

        // Reescrita intencional do arquivo inteiro (blocos alheios já lidos acima).
        try (FileWriter fw = new FileWriter(caminhoArquivo);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.print(blocosDosOutros);
            pw.println(MARCADOR + nomeJogador);
            pw.println("Tentativas: " + todasTentativas.size());
            pw.println("Último Resultado: " + (venceu ? "Caso Solucionado" : "Investigação Mal Sucedida"));
            for (int i = 0; i < todasTentativas.size(); i++) {
                pw.println("  Caminho " + (i + 1) + ": " + todasTentativas.get(i).formatarHistorico());
            }
            pw.println(SEPARADOR);

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

                if (linha.equals(MARCADOR + nomeJogador)) {
                    copiandoBloco = true;
                } else if (linha.startsWith(MARCADOR)) {
                    copiandoBloco = false;
                }

                if (copiandoBloco) {
                    historico.append(linha).append("\n");

                    if (linha.equals(SEPARADOR)) {
                        copiandoBloco = false;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar histórico: " + e.getMessage());
        }

        return historico.toString();
    }

    // Reconstrói as ListaEncadeada dos caminhos já salvos do jogador, lendo
    // as linhas "  Caminho N: [a] -> [b] -> FIM" do bloco dele no arquivo.
    // Lista vazia se o jogador ainda não tem bloco.
    public List<ListaEncadeada> carregarCaminhos(String nomeJogador) {
        List<ListaEncadeada> caminhos = new ArrayList<>();
        for (String linha : carregarHistorico(nomeJogador).split("\n")) {
            if (!linha.startsWith("  Caminho ")) continue;
            int doisPontos = linha.indexOf(": ");
            if (doisPontos < 0) continue;

            ListaEncadeada lista = new ListaEncadeada();
            for (String parte : linha.substring(doisPontos + 2).split(" -> ")) {
                if (parte.startsWith("[") && parte.endsWith("]")) {
                    lista.inserirPista(parte.substring(1, parte.length() - 1));
                }
            }
            if (lista.getUltimaPista() != null) {
                caminhos.add(lista);
            }
        }
        return caminhos;
    }

    // Conteúdo do arquivo SEM o bloco do jogador (preserva os outros detetives).
    private String lerBlocosExceto(String nomeJogador) {
        File arquivo = new File(caminhoArquivo);

        if (!arquivo.exists()) {
            return "";
        }

        StringBuilder outros = new StringBuilder();
        boolean pulandoBloco = false;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;

            while ((linha = br.readLine()) != null) {

                if (linha.equals(MARCADOR + nomeJogador)) {
                    pulandoBloco = true;
                } else if (linha.startsWith(MARCADOR)) {
                    pulandoBloco = false;
                }

                if (!pulandoBloco) {
                    outros.append(linha).append("\n");
                } else if (linha.equals(SEPARADOR)) {
                    pulandoBloco = false; // separador fecha o bloco pulado
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler dados existentes: " + e.getMessage());
        }

        return outros.toString();
    }
}
