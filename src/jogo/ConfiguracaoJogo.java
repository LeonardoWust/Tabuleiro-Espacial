package jogo;

public class ConfiguracaoJogo {
    private double saldoInicial      = 15_000.0;
    private double salarioPorVolta   = 2_000.0;
    private int    maxRodadas        = 50;
    private double valorFianca       = 200.0;   // 10% do salário padrão
    private int    capacidadeHistorico = 10;

    // ── Getters / Setters ──────────────────────────────────────────────────────
    public double getSaldoInicial()          { return saldoInicial; }
    public void   setSaldoInicial(double v)  { saldoInicial = v; }

    public double getSalarioPorVolta()          { return salarioPorVolta; }
    public void   setSalarioPorVolta(double v)  { salarioPorVolta = v; }

    public int  getMaxRodadas()         { return maxRodadas; }
    public void setMaxRodadas(int v)    { maxRodadas = v; }

    public double getValorFianca()          { return valorFianca; }
    public void   setValorFianca(double v)  { valorFianca = v; }

    public int  getCapacidadeHistorico()         { return capacidadeHistorico; }
    public void setCapacidadeHistorico(int v)    { capacidadeHistorico = v; }

    @Override
    public String toString() {
        return String.format(
            "  Saldo inicial:       R$%,.2f%n" +
            "  Salário por volta:   R$%,.2f%n" +
            "  Máx. de rodadas:     %d%n" +
            "  Fiança:              R$%,.2f%n" +
            "  Capacidade histórico:%d",
            saldoInicial, salarioPorVolta, maxRodadas,
            valorFianca, capacidadeHistorico);
    }
}
