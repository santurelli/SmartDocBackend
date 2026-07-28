package it.tinna.smartdoc.server.controller;

import it.tinna.smartdoc.server.delegate.lipe.LipeDelegate;
import it.tinna.smartdoc.shared.dto.lipe.LipeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/lipe")
public class LipeController {

    private static final Logger log = LoggerFactory.getLogger(LipeController.class);

    private final LipeDelegate lipeDelegate;

    public LipeController(LipeDelegate lipeDelegate) {
        this.lipeDelegate = lipeDelegate;
    }

    /**
     * Restituisce l'anteprima dei dati LIPE calcolati dal DB per il trimestre indicato.
     * I campi integrativi (debiti/crediti precedenti, acconto, ecc.) sono a zero —
     * l'utente li completa sul frontend e poi chiama /genera-xml.
     *
     * GET /api/lipe/anteprima?anno=2024&trimestre=1
     */
    @GetMapping("/anteprima")
    public ResponseEntity<?> getAnteprima(
            @RequestParam int anno,
            @RequestParam int trimestre) {
        if (trimestre < 1 || trimestre > 4) {
            return ResponseEntity.badRequest().body("Il trimestre deve essere compreso tra 1 e 4.");
        }
        try {
            LipeDto dto = lipeDelegate.calcolaAnteprima(anno, trimestre);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SQLException e) {
            log.error("Errore nel calcolo anteprima LIPE anno={} trimestre={}", anno, trimestre, e);
            return ResponseEntity.internalServerError().body("Errore nel calcolo dei dati LIPE.");
        }
    }

    /**
     * Genera il file XML LIPE pronto per la trasmissione all'AdE.
     * Riceve il DTO completo con i campi integrativi valorizzati dall'utente.
     *
     * POST /api/lipe/genera-xml
     * Body: LipeDto (JSON)
     * Response: file XML scaricabile
     */
    @PostMapping("/genera-xml")
    public ResponseEntity<byte[]> generaXml(@RequestBody LipeDto dto) {
        if (dto.getAnno() == null || dto.getTrimestre() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (dto.getTrimestre() < 1 || dto.getTrimestre() > 4) {
            return ResponseEntity.badRequest().build();
        }
        try {
            String xml = lipeDelegate.generaXml(dto);
            byte[] xmlBytes = xml.getBytes(StandardCharsets.UTF_8);

            String filename = String.format("LIPE_%d_T%d.xml", dto.getAnno(), dto.getTrimestre());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_XML)
                    .contentLength(xmlBytes.length)
                    .body(xmlBytes);
        } catch (Exception e) {
            log.error("Errore nella generazione XML LIPE anno={} trimestre={}", dto.getAnno(), dto.getTrimestre(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
