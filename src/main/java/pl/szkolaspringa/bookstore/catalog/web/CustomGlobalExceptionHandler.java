package pl.szkolaspringa.bookstore.catalog.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;

@ControllerAdvice
public class CustomGlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        var body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", exception.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + " - " + err.getDefaultMessage()).toList());
        return ResponseEntity.badRequest().body(body);
    }
}
