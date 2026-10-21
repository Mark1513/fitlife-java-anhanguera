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
