package pl.szkolaspringa.bookstore;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class CustomGlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        var errors = exception.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + " - " + err.getDefaultMessage())
                .toArray(String[]::new);
        return toErrorResp(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException exception) {
        return toErrorResp(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException exception) {
        return toErrorResp(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException exception) {
        return toErrorResp(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    private static ResponseEntity<?> toErrorResp(HttpStatus status, String... errors) {
        return new ResponseEntity<>(toErrorObj(status, errors), status);
    }

    private static Map<?, ?> toErrorObj(HttpStatus status, String... errors) {
        return Map.of("timestamp", new Date(), "status", status.value(), "errors", List.of(errors));
    }
}
