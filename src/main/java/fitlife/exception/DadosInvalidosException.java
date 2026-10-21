package fitlife.exception;

/** Erro de validação; também pode ser tratado como IllegalArgumentException. */
public class DadosInvalidosException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
