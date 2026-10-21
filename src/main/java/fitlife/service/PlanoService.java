package fitlife.service;

import fitlife.exception.DadosInvalidosException;
import fitlife.exception.EntidadeNaoEncontradaException;
import fitlife.model.Plano;
import java.util.ArrayList;
import java.util.List;

public class PlanoService {
    private final List<Plano> planos = new ArrayList<>();

    public Plano cadastrar(Plano plano) {
        if (plano == null) {
            throw new DadosInvalidosException("Plano inválido.");
        }
        if (planos.stream().anyMatch(p -> p.getNome().equalsIgnoreCase(plano.getNome()))) {
            throw new DadosInvalidosException("Já existe um plano com esse nome.");
        }
        planos.add(plano);
        return plano;
    }

    public Plano buscarPorId(int id) {
        // O ID é a posição de cadastro + 1; não há exclusão nesta atividade.
        if (id < 1 || id > planos.size()) {
            throw new EntidadeNaoEncontradaException("Plano inexistente: " + id);
        }
        return planos.get(id - 1);
    }

    public List<Plano> listar() {
        return List.copyOf(planos);
    }
}
