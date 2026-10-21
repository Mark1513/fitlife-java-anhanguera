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
