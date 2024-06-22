package br.com.franca.helpdesk.exceptions;

public class TecnicoAndChamadosNotDeleted extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TecnicoAndChamadosNotDeleted(String message, Throwable cause) {
        super(message, cause);
    }

    public TecnicoAndChamadosNotDeleted(String message) {
        super(message);
    }
}