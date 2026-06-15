package modelo;

public class Imovel {
    private static int contadorId = 1;

    private final int id;
    private String nome;
    private double valorCompra;
    private double aluguelBase;
    private Jogador dono;
    private int visitasAluguel;          // visitas de outros jogadores
    private double multiplicadorDemanda; // começa em 1.0, +0.1 por visita, max 2.0
    private double maiorAluguelCobrado;

    public Imovel(String nome, double valorCompra, double aluguelBase) {
        this.id = contadorId++;
        this.nome = nome;
        this.valorCompra = valorCompra;
        this.aluguelBase = aluguelBase;
        this.dono = null;
        this.visitasAluguel = 0;
        this.multiplicadorDemanda = 1.0;
        this.maiorAluguelCobrado = 0.0;
    }

    /** Calcula o aluguel atual levando em conta multiplicador e bônus de Construtor. */
    public double calcularAluguel() {
        double base = aluguelBase;
        if (dono != null && dono.getTipo() == TipoPersonagem.CONSTRUTOR) {
            base *= 1.15;
        }
        return base * multiplicadorDemanda;
    }

    /** Registra uma visita de outro jogador (valorização por demanda). */
    public void registrarVisita() {
        visitasAluguel++;
        if (multiplicadorDemanda < 2.0) {
            multiplicadorDemanda = Math.min(2.0, multiplicadorDemanda + 0.1);
        }
    }

    public void registrarAluguelCobrado(double valor) {
        if (valor > maiorAluguelCobrado) maiorAluguelCobrado = valor;
    }

    public void liberarPropriedade() {
        this.dono = null;
        this.multiplicadorDemanda = 1.0;
        this.visitasAluguel = 0;
    }

    // ── Getters / Setters ──────────────────────────────────────────────────────
    public int    getId()                  { return id; }
    public String getNome()                { return nome; }
    public void   setNome(String nome)     { this.nome = nome; }
    public double getValorCompra()         { return valorCompra; }
    public void   setValorCompra(double v) { this.valorCompra = v; }
    public double getAluguelBase()         { return aluguelBase; }
    public void   setAluguelBase(double v) { this.aluguelBase = v; }
    public Jogador getDono()               { return dono; }
    public void    setDono(Jogador dono)   { this.dono = dono; }
    public double  getMultiplicador()      { return multiplicadorDemanda; }
    public double  getMaiorAluguelCobrado(){ return maiorAluguelCobrado; }

    public double getPatrimonio() { return valorCompra; }

    @Override
    public String toString() {
        String donoStr = (dono == null) ? "Sem dono" : dono.getNome();
        return String.format("[#%d] %-30s | Compra: R$%,.2f | Aluguel base: R$%,.2f | Mult: %.1fx | Dono: %s",
                id, nome, valorCompra, aluguelBase, multiplicadorDemanda, donoStr);
    }

    public static void resetContador() { contadorId = 1; }
}
