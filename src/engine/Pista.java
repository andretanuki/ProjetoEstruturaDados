package engine;

// PISTA - o modelo de dado do jogo: id, textos e papel visual (cor/símbolo),
// com a regra de pintura ANSI embutida.
public class Pista {

    // Códigos ANSI do jogo - regra de pintura do jogo
    private static final String ANSI_RESET = "\033[0m";
    private static final String[] ANSI_CORES = {
        "\033[34m", // 0 = pista comum (azul)
        "\033[32m", // 1 = pista importante (verde)
        "\033[33m"  // 2 = pista excelência (amarelo)
    };

    public String id; // versao nao formatada do nome (Ex: 'janela_quebrada')
    public String titulo;
    public String descricao;

    public int cor = 0; // 0=comum (azul), 1=importante (verde), 2=excelência (amarelo)
    public String simbolo = "";

    // Cria a pista com id, título e descrição (papel visual default: comum).
    public Pista(String id, String titulo, String descricao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    // Devolve o texto pintado com a cor do papel desta pista.
    // Com a formatação desligada devolve o texto puro, sem códigos ANSI.
    public String pintar(String texto) {
        if (!Terminal.formatacaoAtiva) {
            return texto;
        }
        return ANSI_CORES[cor] + texto + ANSI_RESET;
    }
}
