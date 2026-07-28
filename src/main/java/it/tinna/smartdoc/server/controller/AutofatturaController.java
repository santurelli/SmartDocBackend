package it.tinna.smartdoc.server.controller;

import it.tinna.smartdoc.server.delegate.documenti.AutofatturaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/autofattura")
public class AutofatturaController {

    private static final Logger log = LoggerFactory.getLogger(AutofatturaController.class);

    private final AutofatturaDelegate autofatturaDelegate;

    public AutofatturaController(AutofatturaDelegate autofatturaDelegate) {
        this.autofatturaDelegate = autofatturaDelegate;
    }

    /**
     * Genera il file XML dell'autofattura (TD17/TD18/TD19/TD20/TD28) per una fattura fornitore.
     *
     * GET /api/autofattura/genera-xml/{idFatturaFornitore}
     */
    @GetMapping("/genera-xml/{idFatturaFornitore}")
    public ResponseEntity<byte[]> generaXml(@PathVariable long idFatturaFornitore) {
        try {
            String xml = autofatturaDelegate.generaXml(idFatturaFornitore);
            byte[] xmlBytes = xml.getBytes(StandardCharsets.UTF_8);
            String filename = "autofattura_" + idFatturaFornitore + ".xml";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_XML)
                    .contentLength(xmlBytes.length)
                    .body(xmlBytes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Errore nella generazione dell'autofattura per id {}", idFatturaFornitore, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
