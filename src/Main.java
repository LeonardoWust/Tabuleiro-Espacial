import estruturas.*;
import jogo.*;
import tabuleiro.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║        JOGO DE TABULEIRO ESTRATÉGICO — ESPAÇO & SCI-FI      ║");
        System.out.println("║         Estruturas de Dados | Java | Terminal                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // Instâncias compartilhadas
        ListaJogadores  jogadores    = new ListaJogadores();
        ListaImoveis    todosImoveis = new ListaImoveis();
        ConfiguracaoJogo config      = new ConfiguracaoJogo();

        // Fase pré-jogo
        GerenciadorPreJogo preJogo = new GerenciadorPreJogo(jogadores, todosImoveis, config, scanner);
        preJogo.executarMenu();

        // Montar tabuleiro e baralho
        ListaCircular tabuleiro = preJogo.montarTabuleiro();
        PilhaBaralho  baralho   = preJogo.montarBaralho();

        System.out.println("\n  📋 Tabuleiro montado:");
        tabuleiro.exibirTabuleiro();

        System.out.println("\n  Pressione ENTER para iniciar a partida...");
        scanner.nextLine();

        // Fase de jogo
        MotorJogo motor = new MotorJogo(tabuleiro, jogadores, todosImoveis, baralho, config, scanner);
        motor.iniciar();

        scanner.close();
    }
}
