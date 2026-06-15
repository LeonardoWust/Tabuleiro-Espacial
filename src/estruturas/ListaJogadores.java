package estruturas;

import modelo.Jogador;

/**
 * Lista de jogadores implementada como array dinâmico.
 *
 * Justificativa: O array dinâmico com acesso por índice é ideal porque:
 * - A ordem de turno é fixa e acessada ciclicamente pelo índice.
 * - Remoção de falidos é rara (no máximo 6 jogadores, custo O(n) aceitável).
 * - Busca sequencial é suficiente para N ≤ 6.
 * - Acesso O(1) ao jogador atual do turno (via índice).
 */
public class ListaJogadores {

    private Jogador[] dados;
    private int quantidade;
    private static final int CAPACIDADE_MAX = 6;

    public ListaJogadores() {
        this.dados     = new Jogador[CAPACIDADE_MAX];
        this.quantidade = 0;
    }

    public boolean adicionar(Jogador jogador) {
        if (quantidade >= CAPACIDADE_MAX) return false;
        dados[quantidade++] = jogador;
        return true;
    }

    public Jogador get(int indice) {
        if (indice < 0 || indice >= quantidade) return null;
        return dados[indice];
    }

    public boolean remover(Jogador jogador) {
        for (int i = 0; i < quantidade; i++) {
            if (dados[i] == jogador) {
                for (int j = i; j < quantidade - 1; j++) {
                    dados[j] = dados[j + 1];
                }
                dados[--quantidade] = null;
                return true;
            }
        }
        return false;
    }

    public Jogador buscarPorId(int id) {
        for (int i = 0; i < quantidade; i++) {
            if (dados[i].getId() == id) return dados[i];
        }
        return null;
    }

    public int tamanho()    { return quantidade; }
    public boolean cheia()  { return quantidade >= CAPACIDADE_MAX; }

    public void exibir() {
        if (quantidade == 0) {
            System.out.println("  (nenhum jogador cadastrado)");
            return;
        }
        for (int i = 0; i < quantidade; i++) {
            System.out.println("  " + dados[i]);
        }
    }
}
