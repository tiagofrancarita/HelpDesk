package br.com.franca.helpdesk.exceptions;

public class ClienteAndChamadosNotDeleted extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ClienteAndChamadosNotDeleted(String message, Throwable cause) {
        super(message, cause);
    }

    public ClienteAndChamadosNotDeleted(String message) {
        super(message);
    }
}
