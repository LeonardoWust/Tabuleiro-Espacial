package estruturas;

import modelo.Carta;

/**
 * Pilha (LIFO) implementada manualmente com array.
 * Gerencia o baralho de cartas do jogo.
 * Quando se esgota, é remontada e embaralhada automaticamente.
 */
public class PilhaBaralho {

    private static final int CAPACIDADE_MAX = 100;

    private final Carta[] cartasOriginais; // cópia permanente para reabastecimento
    private int totalOriginais;

    private final Carta[] pilha;
    private int topo; // índice do próximo elemento a ser inserido

    public PilhaBaralho() {
        this.cartasOriginais = new Carta[CAPACIDADE_MAX];
        this.pilha           = new Carta[CAPACIDADE_MAX];
        this.totalOriginais  = 0;
        this.topo            = 0;
    }

    /** Adiciona uma carta ao baralho original e empilha na pilha atual. */
    public void adicionarCarta(Carta carta) {
        if (totalOriginais < CAPACIDADE_MAX) {
            cartasOriginais[totalOriginais++] = carta;
            empilhar(carta);
        }
    }

    private void empilhar(Carta carta) {
        if (topo < CAPACIDADE_MAX) {
            pilha[topo++] = carta;
        }
    }

    /**
     * Remove e retorna a carta do topo (LIFO).
     * Se a pilha estiver vazia, reabastece automaticamente.
     */
    public Carta sacarCarta() {
        if (topo == 0) {
            reabastecer();
        }
        return pilha[--topo];
    }

    public boolean estaVazia() { return topo == 0; }
    public int     tamanho()   { return topo; }

    /**
     * Reabastecimento: copia as cartas originais, embaralha usando
     * Fisher-Yates e empilha na ordem embaralhada.
     */
    private void reabastecer() {
        System.out.println("\n  ♻  Baralho esgotado! Remontando e embaralhando automaticamente...");

        // Copia para array temporário
        Carta[] temp = new Carta[totalOriginais];
        System.arraycopy(cartasOriginais, 0, temp, 0, totalOriginais);

        // Fisher-Yates shuffle
        java.util.Random rng = new java.util.Random();
        for (int i = totalOriginais - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            Carta aux = temp[i];
            temp[i]   = temp[j];
            temp[j]   = aux;
        }

        // Empilha (a primeira do array fica no fundo, a última no topo)
        topo = 0;
        for (int i = 0; i < totalOriginais; i++) {
            empilhar(temp[i]);
        }
        System.out.println("  ♻  Baralho remontado com " + topo + " cartas.\n");
    }
}
