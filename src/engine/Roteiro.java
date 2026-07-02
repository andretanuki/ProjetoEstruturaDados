package engine;

import estruturadados.ListaEncadeada;

// ROTEIRO — todo o CONTEÚDO do caso num lugar só: constantes, a tabela de
// pistas, as listas por cena, os textos das cenas e dos desfechos. Só dados —
// nenhuma regra de jogo vive aqui.
public class Roteiro {

    public static final String SEPARADOR_CENA =
        "  ════════════════════════════════════════════════════════════════════";
    public static final String MOLDURA_TOP =
        "  .----------------------------- ☎ TELEFONE -----------------------------.";
    public static final String MOLDURA_BOT =
        "  '----------------------------------------------------------------------'";
    
    // Caminho da vitória: PRE_REQUISITOS na ordem, fechando com PISTA_FINAL.
    public static final String PISTA_FINAL = "celular_esquecido";
    public static final String[] PRE_REQUISITOS = {"cracha", "camera"};
    
    // Dupla que, coletada junto com a vitória, concede a badge de excelência.
    public static final String[] AUXILIARES = {"registro_saida", "testemunho_zelador"};
    
    // Netas das trilhas malucas: tê-las no histórico define o desfecho alternativo.
    public static final String NETA_ABDUCAO = "luz_estranha";
    public static final String NETA_LOUCURA = "mural_conspiracao";

    // Códigos de desfecho
    public static final int DESFECHO_DERROTA = 0;
    public static final int DESFECHO_VITORIA = 1;
    public static final int DESFECHO_ABDUCAO = 2;
    public static final int DESFECHO_LOUCURA = 3;

    // O desfecho é uma função da última pista do caminho: as pistas de
    // desfecho (celular e netas malucas) só podem ser a 5ª coleta.
    public static int desfechoDe(String ultimaPista) {
        if (PISTA_FINAL.equals(ultimaPista))  return DESFECHO_VITORIA;
        if (NETA_ABDUCAO.equals(ultimaPista)) return DESFECHO_ABDUCAO;
        if (NETA_LOUCURA.equals(ultimaPista)) return DESFECHO_LOUCURA;
        return DESFECHO_DERROTA;
    }

