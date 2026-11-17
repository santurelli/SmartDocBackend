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

    @ExceptionHandler(DocumentoGiaEsistenteException.class)
    public ResponseEntity<String> handleDocumentoGiaEsistente(DocumentoGiaEsistenteException ex, WebRequest request) {
        log.warn("Documento duplicato per la richiesta {}: {}", request.getDescription(false), ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Handle global exceptions capable of throwing any exception.
     * Logs the exception and returns a generic error message to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
        String duplicateMsg = findDuplicateMessage(ex);
        if (duplicateMsg != null) {
            log.warn("Documento duplicato per la richiesta {}: {}", request.getDescription(false), duplicateMsg);
            return new ResponseEntity<>(duplicateMsg, HttpStatus.CONFLICT);
        }
        log.error("Unhandled exception occurred at request: " + request.getDescription(false), ex);
        String message = ex.getMessage() != null ? ex.getMessage() : "Si è verificato un errore interno. Contattare l'amministratore.";
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String findDuplicateMessage(Throwable t) {
        while (t != null) {
            if (t instanceof DocumentoGiaEsistenteException) {
                return t.getMessage();
            }
            String msg = t.getMessage();
            if (msg != null) {
                if (msg.contains("è già presente") || msg.contains("già esistente")) {
                    return msg;
                }
                if (msg.toLowerCase().contains("duplicate key") || msg.toLowerCase().contains("unique constraint")) {
                    return "Il documento con questo numero o riferimento è già presente per l'anno di riferimento.";
                }
            }
            t = t.getCause();
        }
        return null;
    }
}

