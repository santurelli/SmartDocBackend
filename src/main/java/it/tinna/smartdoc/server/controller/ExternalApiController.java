package it.tinna.smartdoc.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.shared.dto.external.fastorder.FatturaDto;
import it.tinna.smartdoc.server.delegate.external.ExternalIntegrationDelegate;
import it.tinna.smartdoc.shared.dto.response.ExternalResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/external/fastorder")
@RequiredArgsConstructor
@Slf4j
public class ExternalApiController {

    private final ExternalIntegrationDelegate externalIntegrationDelegate;

    @PostMapping("/invio-fattura")
    public ResponseEntity<ExternalResponseDto> invioFattura(
            @RequestParam String nomeStore,
            @RequestBody FatturaDto fatturaDto) {
        
        log.info("Ricevuta richiesta invio fattura da FastOrder per store: {}", nomeStore);
        try {
            ExternalResponseDto response = externalIntegrationDelegate.processaFatturaFastOrder(nomeStore, fatturaDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Errore durante l'invio della fattura da FastOrder", e);
            ExternalResponseDto errorResponse = ExternalResponseDto.builder()
                    .success(false)
                    .message("Errore interno: " + e.getMessage())
                    .errorCode("INTERNAL_ERROR")
                    .build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/esito-invio/{idFattura}")
    public ResponseEntity<ExternalResponseDto> getEsitoInvio(
            @PathVariable Long idFattura,
            @RequestParam String nomeStore) {
        
        log.info("Richiesta esito invio per fattura ID: {} e store: {}", idFattura, nomeStore);
        try {
            ExternalResponseDto response = externalIntegrationDelegate.getEsitoInvio(idFattura, nomeStore);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Errore durante il recupero dell'esito invio", e);
            ExternalResponseDto errorResponse = ExternalResponseDto.builder()
                    .success(false)
                    .message("Errore interno: " + e.getMessage())
                    .errorCode("INTERNAL_ERROR")
                    .build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
