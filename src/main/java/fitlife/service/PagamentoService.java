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
