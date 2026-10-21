# Código completo — FitLife

**Autor: Marcos Paulo Santos Mota**

Os arquivos abaixo também estão disponíveis separadamente no projeto. O caminho completo relativo à pasta de entrega é apresentado antes de cada código. README e guia didático acompanham esta listagem em arquivos próprios.

## Caminho: FitLife/.gitignore

```text
target/
build/
distribuicao/
*.class
*.log
.idea/
*.iml
.vscode/
.DS_Store
```

## Caminho: FitLife/.gitattributes

```text
* text=auto
*.java text eol=lf
*.md text eol=lf
*.xml text eol=lf
*.sh text eol=lf
*.cmd text eol=crlf
*.png binary
```

## Caminho: FitLife/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>br.edu.anhanguera</groupId>
    <artifactId>fitlife</artifactId>
    <version>1.0.0</version>
    <name>FitLife</name>
    <description>Aula 15, Unidade 4 — Implementação e Validação do Sistema FitLife</description>
    <developers><developer><name>Marcos Paulo Santos Mota</name></developer></developers>
    <properties>
        <maven.compiler.release>17</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <junit.version>5.11.4</junit.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId><artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version><scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <finalName>fitlife</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId><artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId><artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.2</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId><artifactId>maven-jar-plugin</artifactId>
                <version>3.4.2</version>
                <configuration><archive><manifest><mainClass>fitlife.Main</mainClass></manifest></archive></configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## Caminho: FitLife/compilar-executar.cmd

```bat
@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"
where javac >nul 2>nul
if errorlevel 1 (
    echo JDK 17 ou superior nao encontrado. Configure JAVA_HOME e PATH conforme o README.
    exit /b 1
)
if not exist build mkdir build
if not exist build\classes mkdir build\classes
javac --release 17 -encoding UTF-8 -d build/classes -sourcepath src/main/java src/main/java/fitlife/Main.java
if errorlevel 1 exit /b 1
jar -J-Djava.io.tmpdir=build --create --file build/fitlife.jar --main-class fitlife.Main -C build/classes .
if errorlevel 1 exit /b 1
java -Dfile.encoding=UTF-8 -jar build\fitlife.jar
endlocal
```

## Caminho: FitLife/compilar-executar.sh

```sh
#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/classes
javac --release 17 -encoding UTF-8 -d build/classes -sourcepath src/main/java src/main/java/fitlife/Main.java
jar -J-Djava.io.tmpdir=build --create --file build/fitlife.jar --main-class fitlife.Main -C build/classes .
java -Dfile.encoding=UTF-8 -jar build/fitlife.jar
```

## Caminho: FitLife/Executar_FitLife.cmd

```bat
@echo off
setlocal
chcp 65001 >nul
title FitLife - Marcos Paulo Santos Mota
cd /d "%~dp0"
set "JAVA_FITLIFE=%~dp0..\..\work\tools\jdk17\jdk-17.0.20.1+1\bin\java.exe"
if exist "%JAVA_FITLIFE%" goto localizar_jar
if defined JAVA_HOME (
    set "JAVA_FITLIFE=%JAVA_HOME%\bin\java.exe"
)
if exist "%JAVA_FITLIFE%" goto localizar_jar
where java >nul 2>nul
if errorlevel 1 (
    echo Configure um JDK 17 ou superior em JAVA_HOME ou PATH. Consulte o README.
    pause
    exit /b 1
)
set "JAVA_FITLIFE=java"
:localizar_jar
set "JAR_FITLIFE=distribuicao\fitlife.jar"
if exist "%JAR_FITLIFE%" goto executar
set "JAR_FITLIFE=target\fitlife.jar"
if exist "%JAR_FITLIFE%" goto executar
set "JAR_FITLIFE=build\fitlife.jar"
if exist "%JAR_FITLIFE%" goto executar
echo JAR ainda nao gerado. Execute compilar-executar.cmd ou mvn clean verify.
pause
exit /b 1
:executar
"%JAVA_FITLIFE%" -Dfile.encoding=UTF-8 -jar "%JAR_FITLIFE%"
set "FITLIFE_RESULT=%ERRORLEVEL%"
echo(
pause
exit /b %FITLIFE_RESULT%
```

## Caminho: FitLife/.github/workflows/java.yml

```yaml
name: Compilar e testar FitLife
on: [push, pull_request, workflow_dispatch]
permissions:
  contents: read
jobs:
  teste:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
          cache: maven
      - name: Compilar, testar e empacotar
        run: mvn --batch-mode --no-transfer-progress verify
```

## Caminho: FitLife/src/main/java/fitlife/console/ConsoleApp.java

