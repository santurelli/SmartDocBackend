package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.citta.CittaDelegate;
import it.tinna.smartdoc.shared.dto.citta.CittaDto;

@RestController
@RequestMapping("/api/citta")
public class CittaController {

    @Autowired
    private CittaDelegate cittaDelegate;

    @GetMapping("/suggestion")
    public ResponseEntity<List<CittaDto>> getSuggestion(@RequestParam String q) {
        try {
            return ResponseEntity.ok(cittaDelegate.getSuggestion(q));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

