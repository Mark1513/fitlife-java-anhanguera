package fitlife.exception;

/** Identifica uma consulta que não encontrou um cadastro. */
public class EntidadeNaoEncontradaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
