package fitlife.service;

import fitlife.exception.EntidadeNaoEncontradaException;
import fitlife.model.Cliente;
import fitlife.model.Treino;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TreinoService {
    private final List<Treino> treinos = new ArrayList<>();
    private final ClienteService clienteService;

    public TreinoService(ClienteService clienteService) {
        this.clienteService = Objects.requireNonNull(clienteService, "Serviço de clientes obrigatório.");
    }

    public Treino cadastrar(String emailCliente, String nome, String descricao) {
        Cliente cliente = clienteService.buscarPorEmail(emailCliente);
        Treino treino = new Treino(treinos.size() + 1, nome, descricao, cliente);
        treinos.add(treino);
        return treino;
    }

    public Treino buscarPorId(int id) {
        return treinos.stream().filter(t -> t.getId() == id).findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Treino inexistente: " + id));
    }

    public List<Treino> listar() { return List.copyOf(treinos); }

    public List<Treino> listarPorCliente(String email) {
        Cliente cliente = clienteService.buscarPorEmail(email);
        return treinos.stream().filter(t -> t.getCliente() == cliente).toList();
    }
}
