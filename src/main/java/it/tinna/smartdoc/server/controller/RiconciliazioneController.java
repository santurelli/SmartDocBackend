package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import it.tinna.smartdoc.server.delegate.riconciliazione.RiconciliazioneDelegate;
import it.tinna.smartdoc.server.security.UserContextHolder;
import it.tinna.smartdoc.shared.dto.riconciliazione.RiconciliazioneCsvMappingDto;
import it.tinna.smartdoc.shared.dto.riconciliazione.RiconciliazioneImportDto;
import it.tinna.smartdoc.shared.dto.riconciliazione.RiconciliazioneMovimentoDto;

@RestController
@RequestMapping("/api/riconciliazione")
public class RiconciliazioneController
{

    private static final Logger              log = LoggerFactory.getLogger(RiconciliazioneController.class);

    private static final int                  TIPO_ACCOUNT_ENTERPRISE = 4;

    private final RiconciliazioneDelegate     riconciliazioneDelegate;

    public RiconciliazioneController(RiconciliazioneDelegate riconciliazioneDelegate)
    {
        this.riconciliazioneDelegate = riconciliazioneDelegate;
    }

    private boolean isPianoEnterprise()
    {
        Integer tipoAccount = UserContextHolder.getTipoAccount();
        return tipoAccount != null && tipoAccount >= TIPO_ACCOUNT_ENTERPRISE;
    }

    @GetMapping("/import")
    public ResponseEntity<?> getListImport()
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            List<RiconciliazioneImportDto> list = riconciliazioneDelegate.getListImport();
            return ResponseEntity.ok(list);
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero degli import di riconciliazione", e);
            return ResponseEntity.internalServerError().body("Errore nel recupero degli import.");
        }
    }

    @GetMapping("/import/{id}/movimenti")
    public ResponseEntity<?> getMovimenti(@PathVariable long id)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            List<RiconciliazioneMovimentoDto> list = riconciliazioneDelegate.getMovimentiByImport(id);
            return ResponseEntity.ok(list);
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero dei movimenti per l'import {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nel recupero dei movimenti.");
        }
    }

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<?> importFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam String formato,
                                        @RequestParam(required = false) Integer idRisorsa,
                                        @RequestParam(required = false) Long userId)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            RiconciliazioneImportDto result = riconciliazioneDelegate.importFile(
                file.getBytes(), file.getOriginalFilename(), formato, idRisorsa, userId != null ? userId : 0);
            return ResponseEntity.ok(result);
        }
        catch ( Exception e )
        {
            log.error("Errore nell'import del file di riconciliazione", e);
            return ResponseEntity.internalServerError().body("Errore nell'elaborazione del file.");
        }
    }

    @PostMapping("/movimenti/{id}/abbina")
    public ResponseEntity<?> abbina(@PathVariable long id,
                                    @RequestBody Map<String, Object> body)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            String tipoScadenza = (String) body.get("tipoScadenza");
            long idScadenza = Long.parseLong(body.get("idScadenza").toString());
            String dataValuta = (String) body.get("dataValuta");
            long userId = body.get("userId") != null ? Long.parseLong(body.get("userId").toString()) : 0;
            riconciliazioneDelegate.confermaAbbinamento(id, tipoScadenza, idScadenza, dataValuta, userId);
            return ResponseEntity.ok().build();
        }
        catch ( Exception e )
        {
            log.error("Errore nella conferma dell'abbinamento per il movimento {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nella conferma dell'abbinamento.");
        }
    }

    @PostMapping("/movimenti/{id}/ignora")
    public ResponseEntity<?> ignora(@PathVariable long id,
                                    @RequestParam(required = false) Long userId)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            riconciliazioneDelegate.ignoraMovimento(id, userId != null ? userId : 0);
            return ResponseEntity.ok().build();
        }
        catch ( SQLException e )
        {
            log.error("Errore nell'ignorare il movimento {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nell'operazione.");
        }
    }

    @GetMapping("/csv-mapping")
    public ResponseEntity<?> getCsvMapping(@RequestParam long idRisorsa)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            RiconciliazioneCsvMappingDto mapping = riconciliazioneDelegate.getCsvMapping(idRisorsa);
            return mapping != null ? ResponseEntity.ok(mapping) : ResponseEntity.notFound().build();
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero del mapping CSV per la risorsa {}", idRisorsa, e);
            return ResponseEntity.internalServerError().body("Errore nel recupero del mapping.");
        }
    }

    @PostMapping("/csv-mapping")
    public ResponseEntity<?> saveCsvMapping(@RequestBody RiconciliazioneCsvMappingDto dto,
                                            @RequestParam(required = false) Long userId)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            riconciliazioneDelegate.saveCsvMapping(dto, userId != null ? userId : 0);
            return ResponseEntity.ok().build();
        }
        catch ( SQLException e )
        {
            log.error("Errore nel salvataggio del mapping CSV", e);
            return ResponseEntity.internalServerError().body("Errore nel salvataggio del mapping.");
        }
    }

}