    // ===== TABELA DO GABARITO: { pai, id, título, descrição[, cor][, símbolo] } =====
    // Colunas 5 e 6 são opcionais: cor "0" comum/azul (padrão), "1" importante/verde,
    // "2" excelência/amarelo; símbolo é anexado ao título no mapa quando coletada.
    public static final String[][] PISTAS = {
        {null, "cracha", "Crachá de Acesso", "O crachá do Dr. Almeida está caído perto da porta, e a leitura registrou uma entrada às 23h40 — bem depois de o prédio ter sido esvaziado. O que será que ele veio fazer tão tarde? Alguém viu alguma coisa?", "1"},
        {"cracha", "camera", "Câmera de Segurança", "Puxando as imagens do horário do crachá, a câmera do corredor mostra o Dr. Almeida entrando sozinho, tenso, olhando para trás. Ninguém o forçou — ele voltou por vontade própria. Num detalhe curioso, ele larga algo sobre a mesa antes de sair pela última vez. Valeria procurar o que ficou ali.", "1"},
        {"camera", PISTA_FINAL, "Revirar Tudo", "Sem cerimônia, você revira cada gaveta, pasta e bolso da sala — e é aí que ela aparece, quase escondida sobre a mesa: o próprio CELULAR do Dr. Almeida, largado de propósito para não ser rastreado. Nas mensagens não enviadas, ele planeja o sumiço e a nova vida com a pesquisa no bolso. Não houve sequestro — ele forjou tudo. CASO RESOLVIDO!", "1", "★"},
        {"camera", "registro_saida", "Registro de Saída", "O livro da portaria confirma o Dr. Almeida saindo às 00h15 com uma caixa lacrada de amostras. Não muda a conclusão, mas prova no papel que ele saiu por conta própria levando o material.", "2"},
        {"registro_saida", "extrato_bancario", "Extrato Bancário", "No extrato, um gasto salta aos olhos: uma passagem só de ida para o exterior, comprada em dinheiro semanas antes do sumiço. Ninguém que planeja voltar paga assim. Não é a prova final, mas é o retrato de uma fuga ensaiada com frieza — e um belo troféu para quem quer fechar o caso com chave de ouro."},
        {"camera", "testemunho_zelador", "Testemunho do Zelador", "O zelador o viu sair apressado com uma caixa, murmurando 'não posso mais ficar aqui'. Não prova nada sozinho, mas dá cor à fuga: ele parecia aliviado, não coagido.", "2"},
        {null, "janela_forcada", "Janela Forçada", "A janela fica no 4º andar, sem sacada nem escada: ninguém entrou por aqui. Só um vidro velho que cedeu — a não ser por uma marca de queimadura estranha num dos cacos, que ninguém soube explicar.", "1"},
        {"janela_forcada", "vidro_quebrado", "Estilhaços de Vidro", "A perícia diz que o vidro trincou sozinho — mas a marca de queimadura é perfeitamente radial, como se algo incandescente tivesse pairado rente à janela. E há um círculo de grama chamuscada bem embaixo, no gramado. A física não fecha aqui.", "1"},
        {"vidro_quebrado", NETA_ABDUCAO, "Luz Estranha no Estacionamento", "Você segue a trilha de queimaduras até o estacionamento e encontra um círculo chamuscado perfeito no asfalto. Ao erguer os olhos, um facho de luz te envolve...", "1", "🛸"},
        {null, "copo_cafe", "Copo de Café Abandonado", "O copo de café era da faxineira, que confirma tê-lo esquecido ali. Nada a ver com o caso — embora as manchas secas no fundo formem um desenho curiosamente simétrico, quase proposital.", "1"},
        {"copo_cafe", "bilhete_manchado", "Bilhete Manchado de Café", "O número é de uma pizzaria — mas as manchas de café desenham um padrão que parece... um mapa? E, sozinho no corredor, você jura ter ouvido um sussurro dizer o nome do Dr. Almeida. Você já não tem tanta certeza de que é imaginação.", "1"},
        {"bilhete_manchado", NETA_LOUCURA, "Mural da Conspiração", "As mensagens que só você enxerga te levam a forrar a parede inteira da sala de provas com fotos e barbante vermelho, ligando o caso a coisas cada vez mais absurdas...", "1", "🌀"},
        {null, "luvas_latex", "Luvas de Látex Descartadas", "As luvas de látex na lixeira são idênticas às que o laboratório usa aos montes todo dia. Perfeitamente comuns aqui — não dizem nada."},
        {null, "gaveta", "Gaveta da Mesa", "Você abre a gaveta — e, no fim das contas, devemos seguir os conselhos dos mais velhos. Lá dentro só há coisas pessoais do Dr. Almeida: um cartão de Dia das Mães ainda por enviar, algumas fotos de família, um chaveiro velho. Será que ele era um filhinho da mamãe? Mas não vem ao caso!"},
        {null, "exame_pericial", "Exame Pericial da Sala", "O laudo pericial da sala não achou digitais estranhas nem sinais de luta. Tudo aponta para uma saída tranquila — nada de arrombamento."},
        {null, "foto_corredor", "Foto do Corredor", "Uma foto antiga do corredor pregada no mural: era só decoração institucional, dessas de aniversário do departamento. Irrelevante."},
        {null, "agenda_mesa", "Agenda sobre a Mesa", "A agenda de mesa está aberta num compromisso banal: 'reunião de colegiado, 14h'. Nada de anormal nas anotações."},
        {null, "email_ameaca", "E-mail de Ameaça", "O 'e-mail de ameaça' era spam automático de um golpe conhecido, disparado para centenas de pessoas na mesma noite. Nem pessoal nem real. Descartado."},
        {null, "recibo_taxi", "Recibo de Táxi", "O recibo de táxi é de duas semanas atrás e está no nome de outro professor. Entrou na pilha por engano. Irrelevante."},
        {null, "jornal_velho", "Jornal Velho", "Um jornal amarelado largado sobre o arquivo, com notícias de meses atrás. Só serventia de papel de embrulho. Nada a ver com o caso."},
        {null, "cartao_visita", "Cartão de Visita", "Um cartão de visita de um representante de material de laboratório. Contato comercial de rotina — sem relevância."},
        {null, "contrato_concorrente", "Contrato com o Concorrente", "O 'contrato' com o concorrente era só uma proposta de parceria pública, protocolada e aprovada pela reitoria meses atrás. Legítimo e sem segredo. Descartado."},
        {null, "historico_ligacoes", "Histórico de Ligações", "As ligações repetidas eram todas para o consultório do dentista, remarcando uma consulta. Rotina pessoal. Irrelevante."},
        {null, "turno_seguranca", "Escala do Turno de Segurança", "O turno de segurança daquela noite estava normal, todos presentes, sem ocorrências além da já conhecida. Não acrescenta nada."},
        {null, "relato_vizinho", "Relato do Vizinho", "Um vizinho do laboratório reclamou de barulho, mas era da obra do prédio ao lado, no horário comercial. Sem ligação com o sumiço."},
        {null, "endereco_secreto", "Endereço no Contrato", "O 'endereço secreto' era o do depósito oficial da universidade, que consta em dezenas de documentos públicos. Nada de secreto. Beco sem saída."},
        {null, "confissao_gravada", "Confissão Gravada", "A 'confissão' gravada era um trecho de um podcast de crime que o dono do celular ouvia no carro. Ficção. Descartada."},
        {null, "mapa_local", "Mapa Rabiscado", "O mapa rabiscado era o trajeto de corrida matinal de um funcionário, com horários de pace anotados. Nada a ver com o caso."},
        {null, "depoimento_familia", "Depoimento da Família", "A família relata tensão nos últimos dias, mas não sabe de nada concreto. A mamãe do Dr. Almeida, entre lágrimas, faz questão de dizer que ama muito ele. Comovente — mas sem qualquer informação que aponte um rumo."}
    };

