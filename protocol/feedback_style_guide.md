# Guia de Estilo para Feedback ao Estudante

Guia genérico para a produção de feedback pedagógico em laboratórios de programação orientada a objetos. Válido para qualquer laboratório da disciplina.

## Características desejadas

- Reconheça primeiro o que o estudante acertou ou demonstrou entender.
- Depois apresente os problemas mais importantes de forma concreta e ensinável.
- Explique por que o problema importa para o objetivo do laboratório.
- Sugira próximo passo prático para melhoria.
- Mantenha tom humano, direto e encorajador.
- Lembre que o público principal é de estudantes do primeiro ano de Computação.
- Se usar termo técnico, explique em linguagem simples logo em seguida.
- Prefira "situações limite" ou "casos especiais" a jargões não explicados.
- Diferencie claramente problema conceitual central de detalhe de acabamento.
- Quando o estudante mostra entendimento parcial correto, diga isso explicitamente antes de apontar o que falta consolidar.
- Se a solução estiver realmente forte, aceite comentar só os ajustes mais importantes sem forçar uma lista longa.
- Se não houver ponto positivo claro, não invente elogio apenas para suavizar o texto.
- Quando a solução tiver "cara de pronta", mas não sustentar o comportamento exigido, explique isso com clareza.
- Priorize no comentário os critérios que mais explicam a nota.

## Evite

- Tom humilhante, irônico ou moralizante.
- Linguagem de sentença final, como se o objetivo fosse apenas punir.
- Generalizações vagas sem apontar evidências.
- Feedback que fala só de erros e não menciona aprendizado ou acertos.
- Elogios genéricos sem sustentação concreta no código.
- Jargões soltos, como "casos de borda", "encapsulamento", "coesão" ou "invariante", sem explicação breve.
- Linguagem que faça parecer que um detalhe pequeno invalida todo o trabalho.

## Estrutura recomendada do comentário final

1. Cada comentário deve apontar explicitamente o local do código a que se refere.
2. Cite 1 ou 2 pontos fortes reais quando houver evidência.
3. Cite os principais problemas que mais impactaram a nota.
4. Feche com uma orientação objetiva sobre o que corrigir primeiro.
5. Trate detalhes menores como menores — não os apresente como a falha principal.
6. Quando a solução estiver boa no núcleo, aceite majoritariamente ajustes localizados.

## Regra de linguagem

Se precisar mencionar um termo técnico, faça assim:

- "casos de borda, ou seja, situações limite como posição inválida ou lista vazia"
- "encapsulamento, isto é, evitar que outras classes mexam direto em dados internos"

## Formato inline esperado

O campo `student_feedback` deve ser uma string com múltiplas linhas curtas, cada uma no formato:

`[arquivo/classe/método] comentário curto e claro`

Evite comentários sem localização explícita.

## Frases-modelo de tom

- "No geral, a solução mostra entendimento de parte importante do lab, especialmente em ..."
- "O ponto que mais precisa de atenção agora é ..."
- "Seu código indica que você compreendeu ..., mas ainda precisa consolidar ..."
- "O próximo passo mais valioso é ..."
- "Aqui o caminho está certo, mas ainda falta ..."
- "Isso não invalida o restante, mas vale ajustar ..."
- "Esse ajuste é importante, mas ele não apaga os acertos que já aparecem no restante da solução."

## Regra pedagógica central

O objetivo do comentário não é apenas justificar a nota, mas indicar se o estudante parece ter aprendido os conceitos trabalhados no laboratório.

## Regra de proporcionalidade

- Desconsidere bônus e funcionalidades opcionais ao explicar a nota do trabalho obrigatório.
- Não transforme detalhe de formato em desconto pesado.
- Não trate diferença cosmética como falha funcional principal.
- Se houver um problema forte em um critério e sinais reais de acerto em outro, preserve essa diferença nas justificativas.
