package tabuleiro;

import modelo.Imovel;
import modelo.TipoCasa;

public class NoCasa {
    private final int posicao;       // índice no tabuleiro (0 = Início)
    private final TipoCasa tipo;
    private final String nome;
    private final Imovel imovel;     // só preenchido se tipo == IMOVEL

    NoCasa proximo;
    NoCasa anterior;

    public NoCasa(int posicao, TipoCasa tipo, String nome, Imovel imovel) {
        this.posicao = posicao;
        this.tipo    = tipo;
        this.nome    = nome;
        this.imovel  = imovel;
    }

    public int      getPosicao()  { return posicao; }
    public TipoCasa getTipo()     { return tipo; }
    public String   getNome()     { return nome; }
    public Imovel   getImovel()   { return imovel; }
    public NoCasa   getProximo()  { return proximo; }
    public NoCasa   getAnterior() { return anterior; }

    @Override
    public String toString() {
        return String.format("Casa[%2d] %-12s | %s", posicao, tipo, nome);
    }
}
