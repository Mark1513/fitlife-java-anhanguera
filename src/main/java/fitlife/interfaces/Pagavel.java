package fitlife.interfaces;

import fitlife.model.Cliente;
import fitlife.model.Pagamento;

/** Contrato para componentes capazes de processar uma mensalidade. */
public interface Pagavel {
    Pagamento processarPagamento(Cliente cliente);
}
