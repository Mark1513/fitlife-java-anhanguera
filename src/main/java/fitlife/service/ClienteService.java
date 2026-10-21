package fitlife.service;

import fitlife.exception.DadosInvalidosException;
import fitlife.exception.EntidadeNaoEncontradaException;
import fitlife.model.Cliente;
import fitlife.util.Validacao;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClienteService {
    private final List<Cliente> clientes = new ArrayList<>();
    private final PlanoService planoService;

    public ClienteService(PlanoService planoService) {
        this.planoService = Objects.requireNonNull(planoService, "Serviço de planos obrigatório.");
    }

    public Cliente cadastrar(String nome, String email, int idade) {
        Cliente cliente = new Cliente(nome, email, idade);
        if (clientes.stream().anyMatch(c -> c.getEmail().equals(cliente.getEmail()))) {
            throw new DadosInvalidosException("Já existe um cliente com esse e-mail.");
        }
        clientes.add(cliente);
        return cliente;
    }

    public Cliente buscarPorEmail(String email) {
        String chave = Validacao.email(email);
        return clientes.stream().filter(c -> c.getEmail().equals(chave)).findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente inexistente: " + chave));
    }

    public void associarPlano(String email, int planoId) {
        Cliente cliente = buscarPorEmail(email);
        cliente.associarPlano(planoService.buscarPorId(planoId));
    }

    public List<Cliente> listar() {
        return List.copyOf(clientes);
    }
}
