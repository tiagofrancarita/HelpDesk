package br.com.franca.helpdesk.exceptions;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PerfilNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePerfilNotFoundException(PerfilNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatus().value());
        body.put("error", "Object NOT FOUND");
        body.put("message", ex.getMessage());
        body.put("path", ex.getPath());

        return new ResponseEntity<>(body, ex.getStatus());
    }

    @ExceptionHandler(TecnicoNotExludeAssociantioTicket.class)
    public ResponseEntity<Map<String, Object>> handleTecnicoNotExludeAssociantioTicket(TecnicoNotExludeAssociantioTicket ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatus().value());
        body.put("error", "Object NOT FOUND");
        body.put("message", ex.getMessage());
        body.put("path", ex.getPath());

        return new ResponseEntity<>(body, ex.getStatus());
    }
}