    // Quais pistas cada cena oferece (o menu real é esta lista filtrada pela
    // árvore); pistas-chave se repetem em cenas seguintes até serem coletadas.
    public static final String[][] PISTAS_POR_CENA = {
        {"cracha", "janela_forcada", "copo_cafe", "luvas_latex"},
        {"gaveta", "exame_pericial", "foto_corredor", "agenda_mesa", "cracha", "camera"},
        {"email_ameaca", "recibo_taxi", "jornal_velho", "cartao_visita", "cracha", "camera", "registro_saida", "testemunho_zelador", "janela_forcada", "copo_cafe"},
        {"contrato_concorrente", "historico_ligacoes", "turno_seguranca", "relato_vizinho", "camera", "extrato_bancario", "vidro_quebrado", "bilhete_manchado", "registro_saida", "testemunho_zelador"},
        {"endereco_secreto", "confissao_gravada", "mapa_local", "depoimento_familia", PISTA_FINAL, NETA_ABDUCAO, NETA_LOUCURA}
    };

    // Narrativa fixa das 5 cenas; cada texto menciona, com destaque
    // [ENTRE COLCHETES], as pistas selecionáveis daquela cena.
    public static final String[] TEXTOS_CENAS = {
            tituloCentralizado("CENA 1: A Cena do Desaparecimento") + "\n\n"
                + MOLDURA_TOP + "\n"
                + "   CENTRAL: \"Detetive, o Dr. Almeida, o bioquímico, sumiu ontem à noite.\n"
                + "            A sala tá isolada. Entra e olha tudo.\"\n"
                + MOLDURA_BOT + "\n\n"
                + "A sala do laboratório está exatamente como a segurança a deixou. Perto da\n"
                + "porta, o [CRACHÁ] do Dr. Almeida está caído, com a última leitura ainda\n"
                + "registrada no sistema. Nos fundos, uma [JANELA FORÇADA] chama atenção pelo\n"
                + "estrago. Sobre a recepção, um [COPO DE CAFÉ] esquecido ainda está morno. E na\n"
                + "lixeira, um par de [LUVAS DE LÁTEX] jogado fora do padrão da equipe. Por onde\n"
                + "você começa?",
            tituloCentralizado("CENA 2: A Sala de Segurança") + "\n\n"
                + MOLDURA_TOP + "\n"
                + "   MÃE: \"Filho, você tá comendo direito? E não vai é ficar bisbilhotando\n"
                + "        GAVETA dos outros, hein!\"\n"
                + MOLDURA_BOT + "\n\n"
                + "Você ri, promete almoçar, e entra na sala de monitoramento com a cabeça no\n"
                + "lugar. A [CÂMERA] do corredor guarda as imagens da noite, prontas para serem\n"
                + "cruzadas com o horário do [CRACHÁ] — que, aliás, se você ainda não examinou de\n"
                + "perto, continua ali esperando. Sobre a bancada, uma [GAVETA] entreaberta te\n"
                + "encara (e, veja só, faz você se lembrar da sua mãe), o [EXAME PERICIAL] da\n"
                + "sala, uma velha [FOTO DO CORREDOR] no mural e a [AGENDA] do professor. Tudo\n"
                + "aqui parece sóbrio e concreto. O que você examina?",
            tituloCentralizado("CENA 3: O Rastro Documental") + "\n\n"
                + MOLDURA_TOP + "\n"
                + "   CHEFE (Delegado Canastrão): \"Ê detetive! Já verificou aquele [CRACHÁ] e\n"
                + "        puxou a [CÂMERA]?! Tem caroço nesse angu, quero isso resolvido ONTEM!\"\n"
                + MOLDURA_BOT + "\n\n"
                + "Ele desliga sem esperar resposta. Você volta à papelada: o [REGISTRO DE\n"
                + "SAÍDA] da noite, o [TESTEMUNHO DO ZELADOR], um [E-MAIL DE AMEAÇA], um [RECIBO\n"
                + "DE TÁXI], um [JORNAL VELHO] e um [CARTÃO DE VISITA]. Mas seus olhos teimam em\n"
                + "voltar para aquela [JANELA FORÇADA] e aquele [COPO DE CAFÉ] — se você ainda não\n"
                + "os examinou, eles continuam ali, e você sente vibrações estranhas e calafrios\n"
                + "só de pensar neles. Parecem sussurrar algo que a razão não explica. O que\n"
                + "merece atenção? ...Sou um investigador ou um caçador de fantasmas, afinal?",
            tituloCentralizado("CENA 4: O Confronto de Pistas") + "\n\n"
                + MOLDURA_TOP + "\n"
                + "   TELEMARKETING (CamerAção Ofertas): \"Boa tarde! O senhor não vai querer\n"
                + "        perder nossa câmera noturna 4K: enxerga TUDO no escuro, até o que as\n"
                + "        pessoas andam aprontando às 23h41 da madrugada!\"\n"
                + MOLDURA_BOT + "\n\n"
                + "Você quase desliga na cara — mas o horário citado te arrepia (e lembra que a\n"
                + "[CÂMERA] do prédio ainda pode ter mais a mostrar). Voltando à mesa, um [EXTRATO\n"
                + "BANCÁRIO] parece ter inconsistências bem marcantes. Ao lado, um [CONTRATO COM O\n"
                + "CONCORRENTE], um [HISTÓRICO DE LIGAÇÕES], a [ESCALA DO TURNO DE SEGURANÇA] e um\n"
                + "[RELATO DO VIZINHO] esperam. Já os [ESTILHAÇOS DE VIDRO] da janela e o [BILHETE\n"
                + "MANCHADO] do café são inexplicáveis — mas será que precisamos explicar e seguir\n"
                + "o nosso cérebro? Ou seguir o nosso coração? Em qual você foca?",
            tituloCentralizado("CENA 5: O Desfecho") + "\n\n"
                + MOLDURA_TOP + "\n"
                + "   CHEFE (furioso): \"DETETIVE! A imprensa tá na porta, o reitor no meu pé, e\n"
                + "        eu SEM RESPOSTA?! Vira essa sala do avesso, mas me traz uma CONCLUSÃO\n"
                + "        AGORA!\"\n"
                + MOLDURA_BOT + "\n\n"
                + "Você desliga suando frio. Sobre a mesa há pontas soltas que não levam a nada\n"
                + "— um [ENDEREÇO] de contrato, uma [CONFISSÃO GRAVADA], um [MAPA RABISCADO], o\n"
                + "[DEPOIMENTO DA FAMÍLIA]. Você lembra da sua mãe, e da vergonha que passou com\n"
                + "aquela gaveta — mas que se dane o decoro: sua vontade é [REVIRAR TUDO] atrás\n"
                + "dos segredos do Sr. Almeida. Mas será que os caminhos tradicionais realmente\n"
                + "resolvem o caso? De onde vêm aquelas [LUZ ESTRANHA]? Às vezes as ideias que\n"
                + "desafiam o senso comum acertam em cheio — e se você montar o seu [MURAL DA\n"
                + "CONSPIRAÇÃO]? Qual é a resposta certa?"
    };

