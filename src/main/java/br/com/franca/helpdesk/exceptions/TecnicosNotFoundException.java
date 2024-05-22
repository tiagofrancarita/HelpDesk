package br.com.franca.helpdesk.exceptions;

public class TecnicosNotFoundException extends RuntimeException {
    public TecnicosNotFoundException(String message) {
        super(message);
    }
}
