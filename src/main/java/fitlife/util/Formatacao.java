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
