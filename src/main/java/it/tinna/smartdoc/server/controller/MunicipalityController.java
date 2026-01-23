package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.municipality.MunicipalityDelegate;
import it.tinna.smartdoc.shared.dto.municipality.MunicipalityDto;

@RestController
@RequestMapping("/api/municipalities")
public class MunicipalityController {

    @Autowired
    private MunicipalityDelegate municipalityDelegate;

    @GetMapping("/suggestion")
    public ResponseEntity<?> getSuggestion(@RequestParam("q") String q) {
        try {
            List<MunicipalityDto> suggestions = municipalityDelegate.getSuggestion(q);
            return ResponseEntity.ok(suggestions);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("Error fetching suggestions: " + e.getMessage());
        }
    }
}
