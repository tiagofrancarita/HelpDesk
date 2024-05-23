package br.com.franca.helpdesk.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TecnicosNotFoundException.class)
    public ResponseEntity<Object> handleTecnicosNotFoundException(TecnicosNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", ex.getTimestamp());
        body.put("status", ex.getStatus().value());
        body.put("error", ex.getError());
        body.put("message", ex.getMessage());
        body.put("path", ex.getPath());

        return new ResponseEntity<>(body, ex.getStatus());
    }
}
