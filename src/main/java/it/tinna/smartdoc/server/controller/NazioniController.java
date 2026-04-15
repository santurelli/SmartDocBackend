package it.tinna.smartdoc.server.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.dao.nazioni.NazioniDao;
import it.tinna.smartdoc.shared.dto.nazioni.NazioneDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/nazioni")
@RequiredArgsConstructor
@Slf4j
public class NazioniController {

    private final NazioniDao nazioniDao;

    @GetMapping
    public ResponseEntity<List<NazioneDto>> getAll() {
        try {
            List<NazioneDto> list = nazioniDao.getAll();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("Errore nel recupero delle nazioni", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
