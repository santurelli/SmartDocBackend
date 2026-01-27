package it.tinna.smartdoc.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle global exceptions capable of throwing any exception.
     * Logs the exception and returns a generic error message to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred at request: " + request.getDescription(false), ex);
        return new ResponseEntity<>("Si è verificato un errore interno. Contattare l'amministratore.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