```java
package fitlife.console;

import fitlife.exception.EntidadeNaoEncontradaException;
import fitlife.interfaces.Pagavel;
import fitlife.model.Cliente;
import fitlife.model.Plano;
import fitlife.model.PlanoAnual;
import fitlife.model.PlanoMensal;
import fitlife.model.PlanoPremium;
import fitlife.service.ClienteService;
import fitlife.service.PagamentoService;
import fitlife.service.PlanoService;
import fitlife.service.TreinoService;
import fitlife.util.Formatacao;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Scanner;

/** Entrada e saída isoladas para permitir testes do menu com entradas simuladas. */
public class ConsoleApp {
    private final Scanner entrada;
    private final PrintStream saida;
    private final PlanoService planos = new PlanoService();
    private final ClienteService clientes = new ClienteService(planos);
    private final TreinoService treinos = new TreinoService(clientes);
    private final Pagavel pagamentos;

    public ConsoleApp(Scanner entrada, PrintStream saida) {
        this.entrada = entrada;
        this.saida = saida;
        this.pagamentos = new PagamentoService(saida);
    }

    public void executar() {
        saida.println("Autor: Marcos Paulo Santos Mota | Anhanguera — Aula 15, Unidade 4");
        saida.println("Dados em memória. Pagamentos exclusivamente simulados.");
        boolean ativo = true;
        while (ativo) {
            exibirMenu();
            try {
                int opcao = lerInteiro("Escolha uma opção: ");
                switch (opcao) {
                    case 1 -> cadastrarCliente();
                    case 2 -> listarClientes();
                    case 3 -> cadastrarPlano();
                    case 4 -> listarPlanos();
                    case 5 -> associarPlano();
                    case 6 -> cadastrarTreino();
                    case 7 -> listarTreinos();
                    case 8 -> pagamentos.processarPagamento(clientes.buscarPorEmail(ler("E-mail do cliente: ")));
                    case 9 -> exibirCliente();
                    case 0 -> ativo = false;
                    default -> saida.println("Opção inválida. Escolha um número de 0 a 9.");
                }
            } catch (FimDaEntradaException e) {
                saida.println("\nEntrada encerrada.");
                ativo = false;
            } catch (IllegalArgumentException | EntidadeNaoEncontradaException e) {
                saida.println("Não foi possível concluir: " + e.getMessage());
            }
        }
        saida.println("FitLife encerrado. Os dados desta sessão não são persistidos.");
    }

    private void exibirMenu() {
        saida.println("""

                =================================
                        SISTEMA FITLIFE
                =================================
                1 - Cadastrar cliente
                2 - Listar clientes
                3 - Cadastrar plano
                4 - Listar planos
                5 - Associar plano ao cliente
                6 - Cadastrar treino
                7 - Listar treinos
                8 - Processar pagamento
                9 - Exibir dados de um cliente
                0 - Sair
                =================================""");
    }

    private void cadastrarCliente() {
        String nome = ler("Nome: ");
        String email = ler("E-mail: ");
        int idade = lerInteiro("Idade: ");
        Cliente cliente = clientes.cadastrar(nome, email, idade);
        saida.println("Cliente cadastrado: " + cliente);
    }

    private void listarClientes() {
        if (clientes.listar().isEmpty()) {
            saida.println("Nenhum cliente cadastrado.");
        } else {
            clientes.listar().forEach(saida::println);
        }
    }

    private void cadastrarPlano() {
        int tipo = lerInteiro("Tipo (1 - Mensal, 2 - Anual, 3 - Premium): ");
        if (tipo < 1 || tipo > 3) {
            throw new IllegalArgumentException("Tipo de plano inválido.");
        }
        String nome = ler("Nome do plano: ");
        BigDecimal valor = lerDinheiro("Valor base MENSAL (ex.: 100,00; sem separador de milhar): ");
        Plano plano = switch (tipo) {
            case 1 -> new PlanoMensal(nome, valor);
            case 2 -> new PlanoAnual(nome, valor);
            case 3 -> new PlanoPremium(nome, valor, ler("Benefícios: "));
            default -> throw new IllegalArgumentException("Tipo de plano inválido.");
        };
        planos.cadastrar(plano);
        saida.println("Plano cadastrado: " + plano);
        if (plano instanceof PlanoAnual anual) {
            saida.println("Desconto anual: 10%. Total de 12 mensalidades: "
                    + Formatacao.moeda(anual.calcularTotalAnual()));
        }
    }

    private void listarPlanos() {
        if (planos.listar().isEmpty()) {
            saida.println("Nenhum plano cadastrado.");
        } else {
            for (int i = 0; i < planos.listar().size(); i++) {
                saida.println("ID " + (i + 1) + " | " + planos.listar().get(i));
            }
        }
    }

    private void associarPlano() {
        String email = ler("E-mail do cliente: ");
        listarPlanos();
        int id = lerInteiro("ID do plano: ");
        clientes.associarPlano(email, id);
        saida.println("Plano associado com sucesso.");
    }

    private void cadastrarTreino() {
        String email = ler("E-mail do cliente: ");
        // Valida a existência antes de solicitar os demais dados.
        clientes.buscarPorEmail(email);
        String nome = ler("Nome do treino: ");
        String descricao = ler("Descrição: ");
        saida.println("Treino cadastrado: " + treinos.cadastrar(email, nome, descricao));
    }

    private void listarTreinos() {
        if (treinos.listar().isEmpty()) {
            saida.println("Nenhum treino cadastrado.");
        } else {
            treinos.listar().forEach(saida::println);
        }
    }

    private void exibirCliente() {
        Cliente cliente = clientes.buscarPorEmail(ler("E-mail do cliente: "));
        saida.println(cliente);
        if (cliente.getPlano() != null) {
            saida.println(cliente.getPlano());
        }
        var treinosDoCliente = treinos.listarPorCliente(cliente.getEmail());
        saida.println("Treinos: " + treinosDoCliente.size());
        treinosDoCliente.forEach(saida::println);
    }

    private String ler(String mensagem) {
        saida.print(mensagem);
        if (!entrada.hasNextLine()) {
            throw new FimDaEntradaException();
        }
        return entrada.nextLine().strip();
    }

    private int lerInteiro(String mensagem) {
        try {
            return Integer.parseInt(ler(mensagem));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe um número inteiro válido.");
        }
    }

    private BigDecimal lerDinheiro(String mensagem) {
        String texto = ler(mensagem);
        if (!texto.matches("[0-9]+(?:[.,][0-9]{1,2})?")) {
            throw new IllegalArgumentException("Valor inválido. Use 100,00 ou 100.00, sem separador de milhar.");
        }
        return new BigDecimal(texto.replace(',', '.'));
    }

    private static class FimDaEntradaException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/exception/DadosInvalidosException.java

```java
package fitlife.exception;