    // Linha de resultado exibida no relatório.
    public static String textoDesfecho(int desfecho, boolean venceuComExcelencia) {
        switch (desfecho) {
            case DESFECHO_VITORIA:
                return venceuComExcelencia
                    ? "CASO RESOLVIDO COM EXCELÊNCIA — nenhuma pista escapou!"
                    : "CASO RESOLVIDO — o Dr. Almeida forjou o próprio sumiço.";
            case DESFECHO_ABDUCAO:
                return "FINAL ALTERNATIVO — Você foi ABDUZIDO investigando o inexplicável!";
            case DESFECHO_LOUCURA:
                return "FINAL ALTERNATIVO — Você MERGULHOU NA LOUCURA da conspiração!";
            default:
                return "CASO NÃO RESOLVIDO — as pistas certas escaparam desta vez.";
        }
    }

    // Centraliza um título "=== x ===" em relação à largura da moldura do telefone.
    private static String tituloCentralizado(String titulo) {
        String texto = "=== " + titulo + " ===";
        int espacos = Math.max(0, (MOLDURA_TOP.length() - texto.length()) / 2);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < espacos; i++) {
            sb.append(' ');
        }
        return sb.append(texto).toString();
    }

    // Epílogo narrativo de cada desfecho, no mesmo formato de bloco das cenas.
    public static String epilogoDesfecho(int desfecho, boolean venceuComExcelencia, ListaEncadeada historico) {
        switch (desfecho) {
            case DESFECHO_VITORIA:
                if (venceuComExcelencia) {
                    return tituloCentralizado("EPÍLOGO: CASO ENCERRADO COM EXCELÊNCIA") + "\n\n"
                        + MOLDURA_TOP + "\n"
                        + "   CHEFE (solene, pigarreando): \"Detetive... o reitor ligou. A imprensa\n"
                        + "        ligou. Minha MÃE ligou. Todos querem saber quem fechou o caso\n"
                        + "        Almeida sem deixar UMA ponta solta. Amanhã: medalha e foto.\"\n"
                        + MOLDURA_BOT + "\n\n"
                        + "Não foi só o celular: o registro de saída no papel e a prosa com o zelador\n"
                        + "costuraram um dossiê que o promotor chamou de \"obra de arte\". Cada passo da\n"
                        + "fuga forjada do Dr. Almeida está documentado, datado e testemunhado — não\n"
                        + "sobrou nem uma vírgula para a defesa. Sua foto agora está pendurada no mural\n"
                        + "da delegacia (o do orgulho, não o da conspiração) — e o zelador conta para\n"
                        + "quem quiser ouvir que sempre soube que você ia longe.";
                }
                return tituloCentralizado("EPÍLOGO: CASO ENCERRADO") + "\n\n"
                    + MOLDURA_TOP + "\n"
                    + "   CHEFE (eufórico): \"Detetive! A federal interceptou o Dr. Almeida no\n"
                    + "        aeroporto — bigode falso, passagem só de ida e a pesquisa na mala.\n"
                    + "        Confessou tudo em dez minutos. BELO trabalho!\"\n"
                    + MOLDURA_BOT + "\n\n"
                    + "O celular esquecido contou o resto: nas mensagens que nunca enviou, o\n"
                    + "Dr. Almeida ensaiava a despedida e a vida nova com a pesquisa no bolso.\n"
                    + "Não houve crime — houve encenação. E você a desmontou seguindo o rastro\n"
                    + "certo: o crachá, a câmera, e a coragem de revirar a sala quando todo mundo\n"
                    + "já tinha desistido. O caso vai para a estante dos resolvidos.";
            case DESFECHO_ABDUCAO:
                return tituloCentralizado("EPÍLOGO: VOO NOTURNO") + "\n\n"
                    + MOLDURA_TOP + "\n"
                    + "   CENTRAL (chiado): \"Detetive?... Detetive, na escuta?... Alô?...\n"
                    + "        ...registrando na ocorrência: paradeiro desconhecido. Última\n"
                    + "        posição, o estacionamento do laboratório.\"\n"
                    + MOLDURA_BOT + "\n\n"
                    + "Lá embaixo, a Terra vira uma bolinha azul na janela oval. Os seres de luz\n"
                    + "não falam português, mas apontam para o seu bloquinho de anotações com o\n"
                    + "que só pode ser admiração profissional. Na delegacia, seu relatório final —\n"
                    + "\"levado por um facho de luz\" — foi arquivado como licença médica. Mas você\n"
                    + "sabe a verdade. De todos os detetives do caso Almeida, você foi o único que\n"
                    + "olhou para CIMA. Parabéns: o universo aprova os curiosos.";
            case DESFECHO_LOUCURA:
                return tituloCentralizado("EPÍLOGO: O MURAL SABE") + "\n\n"
                    + MOLDURA_TOP + "\n"
                    + "   ENFERMEIRO (gentil): \"Visita pro senhor! E olha só: trouxemos barbante\n"
                    + "        novinho. Do vermelho. Daquele que o senhor gosta.\"\n"
                    + MOLDURA_BOT + "\n\n"
                    + "A equipe encontrou a sala de provas forrada de fotos, recortes e barbante\n"
                    + "vermelho — dois peritos entraram céticos e saíram \"estranhamente\n"
                    + "convencidos\". Na ala tranquila do sanatório, você segue conectando pontos\n"
                    + "que ninguém mais vê, feliz como nunca. E toda noite, da foto central do\n"
                    + "mural, o Dr. Almeida parece sorrir para você. Como quem agradece por\n"
                    + "alguém, enfim, ter entendido TUDO.";
            default:
                return tituloCentralizado("EPÍLOGO: ARQUIVO MORTO") + "\n\n"
                    + MOLDURA_TOP + "\n"
                    + "   CHEFE (cansado): \"Sem prova, sem suspeito, sem história pra imprensa.\n"
                    + "        O caso Almeida vai pro arquivo morto, detetive. Vai pra casa.\n"
                    + "        Descansa. Amanhã a gente conversa.\"\n"
                    + MOLDURA_BOT + "\n\n"
                    + feedbackDerrota(historico) + "\n\n"
                    + "O laboratório continua lá, lacrado, do jeitinho que você deixou — e toda\n"
                    + "investigação merece uma segunda chance.";
        }
    }

    // Feedback da derrota: aponta o primeiro elo da corrente da vitória
    // (crachá -> câmera -> celular) que faltou no histórico.
    private static String feedbackDerrota(ListaEncadeada historico) {
        if (!historico.contemPista(PRE_REQUISITOS[0])) {
            return "A perícia foi direta no arquivamento: ninguém examinou o crachá caído\n"
                 + "junto à porta — e era ele que abria a linha do tempo daquela noite. Sem o\n"
                 + "ponto de partida, o resto virou papel solto.";
        }
        if (!historico.contemPista(PRE_REQUISITOS[1])) {
            return "Você tinha o horário do crachá na mão, mas nunca o cruzou com as imagens\n"
                 + "da câmera do corredor. A noite inteira do Dr. Almeida estava gravada — e\n"
                 + "ninguém apertou o play.";
        }
        return "Você cruzou o crachá com a câmera e parou a um passo da verdade: faltou\n"
             + "revirar a sala no fim. O celular do Dr. Almeida ainda está lá, sobre a\n"
             + "mesa, esperando um detetive sem cerimônia.";
    }
}
