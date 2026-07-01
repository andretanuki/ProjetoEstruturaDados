package br.com.investigativo.engine;

import br.com.investigativo.datastructures.Arvore;
import br.com.investigativo.datastructures.ListaEncadeada;
import br.com.investigativo.model.Pista;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Jogo {

    private ListaEncadeada historico;
    private Arvore dependencias;
    private Terminal terminal;
    private Persistencia persistencia;
    private String nomeJogador;
    private int tentativas;
    private List<String[]> todosCaminhos;

    // Textos fixos das 5 cenas e as pistas "normais" que cada uma oferece
    // (não cumulativas — cada cena mostra só a sua própria lista).
    //
    // As pistas se dividem em três papéis:
    //
    //  (a) CAMINHO DA VITÓRIA — 2 pré-requisitos geridos pela Arvore
    //      ("cracha" -> "camera") mais a pista final "celular_esquecido"
    //      (a evidência que resolve o caso), que SÓ aparece na Cena 5 e só
    //      fica disponível quando cracha+camera já estão no histórico. As
    //      pistas-chave continuam aparecendo como opção nas cenas seguintes
    //      (transbordo) até serem coletadas — cracha até a C3, camera até a
    //      C4 — o que já coincide com o limite em que pegá-las ainda leva à
    //      vitória (sem precisar de cálculo dinâmico de viabilidade).
    //
    //  (b) AUXILIARES VERDADEIRAS — pistas reais que enriquecem a história
    //      mas NÃO são necessárias para vencer. Coletar todas elas concede a
    //      badge "Venceu com Excelência" no relatório final.
    //
    //  (c) DISTRAÇÕES — pistas cujo texto se auto-descarta (álibi,
    //      impossibilidade, irrelevância).
    private String[] textosCenas;
    private String[][] pistasPorCena;
    private static final String PISTA_FINAL = "celular_esquecido";
    // Pré-requisitos obrigatórios, na ordem do caminho (a final é liberada
    // quando todos estes estão no histórico). Caminho ENCURTADO para 2
    // pré-requisitos + a final (3 coletas no total) — permite vencer mesmo
    // pegando cracha só na Cena 3 (cracha C3 -> camera C4 -> final C5).
    private static final String[] PRE_REQUISITOS = {"cracha", "camera"};
    // Auxiliares opcionais que concedem a badge de excelência (não entram no
    // caminho obrigatório — registro_saida saiu dos pré-requisitos ao encurtar):
    private static final String[] AUXILIARES = {"registro_saida", "testemunho_zelador", "extrato_bancario"};

    // Finais malucos: cada trilha é pai -> filho -> neto na Arvore. Coletar
    // a 3ª pista (neta) só é possível gastando 3 escolhas na trilha, o que
    // torna matematicamente impossível ter completado a vitória séria (4
    // coletas) — por isso a neta no histórico já prova derrota no caso sério.
    // A neta de cada trilha SUBSTITUI uma distração da cena quando disponível
    // (ver rodarCenas()), preservando o piso de 4 sem inflar o menu.
    private static final String NETA_ABDUCAO = "luz_estranha";
    private static final String NETA_LOUCURA = "mural_conspiracao";

    public Jogo(Terminal terminal) {
        this.terminal = terminal;
        this.persistencia = new Persistencia("dados/partidas.txt");
        this.historico = new ListaEncadeada();
        this.dependencias = new Arvore();
        this.tentativas = 0;
        this.todosCaminhos = new ArrayList<>();
    }

    // Ponto de entrada: login, carrega histórico, monta gabarito, inicia loop
    public void iniciar() {
        nomeJogador = terminal.loginUsuario();

        String historicoAnterior = persistencia.carregarHistorico(nomeJogador);
        if (!historicoAnterior.isEmpty()) {
            terminal.exibir("\n=== Bem-vindo de volta, " + nomeJogador + "! ===");
            terminal.exibir("Suas partidas anteriores:");
            terminal.exibir(historicoAnterior);
        } else {
            terminal.exibir("\n=== Bem-vindo, Detetive " + nomeJogador + "! ===");
            terminal.exibir("Primeira vez jogando. Boa sorte!");
        }

        montarGabarito();
        rodarCenas();
    }

    // Monta o roteiro do caso: desaparecimento do pesquisador Dr. Almeida no
    // Laboratório de Bioquímica da universidade.
    //
    // O jogo tem 5 cenas de TEXTO FIXO — o roteiro não muda entre partidas.
    // O que muda é só quais pistas o jogador escolhe destacar em cada cena.
    //
    // Enredo: o caminho da vitória tem 3 coletas —
    // "cracha" -> "camera" -> "celular_esquecido" (a evidência final).
    // cracha e camera são nativos das Cenas 1 e 2, mas continuam aparecendo
    // como opção (transbordo) nas cenas seguintes até serem coletados, para
    // não deixar o jogo perdido sem o jogador perceber. O celular_esquecido
    // só fica disponível na Cena 5, quando cracha+camera já estão no
    // histórico.
    //
    // O "corte" (parar de oferecer uma pista-chave quando pegá-la já não
    // levaria à vitória) é resolvido pela DISTRIBUIÇÃO das listas por cena:
    // cracha transborda até a C3, camera até a C4 — exatamente o limite em
    // que ainda dá tempo de chegar ao celular na C5. Não há cálculo dinâmico.
    private void montarGabarito() {
        // ================================================================
        // ÁRVORE DE DEPENDÊNCIAS — todas as pistas são nós da Arvore.
        // As 3 REGRAS SUPREMAS (ver Instrucoes_Claude_UX.md):
        //  (1) PISO DE 4: cada cena tem >= 4 pistas filhas DIRETAS da raiz
        //      (distrações sem pré-requisito), garantindo menu nunca < 4.
        //  (2) TRANSBORDO: pistas-chave da trilha séria (cracha, camera)
        //      são repetidas nas listas de cenas seguintes até serem
        //      coletadas — assim o jogador atrasado ainda as encontra.
        //  (3) MENU FILTRADO: o menu só oferece pistas cujo pai já está no
        //      histórico (lógica de rodarCenas, via getPistasDisponiveis).
        // ================================================================

        // --- Trilha SÉRIA: cracha -> camera -> celular_esquecido (final) ---
        dependencias.inserirDependencia(null, new Pista("cracha", "Crachá de Acesso", ""));
        dependencias.inserirDependencia("cracha", new Pista("camera", "Câmera de Segurança", ""));
        // Pista final sob "camera" (ter camera implica ter cracha). Só é
        // oferecida na Cena 5 (rodarCenas filtra por cena).
        dependencias.inserirDependencia("camera", new Pista(PISTA_FINAL, "Celular Esquecido", ""));
        // Auxiliares sérios (fora do caminho da vitória, contam p/ badge):
        dependencias.inserirDependencia("camera", new Pista("registro_saida", "Registro de Saída", ""));
        dependencias.inserirDependencia("registro_saida", new Pista("extrato_bancario", "Extrato Bancário", ""));
        dependencias.inserirDependencia("camera", new Pista("testemunho_zelador", "Testemunho do Zelador", ""));

        // --- Trilhas malucas: 3 passos (pai -> filho -> neto), distribuídos:
        //     pista-pai na C1 (reaparece na C3 como retake / 2ª chance),
        //     filho na C4, neto na C5. As duas iscas-pai reaparecem na C3, então
        //     quem mordeu uma pode pegar a outra e depois escolher qual trilha
        //     desenvolver (completar as DUAS é impossível: 6 coletas em 5 cenas).
        //
        // ABDUÇÃO: janela_forcada(C1/C3) -> vidro_quebrado(C4) -> luz_estranha(C5)
        dependencias.inserirDependencia(null, new Pista("janela_forcada", "Janela Forçada", ""));
        dependencias.inserirDependencia("janela_forcada", new Pista("vidro_quebrado", "Estilhaços de Vidro", ""));
        dependencias.inserirDependencia("vidro_quebrado", new Pista(NETA_ABDUCAO, "Luz Estranha no Estacionamento", ""));

        // LOUCURA: copo_cafe(C1/C3) -> bilhete_manchado(C4) -> mural_conspiracao(C5)
        dependencias.inserirDependencia(null, new Pista("copo_cafe", "Copo de Café Abandonado", ""));
        dependencias.inserirDependencia("copo_cafe", new Pista("bilhete_manchado", "Bilhete Manchado de Café", ""));
        dependencias.inserirDependencia("bilhete_manchado", new Pista(NETA_LOUCURA, "Mural da Conspiração", ""));

        // --- Distrações: TODAS filhas diretas da raiz (garantem o piso de 4
        //     em cada cena). Cada cena tem 4 destas reservadas para si. ---
        for (String id : new String[]{
                "luvas_latex",                                             // C1
                "gaveta", "exame_pericial", "foto_corredor", "agenda_mesa",   // C2
                "email_ameaca", "recibo_taxi", "jornal_velho", "cartao_visita",       // C3
                "contrato_concorrente", "historico_ligacoes", "turno_seguranca", "relato_vizinho", // C4
                "endereco_secreto", "confissao_gravada", "mapa_local", "depoimento_familia"}) {      // C5
            dependencias.inserirDependencia(null, new Pista(id, id, ""));
        }

        // ===== TEXTOS DAS PISTAS (título + descrição exibida ao investigar) =====
        // Distrações se AUTO-DESCARTAM (álibi/impossibilidade/irrelevância).
        // Pistas do caminho sério puxam para a próxima. Pistas de trilha
        // maluca começam se auto-descartando mas deixam um gancho bizarro.

        // --- Trilha SÉRIA ---
        registrarTextoPista("cracha", "Crachá de Acesso",
                "O crachá do Dr. Almeida está caído perto da porta, e a leitura registrou uma entrada às 23h40 — bem depois de o prédio ter sido esvaziado. O que será que ele veio fazer tão tarde? Alguém viu alguma coisa?");
        registrarTextoPista("camera", "Câmera de Segurança",
                "Puxando as imagens do horário do crachá, a câmera do corredor mostra o Dr. Almeida entrando sozinho, tenso, olhando para trás. Ninguém o forçou — ele voltou por vontade própria. Num detalhe curioso, ele larga algo sobre a mesa antes de sair pela última vez. Valeria procurar o que ficou ali.");
        registrarTextoPista("registro_saida", "Registro de Saída",
                "O livro da portaria confirma o Dr. Almeida saindo às 00h15 com uma caixa lacrada de amostras. Não muda a conclusão, mas prova no papel que ele saiu por conta própria levando o material.");
        registrarTextoPista("testemunho_zelador", "Testemunho do Zelador",
                "O zelador o viu sair apressado com uma caixa, murmurando 'não posso mais ficar aqui'. Não prova nada sozinho, mas dá cor à fuga: ele parecia aliviado, não coagido.");
        registrarTextoPista("extrato_bancario", "Extrato Bancário",
                "No extrato, um gasto salta aos olhos: uma passagem só de ida para o exterior, comprada em dinheiro semanas antes do sumiço. Ninguém que planeja voltar paga assim. Não é a prova final, mas é o retrato de uma fuga ensaiada com frieza — e um belo troféu para quem quer fechar o caso com chave de ouro.");
        // Pista final séria (só selecionável na C5, com cracha+camera no
        // histórico). É a evidência que crava a fuga forjada: a câmera
        // mostrou o Dr. Almeida deixando algo na mesa — era o próprio
        // celular, largado de propósito para não ser rastreado.
        registrarTextoPista(PISTA_FINAL, "Revirar Tudo",
                "Sem cerimônia, você revira cada gaveta, pasta e bolso da sala — e é aí que ela aparece, quase escondida sobre a mesa: o próprio CELULAR do Dr. Almeida, largado de propósito para não ser rastreado. Nas mensagens não enviadas, ele planeja o sumiço e a nova vida com a pesquisa no bolso. Não houve sequestro — ele forjou tudo. CASO RESOLVIDO!");

        // --- Trilha ABDUÇÃO (janela -> vidro -> luz): escala o absurdo ---
        registrarTextoPista("janela_forcada", "Janela Forçada",
                "A janela fica no 4º andar, sem sacada nem escada: ninguém entrou por aqui. Só um vidro velho que cedeu — a não ser por uma marca de queimadura estranha num dos cacos, que ninguém soube explicar.");
        registrarTextoPista("vidro_quebrado", "Estilhaços de Vidro",
                "A perícia diz que o vidro trincou sozinho — mas a marca de queimadura é perfeitamente radial, como se algo incandescente tivesse pairado rente à janela. E há um círculo de grama chamuscada bem embaixo, no gramado. A física não fecha aqui.");
        registrarTextoPista(NETA_ABDUCAO, "Luz Estranha no Estacionamento",
                "Você segue a trilha de queimaduras até o estacionamento e encontra um círculo chamuscado perfeito no asfalto. Ao erguer os olhos, um facho de luz te envolve...");

        // --- Trilha LOUCURA (copo -> bilhete -> mural): mergulho na paranoia ---
        registrarTextoPista("copo_cafe", "Copo de Café Abandonado",
                "O copo de café era da faxineira, que confirma tê-lo esquecido ali. Nada a ver com o caso — embora as manchas secas no fundo formem um desenho curiosamente simétrico, quase proposital.");
        registrarTextoPista("bilhete_manchado", "Bilhete Manchado de Café",
                "O número é de uma pizzaria — mas as manchas de café desenham um padrão que parece... um mapa? E, sozinho no corredor, você jura ter ouvido um sussurro dizer o nome do Dr. Almeida. Você já não tem tanta certeza de que é imaginação.");
        registrarTextoPista(NETA_LOUCURA, "Mural da Conspiração",
                "As mensagens que só você enxerga te levam a forrar a parede inteira da sala de provas com fotos e barbante vermelho, ligando o caso a coisas cada vez mais absurdas...");

        // --- Distrações da Cena 1 ---
        registrarTextoPista("luvas_latex", "Luvas de Látex Descartadas",
                "As luvas de látex na lixeira são idênticas às que o laboratório usa aos montes todo dia. Perfeitamente comuns aqui — não dizem nada.");

        // --- Distrações da Cena 2 ---
        registrarTextoPista("gaveta", "Gaveta da Mesa",
                "Você abre a gaveta — e, no fim das contas, devemos seguir os conselhos dos mais velhos. Lá dentro só há coisas pessoais do Dr. Almeida: um cartão de Dia das Mães ainda por enviar, algumas fotos de família, um chaveiro velho. Será que ele era um filhinho da mamãe? Mas não vem ao caso!");
        registrarTextoPista("exame_pericial", "Exame Pericial da Sala",
                "O laudo pericial da sala não achou digitais estranhas nem sinais de luta. Tudo aponta para uma saída tranquila — nada de arrombamento.");
        registrarTextoPista("foto_corredor", "Foto do Corredor",
                "Uma foto antiga do corredor pregada no mural: era só decoração institucional, dessas de aniversário do departamento. Irrelevante.");
        registrarTextoPista("agenda_mesa", "Agenda sobre a Mesa",
                "A agenda de mesa está aberta num compromisso banal: 'reunião de colegiado, 14h'. Nada de anormal nas anotações.");

        // --- Distrações da Cena 3 ---
        registrarTextoPista("email_ameaca", "E-mail de Ameaça",
                "O 'e-mail de ameaça' era spam automático de um golpe conhecido, disparado para centenas de pessoas na mesma noite. Nem pessoal nem real. Descartado.");
        registrarTextoPista("recibo_taxi", "Recibo de Táxi",
                "O recibo de táxi é de duas semanas atrás e está no nome de outro professor. Entrou na pilha por engano. Irrelevante.");
        registrarTextoPista("jornal_velho", "Jornal Velho",
                "Um jornal amarelado largado sobre o arquivo, com notícias de meses atrás. Só serventia de papel de embrulho. Nada a ver com o caso.");
        registrarTextoPista("cartao_visita", "Cartão de Visita",
                "Um cartão de visita de um representante de material de laboratório. Contato comercial de rotina — sem relevância.");

        // --- Distrações da Cena 4 ---
        registrarTextoPista("contrato_concorrente", "Contrato com o Concorrente",
                "O 'contrato' com o concorrente era só uma proposta de parceria pública, protocolada e aprovada pela reitoria meses atrás. Legítimo e sem segredo. Descartado.");
        registrarTextoPista("historico_ligacoes", "Histórico de Ligações",
                "As ligações repetidas eram todas para o consultório do dentista, remarcando uma consulta. Rotina pessoal. Irrelevante.");
        registrarTextoPista("turno_seguranca", "Escala do Turno de Segurança",
                "O turno de segurança daquela noite estava normal, todos presentes, sem ocorrências além da já conhecida. Não acrescenta nada.");
        registrarTextoPista("relato_vizinho", "Relato do Vizinho",
                "Um vizinho do laboratório reclamou de barulho, mas era da obra do prédio ao lado, no horário comercial. Sem ligação com o sumiço.");

        // --- Distrações da Cena 5 ---
        registrarTextoPista("endereco_secreto", "Endereço no Contrato",
                "O 'endereço secreto' era o do depósito oficial da universidade, que consta em dezenas de documentos públicos. Nada de secreto. Beco sem saída.");
        registrarTextoPista("confissao_gravada", "Confissão Gravada",
                "A 'confissão' gravada era um trecho de um podcast de crime que o dono do celular ouvia no carro. Ficção. Descartada.");
        registrarTextoPista("mapa_local", "Mapa Rabiscado",
                "O mapa rabiscado era o trajeto de corrida matinal de um funcionário, com horários de pace anotados. Nada a ver com o caso.");
        registrarTextoPista("depoimento_familia", "Depoimento da Família",
                "A família relata tensão nos últimos dias, mas não sabe de nada concreto. A mamãe do Dr. Almeida, entre lágrimas, faz questão de dizer que ama muito ele. Comovente — mas sem qualquer informação que aponte um rumo.");

        // Textos fixos das 5 cenas (não mudam entre partidas). Cada texto
        // menciona, em prosa, as mesmas palavras/objetos que aparecem como
        // pistas selecionáveis daquela cena — o destaque [ENTRE COLCHETES]
        // simula o "highlight" da palavra no texto corrido.
        //
        // A Cena 3 inclui uma menção fixa (não condicional) a "crachá" e
        // "câmera" — soa como confirmação de rotina para quem já os
        // coletou, e como alerta real para quem ainda não os tem.
        String molduraTop  = "  .----------------------------- ☎ TELEFONE -----------------------------.";
        String molduraBot  = "  '----------------------------------------------------------------------'";
        textosCenas = new String[]{
            "=== CENA 1: A Cena do Desaparecimento ===\n"
                + molduraTop + "\n"
                + "   CENTRAL: \"Detetive, o Dr. Almeida, o bioquímico, sumiu ontem à noite.\n"
                + "            A sala tá isolada. Entra e olha tudo.\"\n"
                + molduraBot + "\n\n"
                + "A sala do laboratório está exatamente como a segurança a deixou. Perto da\n"
                + "porta, o [CRACHÁ] do Dr. Almeida está caído, com a última leitura ainda\n"
                + "registrada no sistema. Nos fundos, uma [JANELA FORÇADA] chama atenção pelo\n"
                + "estrago. Sobre a recepção, um [COPO DE CAFÉ] esquecido ainda está morno. E na\n"
                + "lixeira, um par de [LUVAS DE LÁTEX] jogado fora do padrão da equipe. Por onde\n"
                + "você começa?",
            "=== CENA 2: A Sala de Segurança ===\n"
                + molduraTop + "\n"
                + "   MÃE: \"Filho, você tá comendo direito? E não vai é ficar bisbilhotando\n"
                + "        GAVETA dos outros, hein!\"\n"
                + molduraBot + "\n\n"
                + "Você ri, promete almoçar, e entra na sala de monitoramento com a cabeça no\n"
                + "lugar. A [CÂMERA] do corredor guarda as imagens da noite, prontas para serem\n"
                + "cruzadas com o horário do [CRACHÁ] — que, aliás, se você ainda não examinou de\n"
                + "perto, continua ali esperando. Sobre a bancada, uma [GAVETA] entreaberta te\n"
                + "encara (e, veja só, faz você se lembrar da sua mãe), o [EXAME PERICIAL] da\n"
                + "sala, uma velha [FOTO DO CORREDOR] no mural e a [AGENDA] do professor. Tudo\n"
                + "aqui parece sóbrio e concreto. O que você examina?",
            "=== CENA 3: O Rastro Documental ===\n"
                + molduraTop + "\n"
                + "   CHEFE (Delegado Canastrão): \"Ê detetive! Já verificou aquele [CRACHÁ] e\n"
                + "        puxou a [CÂMERA]?! Tem caroço nesse angu, quero isso resolvido ONTEM!\"\n"
                + molduraBot + "\n\n"
                + "Ele desliga sem esperar resposta. Você volta à papelada: o [REGISTRO DE\n"
                + "SAÍDA] da noite, o [TESTEMUNHO DO ZELADOR], um [E-MAIL DE AMEAÇA], um [RECIBO\n"
                + "DE TÁXI], um [JORNAL VELHO] e um [CARTÃO DE VISITA]. Mas seus olhos teimam em\n"
                + "voltar para aquela [JANELA FORÇADA] e aquele [COPO DE CAFÉ] — se você ainda não\n"
                + "os examinou, eles continuam ali, e você sente vibrações estranhas e calafrios\n"
                + "só de pensar neles. Parecem sussurrar algo que a razão não explica. O que\n"
                + "merece atenção? ...Sou um investigador ou um caçador de fantasmas, afinal?",
            "=== CENA 4: O Confronto de Pistas ===\n"
                + molduraTop + "\n"
                + "   TELEMARKETING (CamerAção Ofertas): \"Boa tarde! O senhor não vai querer\n"
                + "        perder nossa câmera noturna 4K: enxerga TUDO no escuro, até o que as\n"
                + "        pessoas andam aprontando às 23h41 da madrugada!\"\n"
                + molduraBot + "\n\n"
                + "Você quase desliga na cara — mas o horário citado te arrepia (e lembra que a\n"
                + "[CÂMERA] do prédio ainda pode ter mais a mostrar). Voltando à mesa, um [EXTRATO\n"
                + "BANCÁRIO] parece ter inconsistências bem marcantes. Ao lado, um [CONTRATO COM O\n"
                + "CONCORRENTE], um [HISTÓRICO DE LIGAÇÕES], a [ESCALA DO TURNO DE SEGURANÇA] e um\n"
                + "[RELATO DO VIZINHO] esperam. Já os [ESTILHAÇOS DE VIDRO] da janela e o [BILHETE\n"
                + "MANCHADO] do café são inexplicáveis — mas será que precisamos explicar e seguir\n"
                + "o nosso cérebro? Ou seguir o nosso coração? Em qual você foca?",
            "=== CENA 5: O Desfecho ===\n"
                + molduraTop + "\n"
                + "   CHEFE (furioso): \"DETETIVE! A imprensa tá na porta, o reitor no meu pé, e\n"
                + "        eu SEM RESPOSTA?! Vira essa sala do avesso, mas me traz uma CONCLUSÃO\n"
                + "        AGORA!\"\n"
                + molduraBot + "\n\n"
                + "Você desliga suando frio. Sobre a mesa há pontas soltas que não levam a nada\n"
                + "— um [ENDEREÇO] de contrato, uma [CONFISSÃO GRAVADA], um [MAPA RABISCADO], o\n"
                + "[DEPOIMENTO DA FAMÍLIA]. Você lembra da sua mãe, e da vergonha que passou com\n"
                + "aquela gaveta — mas que se dane o decoro: sua vontade é [REVIRAR TUDO] atrás\n"
                + "dos segredos do Sr. Almeida. Mas será que os caminhos tradicionais realmente\n"
                + "resolvem o caso? De onde vêm aquelas [LUZ ESTRANHA]? Às vezes as ideias que\n"
                + "desafiam o senso comum acertam em cheio — e se você montar o seu [MURAL DA\n"
                + "CONSPIRAÇÃO]? Qual é a resposta certa?"
        };

        // Listas fixas de pistas por cena. O menu real de cada cena é esta
        // lista FILTRADA pela árvore (só entra no menu o que tem o pai já no
        // histórico). As 4 primeiras de cada linha são distrações filhas da
        // raiz (piso garantido); o restante são pistas de trilha e o
        // TRANSBORDO das pistas-chave sérias (cracha/camera repetidas nas
        // cenas seguintes para o jogador atrasado ainda poder pegá-las).
        pistasPorCena = new String[][]{
            // C1: 4 filhas da raiz. cracha (sério) + as 2 pistas-pai malucas
            //     (janela, copo) + 1 distração pura (luvas). Aqui o jogador
            //     morde (ou não) a primeira isca maluca.
            {"cracha", "janela_forcada", "copo_cafe", "luvas_latex"},
            // C2: "SÓ JOGO SÉRIO" — sem tentação maluca. 4 distrações-raiz +
            //     transbordo(cracha, 2ª chance) + camera (sério).
            {"gaveta", "exame_pericial", "foto_corredor", "agenda_mesa",
             "cracha", "camera"},
            // C3: ENCRUZILHADA. 4 distrações-raiz + transbordo(cracha, camera)
            //     + aux(registro, testemunho) + RETAKE das 2 pistas-pai malucas
            //     (janela, copo — 2ª chance de pegar; quem tem uma pode pegar
            //     a outra). Os filhos malucos ainda NÃO aparecem aqui.
            {"email_ameaca", "recibo_taxi", "jornal_velho", "cartao_visita",
             "cracha", "camera", "registro_saida", "testemunho_zelador",
             "janela_forcada", "copo_cafe"},
            // C4: 4 distrações-raiz + transbordo(camera) + aux(extrato) +
            //     FILHOS malucos avançam (vidro_quebrado, bilhete_manchado —
            //     só selecionáveis se a pista-pai já foi coletada).
            {"contrato_concorrente", "historico_ligacoes", "turno_seguranca", "relato_vizinho",
             "camera", "extrato_bancario", "vidro_quebrado", "bilhete_manchado"},
            // C5: 4 distrações-raiz + os TRÊS desfechos competindo
            //     (final sério + 2 netas malucas).
            {"endereco_secreto", "confissao_gravada", "mapa_local", "depoimento_familia",
             PISTA_FINAL, NETA_ABDUCAO, NETA_LOUCURA}
        };
    }

    private java.util.Map<String, Pista> textosPistas = new java.util.HashMap<>();

    private void registrarTextoPista(String id, String titulo, String descricao) {
        textosPistas.put(id, new Pista(id, titulo, descricao));
    }

    // Loop principal: percorre as 5 cenas fixas. A cada cena, monta o menu
    // (lista da cena FILTRADA pela árvore de dependências), lê a escolha do
    // jogador e registra a pista no histórico. Ao fim das 5 cenas, decide o
    // desfecho e pergunta se o jogador quer jogar de novo (para explorar
    // outros caminhos da árvore). Nunca reinicia no meio de uma partida.
    private void rodarCenas() {
        for (int cena = 0; cena < textosCenas.length; cena++) {
            terminal.exibir("\n" + textosCenas[cena]);

            List<String> menu = montarMenuDaCena(cena);
            // Piso garantido no gabarito, mas por segurança: se por algum
            // motivo o menu vier vazio, pula a cena sem travar.
            if (menu.isEmpty()) {
                continue;
            }

            terminal.exibir("");
            for (int i = 0; i < menu.size(); i++) {
                Pista p = textosPistas.get(menu.get(i));
                terminal.exibir("  " + (i + 1) + ". " + p.titulo);
            }

            String idEscolhido = lerEscolha(menu);
            Pista escolhida = textosPistas.get(idEscolhido);
            terminal.exibir("\n>> " + escolhida.titulo);
            terminal.exibir("   " + escolhida.descricao);
            historico.inserirPista(idEscolhido);
        }

        // Fim das 5 cenas: decide o desfecho e fecha a partida.
        int desfecho = verificarGameOver();
        imprimirRelatorio(desfecho);
        reiniciar();
    }

    // Monta o menu da cena: percorre a lista fixa de pistas daquela cena e
    // mantém apenas as que estão SELECIONÁVEIS agora — a árvore só libera uma
    // pista quando seu pai já está no histórico (getPistasDisponiveis), e não
    // repetimos pistas já coletadas. Ids duplicados na lista são ignorados.
    private List<String> montarMenuDaCena(int cena) {
        List<Pista> disponiveis = dependencias.getPistasDisponiveis(historico);
        List<String> idsDisponiveis = new ArrayList<>();
        for (Pista p : disponiveis) {
            idsDisponiveis.add(p.id);
        }

        List<String> menu = new ArrayList<>();
        for (String id : pistasPorCena[cena]) {
            if (menu.contains(id)) continue;                 // sem duplicatas
            if (historico.contemPista(id)) continue;         // já coletada
            if (!idsDisponiveis.contains(id)) continue;      // pré-requisito não cumprido
            menu.add(id);
        }
        return menu;
    }

    // Lê a escolha do jogador de forma robusta: aceita só um número dentro do
    // range do menu. Entrada inválida (não numérica ou fora do range) reexibe
    // o aviso e pede de novo — nunca lança exceção nem trava o script.
    private String lerEscolha(List<String> menu) {
        while (true) {
            terminal.exibir("\nDigite o número da pista que quer investigar:");
            String entrada = terminal.lerEntrada();
            if (entrada != null) {
                entrada = entrada.trim();
                try {
                    int indice = Integer.parseInt(entrada);
                    if (indice >= 1 && indice <= menu.size()) {
                        return menu.get(indice - 1);
                    }
                } catch (NumberFormatException e) {
                    // cai no aviso abaixo
                }
            }
            terminal.exibir("Entrada inválida. Escolha um número entre 1 e " + menu.size() + ".");
        }
    }

    // Códigos de desfecho retornados por verificarGameOver().
    private static final int DESFECHO_DERROTA = 0;
    private static final int DESFECHO_VITORIA = 1;
    private static final int DESFECHO_ABDUCAO = 2;
    private static final int DESFECHO_LOUCURA = 3;

    // Ao fim das 5 cenas, decide o desfecho pelo que o jogador coletou.
    // Prioridade: vitória séria > abdução > loucura > derrota padrão.
    // (As três pistas de desfecho são mutuamente exclusivas na prática, pois
    // completar mais de uma trilha é matematicamente impossível — a prioridade
    // é só uma salvaguarda determinística.)
    private int verificarGameOver() {
        if (historico.contemPista(PISTA_FINAL)) {
            return DESFECHO_VITORIA;
        }
        if (historico.contemPista(NETA_ABDUCAO)) {
            return DESFECHO_ABDUCAO;
        }
        if (historico.contemPista(NETA_LOUCURA)) {
            return DESFECHO_LOUCURA;
        }
        return DESFECHO_DERROTA;
    }

    // Pergunta se o jogador quer jogar de novo. Se sim, zera o histórico e
    // roda as 5 cenas de novo (para o jogador explorar outros ramos da
    // árvore). Se não, encerra. O snapshot do caminho já foi salvo em
    // todosCaminhos por imprimirRelatorio(), então aqui só limpamos.
    private void reiniciar() {
        terminal.exibir("\nQuer investigar o caso de novo, por outro caminho? (s/n)");
        String resposta = terminal.lerEntrada();
        if (resposta != null && resposta.trim().toLowerCase().startsWith("s")) {
            historico = new ListaEncadeada();
            terminal.exibir("\n--- Reabrindo a investigação. Boa sorte, detetive! ---");
            rodarCenas();
        } else {
            terminal.exibir("\nCaso encerrado. Até a próxima, detetive.");
        }
    }

    // Exibe o relatório da partida e persiste os dados. A versão detalhada
    // (listar todos os caminhos percorridos) fica para a Etapa 6 — aqui já
    // registramos o snapshot da partida atual, mostramos o desfecho e
    // salvamos. 'desfecho' é um dos DESFECHO_*.
    private void imprimirRelatorio(int desfecho) {
        todosCaminhos.add(historico.toArray());
        tentativas++;

        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        terminal.exibir("\n============================================");
        terminal.exibir("         RELATÓRIO DE INVESTIGAÇÃO");
        terminal.exibir("============================================");
        terminal.exibir("Detetive  : " + nomeJogador);
        terminal.exibir("Partidas  : " + tentativas);
        terminal.exibir("Data/Hora : " + dataHora);
        terminal.exibir("");
        terminal.exibir("Caminho desta partida:");
        historico.imprimirHistorico();
        terminal.exibir("");
        terminal.exibir("Resultado : " + textoDesfecho(desfecho));
        terminal.exibir("============================================");

        persistencia.salvar(nomeJogador, tentativas, todosCaminhos, desfecho == DESFECHO_VITORIA);
    }

    // Texto do desfecho exibido no relatório. A badge de excelência exige
    // vencer E ter coletado as 3 pistas auxiliares verdadeiras.
    private String textoDesfecho(int desfecho) {
        switch (desfecho) {
            case DESFECHO_VITORIA:
                boolean excelencia = true;
                for (String aux : AUXILIARES) {
                    if (!historico.contemPista(aux)) { excelencia = false; break; }
                }
                return excelencia
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
}
