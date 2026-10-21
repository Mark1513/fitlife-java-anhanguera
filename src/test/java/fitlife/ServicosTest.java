package fitlife;

import fitlife.exception.DadosInvalidosException;
import fitlife.exception.EntidadeNaoEncontradaException;
import fitlife.interfaces.Pagavel;
import fitlife.model.Cliente;
import fitlife.model.Pagamento;
import fitlife.model.PlanoAnual;
import fitlife.model.PlanoMensal;
import fitlife.model.Treino;
import fitlife.service.ClienteService;
import fitlife.service.PagamentoService;
import fitlife.service.PlanoService;
import fitlife.service.TreinoService;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServicosTest {
    private PlanoService planos;
    private ClienteService clientes;
    private TreinoService treinos;
    private PagamentoService pagamentos;
    private ByteArrayOutputStream console;
    private Cliente cliente;

    @BeforeEach
    void preparar() {
        planos = new PlanoService();
        clientes = new ClienteService(planos);
        treinos = new TreinoService(clientes);
        console = new ByteArrayOutputStream();
        pagamentos = new PagamentoService(new PrintStream(console, true, StandardCharsets.UTF_8));
        cliente = clientes.cadastrar("Marcos Paulo Santos Mota", "marcos@exemplo.com", 20);
    }

    @Test
    void cadastraEConsultaCliente() {
        assertSame(cliente, clientes.buscarPorEmail(" MARCOS@EXEMPLO.COM "));
        assertEquals(1, clientes.listar().size());
    }

    @Test
    void rejeitaCadastroDuplicadoEInvalidoSemModificarLista() {
        assertThrows(DadosInvalidosException.class,
                () -> clientes.cadastrar("Outro", "MARCOS@EXEMPLO.COM", 30));
        assertThrows(DadosInvalidosException.class,
                () -> clientes.cadastrar("Menor", "menor@exemplo.com", 14));
        assertEquals(1, clientes.listar().size());
    }

    @Test
    void associaPlanoCadastradoAoCliente() {
        PlanoMensal plano = new PlanoMensal();
        planos.cadastrar(plano);
        clientes.associarPlano(cliente.getEmail(), 1);
        assertSame(plano, cliente.getPlano());
    }

    @Test
    void rejeitaAssociacoesInexistentesSemAlterarCliente() {
        planos.cadastrar(new PlanoMensal());
        assertThrows(EntidadeNaoEncontradaException.class,
                () -> clientes.associarPlano("ausente@exemplo.com", 1));
        assertThrows(EntidadeNaoEncontradaException.class,
                () -> clientes.associarPlano(cliente.getEmail(), 99));
        assertNull(cliente.getPlano());
    }

    @Test
    void rejeitaPlanosInvalidosEDuplicados() {
        planos.cadastrar(new PlanoMensal());
        assertThrows(DadosInvalidosException.class, () -> planos.cadastrar(null));
        assertThrows(DadosInvalidosException.class,
                () -> planos.cadastrar(new PlanoMensal("MENSAL", BigDecimal.TEN)));
        assertThrows(EntidadeNaoEncontradaException.class, () -> planos.buscarPorId(0));
        assertThrows(EntidadeNaoEncontradaException.class, () -> planos.buscarPorId(2));
        assertEquals(1, planos.listar().size());
    }

    @Test
    void cadastraEConsultaTreinoPorIdEPorCliente() {
        Treino treino = treinos.cadastrar(cliente.getEmail(), "Treino A", "Exercício demonstrativo");
        assertSame(cliente, treino.getCliente());
        assertEquals(1, treino.getId());
        assertSame(treino, treinos.buscarPorId(1));
        assertEquals(1, treinos.listarPorCliente(cliente.getEmail()).size());
        clientes.cadastrar("Ana", "ana@exemplo.com", 18);
        assertTrue(treinos.listarPorCliente("ana@exemplo.com").isEmpty());
    }

    @Test
    void rejeitaTreinoSemClienteCadastrado() {
        assertThrows(EntidadeNaoEncontradaException.class,
                () -> treinos.cadastrar("ausente@exemplo.com", "A", "Descrição"));
        assertThrows(EntidadeNaoEncontradaException.class, () -> treinos.buscarPorId(1));
        assertThrows(EntidadeNaoEncontradaException.class,
                () -> treinos.listarPorCliente("ausente@exemplo.com"));
        assertTrue(treinos.listar().isEmpty());
    }

    @Test
    void rejeitaTreinoInvalidoSemConsumirIdentificador() {
        assertThrows(DadosInvalidosException.class,
                () -> treinos.cadastrar(cliente.getEmail(), " ", "Descrição"));
        assertThrows(DadosInvalidosException.class,
                () -> treinos.cadastrar(cliente.getEmail(), "A", " "));
        assertThrows(DadosInvalidosException.class, () -> new Treino(0, "A", "D", cliente));
        assertThrows(DadosInvalidosException.class, () -> new Treino(1, "A", "D", null));
        assertTrue(treinos.listar().isEmpty());
        assertEquals(1, treinos.cadastrar(cliente.getEmail(), "A", "Descrição").getId());
    }

    @Test
    void processaPagamentoPelaInterfaceEConfirmaNoConsole() {
        planos.cadastrar(new PlanoAnual("Anual", new BigDecimal("100")));
        clientes.associarPlano(cliente.getEmail(), 1);
        Pagavel contrato = pagamentos;
        Pagamento recibo = contrato.processarPagamento(cliente);
        assertEquals(new BigDecimal("90.00"), recibo.valor());
        assertEquals(1, recibo.id());
        assertEquals(1, pagamentos.listar().size());
        String texto = console.toString(StandardCharsets.UTF_8);
        assertTrue(texto.contains("SIMULADO"));
        assertTrue(texto.contains("Marcos Paulo Santos Mota"));
        assertTrue(texto.contains("90,00"));
        assertTrue(texto.contains("sucesso"));
    }

    @Test
    void rejeitaPagamentoSemPlanoENuloSemGerarRecibo() {
        assertThrows(DadosInvalidosException.class, () -> pagamentos.processarPagamento(cliente));
        assertThrows(DadosInvalidosException.class, () -> pagamentos.processarPagamento(null));
        assertTrue(pagamentos.listar().isEmpty());
        assertEquals("", console.toString(StandardCharsets.UTF_8));
    }

    @Test
    void reciboPreservaDadosDepoisDaTrocaDePlanoENome() {
        cliente.associarPlano(new PlanoMensal());
        Pagamento primeiro = pagamentos.processarPagamento(cliente);
        cliente.setNome("Marcos Mota");
        cliente.associarPlano(new PlanoAnual("Anual", new BigDecimal("100")));
        Pagamento segundo = pagamentos.processarPagamento(cliente);
        assertEquals("Marcos Paulo Santos Mota", primeiro.nomeCliente());
        assertEquals("Mensal", primeiro.nomePlano());
        assertEquals(new BigDecimal("100.00"), primeiro.valor());
        assertEquals(2, segundo.id());
        assertEquals(new BigDecimal("90.00"), segundo.valor());
    }

    @Test
    void listasExternasNaoPermitemRemoverCadastros() {
        planos.cadastrar(new PlanoMensal());
        treinos.cadastrar(cliente.getEmail(), "A", "Descrição");
        cliente.associarPlano(planos.buscarPorId(1));
        pagamentos.processarPagamento(cliente);
        assertThrows(UnsupportedOperationException.class, () -> clientes.listar().clear());
        assertThrows(UnsupportedOperationException.class, () -> planos.listar().clear());
        assertThrows(UnsupportedOperationException.class, () -> treinos.listar().clear());
        assertThrows(UnsupportedOperationException.class, () -> pagamentos.listar().clear());
    }
}
