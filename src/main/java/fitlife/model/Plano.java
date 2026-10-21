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
