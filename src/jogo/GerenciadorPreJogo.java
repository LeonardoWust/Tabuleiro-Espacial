package jogo;

import estruturas.*;
import modelo.*;
import tabuleiro.*;

import java.util.Scanner;

/**
 * Gerencia toda a fase pré-jogo:
 * - CRUD de jogadores e imóveis
 * - Configurações da partida
 * - Montagem do tabuleiro e do baralho
 */
public class GerenciadorPreJogo {

    private final ListaJogadores  jogadores;
    private final ListaImoveis    todosImoveis;
    private final ConfiguracaoJogo config;
    private final Scanner          scanner;

    public GerenciadorPreJogo(ListaJogadores jogadores, ListaImoveis todosImoveis,
                               ConfiguracaoJogo config, Scanner scanner) {
        this.jogadores    = jogadores;
        this.todosImoveis = todosImoveis;
        this.config       = config;
        this.scanner      = scanner;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  MENU PRINCIPAL PRÉ-JOGO
    // ═══════════════════════════════════════════════════════════════════════════

    public void executarMenu() {
        boolean sair = false;
        while (!sair) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║         CONFIGURAÇÃO DA PARTIDA      ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║ [1] Gerenciar Jogadores              ║");
            System.out.println("║ [2] Gerenciar Imóveis                ║");
            System.out.println("║ [3] Configurações da Partida         ║");
            System.out.println("║ [4] Carregar dados pré-definidos     ║");
            System.out.println("║ [5] Iniciar Jogo                     ║");
            System.out.println("║ [0] Sair do sistema                  ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("  Opção: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1": menuJogadores(); break;
                case "2": menuImoveis();   break;
                case "3": menuConfiguracoes(); break;
                case "4": carregarDadosPreDefinidos(); break;
                case "5":
                    if (validarInicioJogo()) sair = true;
                    break;
                case "0":
                    System.out.println("Saindo...");
                    System.exit(0);
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  CRUD JOGADORES
    // ═══════════════════════════════════════════════════════════════════════════

    private void menuJogadores() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n── JOGADORES ─────────────────────────────");
            System.out.println("  [1] Cadastrar jogador");
            System.out.println("  [2] Listar jogadores");
            System.out.println("  [3] Atualizar jogador");
            System.out.println("  [4] Remover jogador");
            System.out.println("  [0] Voltar");
            System.out.print("  Opção: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1": cadastrarJogador(); break;
                case "2": listarJogadores();  break;
                case "3": atualizarJogador(); break;
                case "4": removerJogador();   break;
                case "0": voltar = true;       break;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private void cadastrarJogador() {
        if (jogadores.cheia()) {
            System.out.println("  Máximo de 6 jogadores atingido.");
            return;
        }
        System.out.print("  Nome do jogador: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) { System.out.println("  Nome inválido."); return; }

        System.out.println("  Personagem:");
        TipoPersonagem[] tipos = TipoPersonagem.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.printf("    [%d] %s%n", i + 1, tipos[i]);
        }
        System.out.print("  Escolha (1-4): ");
        int escolha = lerInt() - 1;
        if (escolha < 0 || escolha >= tipos.length) {
            System.out.println("  Opção inválida. Usando Especulador.");
            escolha = 0;
        }
        Jogador j = new Jogador(nome, config.getSaldoInicial(), tipos[escolha]);
        jogadores.adicionar(j);
        System.out.printf("  ✅ Jogador cadastrado: %s%n", j);
    }

    private void listarJogadores() {
        System.out.println("\n  ── LISTA DE JOGADORES ──");
        jogadores.exibir();
    }

    private void atualizarJogador() {
        listarJogadores();
        System.out.print("  ID do jogador para atualizar: ");
        int id = lerInt();
        Jogador j = jogadores.buscarPorId(id);
        if (j == null) { System.out.println("  Jogador não encontrado."); return; }

        System.out.print("  Novo nome [" + j.getNome() + "]: ");
        String nome = scanner.nextLine().trim();
        if (!nome.isEmpty()) j.setNome(nome);

        System.out.println("  Novo personagem:");
        TipoPersonagem[] tipos = TipoPersonagem.values();
        for (int i = 0; i < tipos.length; i++) System.out.printf("    [%d] %s%n", i + 1, tipos[i]);
        System.out.print("  Escolha (1-4) ou [ENTER] para manter: ");
        String linha = scanner.nextLine().trim();
        if (!linha.isEmpty()) {
            try {
                int idx = Integer.parseInt(linha) - 1;
                if (idx >= 0 && idx < tipos.length) j.setTipo(tipos[idx]);
            } catch (NumberFormatException ignored) {}
        }
        System.out.printf("  ✅ Atualizado: %s%n", j);
    }

    private void removerJogador() {
        listarJogadores();
        System.out.print("  ID do jogador para remover: ");
        int id = lerInt();
        Jogador j = jogadores.buscarPorId(id);
        if (j == null) { System.out.println("  Jogador não encontrado."); return; }
        jogadores.remover(j);
        System.out.printf("  ✅ Jogador %s removido.%n", j.getNome());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  CRUD IMÓVEIS
    // ═══════════════════════════════════════════════════════════════════════════

    private void menuImoveis() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n── IMÓVEIS ───────────────────────────────");
            System.out.println("  [1] Cadastrar imóvel");
            System.out.println("  [2] Listar imóveis");
            System.out.println("  [3] Atualizar imóvel");
            System.out.println("  [4] Remover imóvel");
            System.out.println("  [0] Voltar");
            System.out.print("  Opção: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1": cadastrarImovel(); break;
                case "2": listarImoveis();   break;
                case "3": atualizarImovel(); break;
                case "4": removerImovel();   break;
                case "0": voltar = true;      break;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private void cadastrarImovel() {
        if (todosImoveis.tamanho() >= 40) {
            System.out.println("  Máximo de 40 imóveis atingido.");
            return;
        }
        System.out.print("  Nome do imóvel: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) { System.out.println("  Nome inválido."); return; }

        System.out.print("  Valor de compra: R$ ");
        double compra = lerDouble();
        System.out.print("  Aluguel base:    R$ ");
        double aluguel = lerDouble();

        Imovel im = new Imovel(nome, compra, aluguel);
        todosImoveis.adicionar(im);
        System.out.printf("  ✅ Imóvel cadastrado: %s%n", im);
    }

    private void listarImoveis() {
        System.out.println("\n  ── LISTA DE IMÓVEIS ──");
        todosImoveis.exibir();
    }

    private void atualizarImovel() {
        listarImoveis();
        System.out.print("  ID do imóvel para atualizar: ");
        int id = lerInt();
        Imovel im = todosImoveis.buscarPorId(id);
        if (im == null) { System.out.println("  Imóvel não encontrado."); return; }

        System.out.print("  Novo nome [" + im.getNome() + "]: ");
        String nome = scanner.nextLine().trim();
        if (!nome.isEmpty()) im.setNome(nome);

        System.out.print("  Novo valor de compra [" + im.getValorCompra() + "]: R$ ");
        String vc = scanner.nextLine().trim();
        if (!vc.isEmpty()) { try { im.setValorCompra(Double.parseDouble(vc.replace(",","."))); } catch (Exception ignored) {} }

        System.out.print("  Novo aluguel base [" + im.getAluguelBase() + "]: R$ ");
        String al = scanner.nextLine().trim();
        if (!al.isEmpty()) { try { im.setAluguelBase(Double.parseDouble(al.replace(",","."))); } catch (Exception ignored) {} }

        System.out.printf("  ✅ Atualizado: %s%n", im);
    }

    private void removerImovel() {
        listarImoveis();
        System.out.print("  ID do imóvel para remover: ");
        int id = lerInt();
        Imovel im = todosImoveis.buscarPorId(id);
        if (im == null) { System.out.println("  Imóvel não encontrado."); return; }
        if (im.getDono() != null) {
            System.out.println("  Não é possível remover imóvel com dono.");
            return;
        }
        todosImoveis.remover(im);
        System.out.printf("  ✅ Imóvel %s removido.%n", im.getNome());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  CONFIGURAÇÕES
    // ═══════════════════════════════════════════════════════════════════════════

    private void menuConfiguracoes() {
        System.out.println("\n── CONFIGURAÇÕES ATUAIS ──");
        System.out.println(config);
        System.out.println("\n  [1] Alterar saldo inicial");
        System.out.println("  [2] Alterar salário por volta");
        System.out.println("  [3] Alterar máx. de rodadas");
        System.out.println("  [4] Alterar fiança");
        System.out.println("  [5] Alterar capacidade do histórico");
        System.out.println("  [0] Voltar");
        System.out.print("  Opção: ");
        String op = scanner.nextLine().trim();
        switch (op) {
            case "1":
                System.out.print("  Saldo inicial: R$ ");
                config.setSaldoInicial(lerDouble());
                break;
            case "2":
                System.out.print("  Salário por volta: R$ ");
                config.setSalarioPorVolta(lerDouble());
                break;
            case "3":
                System.out.print("  Máx. rodadas: ");
                config.setMaxRodadas(lerInt());
                break;
            case "4":
                System.out.print("  Fiança: R$ ");
                config.setValorFianca(lerDouble());
                break;
            case "5":
                System.out.print("  Capacidade do histórico: ");
                config.setCapacidadeHistorico(lerInt());
                break;
            default: break;
        }
        System.out.println("  ✅ Configurações atualizadas.");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  DADOS PRÉ-DEFINIDOS (Espaço e Ficção Científica)
    // ═══════════════════════════════════════════════════════════════════════════

    public void carregarDadosPreDefinidos() {
        System.out.println("\n  Carregando dados pré-definidos — Tema: Espaço e Ficção Científica...");

        // Imóveis (Anexo C)
        double[][] dados = {
            {200_000, 10_000},  // Módulo Lunar Alfa
            {420_000, 21_000},  // Estação Orbital Beta
            {500_000, 25_000},  // Colônia de Marte
            {350_000, 17_500},  // Domo de Titã
            {750_000, 37_500},  // Nave-Mãe Andrômeda
            {230_000, 11_500},  // Bunker Subterrâneo
            {600_000, 30_000},  // Plataforma de Europa
            {870_000, 43_500},  // Cúpula de Kepler-22b
            {460_000, 23_000},  // Laboratório do Asteroid Belt
            {210_000, 10_500},  // Refúgio de Plutão
            {1_000_000, 50_000},// Torre de Observação Solar
            {290_000, 14_500},  // Base Antártica Omega
        };
        String[] nomes = {
            "Módulo Lunar Alfa", "Estação Orbital Beta", "Colônia de Marte",
            "Domo de Titã", "Nave-Mãe Andrômeda", "Bunker Subterrâneo",
            "Plataforma de Europa", "Cúpula de Kepler-22b",
            "Laboratório do Asteroid Belt", "Refúgio de Plutão",
            "Torre de Observação Solar", "Base Antártica Omega"
        };

        Imovel.resetContador();
        for (int i = 0; i < nomes.length; i++) {
            todosImoveis.adicionar(new Imovel(nomes[i], dados[i][0], dados[i][1]));
        }

        // Jogadores de exemplo
        if (jogadores.tamanho() == 0) {
            Jogador.resetContador();
            jogadores.adicionar(new Jogador("Capitã Lyra",  config.getSaldoInicial(), TipoPersonagem.ESPECULADOR));
            jogadores.adicionar(new Jogador("Dr. Vance",    config.getSaldoInicial(), TipoPersonagem.NEGOCIANTE));
            jogadores.adicionar(new Jogador("Adv. Turing",  config.getSaldoInicial(), TipoPersonagem.ADVOGADO));
            jogadores.adicionar(new Jogador("Eng. Solis",   config.getSaldoInicial(), TipoPersonagem.CONSTRUTOR));
        }

        System.out.println("  ✅ " + todosImoveis.tamanho() + " imóveis carregados.");
        System.out.println("  ✅ " + jogadores.tamanho()    + " jogadores carregados.");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  MONTAGEM DO TABULEIRO
    // ═══════════════════════════════════════════════════════════════════════════

    public ListaCircular montarTabuleiro() {
        ListaCircular tab = new ListaCircular();

        // Casa 0: Início
        tab.adicionarCasa(TipoCasa.INICIO, "INÍCIO", null);

        // Distribuição de 20 casas com imóveis intercalados com especiais
        // Usamos até 12 imóveis disponíveis
        int imovelIdx = 0;
        int total = todosImoveis.tamanho();

        String[] nomesEspeciais = {
            "Imposto Estelar",    // imposto
            "Restituição Fiscal", // restituição
            "PRISÃO",             // prisão
            "Leilão Orbital",     // leilão
            "Sorte/Revés",        // sorte/reves
        };
        TipoCasa[] tiposEspeciais = {
            TipoCasa.IMPOSTO, TipoCasa.RESTITUICAO,
            TipoCasa.PRISAO, TipoCasa.LEILAO, TipoCasa.SORTE_REVES
        };

        // Padrão de casas: I=imóvel, E=especial
        // Posições: 0=Início, 1-I, 2-I, 3-E(imposto), 4-I, 5-E(sorte),
        //           6-I, 7-I, 8-E(restit.), 9-I, 10-E(leilão),
        //           11-I, 12-I, 13-E(prisão), 14-I, 15-E(sorte),
        //           16-I, 17-I, 18-E(imposto), 19-I, 20-E(leilão)
        int[] layout = {
            0, // Início já adicionado
            1,1,2,1,4,1,1,3,1,5,1,1,4,1,4,1,1,2,1,5
        };
        // 0=início(já ok), 1=imóvel, 2=imposto, 3=restit., 4=leilão, 5=sorte

        for (int i = 1; i < layout.length; i++) {
            switch (layout[i]) {
                case 1:
                    if (imovelIdx < total) {
                        Imovel im = todosImoveis.get(imovelIdx++);
                        tab.adicionarCasa(TipoCasa.IMOVEL, im.getNome(), im);
                    }
                    break;
                case 2:
                    tab.adicionarCasa(TipoCasa.IMPOSTO, "Imposto Estelar", null);
                    break;
                case 3:
                    tab.adicionarCasa(TipoCasa.RESTITUICAO, "Restituição Fiscal", null);
                    break;
                case 4:
                    tab.adicionarCasa(TipoCasa.LEILAO, "Leilão Orbital", null);
                    break;
                case 5:
                    tab.adicionarCasa(TipoCasa.SORTE_REVES, "Sorte/Revés", null);
                    break;
            }
        }

        // Prisão
        tab.adicionarCasa(TipoCasa.PRISAO, "PRISÃO", null);

        return tab;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  MONTAGEM DO BARALHO
    // ═══════════════════════════════════════════════════════════════════════════

    public PilhaBaralho montarBaralho() {
        PilhaBaralho baralho = new PilhaBaralho();

        // Cartas de ganho / avanço (≥6)
        baralho.adicionarCarta(new Carta("Subsídio do Conselho Galáctico: receba R$3.000,00",
                Carta.TipoCarta.GANHO_DINHEIRO, 3000, 0));
        baralho.adicionarCarta(new Carta("Descoberta científica! Receba R$5.000,00 do banco.",
                Carta.TipoCarta.GANHO_DINHEIRO, 5000, 0));
        baralho.adicionarCarta(new Carta("Contrato de mineração aprovado: receba R$2.000,00.",
                Carta.TipoCarta.GANHO_DINHEIRO, 2000, 0));
        baralho.adicionarCarta(new Carta("Missão bem-sucedida! Avance 3 casas.",
                Carta.TipoCarta.AVANCAR_CASAS, 0, 3));
        baralho.adicionarCarta(new Carta("Impulso de propulsão extra! Avance 5 casas.",
                Carta.TipoCarta.AVANCAR_CASAS, 0, 5));
        baralho.adicionarCarta(new Carta("Portão de dobramento detectado! Avance direto ao INÍCIO.",
                Carta.TipoCarta.IR_PARA_INICIO, 0, 0));
        baralho.adicionarCarta(new Carta("Todos os outros jogadores pagam R$1.500,00 de tributo espacial.",
                Carta.TipoCarta.RECEBER_DE_JOGADORES, 1500, 0));
        baralho.adicionarCarta(new Carta("Patrocínio da Academia Estelar: receba R$4.000,00.",
                Carta.TipoCarta.GANHO_DINHEIRO, 4000, 0));

        // Cartas de perda / penalidade / retrocesso (≥6)
        baralho.adicionarCarta(new Carta("Falha no reator! Pague R$2.500,00 ao banco.",
                Carta.TipoCarta.PERDA_DINHEIRO, 2500, 0));
        baralho.adicionarCarta(new Carta("Multa por invasão de órbita restrita: pague R$1.800,00.",
                Carta.TipoCarta.PERDA_DINHEIRO, 1800, 0));
        baralho.adicionarCarta(new Carta("Tempestade de asteroides! Recue 4 casas.",
                Carta.TipoCarta.RECUAR_CASAS, 0, 4));
        baralho.adicionarCarta(new Carta("Buraco de minhoca instável! Recue 2 casas.",
                Carta.TipoCarta.RECUAR_CASAS, 0, 2));
        baralho.adicionarCarta(new Carta("Detenção pela Patrulha Intergaláctica! Vá direto à PRISÃO.",
                Carta.TipoCarta.IR_PARA_PRISAO, 0, 0));
        baralho.adicionarCarta(new Carta("Pague R$1.000,00 de taxa de manutenção a cada jogador.",
                Carta.TipoCarta.PAGAR_A_JOGADORES, 1000, 0));
        baralho.adicionarCarta(new Carta("Volta ao último posto de controle! Volte à última casa.",
                Carta.TipoCarta.VOLTAR_ULTIMA_CASA, 0, 0));
        baralho.adicionarCarta(new Carta("Sobrecarga de sistemas! Pague R$3.500,00 ao banco.",
                Carta.TipoCarta.PERDA_DINHEIRO, 3500, 0));

        return baralho;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  VALIDAÇÃO
    // ═══════════════════════════════════════════════════════════════════════════

    private boolean validarInicioJogo() {
        if (jogadores.tamanho() < 2) {
            System.out.println("  ❌ Mínimo de 2 jogadores para iniciar.");
            return false;
        }
        if (todosImoveis.tamanho() < 10) {
            System.out.println("  ❌ Mínimo de 10 imóveis para iniciar.");
            return false;
        }
        return true;
    }

    // ── Helpers de leitura ────────────────────────────────────────────────────
    private int lerInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (Exception e) { return 0; }
    }

    private double lerDouble() {
        try { return Double.parseDouble(scanner.nextLine().trim().replace(",",".")); }
        catch (Exception e) { return 0.0; }
    }
}