/** Erro de validação; também pode ser tratado como IllegalArgumentException. */
public class DadosInvalidosException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/exception/EntidadeNaoEncontradaException.java

```java
package fitlife.exception;

/** Identifica uma consulta que não encontrou um cadastro. */
public class EntidadeNaoEncontradaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/interfaces/Pagavel.java

```java
package fitlife.interfaces;

import fitlife.model.Cliente;
import fitlife.model.Pagamento;

/** Contrato para componentes capazes de processar uma mensalidade. */
public interface Pagavel {
    Pagamento processarPagamento(Cliente cliente);
}
```

## Caminho: FitLife/src/main/java/fitlife/Main.java

```java
package fitlife;

import fitlife.console.ConsoleApp;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * FitLife — Anhanguera, Aula 15, Unidade 4.
 * @author Marcos Paulo Santos Mota
 */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in, StandardCharsets.UTF_8)) {
            new ConsoleApp(entrada, System.out).executar();
        }
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/model/Cliente.java

```java
package fitlife.model;

import fitlife.exception.DadosInvalidosException;
import fitlife.util.Validacao;

public class Cliente {
    private String nome;
    private final String email;
    private int idade;
    private Plano plano;

    public Cliente(String nome, String email, int idade) {
        this.nome = Validacao.textoObrigatorio(nome, "Nome");
        this.email = Validacao.email(email);
        this.idade = Validacao.idade(idade);
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public int getIdade() { return idade; }
    public Plano getPlano() { return plano; }

    public void setNome(String nome) {
        this.nome = Validacao.textoObrigatorio(nome, "Nome");
    }

    public void setIdade(int idade) {
        this.idade = Validacao.idade(idade);
    }

    // O e-mail é a chave estável do cadastro; não possui setter.
    public void associarPlano(Plano plano) {
        if (plano == null) {
            throw new DadosInvalidosException("Selecione um plano válido.");
        }
        this.plano = plano;
    }

    @Override
    public String toString() {
        return nome + " | " + email + " | " + idade + " anos | Plano: "
                + (plano == null ? "não associado" : plano.getNome());
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/model/Pagamento.java

```java
package fitlife.model;

import fitlife.exception.DadosInvalidosException;
import fitlife.util.Formatacao;
import fitlife.util.Validacao;
import java.math.BigDecimal;

