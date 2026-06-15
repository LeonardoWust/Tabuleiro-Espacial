package modelo;

public class Carta {

    public enum TipoCarta {
        GANHO_DINHEIRO,
        PERDA_DINHEIRO,
        AVANCAR_CASAS,
        RECUAR_CASAS,
        IR_PARA_INICIO,
        IR_PARA_PRISAO,
        VOLTAR_ULTIMA_CASA,
        RECEBER_DE_JOGADORES,
        PAGAR_A_JOGADORES
    }

    private final String descricao;
    private final TipoCarta tipo;
    private final double valorMonetario; // positivo = ganho, negativo = perda
    private final int casas;             // número de casas a avançar/recuar

    public Carta(String descricao, TipoCarta tipo, double valorMonetario, int casas) {
        this.descricao = descricao;
        this.tipo = tipo;
        this.valorMonetario = valorMonetario;
        this.casas = casas;
    }

    public String    getDescricao()      { return descricao; }
    public TipoCarta getTipo()           { return tipo; }
    public double    getValorMonetario() { return valorMonetario; }
    public int       getCasas()          { return casas; }

    @Override
    public String toString() { return "[CARTA] " + descricao; }
}
