# Evidência de validação — FitLife

**Autor: Marcos Paulo Santos Mota**  
**Data:** 21/10/2026 — horário de São Paulo.

## Ambiente efetivamente utilizado

- Windows, Eclipse Temurin OpenJDK **17.0.20.1+1**, compilador `javac 17.0.20.1`.
- Apache Maven **3.9.11** e JUnit Jupiter **5.11.4**.
- Ferramentas preparadas localmente para a validação; elas não estão incorporadas ao código nem ao ZIP.

## Compilação e testes efetivamente executados

Foi executado o ciclo Maven `verify`, com repositório de dependências local separado da entrega. O projeto foi compilado para Java 17, os testes foram executados e o JAR `target/fitlife.jar` foi gerado. Abaixo estão os totais extraídos dos relatórios XML dessa execução, sem publicar as propriedades de ambiente dos relatórios.

| Classe de teste | Casos | Falhas | Erros | Ignorados |
|---|---:|---:|---:|---:|
| fitlife.ClienteTest | 22 | 0 | 0 | 0 |
| fitlife.ConsoleAppTest | 20 | 0 | 0 | 0 |
| fitlife.PlanosTest | 8 | 0 | 0 | 0 |
| fitlife.ServicosTest | 12 | 0 | 0 | 0 |
| **Total** | **62** | **0** | **0** | **0** |

O total inclui as variações dos testes parametrizados; não significa 62 métodos diferentes. Resultado da compilação: **BUILD SUCCESS**.

## Relação com os nove testes obrigatórios

| Cenário | Teste representativo |
|---|---|
| 1. Cadastro válido | `ClienteTest.cadastraClienteValidoENormalizaDados` |
| 2. Idade inválida | `ClienteTest.rejeitaIdadeInferiorAQuinze` |
| 3. Plano mensal | `PlanosTest.calculaMensalidadeMensal` |
| 4. Plano anual | `PlanosTest.aplicaDezPorCentoNoAnualESomaDozeMensalidades` |
| 5. Associação | `ServicosTest.associaPlanoCadastradoAoCliente` |
| 6. Treino | `ServicosTest.cadastraEConsultaTreinoPorIdEPorCliente` |
| 7. Pagamento válido | `ServicosTest.processaPagamentoPelaInterfaceEConfirmaNoConsole` |
| 8. Sem plano | `ServicosTest.rejeitaPagamentoSemPlanoENuloSemGerarRecibo` |
| 9. Entradas inválidas | `ConsoleAppTest.menuResisteAEntradaInvalida` e testes de validação dos modelos |

Também foram verificados limites de idade, valores negativos, arredondamento de centavos, nomes e e-mails vazios, duplicidades, busca de cliente inexistente, associação inválida, treino sem cliente, preservação do estado após falhas, preservação de recibos após troca de plano, proteção das listas e fim de entrada.

## Execuções efetivamente realizadas fora do JUnit

1. O JAR gerado foi iniciado por um processo Java real, com o roteiro UTF-8 de `docs/exemplo-entrada.txt`.
2. Foram cadastrados cliente, planos Mensal/Anual/Premium e treino; o Anual foi associado e pago na simulação; todas as consultas e a saída foram executadas.
3. O processo terminou com código **0**, sem saída de erro. A saída completa está em [execucao-real.txt](execucao-real.txt).
4. `compilar-executar.cmd` foi executado em uma cópia do projeto localizada em uma pasta com espaços. Compilou pelo JDK, gerou o JAR, abriu o menu e encerrou pela opção 0, sem usar Maven, com código **0**.

O arquivo de saída foi verificado como UTF-8. Em redirecionamento de entrada, os valores fornecidos ao programa podem não aparecer como eco no terminal; por isso o roteiro de entrada também está incluído.

## Como repetir

Dentro da pasta que contém o `pom.xml`:

```text
mvn clean verify
java -Dfile.encoding=UTF-8 -jar target/fitlife.jar
```

No PowerShell, utilize `java "-Dfile.encoding=UTF-8" -jar target/fitlife.jar`. Para testar o caminho sem Maven no Windows, execute `.\compilar-executar.cmd`.

## Limites da evidência

- Os resultados acima referem-se à execução local realizada; não são uma previsão ou uma mera instrução.
- O script Linux/macOS foi fornecido, mas **não foi executado nesses sistemas**.
- Os testes não provam ausência de todos os defeitos e não envolvem um pagamento financeiro real.
- A transcrição é uma saída real do programa, **não uma captura de tela do terminal**. O README explica como produzir o print no ambiente da apresentação.
- O workflow GitHub Actions é uma verificação adicional. Seu estado deve ser consultado no GitHub; não é inferido dos resultados locais.