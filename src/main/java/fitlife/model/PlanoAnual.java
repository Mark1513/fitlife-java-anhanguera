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
