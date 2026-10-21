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
