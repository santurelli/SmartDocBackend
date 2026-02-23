package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.progetti.ProgettiDelegate;
import it.tinna.smartdoc.shared.dto.progetti.ProgettoDto;

@RestController
@RequestMapping("/api/progetti")
public class ProgettiController {

    @Autowired
    private ProgettiDelegate progettiDelegate;

    @GetMapping("/suggestion")
    public ResponseEntity<List<ProgettoDto>> getSuggestion(@RequestParam(defaultValue = "") String q) {
        try {
            return ResponseEntity.ok(progettiDelegate.getSuggestion(q));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<ProgettoDto> insert(@RequestBody ProgettoDto dto) {
        try {
            Integer id = progettiDelegate.insert(dto);
            dto.setId(id.longValue());
            return ResponseEntity.ok(dto);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

