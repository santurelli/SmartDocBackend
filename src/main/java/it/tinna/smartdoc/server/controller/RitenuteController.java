package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.ritenute.RitenuteDelegate;
import it.tinna.smartdoc.shared.dto.ritenute.RitenutaFornitoreDto;

@RestController
@RequestMapping("/api/ritenute")
public class RitenuteController {

    private static final Logger log = LoggerFactory.getLogger(RitenuteController.class);

    private final RitenuteDelegate ritenuteDelegate;

    public RitenuteController(RitenuteDelegate ritenuteDelegate) {
        this.ritenuteDelegate = ritenuteDelegate;
    }

    @GetMapping("/770")
    public ResponseEntity<?> get770(
            @RequestParam int anno,
            @RequestParam(required = false) Integer idFornitore) {
        try {
            List<RitenutaFornitoreDto> result = ritenuteDelegate.get770(anno, idFornitore);
            return ResponseEntity.ok(result);
        } catch (SQLException e) {
            log.error("Errore nel recupero del prospetto 770 per anno {}", anno, e);
            return ResponseEntity.internalServerError().body("Errore nel calcolo del prospetto 770.");
        }
    }

    @GetMapping("/combos")
    public ResponseEntity<Map<String, Object>> getCombos() {
        return ResponseEntity.ok(ritenuteDelegate.getCombos());
    }
}
