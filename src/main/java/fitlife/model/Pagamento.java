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
