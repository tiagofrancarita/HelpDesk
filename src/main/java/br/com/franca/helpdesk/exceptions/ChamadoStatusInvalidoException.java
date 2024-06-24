package br.com.franca.helpdesk.exceptions;

public class ChamadoStatusInvalidoException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public ChamadoStatusInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChamadoStatusInvalidoException(String message) {
        super(message);
    }

}