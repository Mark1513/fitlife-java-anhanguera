package fitlife;

import fitlife.console.ConsoleApp;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * FitLife — Anhanguera, Aula 15, Unidade 4.
 * @author Marcos Paulo Santos Mota
 */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in, StandardCharsets.UTF_8)) {
            new ConsoleApp(entrada, System.out).executar();
        }
    }
}
