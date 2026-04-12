# Especificação do Lab 03 - FilmNow

O sistema a ser avaliado implementa o FilmNow, uma lista de filmes e séries com acervo principal e HotList.

## Recorte desta rodada

- Para esta rodada de avaliação, desconsidere os bônus do enunciado.
- A correção deve olhar apenas para os requisitos obrigatórios do laboratório.

## Regras principais

- O acervo principal suporta até `100` filmes.
- Um filme possui `nome`, `ano de lançamento` e `local` onde pode ser assistido.
- O sistema deve permitir:
  - adicionar filme em uma posição de `1` a `100`
  - sobrescrever o filme já existente na posição
  - impedir duplicidade de filme por `nome + ano`, mesmo em outra posição
  - listar todos os filmes cadastrados
  - detalhar um filme específico pela posição

- Em posição inválida no fluxo normal do sistema, a resposta esperada é `POSICAO INVALIDA`.
- Ao tentar adicionar filme duplicado, a resposta esperada é `FILME JA ADICIONADO`.
- Ao tentar adicionar filme com `nome` vazio ou `local` vazio, a resposta esperada é `FILME INVALIDO`.
- O `ano` pode estar vazio no fluxo básico e, nesse caso, o filme ainda pode ser adicionado.
- Ao detalhar um filme:
  - se a posição for válida e estiver vazia, retorna vazio
  - se estiver na HotList, deve ser sinalizado como hot

## Classe `Filme`

- A modelagem esperada usa uma classe `Filme` ou equivalente.
- A igualdade entre filmes considera `nome` e `ano de lançamento`.
- O filme deve fornecer representações textuais adequadas para detalhes e listagem.

## HotList

- Existe uma HotList separada, com até `10` posições.
- O sistema deve permitir:
  - atribuir hot a um filme
  - exibir a HotList
  - remover hot
- Um filme não pode aparecer mais de uma vez na HotList.
- Se um novo filme for colocado numa posição ocupada da HotList, o antigo deixa de ser hot.
- Operações com posição inválida na HotList devem informar `POSICAO INVALIDA`.
- Ao tentar inserir na HotList um filme já presente nela, deve informar `FILME JA ADICIONADO`.

## Testes

- O lab pede testes JUnit das classes com lógica testável.
- A avaliação enfatiza:
  - testes da classe `Filme`
  - testes da classe `FilmNow`
  - cobertura de cenários normais, alternativos e valores limite

## Bônus 1

- O programa deve lançar exceções para entradas inválidas na criação de `Filme`.
- `NullPointerException` para argumentos nulos.
- `IllegalArgumentException` para strings inválidas, como vazias ou apenas com espaços.

## Bônus 2

- Novas funcionalidades podem incluir:
  - adicionar e remover locais de exibição em `Filme`
  - remover filme do acervo
  - remover o filme também da HotList ao removê-lo do acervo
  - mostrar filmes por nome
  - mostrar filmes por ano

## Design esperado

- `FilmNow` deve compor `Filme`.
- A lógica do sistema deve ficar separada da interface com o usuário.
- O `main` deve ficar modularizado e sem concentrar lógica de negócio.
- O design deve ser simples, legível e coerente com os conceitos de composição vistos no lab.
