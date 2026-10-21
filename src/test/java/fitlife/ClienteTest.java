package fitlife;

import fitlife.exception.DadosInvalidosException;
import fitlife.model.Cliente;
import fitlife.model.PlanoMensal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {
    @Test
    void cadastraClienteValidoENormalizaDados() {
        Cliente cliente = new Cliente("  Marcos Paulo Santos Mota  ", " MARCOS@EXEMPLO.COM ", 20);
        assertAll(() -> assertEquals("Marcos Paulo Santos Mota", cliente.getNome()),
                () -> assertEquals("marcos@exemplo.com", cliente.getEmail()),
                () -> assertEquals(20, cliente.getIdade()),
                () -> assertNull(cliente.getPlano()));
    }

    @Test
    void aceitaIdadeMinima() {
        assertEquals(15, new Cliente("Ana", "ana@exemplo.com", 15).getIdade());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 14})
    void rejeitaIdadeInferiorAQuinze(int idade) {
        assertThrows(DadosInvalidosException.class, () -> new Cliente("Ana", "ana@exemplo.com", idade));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejeitaNomeVazio(String nome) {
        assertThrows(IllegalArgumentException.class, () -> new Cliente(nome, "ana@exemplo.com", 18));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"ana", "ana@", "@exemplo.com", "ana@localhost", "a b@exemplo.com",
            "ana@@exemplo.com", ".ana@exemplo.com", "ana..silva@exemplo.com", "ana@-exemplo.com"})
    void rejeitaEmailInvalido(String email) {
        assertThrows(DadosInvalidosException.class, () -> new Cliente("Ana", email, 18));
    }

    @Test
    void settersValidamAntesDeAlterarEstado() {
        Cliente cliente = new Cliente("Ana", "ana@exemplo.com", 18);
        assertThrows(DadosInvalidosException.class, () -> cliente.setNome(" "));
        assertThrows(DadosInvalidosException.class, () -> cliente.setIdade(14));
        assertEquals("Ana", cliente.getNome());
        assertEquals(18, cliente.getIdade());
        cliente.setNome("Ana Silva");
        cliente.setIdade(19);
        assertEquals("Ana Silva", cliente.getNome());
        assertEquals(19, cliente.getIdade());
    }

    @Test
    void associaPlanoERejeitaPlanoNulo() {
        Cliente cliente = new Cliente("Ana", "ana@exemplo.com", 18);
        PlanoMensal plano = new PlanoMensal();
        cliente.associarPlano(plano);
        assertSame(plano, cliente.getPlano());
        assertThrows(DadosInvalidosException.class, () -> cliente.associarPlano(null));
        assertSame(plano, cliente.getPlano());
    }
}
