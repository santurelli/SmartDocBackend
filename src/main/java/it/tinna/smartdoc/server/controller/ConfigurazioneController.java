package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate;
import it.tinna.smartdoc.shared.dto.configurazione.ConfigurazioneDto;

@RestController
@RequestMapping("/api/configurazione")
public class ConfigurazioneController {

    @Autowired
    private ConfigurazioneDelegate configurazioneDelegate;

    @GetMapping("/get-by-domain")
    public ResponseEntity<Map<String, String>> getByDomain(@RequestParam String domain) {
        try {
            Map<String, ConfigurazioneDto> configMap = configurazioneDelegate.getByDomain(domain);
            Map<String, String> resultMap = new HashMap<>();
            
            if (configMap != null) {
                for (Map.Entry<String, ConfigurazioneDto> entry : configMap.entrySet()) {
                    resultMap.put(entry.getKey(), entry.getValue().getValore());
                }
            }
            
            return ResponseEntity.ok(resultMap);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ConfigurazioneDto>> getAll() {
        try {
            return ResponseEntity.ok(configurazioneDelegate.getAll());
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody ConfigurazioneDto dto) {
        try {
            configurazioneDelegate.save(dto);
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

