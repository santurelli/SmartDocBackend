package it.tinna.smartdoc.server.exception;

public class DocumentoGiaEsistenteException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DocumentoGiaEsistenteException(String message) {
        super(message);
    }

    public DocumentoGiaEsistenteException(String message, Throwable cause) {
        super(message, cause);
    }
}
