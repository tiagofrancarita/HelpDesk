package br.com.franca.helpdesk.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class TecnicoNotExludeAssociantioTicket extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private HttpStatus status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public TecnicoNotExludeAssociantioTicket() {
        super();
    }

    public TecnicoNotExludeAssociantioTicket(String message, HttpStatus status, String error, String path) {
        super(message);
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
