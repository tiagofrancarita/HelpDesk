package br.com.franca.helpdesk.exceptions;

public class ChamadoStatusUpdateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ChamadoStatusUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChamadoStatusUpdateException(String message) {
        super(message);
    }

}