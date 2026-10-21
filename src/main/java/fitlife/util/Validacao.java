package fitlife.util;

import fitlife.exception.DadosInvalidosException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.regex.Pattern;

/** Regras compartilhadas pelos modelos, serviços e console. */
public final class Validacao {
    private static final Pattern EMAIL = Pattern.compile(
            "[A-Za-z0-9_+%-]+(?:\\.[A-Za-z0-9_+%-]+)*@"
            + "[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?"
            + "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)+");

    private Validacao() { }

    public static String textoObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(campo + " é obrigatório.");
        }
        return valor.strip();
    }

    public static String email(String valor) {
        String normalizado = textoObrigatorio(valor, "E-mail").toLowerCase(Locale.ROOT);
        if (normalizado.length() > 254 || !EMAIL.matcher(normalizado).matches()) {
            throw new DadosInvalidosException("E-mail inválido. Exemplo: aluno@exemplo.com");
        }
        return normalizado;
    }

    public static int idade(int valor) {
        if (valor < 15) {
            throw new DadosInvalidosException("A idade mínima é 15 anos.");
        }
        return valor;
    }

    public static BigDecimal dinheiro(BigDecimal valor) {
        if (valor == null || valor.signum() < 0) {
            throw new DadosInvalidosException("O valor deve ser maior ou igual a zero.");
        }
        try {
            return valor.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new DadosInvalidosException("Informe um valor com no máximo duas casas decimais.");
        }
    }
}
