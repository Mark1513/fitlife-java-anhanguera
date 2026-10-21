package fitlife.model;

import fitlife.exception.DadosInvalidosException;
import fitlife.util.Validacao;

public class Cliente {
    private String nome;
    private final String email;
    private int idade;
    private Plano plano;

    public Cliente(String nome, String email, int idade) {
        this.nome = Validacao.textoObrigatorio(nome, "Nome");
        this.email = Validacao.email(email);
        this.idade = Validacao.idade(idade);
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public int getIdade() { return idade; }
    public Plano getPlano() { return plano; }

    public void setNome(String nome) {
        this.nome = Validacao.textoObrigatorio(nome, "Nome");
    }

    public void setIdade(int idade) {
        this.idade = Validacao.idade(idade);
    }

    // O e-mail é a chave estável do cadastro; não possui setter.
    public void associarPlano(Plano plano) {
        if (plano == null) {
            throw new DadosInvalidosException("Selecione um plano válido.");
        }
        this.plano = plano;
    }

    @Override
    public String toString() {
        return nome + " | " + email + " | " + idade + " anos | Plano: "
                + (plano == null ? "não associado" : plano.getNome());
    }
}
