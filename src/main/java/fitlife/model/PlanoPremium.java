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
