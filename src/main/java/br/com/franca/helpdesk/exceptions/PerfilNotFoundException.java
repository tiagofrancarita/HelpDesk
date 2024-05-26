package br.com.franca.helpdesk.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


public class PerfilNotFoundException extends RuntimeException {

    private final HttpStatus status;
    private final String error;
    private final String path;
    private final LocalDateTime timestamp;



    public PerfilNotFoundException(HttpStatus status, String error, String path, LocalDateTime timestamp) {
        super();
        this.status = status;
        this.error = error;
        this.path = path;
        this.timestamp = timestamp;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
