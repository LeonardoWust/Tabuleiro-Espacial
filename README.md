# 🚀 Tabuleiro Espacial

Jogo de tabuleiro estratégico para terminal com tema de **Espaço & Sci-Fi**, desenvolvido em Java.  
Projeto acadêmico da disciplina de **Estruturas de Dados** — todas as estruturas foram implementadas manualmente, sem uso de `ArrayList`, `Stack`, `Queue` ou qualquer classe do `java.util.Collection`.

---

## 🎮 Sobre o Jogo

Cada jogador escolhe um personagem com habilidade única e percorre um tabuleiro circular de 22 casas comprando imóveis, pagando aluguéis, participando de leilões e enfrentando cartas de Sorte/Revés.

**Vence quem tiver o maior patrimônio ao fim das rodadas.**

---

## 👾 Personagens

| Personagem | Habilidade |
|---|---|
| Especulador | +20% no salário ao passar pelo Início |
| Negociante | Paga 10% a menos de aluguel |
| Advogado | Sai da prisão sem custo uma vez por partida |
| Construtor | Imóveis próprios geram +15% de aluguel |

---

## 🗺️ Tabuleiro

22 casas com os seguintes tipos:

- 🏠 **Imóvel** — compra, aluguel ou valorização por demanda
- 🔨 **Leilão** — imóvel sorteado, lance mínimo de 50% do valor
- 💸 **Imposto** — paga 5% do patrimônio total
- 💰 **Restituição** — recebe 10% do salário por volta
- 🃏 **Sorte/Revés** — saca uma carta com efeito aleatório
- 🚔 **Prisão** — tenta sair por dados duplos, fiança ou isenção do Advogado
- 🏁 **Início** — receber salário ao passar

---

## 🧱 Estruturas de Dados Implementadas

| Estrutura | Tipo | Uso no jogo |
|---|---|---|
| `ListaCircular` | Lista duplamente ligada circular | Tabuleiro |
| `PilhaBaralho` | Pilha LIFO com reabastecimento | Baralho de cartas |
| `FilaPrisao` | Fila FIFO | Ordem de tentativas de saída |
| `FilaHistorico` | Fila circular com descarte | Histórico das últimas rodadas |
| `ListaJogadores` | Array dinâmico | Lista de participantes |
| `ListaImoveis` | Array com redimensionamento | Catálogo e propriedades |

---

## ▶️ Como Executar

**Pré-requisito:** Java 21 ou superior instalado.

```bash
# Compilar
javac -d bin $(find src -name "*.java")

# Executar
java -cp bin Main
```

> No Windows, use o `rodar.bat`. No Linux/Mac, use o `rodar.sh`.

---

## 📁 Estrutura do Projeto

```
tabuleiro/
├── src/
│   ├── Main.java
│   ├── modelo/        # Jogador, Imovel, Carta, enums
│   ├── estruturas/    # Estruturas de dados manuais
│   ├── tabuleiro/     # ListaCircular e NoCasa
│   └── jogo/          # ConfiguracaoJogo, GerenciadorPreJogo, MotorJogo
├── bin/               # Arquivos .class compilados
├── rodar.sh
└── rodar.bat
```

---

## ⚙️ Configurações

Ajustáveis no menu pré-jogo (opção 3):

| Parâmetro | Padrão |
|---|---|
| Saldo inicial | R$ 15.000,00 |
| Salário por volta | R$ 2.000,00 |
| Máximo de rodadas | 50 |
| Valor da fiança | R$ 200,00 |

> 💡 Para testar compras de imóveis desde o início, aumente o saldo inicial para R$ 500.000,00 — o imóvel mais barato custa R$ 200.000,00.

---

## 🧪 Dados de Teste

Use a opção **4 — Carregar dados pré-definidos** no menu pré-jogo para iniciar rapidamente com 4 jogadores e 12 imóveis com tema espacial já configurados.
