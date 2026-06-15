package tabuleiro;

import modelo.Imovel;
import modelo.TipoCasa;

/**
 * Lista Duplamente Ligada Circular.
 * Cada nó é uma casa do tabuleiro.
 * next  → sentido horário (avanço normal)
 * prev  → sentido anti-horário (retrocesso por carta de Revés)
 */
public class ListaCircular {
    private NoCasa inicio;   // nó da casa INÍCIO (posição 0)
    private int tamanho;

    public ListaCircular() {
        this.inicio = null;
        this.tamanho = 0;
    }

    /** Adiciona uma nova casa no final (antes de fechar o ciclo). */
    public void adicionarCasa(TipoCasa tipo, String nome, Imovel imovel) {
        NoCasa novo = new NoCasa(tamanho, tipo, nome, imovel);

        if (inicio == null) {
            // Único nó: aponta para si mesmo
            novo.proximo  = novo;
            novo.anterior = novo;
            inicio = novo;
        } else {
            // Último nó atual (o que aponta para início)
            NoCasa ultimo = inicio.anterior;
            // Inserir novo entre ultimo e inicio
            ultimo.proximo = novo;
            novo.anterior  = ultimo;
            novo.proximo   = inicio;
            inicio.anterior = novo;
        }
        tamanho++;
    }

    /**
     * Avança 'passos' casas a partir de 'atual' no sentido horário.
     * Retorna o nó de destino e conta quantas vezes passou pelo Início.
     */
    public ResultadoMovimento avancar(NoCasa atual, int passos) {
        NoCasa no = atual;
        int passagensPeloInicio = 0;

        for (int i = 0; i < passos; i++) {
            no = no.proximo;
            if (no == inicio && i < passos - 1) {
                // passou pelo Início mas não parou nele
                passagensPeloInicio++;
            }
        }
        // Se parou exatamente no Início também conta
        if (no == inicio) {
            passagensPeloInicio++;
        }
        return new ResultadoMovimento(no, passagensPeloInicio);
    }

    /**
     * Recua 'passos' casas a partir de 'atual' no sentido anti-horário.
     * Passagens pelo Início durante retrocesso NÃO concedem salário.
     */
    public NoCasa recuar(NoCasa atual, int passos) {
        NoCasa no = atual;
        for (int i = 0; i < passos; i++) {
            no = no.anterior;
        }
        return no;
    }

    public NoCasa getInicio()  { return inicio; }
    public int    getTamanho() { return tamanho; }

    /** Exibe todas as casas em ordem circular. */
    public void exibirTabuleiro() {
        if (inicio == null) return;
        NoCasa atual = inicio;
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                     TABULEIRO                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        do {
            System.out.println("  " + atual);
            atual = atual.proximo;
        } while (atual != inicio);
        System.out.println("  [Circular: última casa → Casa 0 (Início)]");
    }

    // ── Classe auxiliar de resultado ──────────────────────────────────────────
    public static class ResultadoMovimento {
        public final NoCasa destino;
        public final int passagensPeloInicio;

        public ResultadoMovimento(NoCasa destino, int passagensPeloInicio) {
            this.destino = destino;
            this.passagensPeloInicio = passagensPeloInicio;
        }
    }
}
