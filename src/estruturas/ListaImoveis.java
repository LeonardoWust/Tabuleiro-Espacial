package estruturas;

import modelo.Imovel;

/**
 * Lista dinâmica simples para armazenar imóveis.
 * Usada tanto na lista geral de imóveis do jogo quanto na lista de propriedades de cada jogador.
 */
public class ListaImoveis {

    private Imovel[] dados;
    private int quantidade;
    private static final int CAPACIDADE_INICIAL = 50;

    public ListaImoveis() {
        this.dados     = new Imovel[CAPACIDADE_INICIAL];
        this.quantidade = 0;
    }

    public void adicionar(Imovel imovel) {
        if (quantidade == dados.length) {
            // dobra capacidade
            Imovel[] novo = new Imovel[dados.length * 2];
            System.arraycopy(dados, 0, novo, 0, quantidade);
            dados = novo;
        }
        dados[quantidade++] = imovel;
    }

    public Imovel get(int indice) {
        if (indice < 0 || indice >= quantidade) return null;
        return dados[indice];
    }

    public boolean remover(Imovel imovel) {
        for (int i = 0; i < quantidade; i++) {
            if (dados[i] == imovel) {
                return removerPorIndice(i);
            }
        }
        return false;
    }

    public boolean removerPorIndice(int indice) {
        if (indice < 0 || indice >= quantidade) return false;
        for (int i = indice; i < quantidade - 1; i++) {
            dados[i] = dados[i + 1];
        }
        dados[--quantidade] = null;
        return true;
    }

    public Imovel buscarPorId(int id) {
        for (int i = 0; i < quantidade; i++) {
            if (dados[i].getId() == id) return dados[i];
        }
        return null;
    }

    public int tamanho() { return quantidade; }

    /** Retorna imóvel aleatório sem dono (para leilão). */
    public Imovel getImoveSemDonoAleatorio() {
        // coleta índices sem dono
        int[] semDono = new int[quantidade];
        int count = 0;
        for (int i = 0; i < quantidade; i++) {
            if (dados[i].getDono() == null) semDono[count++] = i;
        }
        if (count == 0) return null;
        int escolha = new java.util.Random().nextInt(count);
        return dados[semDono[escolha]];
    }

    public void exibir() {
        if (quantidade == 0) {
            System.out.println("  (nenhum imóvel cadastrado)");
            return;
        }
        for (int i = 0; i < quantidade; i++) {
            System.out.println("  " + dados[i]);
        }
    }
}
