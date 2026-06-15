package modelo;

public enum TipoPersonagem {
    ESPECULADOR("Especulador", "+20% salário ao completar volta, +10% imposto"),
    NEGOCIANTE("Negociante",  "Paga 10% a menos de aluguel"),
    ADVOGADO("Advogado",      "Sai da prisão sem custo uma vez por jogo"),
    CONSTRUTOR("Construtor",  "Imóveis comprados têm aluguel +15%");

    private final String nome;
    private final String descricao;

    TipoPersonagem(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getNome()      { return nome; }
    public String getDescricao() { return descricao; }

    @Override
    public String toString() { return nome + " — " + descricao; }
}
