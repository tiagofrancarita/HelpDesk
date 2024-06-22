package br.com.franca.helpdesk.exceptions;

public class ObjectnotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public ObjectnotFoundException(String message) {
        super(message);
    }
}