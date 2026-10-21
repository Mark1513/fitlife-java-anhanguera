package fitlife;

import fitlife.console.ConsoleApp;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class ConsoleAppTest {
    private String executar(String roteiro) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (Scanner entrada = new Scanner(roteiro);
             PrintStream saida = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            new ConsoleApp(entrada, saida).executar();
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void executaTodasAsOpcoesEmUmaSessao() {
        String texto = executar("""
                1
                Marcos Paulo Santos Mota
                marcos@exemplo.com
                20
                2
                3
                1
                Mensal
                100,00
                3
                2
                Anual
                100.00
                3
                3
                Premium
                150,00
                Aulas coletivas
                4
                5
                marcos@exemplo.com
                2
                6
                marcos@exemplo.com
                Treino A
                Descrição demonstrativa
                7
                8
                marcos@exemplo.com
                9
                marcos@exemplo.com
                0
                """);
        assertAll(() -> assertTrue(texto.contains("Cliente cadastrado")),
                () -> assertTrue(texto.contains("ID 3")),
                () -> assertTrue(texto.contains("Plano associado com sucesso")),
                () -> assertTrue(texto.contains("Treino #1")),
                () -> assertTrue(texto.contains("Pagamento SIMULADO #1")),
                () -> assertTrue(texto.contains("90,00")),
                () -> assertTrue(texto.contains("Treinos: 1")),
                () -> assertTrue(texto.contains("FitLife encerrado")),
                () -> assertFalse(texto.contains("Não foi possível concluir")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "", "2147483648", "-1", "10", "1.5"})
    void menuResisteAEntradaInvalida(String invalida) {
        String texto = executar(invalida + "\n0\n");
        assertTrue(texto.contains("Não foi possível concluir") || texto.contains("Opção inválida"));
        assertTrue(texto.contains("FitLife encerrado"));
    }

    @Test
    void menuTrataRegrasDeNegocioEContinua() {
        String texto = executar("""
                1
                Ana
                ana@exemplo.com
                14
                1
                Ana
                ana@exemplo.com
                18
                8
                ana@exemplo.com
                6
                ausente@exemplo.com
                9
                ausente@exemplo.com
                3
                9
                0
                """);
        assertTrue(texto.contains("idade mínima"));
        assertTrue(texto.contains("Cliente cadastrado"));
        assertTrue(texto.contains("Cliente sem plano"));
        assertTrue(texto.contains("Cliente inexistente"));
        assertTrue(texto.contains("Tipo de plano inválido"));
        assertTrue(texto.contains("FitLife encerrado"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "1.000,00", "abc", "NaN", "1,234", "1e3", ""})
    void menuRejeitaValorMonetarioInvalido(String valor) {
        String texto = executar("3\n1\nMensal\n" + valor + "\n4\n0\n");
        assertTrue(texto.contains("Valor inválido"));
        assertTrue(texto.contains("Nenhum plano cadastrado"));
        assertTrue(texto.contains("FitLife encerrado"));
    }

    @Test
    void listaColecoesVazias() {
        String texto = executar("2\n4\n7\n0\n");
        assertTrue(texto.contains("Nenhum cliente cadastrado"));
        assertTrue(texto.contains("Nenhum plano cadastrado"));
        assertTrue(texto.contains("Nenhum treino cadastrado"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1\nAna\n", "3\n", "8\n"})
    void encerraSemFalhaQuandoEntradaAcaba(String roteiro) {
        String texto = executar(roteiro);
        assertTrue(texto.contains("Entrada encerrada"));
        assertTrue(texto.contains("FitLife encerrado"));
    }
}
