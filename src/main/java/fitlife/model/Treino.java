package fitlife.model;

import fitlife.exception.DadosInvalidosException;
import fitlife.util.Validacao;

public class Treino {
    private final int id;
    private final String nome;
    private final String descricao;
    private final Cliente cliente;

    public Treino(int id, String nome, String descricao, Cliente cliente) {
        if (id <= 0 || cliente == null) {
            throw new DadosInvalidosException("Treino exige identificador positivo e cliente válido.");
        }
        this.id = id;
        this.nome = Validacao.textoObrigatorio(nome, "Nome do treino");
        this.descricao = Validacao.textoObrigatorio(descricao, "Descrição do treino");
        this.cliente = cliente;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public Cliente getCliente() { return cliente; }

    @Override
    public String toString() {
        return "Treino #" + id + " | " + nome + " | " + descricao
                + " | Cliente: " + cliente.getNome() + " (" + cliente.getEmail() + ")";
    }
}
