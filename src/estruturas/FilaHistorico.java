package estruturas;

/**
 * Fila circular com capacidade fixa N.
 * Quando cheia, descarta automaticamente a entrada mais antiga (FIFO).
 * Usada tanto para o histórico de rodadas quanto para a fila de prisão.
 */
public class FilaHistorico {

    private final String[] dados;
    private int frente;    // índice do primeiro elemento
    private int fundo;     // índice do próximo slot livre
    private int quantidade;
    private final int capacidade;

    public FilaHistorico(int capacidade) {
        this.capacidade = capacidade;
        this.dados      = new String[capacidade];
        this.frente     = 0;
        this.fundo      = 0;
        this.quantidade = 0;
    }

    /** Insere uma entrada. Se cheia, descarta a mais antiga. */
    public void inserir(String entrada) {
        if (quantidade == capacidade) {
            // descarta o mais antigo (avança frente)
            frente = (frente + 1) % capacidade;
            quantidade--;
        }
        dados[fundo] = entrada;
        fundo = (fundo + 1) % capacidade;
        quantidade++;
    }

    /** Remove e retorna o elemento mais antigo (FIFO). */
    public String remover() {
        if (quantidade == 0) return null;
        String val = dados[frente];
        frente = (frente + 1) % capacidade;
        quantidade--;
        return val;
    }

    /** Retorna o elemento mais antigo sem remover. */
    public String espiar() {
        if (quantidade == 0) return null;
        return dados[frente];
    }

    public boolean estaVazia() { return quantidade == 0; }
    public int     tamanho()   { return quantidade; }
    public int     getCapacidade() { return capacidade; }

    /** Exibe todos os elementos da fila sem removê-los. */
    public void exibir() {
        if (quantidade == 0) {
            System.out.println("  (fila vazia)");
            return;
        }
        for (int i = 0; i < quantidade; i++) {
            int idx = (frente + i) % capacidade;
            System.out.println("  " + (i + 1) + ". " + dados[idx]);
        }
    }
}
