package engine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import estruturadados.ListaEncadeada;

// PERSISTENCIA - lembra os jogadores entre sessões: um bloco por detetive
// num arquivo texto, com todas as rotas acumuladas.
public class Persistencia {

    //UM bloco por detetive, com todas as rotas acumuladas
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

    // Garante que o diretório e o arquivo de dados existem.
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

    // Regrava o arquivo inteiro: blocos dos outros detetives +
    // bloco atualizado do jogador com todas as tentativas.
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

    // Bloco do jogador, como texto pronto para exibir ("" se não existe).
    public String carregarHistorico(String nomeJogador) {
        return separarBlocos(nomeJogador)[0];
    }

    // Conteúdo do arquivo SEM o bloco do jogador (preserva os outros detetives).
    private String lerBlocosExceto(String nomeJogador) {
        return separarBlocos(nomeJogador)[1];
    }

    // Varre o arquivo UMA vez e separa: [0] = bloco do jogador, [1] = o resto.
    private String[] separarBlocos(String nomeJogador) {
        StringBuilder doJogador = new StringBuilder();
        StringBuilder dosOutros = new StringBuilder();
        File arquivo = new File(caminhoArquivo);

        if (arquivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
                boolean noBlocoDoJogador = false;
                String linha;
                while ((linha = br.readLine()) != null) {
                    if (linha.equals(MARCADOR + nomeJogador)) {
                        noBlocoDoJogador = true;
                    } else if (linha.startsWith(MARCADOR)) {
                        noBlocoDoJogador = false;
                    }

                    (noBlocoDoJogador ? doJogador : dosOutros).append(linha).append("\n");

                    if (noBlocoDoJogador && linha.equals(SEPARADOR)) {
                        noBlocoDoJogador = false; // separador fecha o bloco
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler o arquivo de partidas: " + e.getMessage());
            }
        }
        return new String[]{doJogador.toString(), dosOutros.toString()};
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

}
