package estruturas;

import modelo.Jogador;

/**
 * Fila FIFO para gerenciar a ordem de soltura dos jogadores presos.
 * Implementação manual com array de tamanho fixo (máximo 6 jogadores).
 */
public class FilaPrisao {

    private final Jogador[] fila;
    private int frente;
    private int fundo;
    private int quantidade;
    private final int capacidade;

    public FilaPrisao(int capacidade) {
        this.capacidade = capacidade;
        this.fila       = new Jogador[capacidade];
        this.frente     = 0;
        this.fundo      = 0;
        this.quantidade = 0;
    }

    public void enfileirar(Jogador jogador) {
        if (quantidade < capacidade) {
            fila[fundo] = jogador;
            fundo = (fundo + 1) % capacidade;
            quantidade++;
        }
    }

    public Jogador desenfileirar() {
        if (quantidade == 0) return null;
        Jogador j = fila[frente];
        fila[frente] = null;
        frente = (frente + 1) % capacidade;
        quantidade--;
        return j;
    }

    public Jogador espiar() {
        if (quantidade == 0) return null;
        return fila[frente];
    }

    /** Remove um jogador específico da fila (saiu da prisão por dados/fiança). */
    public void remover(Jogador jogador) {
        Jogador[] temp = new Jogador[quantidade];
        int count = 0;
        for (int i = 0; i < quantidade; i++) {
            Jogador j = fila[(frente + i) % capacidade];
            if (j != jogador) {
                temp[count++] = j;
            }
        }
        frente = 0;
        fundo  = 0;
        quantidade = 0;
        for (int i = 0; i < count; i++) {
            enfileirar(temp[i]);
        }
    }

    public boolean contem(Jogador jogador) {
        for (int i = 0; i < quantidade; i++) {
            if (fila[(frente + i) % capacidade] == jogador) return true;
        }
        return false;
    }

    public boolean estaVazia() { return quantidade == 0; }
    public int     tamanho()   { return quantidade; }

    public void exibir() {
        if (quantidade == 0) {
            System.out.println("  (nenhum jogador preso)");
            return;
        }
        for (int i = 0; i < quantidade; i++) {
            Jogador j = fila[(frente + i) % capacidade];
            System.out.printf("  %d° na fila: %s (tentativas: %d/3)%n",
                    i + 1, j.getNome(), j.getTentativasPrisao());
        }
    }
}
