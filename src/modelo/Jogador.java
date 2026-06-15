package modelo;

import estruturas.ListaImoveis;
import tabuleiro.NoCasa;

public class Jogador {
    private static int contadorId = 1;

    private final int id;
    private String nome;
    private double saldo;
    private TipoPersonagem tipo;

    private NoCasa posicaoAtual;
    private NoCasa posicaoAnterior;   // para carta "volte à última casa"

    private final ListaImoveis propriedades;

    private boolean preso;
    private int tentativasPrisao;
    private boolean usouIsencaoAdvogado;

    private int voltasCompletas;
    private boolean falido;

    public Jogador(String nome, double saldoInicial, TipoPersonagem tipo) {
        this.id = contadorId++;
        this.nome = nome;
        this.saldo = saldoInicial;
        this.tipo = tipo;
        this.propriedades = new ListaImoveis();
        this.preso = false;
        this.tentativasPrisao = 0;
        this.usouIsencaoAdvogado = false;
        this.voltasCompletas = 0;
        this.falido = false;
    }

    // ── Saldo ──────────────────────────────────────────────────────────────────
    public void receberDinheiro(double valor) { saldo += valor; }

    public boolean pagarDinheiro(double valor) {
        saldo -= valor;
        return saldo >= 0;
    }

    // ── Patrimônio total ───────────────────────────────────────────────────────
    public double getPatrimonioTotal() {
        double total = saldo;
        for (int i = 0; i < propriedades.tamanho(); i++) {
            total += propriedades.get(i).getValorCompra();
        }
        return total;
    }

    // ── Propriedades ───────────────────────────────────────────────────────────
    public void adquirirImovel(Imovel imovel) {
        imovel.setDono(this);
        propriedades.adicionar(imovel);
    }

    public void perderImovel(Imovel imovel) {
        imovel.liberarPropriedade();
        propriedades.remover(imovel);
    }

    public void liberarTodosImoveis() {
        while (propriedades.tamanho() > 0) {
            Imovel im = propriedades.get(0);
            im.liberarPropriedade();
            propriedades.removerPorIndice(0);
        }
    }

    // ── Prisão ─────────────────────────────────────────────────────────────────
    public void entrarNaPrisao() {
        preso = true;
        tentativasPrisao = 0;
    }

    public void sairDaPrisao() {
        preso = false;
        tentativasPrisao = 0;
    }

    public void incrementarTentativaPrisao() { tentativasPrisao++; }

    // ── Getters / Setters ──────────────────────────────────────────────────────
    public int    getId()                        { return id; }
    public String getNome()                      { return nome; }
    public void   setNome(String nome)           { this.nome = nome; }
    public double getSaldo()                     { return saldo; }
    public void   setSaldo(double saldo)         { this.saldo = saldo; }
    public TipoPersonagem getTipo()              { return tipo; }
    public void   setTipo(TipoPersonagem tipo)   { this.tipo = tipo; }
    public NoCasa getPosicaoAtual()              { return posicaoAtual; }
    public void   setPosicaoAtual(NoCasa no)     { posicaoAtual = no; }
    public NoCasa getPosicaoAnterior()           { return posicaoAnterior; }
    public void   setPosicaoAnterior(NoCasa no)  { posicaoAnterior = no; }
    public ListaImoveis getPropriedades()        { return propriedades; }
    public boolean isPreso()                     { return preso; }
    public int    getTentativasPrisao()          { return tentativasPrisao; }
    public boolean isUsouIsencaoAdvogado()       { return usouIsencaoAdvogado; }
    public void   setUsouIsencaoAdvogado(boolean b) { usouIsencaoAdvogado = b; }
    public int    getVoltasCompletas()           { return voltasCompletas; }
    public void   incrementarVoltas()            { voltasCompletas++; }
    public boolean isFalido()                    { return falido; }
    public void   setFalido(boolean falido)      { this.falido = falido; }

    @Override
    public String toString() {
        return String.format("[#%d] %-15s | %s | Saldo: R$%,.2f | Voltas: %d | Imóveis: %d%s",
                id, nome, tipo.getNome(), saldo, voltasCompletas,
                propriedades.tamanho(), preso ? " [PRESO]" : "");
    }

    public static void resetContador() { contadorId = 1; }
}
