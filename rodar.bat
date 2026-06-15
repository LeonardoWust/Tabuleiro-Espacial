@echo off
chcp 65001 > nul
echo === Compilando o projeto ===
if not exist bin mkdir bin

javac -encoding UTF-8 -d bin ^
  src\modelo\TipoPersonagem.java ^
  src\modelo\TipoCasa.java ^
  src\modelo\Carta.java ^
  src\estruturas\ListaImoveis.java ^
  src\modelo\Imovel.java ^
  src\modelo\Jogador.java ^
  src\tabuleiro\NoCasa.java ^
  src\tabuleiro\ListaCircular.java ^
  src\estruturas\PilhaBaralho.java ^
  src\estruturas\FilaHistorico.java ^
  src\estruturas\FilaPrisao.java ^
  src\estruturas\ListaJogadores.java ^
  src\jogo\ConfiguracaoJogo.java ^
  src\jogo\MotorJogo.java ^
  src\jogo\GerenciadorPreJogo.java ^
  src\Main.java

if %ERRORLEVEL% == 0 (
  echo === Compilado com sucesso! Iniciando o jogo ===
  java -Dfile.encoding=UTF-8 -cp bin Main
) else (
  echo ERRO na compilacao.
)
pause
