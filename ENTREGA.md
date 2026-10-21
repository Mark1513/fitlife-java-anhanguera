# Entrega acadêmica — FitLife

**Aluno: Marcos Paulo Santos Mota**  
**Instituição:** Anhanguera  
**Curso:** Sistemas de Informação  
**Atividade:** Aula 15 — Unidade 4 — Implementação e Validação do Sistema FitLife

## Link para o professor

[Repositório público do FitLife](https://github.com/Mark1513/fitlife-java-anhanguera)

O código e a documentação podem ser consultados pelo link sem convite.

## Arquivos reunidos no ZIP completo

| Conteúdo | Localização dentro da pasta FitLife |
|---|---|
| Código Java | `src/main/java/fitlife/` |
| Testes JUnit 5 | `src/test/java/fitlife/` |
| Compilação e dependências de teste | `pom.xml` |
| Objetivo, arquitetura e instruções | `README.md` |
| Explicação de POO, apresentação e perguntas | `docs/GUIA_DIDATICO.md` |
| Todos os códigos identificados por caminho | `docs/CODIGO_COMPLETO.md` |
| Evidência dos 62 testes executados | `docs/VALIDACAO.md` |
| Print escolhido | `docs/imagens/fitlife-execucao.png` |
| Entradas da sessão mostrada no print | `docs/print-entrada.txt` |
| Saída integral dessa sessão | `docs/print-saida.txt` |
| JAR compilado, incluído no ZIP local de entrega | `distribuicao/fitlife.jar` |
| Inicialização do JAR no Windows | `Executar_FitLife.cmd` |

## Como executar

1. Extraia o ZIP completo antes de executar os arquivos.
2. Abra a pasta `FitLife` extraída.
3. Com Java 17 ou superior configurado, execute `Executar_FitLife.cmd` no Windows.
4. O menu começa sem cadastros; consulte o exemplo do README para cadastrar e associar os dados.
5. Para compilar a partir das fontes e executar os testes, use `mvn clean verify`.

O JDK e o Maven não foram incluídos no ZIP. O script de inicialização pode aproveitar o JDK local preparado neste computador; em outro computador, é necessário configurar Java 17 ou superior. Os pagamentos são simulados e os dados são mantidos em memória.

## Sobre a imagem

O primeiro dos dois prints fornecidos pelo aluno foi escolhido por mostrar o nome, o plano, o treino e o pagamento, preservando o cabeçalho que identifica a demonstração. A imagem foi incluída sem alteração. Ela registra a saída real do JAR original em uma janela de demonstração com entradas automatizadas. O e-mail e a idade usados são fictícios. A sessão terminou com código de saída 0.

![Print da execução do FitLife](docs/imagens/fitlife-execucao.png)

O JAR compilado acompanha o ZIP local de entrega. Para manter o repositório focado nas fontes e nas evidências, a pasta de distribuição não é versionada; o clone do GitHub e o ZIP automático do GitHub exigem a compilação descrita no README.
