package br.com.investigativo.engine;

import java.io.*;
import java.util.List;

public class Persistencia {

    private String caminhoArquivo;

    // Cria o arquivo e a pasta se não existirem
    public Persistencia(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
        // TODO: verificar se o diretório pai existe; se não, criar com mkdirs()
        //       verificar se o arquivo existe; se não, criar com createNewFile()
        //
        // ATENÇÃO — RISCO: FileWriter e BufferedReader lançam IOException obrigatoriamente.
        // Java não deixa ignorar — é preciso tratar com try/catch em todos os métodos que
        // acessam arquivo. Sugestão para não travar o desenvolvimento: implementar primeiro
        // salvar() apenas com System.out.println() para validar o fluxo completo do jogo,
        // e só depois adicionar a escrita real em arquivo.
    }

    // Grava o resultado da sessão no final do arquivo
    public void salvar(String nomeJogador, int tentativas, List<String[]> caminhos, boolean venceu) {
        // TODO: abrir o arquivo em modo append com new FileWriter(caminhoArquivo, true)
        //       gravar um bloco por sessão com: nome, data/hora, tentativas, caminhos e resultado
        //       fechar o FileWriter no bloco finally (ou usar try-with-resources)
        //
        // ATENÇÃO — RISCO: abrir o FileWriter sem o segundo argumento 'true' apaga o arquivo
        // a cada execução. Sempre usar new FileWriter(caminhoArquivo, true) para modo append.
        throw new UnsupportedOperationException("salvar() não implementado ainda");
    }

    // Lê o arquivo e retorna as partidas do jogador como texto formatado
    // Retorna string vazia se o jogador nunca jogou
    public String carregarHistorico(String nomeJogador) {
        // TODO: ler o arquivo linha a linha com BufferedReader e filtrar os blocos do nomeJogador
        //       retornar as linhas encontradas como uma única String formatada
        //       retornar "" se o arquivo não existir ou se o jogador não tiver entradas
        //
        // ATENÇÃO — RISCO: o arquivo pode não existir na primeira execução. Verificar com
        // new File(caminhoArquivo).exists() antes de abrir o BufferedReader para evitar
        // FileNotFoundException e retornar "" diretamente nesse caso.
        throw new UnsupportedOperationException("carregarHistorico() não implementado ainda");
    }
}
