package br.com.investigativo.engine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    // Rotas de sessões ANTERIORES do jogador (cache: o Jogo passa o
    // todosCaminhos cumulativo — sem isto, as rotas da sessão contariam 2x).
    private String jogadorCache;
    private List<String> rotasAnteriores;

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

    // 'tentativas' mantido por compatibilidade; o total gravado deriva das rotas.
    public void salvar(String nomeJogador, int tentativas, List<String[]> caminhos, boolean venceu) {
        if (!nomeJogador.equals(jogadorCache)) {
            jogadorCache = nomeJogador;
            rotasAnteriores = extrairRotas(nomeJogador);
        }

        List<String> rotas = new ArrayList<>(rotasAnteriores);
        for (String[] caminho : caminhos) {
            StringBuilder rota = new StringBuilder();
            for (String pista : caminho) {
                rota.append("[").append(pista).append("] -> ");
            }
            rota.append("FIM");
            rotas.add(rota.toString());
        }

        String blocosDosOutros = lerBlocosExceto(nomeJogador);

        // Reescrita intencional do arquivo inteiro (blocos alheios já lidos acima).
        try (FileWriter fw = new FileWriter(caminhoArquivo);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.print(blocosDosOutros);
            pw.println(MARCADOR + nomeJogador);
            pw.println("Tentativas: " + rotas.size());
            pw.println("Último Resultado: " + (venceu ? "Caso Solucionado" : "Investigação Mal Sucedida"));
            for (int i = 0; i < rotas.size(); i++) {
                pw.println("  Caminho " + (i + 1) + ": " + rotas.get(i));
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

    // Extrai só as rotas ("[a] -> [b] -> FIM") do bloco do jogador, sem o
    // prefixo "  Caminho N: " — elas são renumeradas na regravação.
    // Lista vazia se o jogador ainda não tem bloco no arquivo.
    private List<String> extrairRotas(String nomeJogador) {
        List<String> rotas = new ArrayList<>();
        for (String linha : carregarHistorico(nomeJogador).split("\n")) {
            if (linha.startsWith("  Caminho ")) {
                int doisPontos = linha.indexOf(": ");
                if (doisPontos >= 0) {
                    rotas.add(linha.substring(doisPontos + 2));
                }
            }
        }
        return rotas;
    }

    // Devolve os caminhos de sessões anteriores do jogador como List<String[]>,
    // para que o Jogo possa pré-popular o mapa de cores com o histórico completo.
    // Cada String[] contém os ids das pistas coletadas naquele caminho, sem "FIM".
    // Exemplo: "[cracha] -> [camera] -> FIM" -> {"cracha", "camera"}
    public List<String[]> carregarCaminhos(String nomeJogador) {
        List<String[]> caminhos = new ArrayList<>();
        for (String rota : extrairRotas(nomeJogador)) {
            String[] partes = rota.split(" -> ");
            List<String> ids = new ArrayList<>();
            for (String parte : partes) {
                if (parte.startsWith("[") && parte.endsWith("]")) {
                    ids.add(parte.substring(1, parte.length() - 1));
                }
            }
            if (!ids.isEmpty()) {
                caminhos.add(ids.toArray(new String[0]));
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
