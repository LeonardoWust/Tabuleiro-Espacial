package jogo;

import estruturas.*;
import modelo.*;
import tabuleiro.*;

import java.util.Random;
import java.util.Scanner;

/**
 * Motor principal do jogo.
 * Gerencia rodadas, movimentação, eventos de casa e interações.
 */
public class MotorJogo {

    private final ListaCircular      tabuleiro;
    private final ListaJogadores     jogadores;
    private final ListaImoveis       todosImoveis;
    private final PilhaBaralho       baralho;
    private final FilaHistorico      historico;
    private final FilaPrisao         filaPrisao;
    private final ConfiguracaoJogo   config;
    private final Scanner            scanner;
    private final Random             rng;

    private int rodadaAtual;
    private int indiceJogadorAtual;

    // Estatísticas finais
    private Imovel imovelMaiorAluguel;
    private double maiorAluguelPartida;

    public MotorJogo(ListaCircular tabuleiro, ListaJogadores jogadores,
                     ListaImoveis todosImoveis, PilhaBaralho baralho,
                     ConfiguracaoJogo config, Scanner scanner) {
        this.tabuleiro     = tabuleiro;
        this.jogadores     = jogadores;
        this.todosImoveis  = todosImoveis;
        this.baralho       = baralho;
        this.config        = config;
        this.scanner       = scanner;
        this.historico     = new FilaHistorico(config.getCapacidadeHistorico());
        this.filaPrisao    = new FilaPrisao(6);
        this.rng           = new Random();
        this.rodadaAtual   = 0;
        this.indiceJogadorAtual = 0;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  LOOP PRINCIPAL
    // ═══════════════════════════════════════════════════════════════════════════

    public void iniciar() {
        // Posicionar todos no Início
        for (int i = 0; i < jogadores.tamanho(); i++) {
            jogadores.get(i).setPosicaoAtual(tabuleiro.getInicio());
            jogadores.get(i).setPosicaoAnterior(tabuleiro.getInicio());
        }

        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║       JOGO INICIADO!                 ║");
        System.out.println("╚══════════════════════════════════════╝");

        while (!verificarFimDeJogo()) {
            rodadaAtual++;
            System.out.println("\n══════════════════════════════════════════════════");
            System.out.printf("  RODADA %d de %d%n", rodadaAtual, config.getMaxRodadas());
            System.out.println("══════════════════════════════════════════════════");

            // Processa tentativas de saída da prisão no início de cada rodada
            processarFilaPrisao();

            // Turno de cada jogador ativo
            for (indiceJogadorAtual = 0;
                 indiceJogadorAtual < jogadores.tamanho();
                 indiceJogadorAtual++) {

                Jogador j = jogadores.get(indiceJogadorAtual);
                if (j.isFalido()) continue;
                if (j.isPreso())  continue; // já tentou sair no início da rodada

                executarTurno(j);

                if (verificarFimDeJogo()) break;
            }

            // Menu de pausa entre rodadas
            if (!verificarFimDeJogo()) {
                System.out.println("\n[ENTER] Próxima rodada  |  [H] Histórico  |  [E] Estado dos jogadores");
                String op = scanner.nextLine().trim().toUpperCase();
                if (op.equals("H")) exibirHistorico();
                else if (op.equals("E")) exibirEstadoJogadores();
            }
        }

        encerrarJogo();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TURNO DE UM JOGADOR
    // ═══════════════════════════════════════════════════════════════════════════

    private void executarTurno(Jogador jogador) {
        System.out.printf("%n  ▶ Turno de %s (%s) | Saldo: R$%,.2f%n",
                jogador.getNome(), jogador.getTipo().getNome(), jogador.getSaldo());

        int dado1 = rng.nextInt(6) + 1;
        int dado2 = rng.nextInt(6) + 1;
        int total = dado1 + dado2;
        System.out.printf("    Dados: [%d] + [%d] = %d%n", dado1, dado2, total);

        // Guardar posição anterior
        jogador.setPosicaoAnterior(jogador.getPosicaoAtual());

        // Mover
        ListaCircular.ResultadoMovimento rm = tabuleiro.avancar(jogador.getPosicaoAtual(), total);
        jogador.setPosicaoAtual(rm.destino);

        System.out.printf("    Moveu para: %s%n", jogador.getPosicaoAtual());

        // Salário por passagem pelo Início
        if (rm.passagensPeloInicio > 0) {
            double salario = config.getSalarioPorVolta();
            if (jogador.getTipo() == TipoPersonagem.ESPECULADOR) {
                salario *= 1.20;
                System.out.printf("    ⭐ ESPECULADOR: bônus de 20%% no salário!%n");
            }
            double total_salario = salario * rm.passagensPeloInicio;
            jogador.receberDinheiro(total_salario);
            jogador.incrementarVoltas();
            System.out.printf("    ✅ Passou pelo INÍCIO! Recebeu salário: R$%,.2f | Saldo: R$%,.2f%n",
                    total_salario, jogador.getSaldo());
        }

        // Aplicar efeito da casa
        String efeito = aplicarEfeitoCasa(jogador, dado1, dado2);

        // Registrar no histórico
        String entrada = String.format("R%02d | %-12s | Dados: %d+%d=%d | Casa: %-25s | %s",
                rodadaAtual, jogador.getNome(), dado1, dado2, total,
                jogador.getPosicaoAtual().getNome(), efeito);
        historico.inserir(entrada);

        // Verificar falência
        verificarFalencia(jogador);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  EFEITOS DE CASA
    // ═══════════════════════════════════════════════════════════════════════════

    private String aplicarEfeitoCasa(Jogador jogador, int dado1, int dado2) {
        NoCasa casa = jogador.getPosicaoAtual();
        switch (casa.getTipo()) {
            case INICIO:       return "Ponto de partida.";
            case IMOVEL:       return aplicarEfeitoImovel(jogador);
            case IMPOSTO:      return aplicarImposto(jogador);
            case RESTITUICAO:  return aplicarRestituicao(jogador);
            case PRISAO:       return enviarParaPrisao(jogador);
            case LEILAO:       return realizarLeilao(jogador);
            case SORTE_REVES:  return sacarCarta(jogador);
            default:           return "Sem efeito.";
        }
    }

    // ── IMÓVEL ────────────────────────────────────────────────────────────────
    private String aplicarEfeitoImovel(Jogador jogador) {
        Imovel imovel = jogador.getPosicaoAtual().getImovel();
        if (imovel == null) return "Casa de imóvel sem imóvel cadastrado.";

        if (imovel.getDono() == null) {
            // Oferecer compra
            System.out.printf("    🏠 Imóvel disponível: %s | Preço: R$%,.2f%n",
                    imovel.getNome(), imovel.getValorCompra());
            System.out.printf("    Seu saldo: R$%,.2f | Deseja comprar? (S/N): ", jogador.getSaldo());
            String resp = scanner.nextLine().trim().toUpperCase();
            if (resp.equals("S") && jogador.getSaldo() >= imovel.getValorCompra()) {
                jogador.pagarDinheiro(imovel.getValorCompra());
                jogador.adquirirImovel(imovel);
                System.out.printf("    ✅ %s comprou %s! Saldo: R$%,.2f%n",
                        jogador.getNome(), imovel.getNome(), jogador.getSaldo());
                return "Comprou " + imovel.getNome() + " por R$" + String.format("%,.2f", imovel.getValorCompra());
            } else if (resp.equals("S")) {
                System.out.println("    ❌ Saldo insuficiente.");
                imovel.registrarVisita(); // visita sem compra = valorização
                return "Sem saldo para comprar " + imovel.getNome();
            } else {
                imovel.registrarVisita();
                return "Recusou compra de " + imovel.getNome();
            }
        } else if (imovel.getDono() == jogador) {
            System.out.println("    🏠 Você parou no seu próprio imóvel. Nada acontece.");
            return "Parou no próprio imóvel " + imovel.getNome();
        } else {
            // Pagar aluguel
            Jogador dono = imovel.getDono();
            double aluguel = imovel.calcularAluguel();

            // Negociante paga 10% menos
            if (jogador.getTipo() == TipoPersonagem.NEGOCIANTE) {
                aluguel *= 0.90;
                System.out.println("    ⭐ NEGOCIANTE: desconto de 10% no aluguel!");
            }

            imovel.registrarVisita();
            jogador.pagarDinheiro(aluguel);
            dono.receberDinheiro(aluguel);

            // Estatística de maior aluguel
            imovel.registrarAluguelCobrado(aluguel);
            if (aluguel > maiorAluguelPartida) {
                maiorAluguelPartida = aluguel;
                imovelMaiorAluguel  = imovel;
            }

            System.out.printf("    💸 Aluguel: R$%,.2f (mult: %.1fx) pago a %s%n",
                    aluguel, imovel.getMultiplicador(), dono.getNome());
            System.out.printf("    Saldo %s: R$%,.2f | Saldo %s: R$%,.2f%n",
                    jogador.getNome(), jogador.getSaldo(), dono.getNome(), dono.getSaldo());
            return String.format("Pagou aluguel R$%,.2f de %s a %s", aluguel, imovel.getNome(), dono.getNome());
        }
    }

    // ── IMPOSTO ───────────────────────────────────────────────────────────────
    private String aplicarImposto(Jogador jogador) {
        double patrimonio = jogador.getPatrimonioTotal();
        double taxa = 0.05;
        if (jogador.getTipo() == TipoPersonagem.ESPECULADOR) {
            taxa = 0.055;
            System.out.println("    ⭐ ESPECULADOR: paga +10% de imposto (5,5%)!");
        }
        double imposto = patrimonio * taxa;
        jogador.pagarDinheiro(imposto);
        System.out.printf("    🏛  IMPOSTO: %.1f%% sobre patrimônio R$%,.2f = R$%,.2f | Saldo: R$%,.2f%n",
                taxa * 100, patrimonio, imposto, jogador.getSaldo());
        return String.format("Pagou imposto R$%,.2f", imposto);
    }

    // ── RESTITUIÇÃO ───────────────────────────────────────────────────────────
    private String aplicarRestituicao(Jogador jogador) {
        double valor = config.getSalarioPorVolta() * 0.10;
        jogador.receberDinheiro(valor);
        System.out.printf("    💰 RESTITUIÇÃO: recebeu R$%,.2f | Saldo: R$%,.2f%n",
                valor, jogador.getSaldo());
        return String.format("Recebeu restituição R$%,.2f", valor);
    }

    // ── PRISÃO ────────────────────────────────────────────────────────────────
    private String enviarParaPrisao(Jogador jogador) {
        jogador.entrarNaPrisao();
        // Move para casa PRISAO fisicamente
        NoCasa casaPrisao = buscarCasaPorTipo(modelo.TipoCasa.PRISAO);
        if (casaPrisao != null) jogador.setPosicaoAtual(casaPrisao);
        filaPrisao.enfileirar(jogador);
        System.out.printf("    🔒 %s foi preso! Posição na fila: %d°%n",
                jogador.getNome(), filaPrisao.tamanho());
        System.out.println("    Fila de espera da prisão:");
        filaPrisao.exibir();
        return "Enviado para a Prisão";
    }

    private void processarFilaPrisao() {
        if (filaPrisao.estaVazia()) return;

        System.out.println("\n  🔒 Tentativas de saída da prisão:");
        int qtd = filaPrisao.tamanho();

        for (int i = 0; i < qtd; i++) {
            Jogador preso = filaPrisao.espiar();
            if (preso == null || !preso.isPreso()) {
                filaPrisao.desenfileirar();
                continue;
            }

            System.out.printf("%n  → %s (tentativa %d/3)%n", preso.getNome(), preso.getTentativasPrisao() + 1);
            System.out.println("    [1] Pagar fiança (R$" + String.format("%,.2f", config.getValorFianca()) + ")");
            System.out.println("    [2] Tentar dados duplos");
            if (preso.getTipo() == TipoPersonagem.ADVOGADO && !preso.isUsouIsencaoAdvogado()) {
                System.out.println("    [3] Isenção de fiança (Advogado)");
            }
            System.out.print("    Escolha: ");
            String op = scanner.nextLine().trim();

            boolean saiu = false;

            if (op.equals("1")) {
                // Pagar fiança
                if (preso.getSaldo() >= config.getValorFianca()) {
                    preso.pagarDinheiro(config.getValorFianca());
                    preso.sairDaPrisao();
                    filaPrisao.remover(preso);
                    System.out.printf("    ✅ %s pagou fiança e saiu! Saldo: R$%,.2f%n",
                            preso.getNome(), preso.getSaldo());
                    saiu = true;
                } else {
                    System.out.println("    ❌ Saldo insuficiente para pagar fiança.");
                }
            } else if (op.equals("3") && preso.getTipo() == TipoPersonagem.ADVOGADO && !preso.isUsouIsencaoAdvogado()) {
                preso.setUsouIsencaoAdvogado(true);
                preso.sairDaPrisao();
                filaPrisao.remover(preso);
                System.out.printf("    ⭐ ADVOGADO: %s usou isenção e saiu sem custo!%n", preso.getNome());
                saiu = true;
            } else {
                // Tentar dados duplos
                int d1 = rng.nextInt(6) + 1;
                int d2 = rng.nextInt(6) + 1;
                System.out.printf("    Dados: [%d] + [%d]%n", d1, d2);
                if (d1 == d2) {
                    int avancar = d1 + d2;
                    preso.sairDaPrisao();
                    filaPrisao.remover(preso);
                    ListaCircular.ResultadoMovimento rm = tabuleiro.avancar(preso.getPosicaoAtual(), avancar);
                    preso.setPosicaoAtual(rm.destino);
                    System.out.printf("    🎲 DADOS DUPLOS! %s saiu e avançou %d casas para %s!%n",
                            preso.getNome(), avancar, preso.getPosicaoAtual().getNome());
                    saiu = true;
                } else {
                    preso.incrementarTentativaPrisao();
                    System.out.printf("    ❌ Não eram duplos. Tentativas: %d/3%n", preso.getTentativasPrisao());
                    if (preso.getTentativasPrisao() >= 3) {
                        preso.sairDaPrisao();
                        filaPrisao.remover(preso);
                        System.out.printf("    🔓 %s atingiu 3 tentativas e saiu sem jogar nesta rodada.%n", preso.getNome());
                        saiu = true;
                    }
                }
            }

            if (!saiu) {
                // Mantém na fila mas vai para o final
                filaPrisao.remover(preso);
                filaPrisao.enfileirar(preso);
            }
        }
    }

    // ── LEILÃO ────────────────────────────────────────────────────────────────
    private String realizarLeilao(Jogador ativador) {
        Imovel imovel = todosImoveis.getImoveSemDonoAleatorio();
        if (imovel == null) {
            System.out.println("    🔨 LEILÃO: Nenhum imóvel disponível para leilão.");
            return "Leilão sem imóveis disponíveis";
        }

        System.out.println("\n    🔨 LEILÃO!");
        System.out.printf("    Imóvel: %s | Preço original: R$%,.2f%n",
                imovel.getNome(), imovel.getValorCompra());
        double lanceMinimo = imovel.getValorCompra() * 0.50;
        System.out.printf("    Lance mínimo para validar: R$%,.2f (50%%)%n", lanceMinimo);

        double maiorLance = 0;
        Jogador vencedor  = null;

        // Todos os jogadores fazem lance (em ordem de turno a partir do próximo)
        int inicio = (indiceJogadorAtual + 1) % jogadores.tamanho();
        for (int i = 0; i < jogadores.tamanho(); i++) {
            Jogador j = jogadores.get((inicio + i) % jogadores.tamanho());
            if (j.isFalido()) continue;

            System.out.printf("    %s (saldo: R$%,.2f) — lance ou [ENTER] para passar: R$ ",
                    j.getNome(), j.getSaldo());
            String inp = scanner.nextLine().trim();
            if (inp.isEmpty()) {
                System.out.println("    → Passou.");
                continue;
            }
            try {
                double lance = Double.parseDouble(inp.replace(",", "."));
                if (lance <= maiorLance) {
                    System.out.printf("    → Lance R$%,.2f menor que o maior atual R$%,.2f. Ignorado.%n",
                            lance, maiorLance);
                } else if (lance > j.getSaldo()) {
                    System.out.println("    → Saldo insuficiente. Lance ignorado.");
                } else {
                    maiorLance = lance;
                    vencedor   = j;
                    System.out.printf("    → Maior lance até agora: R$%,.2f por %s!%n", lance, j.getNome());
                }
            } catch (NumberFormatException e) {
                System.out.println("    → Entrada inválida. Passou.");
            }
        }

        if (vencedor != null && maiorLance >= lanceMinimo) {
            vencedor.pagarDinheiro(maiorLance);
            vencedor.adquirirImovel(imovel);
            System.out.printf("    🏆 %s arrematou %s por R$%,.2f! Saldo: R$%,.2f%n",
                    vencedor.getNome(), imovel.getNome(), maiorLance, vencedor.getSaldo());
            return String.format("Leilão: %s arrematado por %s (R$%,.2f)", imovel.getNome(), vencedor.getNome(), maiorLance);
        } else {
            System.out.println("    ❌ Nenhum lance válido. Imóvel permanece sem dono.");
            return "Leilão sem vencedor para " + imovel.getNome();
        }
    }

    // ── SORTE / REVÉS ─────────────────────────────────────────────────────────
    private String sacarCarta(Jogador jogador) {
        Carta carta = baralho.sacarCarta();
        System.out.println("    🃏 " + carta);

        switch (carta.getTipo()) {
            case GANHO_DINHEIRO:
                jogador.receberDinheiro(carta.getValorMonetario());
                System.out.printf("    ✅ Recebeu R$%,.2f | Saldo: R$%,.2f%n",
                        carta.getValorMonetario(), jogador.getSaldo());
                break;
            case PERDA_DINHEIRO:
                jogador.pagarDinheiro(carta.getValorMonetario());
                System.out.printf("    💸 Pagou R$%,.2f | Saldo: R$%,.2f%n",
                        carta.getValorMonetario(), jogador.getSaldo());
                break;
            case AVANCAR_CASAS: {
                jogador.setPosicaoAnterior(jogador.getPosicaoAtual());
                ListaCircular.ResultadoMovimento rm = tabuleiro.avancar(jogador.getPosicaoAtual(), carta.getCasas());
                jogador.setPosicaoAtual(rm.destino);
                if (rm.passagensPeloInicio > 0) {
                    double sal = config.getSalarioPorVolta();
                    if (jogador.getTipo() == TipoPersonagem.ESPECULADOR) sal *= 1.20;
                    jogador.receberDinheiro(sal);
                    System.out.printf("    ✅ Passou pelo INÍCIO durante carta! Salário R$%,.2f%n", sal);
                }
                System.out.printf("    ➡ Avançou %d casas para %s%n", carta.getCasas(), jogador.getPosicaoAtual().getNome());
                // Aplicar efeito da nova casa
                aplicarEfeitoCasa(jogador, 0, 0);
                break;
            }
            case RECUAR_CASAS: {
                jogador.setPosicaoAnterior(jogador.getPosicaoAtual());
                NoCasa dest = tabuleiro.recuar(jogador.getPosicaoAtual(), carta.getCasas());
                jogador.setPosicaoAtual(dest);
                System.out.printf("    ⬅ Recuou %d casas para %s (retrocesso não concede salário)%n",
                        carta.getCasas(), jogador.getPosicaoAtual().getNome());
                aplicarEfeitoCasa(jogador, 0, 0);
                break;
            }
            case IR_PARA_INICIO: {
                // Avança até o início (conta como volta)
                jogador.setPosicaoAnterior(jogador.getPosicaoAtual());
                // Determina quantas casas faltam para chegar ao início
                int casasFaltam = 0;
                NoCasa temp = jogador.getPosicaoAtual();
                while (temp != tabuleiro.getInicio()) {
                    temp = temp.getProximo();
                    casasFaltam++;
                }
                if (casasFaltam > 0) {
                    double sal = config.getSalarioPorVolta();
                    if (jogador.getTipo() == TipoPersonagem.ESPECULADOR) sal *= 1.20;
                    jogador.receberDinheiro(sal);
                    System.out.printf("    ✅ Avançou ao INÍCIO! Recebeu salário R$%,.2f | Saldo: R$%,.2f%n",
                            sal, jogador.getSaldo());
                }
                jogador.setPosicaoAtual(tabuleiro.getInicio());
                jogador.incrementarVoltas();
                System.out.println("    ➡ Está no INÍCIO agora.");
                break;
            }
            case IR_PARA_PRISAO:
                return enviarParaPrisao(jogador);
            case VOLTAR_ULTIMA_CASA: {
                NoCasa ultima = jogador.getPosicaoAnterior();
                jogador.setPosicaoAtual(ultima);
                System.out.printf("    ⬅ Voltou para a última casa: %s (não recebe salário)%n", ultima.getNome());
                aplicarEfeitoCasa(jogador, 0, 0);
                break;
            }
            case RECEBER_DE_JOGADORES: {
                double totalRecebido = 0;
                for (int i = 0; i < jogadores.tamanho(); i++) {
                    Jogador outro = jogadores.get(i);
                    if (outro == jogador || outro.isFalido()) continue;
                    outro.pagarDinheiro(carta.getValorMonetario());
                    jogador.receberDinheiro(carta.getValorMonetario());
                    totalRecebido += carta.getValorMonetario();
                    System.out.printf("    → %s pagou R$%,.2f%n", outro.getNome(), carta.getValorMonetario());
                }
                System.out.printf("    ✅ Total recebido: R$%,.2f | Saldo: R$%,.2f%n",
                        totalRecebido, jogador.getSaldo());
                break;
            }
            case PAGAR_A_JOGADORES: {
                for (int i = 0; i < jogadores.tamanho(); i++) {
                    Jogador outro = jogadores.get(i);
                    if (outro == jogador || outro.isFalido()) continue;
                    jogador.pagarDinheiro(carta.getValorMonetario());
                    outro.receberDinheiro(carta.getValorMonetario());
                    System.out.printf("    → Pagou R$%,.2f a %s%n", carta.getValorMonetario(), outro.getNome());
                }
                System.out.printf("    Saldo após: R$%,.2f%n", jogador.getSaldo());
                break;
            }
        }
        return carta.getDescricao();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  FALÊNCIA
    // ═══════════════════════════════════════════════════════════════════════════

    private void verificarFalencia(Jogador jogador) {
        if (jogador.getSaldo() < 0 && jogador.getPropriedades().tamanho() == 0) {
            jogador.setFalido(true);
            System.out.printf("%n  ☠  %s está FALIDO e foi eliminado da partida!%n", jogador.getNome());
            System.out.println("  Suas propriedades retornam ao pool de leilão.");
            jogador.liberarTodosImoveis();
            filaPrisao.remover(jogador);
            historico.inserir(String.format("R%02d | %-12s | FALÊNCIA DECLARADA", rodadaAtual, jogador.getNome()));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  UTILITÁRIOS
    // ═══════════════════════════════════════════════════════════════════════════

    private NoCasa buscarCasaPorTipo(modelo.TipoCasa tipo) {
        NoCasa atual = tabuleiro.getInicio();
        do {
            if (atual.getTipo() == tipo) return atual;
            atual = atual.getProximo();
        } while (atual != tabuleiro.getInicio());
        return null;
    }

    private boolean verificarFimDeJogo() {
        if (rodadaAtual >= config.getMaxRodadas()) return true;
        int ativos = 0;
        for (int i = 0; i < jogadores.tamanho(); i++) {
            if (!jogadores.get(i).isFalido()) ativos++;
        }
        return ativos <= 1;
    }

    private void exibirHistorico() {
        System.out.println("\n── HISTÓRICO DE RODADAS ──────────────────────────────────────");
        historico.exibir();
        System.out.println("──────────────────────────────────────────────────────────────");
    }

    private void exibirEstadoJogadores() {
        System.out.println("\n── ESTADO DOS JOGADORES ──────────────────────────────────────");
        for (int i = 0; i < jogadores.tamanho(); i++) {
            Jogador j = jogadores.get(i);
            System.out.println("  " + j);
            System.out.printf("    Patrimônio total: R$%,.2f%n", j.getPatrimonioTotal());
            if (j.getPropriedades().tamanho() > 0) {
                System.out.println("    Imóveis:");
                j.getPropriedades().exibir();
            }
        }
        System.out.println("──────────────────────────────────────────────────────────────");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  ENCERRAMENTO
    // ═══════════════════════════════════════════════════════════════════════════

    private void encerrarJogo() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                   FIM DE JOGO!                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // Classificação por patrimônio (bubble sort simples)
        Jogador[] rank = new Jogador[jogadores.tamanho()];
        for (int i = 0; i < jogadores.tamanho(); i++) rank[i] = jogadores.get(i);
        for (int i = 0; i < rank.length - 1; i++) {
            for (int j = 0; j < rank.length - 1 - i; j++) {
                if (rank[j].getPatrimonioTotal() < rank[j + 1].getPatrimonioTotal()) {
                    Jogador tmp = rank[j]; rank[j] = rank[j + 1]; rank[j + 1] = tmp;
                }
            }
        }

        System.out.println("\n  🏆 CLASSIFICAÇÃO FINAL (por patrimônio):");
        System.out.println("  ─────────────────────────────────────────────────────────");
        for (int i = 0; i < rank.length; i++) {
            Jogador j = rank[i];
            System.out.printf("  %d° | %-15s | Patrimônio: R$%,.2f | Voltas: %d | %s%n",
                    i + 1, j.getNome(), j.getPatrimonioTotal(), j.getVoltasCompletas(),
                    j.isFalido() ? "FALIDO" : j.getTipo().getNome());
        }

        System.out.println("\n  📊 ESTATÍSTICAS:");
        if (imovelMaiorAluguel != null) {
            System.out.printf("  🏠 Imóvel com maior aluguel cobrado: %s (R$%,.2f)%n",
                    imovelMaiorAluguel.getNome(), maiorAluguelPartida);
        }
        System.out.printf("  📅 Rodadas jogadas: %d de %d%n", rodadaAtual, config.getMaxRodadas());

        System.out.println("\n  📋 HISTÓRICO DAS ÚLTIMAS RODADAS:");
        exibirHistorico();
    }
}