/** Recibo imutável: preserva o valor e o plano mesmo após mudanças no cliente. */
public record Pagamento(int id, String nomeCliente, String emailCliente,
                        String nomePlano, BigDecimal valor) {
    public Pagamento {
        if (id <= 0) {
            throw new DadosInvalidosException("Identificador de pagamento inválido.");
        }
        nomeCliente = Validacao.textoObrigatorio(nomeCliente, "Nome");
        emailCliente = Validacao.email(emailCliente);
        nomePlano = Validacao.textoObrigatorio(nomePlano, "Plano");
        valor = Validacao.dinheiro(valor);
    }

    @Override
    public String toString() {
        return "Pagamento SIMULADO #" + id + " | Cliente: " + nomeCliente
                + " | E-mail: " + emailCliente + " | Plano: " + nomePlano
                + " | Valor pago: " + Formatacao.moeda(valor)
                + " | Pagamento realizado com sucesso!";
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/model/Plano.java

```java
package fitlife.model;

import fitlife.util.Formatacao;
import fitlife.util.Validacao;
import java.math.BigDecimal;

public abstract class Plano {
    protected final String nome;
    protected final BigDecimal valorBase;

    protected Plano(String nome, BigDecimal valorBase) {
        this.nome = Validacao.textoObrigatorio(nome, "Nome do plano");
        this.valorBase = Validacao.dinheiro(valorBase);
    }

    public abstract BigDecimal calcularMensalidade();

    public String getNome() { return nome; }
    public BigDecimal getValorBase() { return valorBase; }

    @Override
    public String toString() {
        return nome + " [" + getClass().getSimpleName() + "] | Base mensal: "
                + Formatacao.moeda(valorBase) + " | Mensalidade: "
                + Formatacao.moeda(calcularMensalidade());
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/model/PlanoAnual.java

```java
package fitlife.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** O valor base é mensal. O compromisso anual concede 10% de desconto por mês. */
public class PlanoAnual extends Plano {
    private static final BigDecimal FATOR_COM_DESCONTO = new BigDecimal("0.90");

    public PlanoAnual(String nome, BigDecimal valorBase) {
        super(nome, valorBase);
    }

    @Override
    public BigDecimal calcularMensalidade() {
        return valorBase.multiply(FATOR_COM_DESCONTO).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTotalAnual() {
        // Soma de 12 mensalidades já arredondadas, coerente com a cobrança mensal.
        return calcularMensalidade().multiply(BigDecimal.valueOf(12));
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/model/PlanoMensal.java

```java
package fitlife.model;

import java.math.BigDecimal;

public class PlanoMensal extends Plano {
    public PlanoMensal() {
        this("Mensal", new BigDecimal("100.00"));
    }

    public PlanoMensal(String nome, BigDecimal valorBase) {
        super(nome, valorBase);
    }

    @Override
    public BigDecimal calcularMensalidade() {
        return valorBase;
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/model/PlanoPremium.java

```java
package fitlife.model;

import fitlife.util.Validacao;
import java.math.BigDecimal;

/** Modalidade adicional citada na imagem do enunciado. */
public class PlanoPremium extends Plano {
    private final String beneficios;

    public PlanoPremium(String nome, BigDecimal valorBase, String beneficios) {
        super(nome, valorBase);
        this.beneficios = Validacao.textoObrigatorio(beneficios, "Benefícios");
    }

    public String getBeneficios() { return beneficios; }

    @Override
    public BigDecimal calcularMensalidade() {
        return valorBase;
    }

    @Override
    public String toString() {
        return super.toString() + " | Benefícios: " + beneficios;
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/model/Treino.java

```java
package fitlife.model;

import fitlife.exception.DadosInvalidosException;
import fitlife.util.Validacao;

public class Treino {
    private final int id;
    private final String nome;
    private final String descricao;
    private final Cliente cliente;

    public Treino(int id, String nome, String descricao, Cliente cliente) {
        if (id <= 0 || cliente == null) {
            throw new DadosInvalidosException("Treino exige identificador positivo e cliente válido.");
        }
        this.id = id;
        this.nome = Validacao.textoObrigatorio(nome, "Nome do treino");
        this.descricao = Validacao.textoObrigatorio(descricao, "Descrição do treino");
        this.cliente = cliente;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public Cliente getCliente() { return cliente; }

    @Override
    public String toString() {
        return "Treino #" + id + " | " + nome + " | " + descricao
                + " | Cliente: " + cliente.getNome() + " (" + cliente.getEmail() + ")";
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/service/ClienteService.java

```java
package fitlife.service;

import fitlife.exception.DadosInvalidosException;
import fitlife.exception.EntidadeNaoEncontradaException;
import fitlife.model.Cliente;
import fitlife.util.Validacao;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClienteService {
    private final List<Cliente> clientes = new ArrayList<>();
    private final PlanoService planoService;

    public ClienteService(PlanoService planoService) {
        this.planoService = Objects.requireNonNull(planoService, "Serviço de planos obrigatório.");
    }

    public Cliente cadastrar(String nome, String email, int idade) {
        Cliente cliente = new Cliente(nome, email, idade);
        if (clientes.stream().anyMatch(c -> c.getEmail().equals(cliente.getEmail()))) {
            throw new DadosInvalidosException("Já existe um cliente com esse e-mail.");
        }
        clientes.add(cliente);
        return cliente;
    }

    public Cliente buscarPorEmail(String email) {
        String chave = Validacao.email(email);
        return clientes.stream().filter(c -> c.getEmail().equals(chave)).findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente inexistente: " + chave));
    }

    public void associarPlano(String email, int planoId) {
        Cliente cliente = buscarPorEmail(email);
        cliente.associarPlano(planoService.buscarPorId(planoId));
    }

    public List<Cliente> listar() {
        return List.copyOf(clientes);
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/service/PagamentoService.java

```java
package fitlife.service;

import fitlife.exception.DadosInvalidosException;
import fitlife.interfaces.Pagavel;
import fitlife.model.Cliente;
import fitlife.model.Pagamento;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PagamentoService implements Pagavel {
    private final List<Pagamento> pagamentos = new ArrayList<>();
    private final PrintStream saida;

    public PagamentoService() { this(System.out); }

    public PagamentoService(PrintStream saida) {
        this.saida = Objects.requireNonNull(saida, "Saída obrigatória.");
    }

    @Override
    public Pagamento processarPagamento(Cliente cliente) {
        if (cliente == null) {
            throw new DadosInvalidosException("Cliente inválido.");
        }
        if (cliente.getPlano() == null) {
            throw new DadosInvalidosException("Cliente sem plano. Associe um plano antes do pagamento.");
        }
        // Polimorfismo: a subclasse do plano determina o cálculo utilizado.
        BigDecimal valor = cliente.getPlano().calcularMensalidade();
        Pagamento pagamento = new Pagamento(pagamentos.size() + 1, cliente.getNome(),
                cliente.getEmail(), cliente.getPlano().getNome(), valor);
        pagamentos.add(pagamento);
        saida.println(pagamento);
        return pagamento;
    }

    public List<Pagamento> listar() { return List.copyOf(pagamentos); }
}
```

## Caminho: FitLife/src/main/java/fitlife/service/PlanoService.java

```java
package fitlife.service;

import fitlife.exception.DadosInvalidosException;
import fitlife.exception.EntidadeNaoEncontradaException;
import fitlife.model.Plano;
import java.util.ArrayList;
import java.util.List;

public class PlanoService {
    private final List<Plano> planos = new ArrayList<>();

    public Plano cadastrar(Plano plano) {
        if (plano == null) {
            throw new DadosInvalidosException("Plano inválido.");
        }
        if (planos.stream().anyMatch(p -> p.getNome().equalsIgnoreCase(plano.getNome()))) {
            throw new DadosInvalidosException("Já existe um plano com esse nome.");
        }
        planos.add(plano);
        return plano;
    }

    public Plano buscarPorId(int id) {
        // O ID é a posição de cadastro + 1; não há exclusão nesta atividade.
        if (id < 1 || id > planos.size()) {
            throw new EntidadeNaoEncontradaException("Plano inexistente: " + id);
        }
        return planos.get(id - 1);
    }

    public List<Plano> listar() {
        return List.copyOf(planos);
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/service/TreinoService.java

```java
package fitlife.service;

import fitlife.exception.EntidadeNaoEncontradaException;
import fitlife.model.Cliente;
import fitlife.model.Treino;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TreinoService {
    private final List<Treino> treinos = new ArrayList<>();
    private final ClienteService clienteService;

    public TreinoService(ClienteService clienteService) {
        this.clienteService = Objects.requireNonNull(clienteService, "Serviço de clientes obrigatório.");
    }

    public Treino cadastrar(String emailCliente, String nome, String descricao) {
        Cliente cliente = clienteService.buscarPorEmail(emailCliente);
        Treino treino = new Treino(treinos.size() + 1, nome, descricao, cliente);
        treinos.add(treino);
        return treino;
    }

    public Treino buscarPorId(int id) {
        return treinos.stream().filter(t -> t.getId() == id).findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Treino inexistente: " + id));
    }

    public List<Treino> listar() { return List.copyOf(treinos); }

    public List<Treino> listarPorCliente(String email) {
        Cliente cliente = clienteService.buscarPorEmail(email);
        return treinos.stream().filter(t -> t.getCliente() == cliente).toList();
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/util/Formatacao.java

```java
package fitlife.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class Formatacao {
    private Formatacao() { }

    public static String moeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor);
    }
}
```

## Caminho: FitLife/src/main/java/fitlife/util/Validacao.java

```java
package fitlife.util;

import fitlife.exception.DadosInvalidosException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.regex.Pattern;

/** Regras compartilhadas pelos modelos, serviços e console. */
public final class Validacao {
    private static final Pattern EMAIL = Pattern.compile(
            "[A-Za-z0-9_+%-]+(?:\\.[A-Za-z0-9_+%-]+)*@"
            + "[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?"
            + "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)+");

    private Validacao() { }

    public static String textoObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(campo + " é obrigatório.");
        }
        return valor.strip();
    }

    public static String email(String valor) {
        String normalizado = textoObrigatorio(valor, "E-mail").toLowerCase(Locale.ROOT);
        if (normalizado.length() > 254 || !EMAIL.matcher(normalizado).matches()) {
            throw new DadosInvalidosException("E-mail inválido. Exemplo: aluno@exemplo.com");
        }
        return normalizado;
    }

    public static int idade(int valor) {
        if (valor < 15) {
            throw new DadosInvalidosException("A idade mínima é 15 anos.");
        }
        return valor;
    }

    public static BigDecimal dinheiro(BigDecimal valor) {
        if (valor == null || valor.signum() < 0) {
            throw new DadosInvalidosException("O valor deve ser maior ou igual a zero.");
        }
        try {
            return valor.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new DadosInvalidosException("Informe um valor com no máximo duas casas decimais.");
        }
    }
}
```

## Caminho: FitLife/src/test/java/fitlife/ClienteTest.java

```java
package fitlife;

import fitlife.exception.DadosInvalidosException;
import fitlife.model.Cliente;
import fitlife.model.PlanoMensal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {
    @Test
    void cadastraClienteValidoENormalizaDados() {
        Cliente cliente = new Cliente("  Marcos Paulo Santos Mota  ", " MARCOS@EXEMPLO.COM ", 20);
        assertAll(() -> assertEquals("Marcos Paulo Santos Mota", cliente.getNome()),
                () -> assertEquals("marcos@exemplo.com", cliente.getEmail()),
                () -> assertEquals(20, cliente.getIdade()),
                () -> assertNull(cliente.getPlano()));
    }

    @Test
    void aceitaIdadeMinima() {
        assertEquals(15, new Cliente("Ana", "ana@exemplo.com", 15).getIdade());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 14})
    void rejeitaIdadeInferiorAQuinze(int idade) {
        assertThrows(DadosInvalidosException.class, () -> new Cliente("Ana", "ana@exemplo.com", idade));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejeitaNomeVazio(String nome) {
        assertThrows(IllegalArgumentException.class, () -> new Cliente(nome, "ana@exemplo.com", 18));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"ana", "ana@", "@exemplo.com", "ana@localhost", "a b@exemplo.com",
            "ana@@exemplo.com", ".ana@exemplo.com", "ana..silva@exemplo.com", "ana@-exemplo.com"})
    void rejeitaEmailInvalido(String email) {
        assertThrows(DadosInvalidosException.class, () -> new Cliente("Ana", email, 18));
    }

    @Test
    void settersValidamAntesDeAlterarEstado() {
        Cliente cliente = new Cliente("Ana", "ana@exemplo.com", 18);
        assertThrows(DadosInvalidosException.class, () -> cliente.setNome(" "));
        assertThrows(DadosInvalidosException.class, () -> cliente.setIdade(14));
        assertEquals("Ana", cliente.getNome());
        assertEquals(18, cliente.getIdade());
        cliente.setNome("Ana Silva");
        cliente.setIdade(19);
        assertEquals("Ana Silva", cliente.getNome());
        assertEquals(19, cliente.getIdade());
    }

    @Test
    void associaPlanoERejeitaPlanoNulo() {
        Cliente cliente = new Cliente("Ana", "ana@exemplo.com", 18);
        PlanoMensal plano = new PlanoMensal();
        cliente.associarPlano(plano);
        assertSame(plano, cliente.getPlano());
        assertThrows(DadosInvalidosException.class, () -> cliente.associarPlano(null));
        assertSame(plano, cliente.getPlano());
    }
}
```

## Caminho: FitLife/src/test/java/fitlife/ConsoleAppTest.java

```java
package fitlife;

import fitlife.console.ConsoleApp;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class ConsoleAppTest {
    private String executar(String roteiro) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (Scanner entrada = new Scanner(roteiro);
             PrintStream saida = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            new ConsoleApp(entrada, saida).executar();
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void executaTodasAsOpcoesEmUmaSessao() {
        String texto = executar("""
                1
                Marcos Paulo Santos Mota
                marcos@exemplo.com
                20
                2
                3
                1
                Mensal
                100,00
                3
                2
                Anual
                100.00
                3
                3
                Premium
                150,00
                Aulas coletivas
                4
                5
                marcos@exemplo.com
                2
                6
                marcos@exemplo.com
                Treino A
                Descrição demonstrativa
                7
                8
                marcos@exemplo.com
                9
                marcos@exemplo.com
                0
                """);
        assertAll(() -> assertTrue(texto.contains("Cliente cadastrado")),
                () -> assertTrue(texto.contains("ID 3")),
                () -> assertTrue(texto.contains("Plano associado com sucesso")),
                () -> assertTrue(texto.contains("Treino #1")),
                () -> assertTrue(texto.contains("Pagamento SIMULADO #1")),
                () -> assertTrue(texto.contains("90,00")),
                () -> assertTrue(texto.contains("Treinos: 1")),
                () -> assertTrue(texto.contains("FitLife encerrado")),
                () -> assertFalse(texto.contains("Não foi possível concluir")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "", "2147483648", "-1", "10", "1.5"})
    void menuResisteAEntradaInvalida(String invalida) {
        String texto = executar(invalida + "\n0\n");
        assertTrue(texto.contains("Não foi possível concluir") || texto.contains("Opção inválida"));
        assertTrue(texto.contains("FitLife encerrado"));
    }

    @Test
    void menuTrataRegrasDeNegocioEContinua() {
        String texto = executar("""
                1
                Ana
                ana@exemplo.com
                14
                1
                Ana
                ana@exemplo.com
                18
                8
                ana@exemplo.com
                6
                ausente@exemplo.com
                9
                ausente@exemplo.com
                3
                9
                0
                """);
        assertTrue(texto.contains("idade mínima"));
        assertTrue(texto.contains("Cliente cadastrado"));
        assertTrue(texto.contains("Cliente sem plano"));
        assertTrue(texto.contains("Cliente inexistente"));
        assertTrue(texto.contains("Tipo de plano inválido"));
        assertTrue(texto.contains("FitLife encerrado"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "1.000,00", "abc", "NaN", "1,234", "1e3", ""})
    void menuRejeitaValorMonetarioInvalido(String valor) {
        String texto = executar("3\n1\nMensal\n" + valor + "\n4\n0\n");
        assertTrue(texto.contains("Valor inválido"));
        assertTrue(texto.contains("Nenhum plano cadastrado"));
        assertTrue(texto.contains("FitLife encerrado"));
    }

    @Test
    void listaColecoesVazias() {
        String texto = executar("2\n4\n7\n0\n");
        assertTrue(texto.contains("Nenhum cliente cadastrado"));
        assertTrue(texto.contains("Nenhum plano cadastrado"));
        assertTrue(texto.contains("Nenhum treino cadastrado"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1\nAna\n", "3\n", "8\n"})
    void encerraSemFalhaQuandoEntradaAcaba(String roteiro) {
        String texto = executar(roteiro);
        assertTrue(texto.contains("Entrada encerrada"));
        assertTrue(texto.contains("FitLife encerrado"));
    }
}
```

## Caminho: FitLife/src/test/java/fitlife/PlanosTest.java

```java
package fitlife;

import fitlife.exception.DadosInvalidosException;
import fitlife.model.Plano;
import fitlife.model.PlanoAnual;
import fitlife.model.PlanoMensal;
import fitlife.model.PlanoPremium;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class PlanosTest {
    @Test
    void calculaMensalidadeMensal() {
        assertEquals(new BigDecimal("100.00"), new PlanoMensal().calcularMensalidade());
    }

    @Test
    void aplicaDezPorCentoNoAnualESomaDozeMensalidades() {
        PlanoAnual anual = new PlanoAnual("Anual", new BigDecimal("100.00"));
        assertEquals(new BigDecimal("90.00"), anual.calcularMensalidade());
        assertEquals(new BigDecimal("1080.00"), anual.calcularTotalAnual());
    }

    @Test
    void arredondaCentavosNoAnual() {
        PlanoAnual anual = new PlanoAnual("Anual", new BigDecimal("99.95"));
        assertEquals(new BigDecimal("89.96"), anual.calcularMensalidade());
        assertEquals(new BigDecimal("1079.52"), anual.calcularTotalAnual());
    }

    @Test
    void usaPolimorfismoPelaReferenciaPlano() {
        List<Plano> planos = List.of(new PlanoMensal(),
                new PlanoAnual("Anual", new BigDecimal("100")),
                new PlanoPremium("Premium", new BigDecimal("150"), "Aulas coletivas"));
        assertEquals(List.of(new BigDecimal("100.00"), new BigDecimal("90.00"),
                new BigDecimal("150.00")), planos.stream().map(Plano::calcularMensalidade).toList());
        assertTrue(planos.get(2).toString().contains("Aulas coletivas"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "-0.01", "0.001"})
    void rejeitaValoresInvalidos(String valor) {
        assertThrows(DadosInvalidosException.class,
                () -> new PlanoMensal("Mensal", new BigDecimal(valor)));
    }

    @Test
    void validaNomeValorNuloEBeneficiosEAceitaZero() {
        assertThrows(DadosInvalidosException.class, () -> new PlanoMensal(" ", BigDecimal.ONE));
        assertThrows(DadosInvalidosException.class, () -> new PlanoAnual("Anual", null));
        assertThrows(DadosInvalidosException.class, () -> new PlanoPremium("P", BigDecimal.ONE, " "));
        assertEquals(new BigDecimal("0.00"), new PlanoMensal("Gratuito", BigDecimal.ZERO).calcularMensalidade());
    }
}
```

## Caminho: FitLife/src/test/java/fitlife/ServicosTest.java

```java
package fitlife;

import fitlife.exception.DadosInvalidosException;
import fitlife.exception.EntidadeNaoEncontradaException;
import fitlife.interfaces.Pagavel;
import fitlife.model.Cliente;
import fitlife.model.Pagamento;
import fitlife.model.PlanoAnual;
import fitlife.model.PlanoMensal;
import fitlife.model.Treino;
import fitlife.service.ClienteService;
import fitlife.service.PagamentoService;
import fitlife.service.PlanoService;
import fitlife.service.TreinoService;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServicosTest {
    private PlanoService planos;
    private ClienteService clientes;
    private TreinoService treinos;
    private PagamentoService pagamentos;
    private ByteArrayOutputStream console;
    private Cliente cliente;

    @BeforeEach
    void preparar() {
        planos = new PlanoService();
        clientes = new ClienteService(planos);
        treinos = new TreinoService(clientes);
        console = new ByteArrayOutputStream();
        pagamentos = new PagamentoService(new PrintStream(console, true, StandardCharsets.UTF_8));
        cliente = clientes.cadastrar("Marcos Paulo Santos Mota", "marcos@exemplo.com", 20);
    }

    @Test
    void cadastraEConsultaCliente() {
        assertSame(cliente, clientes.buscarPorEmail(" MARCOS@EXEMPLO.COM "));
        assertEquals(1, clientes.listar().size());
    }

    @Test
    void rejeitaCadastroDuplicadoEInvalidoSemModificarLista() {
        assertThrows(DadosInvalidosException.class,
                () -> clientes.cadastrar("Outro", "MARCOS@EXEMPLO.COM", 30));
        assertThrows(DadosInvalidosException.class,
                () -> clientes.cadastrar("Menor", "menor@exemplo.com", 14));
        assertEquals(1, clientes.listar().size());
    }

    @Test
    void associaPlanoCadastradoAoCliente() {
        PlanoMensal plano = new PlanoMensal();
        planos.cadastrar(plano);
        clientes.associarPlano(cliente.getEmail(), 1);
        assertSame(plano, cliente.getPlano());
    }

    @Test
    void rejeitaAssociacoesInexistentesSemAlterarCliente() {
        planos.cadastrar(new PlanoMensal());
        assertThrows(EntidadeNaoEncontradaException.class,
                () -> clientes.associarPlano("ausente@exemplo.com", 1));
        assertThrows(EntidadeNaoEncontradaException.class,
                () -> clientes.associarPlano(cliente.getEmail(), 99));
        assertNull(cliente.getPlano());
    }

    @Test
    void rejeitaPlanosInvalidosEDuplicados() {
        planos.cadastrar(new PlanoMensal());
        assertThrows(DadosInvalidosException.class, () -> planos.cadastrar(null));
        assertThrows(DadosInvalidosException.class,
                () -> planos.cadastrar(new PlanoMensal("MENSAL", BigDecimal.TEN)));
        assertThrows(EntidadeNaoEncontradaException.class, () -> planos.buscarPorId(0));
        assertThrows(EntidadeNaoEncontradaException.class, () -> planos.buscarPorId(2));
        assertEquals(1, planos.listar().size());
    }

    @Test
    void cadastraEConsultaTreinoPorIdEPorCliente() {
        Treino treino = treinos.cadastrar(cliente.getEmail(), "Treino A", "Exercício demonstrativo");
        assertSame(cliente, treino.getCliente());
        assertEquals(1, treino.getId());
        assertSame(treino, treinos.buscarPorId(1));
        assertEquals(1, treinos.listarPorCliente(cliente.getEmail()).size());
        clientes.cadastrar("Ana", "ana@exemplo.com", 18);
        assertTrue(treinos.listarPorCliente("ana@exemplo.com").isEmpty());
    }

    @Test
    void rejeitaTreinoSemClienteCadastrado() {
        assertThrows(EntidadeNaoEncontradaException.class,
                () -> treinos.cadastrar("ausente@exemplo.com", "A", "Descrição"));
        assertThrows(EntidadeNaoEncontradaException.class, () -> treinos.buscarPorId(1));
        assertThrows(EntidadeNaoEncontradaException.class,
                () -> treinos.listarPorCliente("ausente@exemplo.com"));
        assertTrue(treinos.listar().isEmpty());
    }

    @Test
    void rejeitaTreinoInvalidoSemConsumirIdentificador() {
        assertThrows(DadosInvalidosException.class,
                () -> treinos.cadastrar(cliente.getEmail(), " ", "Descrição"));
        assertThrows(DadosInvalidosException.class,
                () -> treinos.cadastrar(cliente.getEmail(), "A", " "));
        assertThrows(DadosInvalidosException.class, () -> new Treino(0, "A", "D", cliente));
        assertThrows(DadosInvalidosException.class, () -> new Treino(1, "A", "D", null));
        assertTrue(treinos.listar().isEmpty());
        assertEquals(1, treinos.cadastrar(cliente.getEmail(), "A", "Descrição").getId());
    }

    @Test
    void processaPagamentoPelaInterfaceEConfirmaNoConsole() {
        planos.cadastrar(new PlanoAnual("Anual", new BigDecimal("100")));
        clientes.associarPlano(cliente.getEmail(), 1);
        Pagavel contrato = pagamentos;
        Pagamento recibo = contrato.processarPagamento(cliente);
        assertEquals(new BigDecimal("90.00"), recibo.valor());
        assertEquals(1, recibo.id());
        assertEquals(1, pagamentos.listar().size());
        String texto = console.toString(StandardCharsets.UTF_8);
        assertTrue(texto.contains("SIMULADO"));
        assertTrue(texto.contains("Marcos Paulo Santos Mota"));
        assertTrue(texto.contains("90,00"));
        assertTrue(texto.contains("sucesso"));
    }

    @Test
    void rejeitaPagamentoSemPlanoENuloSemGerarRecibo() {
        assertThrows(DadosInvalidosException.class, () -> pagamentos.processarPagamento(cliente));
        assertThrows(DadosInvalidosException.class, () -> pagamentos.processarPagamento(null));
        assertTrue(pagamentos.listar().isEmpty());
        assertEquals("", console.toString(StandardCharsets.UTF_8));
    }

    @Test
    void reciboPreservaDadosDepoisDaTrocaDePlanoENome() {
        cliente.associarPlano(new PlanoMensal());
        Pagamento primeiro = pagamentos.processarPagamento(cliente);
        cliente.setNome("Marcos Mota");
        cliente.associarPlano(new PlanoAnual("Anual", new BigDecimal("100")));
        Pagamento segundo = pagamentos.processarPagamento(cliente);
        assertEquals("Marcos Paulo Santos Mota", primeiro.nomeCliente());
        assertEquals("Mensal", primeiro.nomePlano());
        assertEquals(new BigDecimal("100.00"), primeiro.valor());
        assertEquals(2, segundo.id());
        assertEquals(new BigDecimal("90.00"), segundo.valor());
    }

    @Test
    void listasExternasNaoPermitemRemoverCadastros() {
        planos.cadastrar(new PlanoMensal());
        treinos.cadastrar(cliente.getEmail(), "A", "Descrição");
        cliente.associarPlano(planos.buscarPorId(1));
        pagamentos.processarPagamento(cliente);
        assertThrows(UnsupportedOperationException.class, () -> clientes.listar().clear());
        assertThrows(UnsupportedOperationException.class, () -> planos.listar().clear());
        assertThrows(UnsupportedOperationException.class, () -> treinos.listar().clear());
        assertThrows(UnsupportedOperationException.class, () -> pagamentos.listar().clear());
    }
}
```
