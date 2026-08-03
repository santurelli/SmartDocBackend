package it.tinna.smartdoc.server.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.studio.StudioDelegate;
import it.tinna.smartdoc.shared.dto.studio.StudioClientiDto;

@RestController
@RequestMapping("/api/studio")
public class StudioController
{
    private static final Logger log = LoggerFactory.getLogger(StudioController.class);

    private final StudioDelegate studioDelegate;
    private final JdbcTemplate   serviceJdbcTemplate;

    public StudioController(StudioDelegate studioDelegate,
                            @Qualifier("serviceJdbcTemplate") JdbcTemplate serviceJdbcTemplate)
    {
        this.studioDelegate = studioDelegate;
        this.serviceJdbcTemplate = serviceJdbcTemplate;
    }

    private Long getCurrentTenantId()
    {
        String dbKey = DatabaseContextHolder.getClientDatabase();
        if ( dbKey == null || dbKey.trim().isEmpty() )
        {
            return null;
        }
        try
        {
            return serviceJdbcTemplate.queryForObject(
                "SELECT k_d_e_enti FROM d_e_enti WHERE nome_db = ? AND fl_deleted = 0 LIMIT 1",
                Long.class, dbKey
            );
        }
        catch ( Exception e )
        {
            log.warn("Impossibile recuperare k_d_e_enti per dbKey {}: {}", dbKey, e.getMessage());
            return null;
        }
    }

    @GetMapping("/clienti")
    public ResponseEntity<?> getClientiStudio()
    {
        Long studioId = getCurrentTenantId();
        if ( studioId == null )
        {
            return ResponseEntity.badRequest().body("Tenant non identificato.");
        }
        try
        {
            List<StudioClientiDto> list = studioDelegate.getClientiStudio(studioId);
            return ResponseEntity.ok(list);
        }
        catch ( Exception e )
        {
            log.error("Errore durante il recupero dei clienti dello studio", e);
            return ResponseEntity.internalServerError().body("Errore nel recupero dei clienti.");
        }
    }

    @PostMapping("/deleghe/invita")
    public ResponseEntity<?> invitaCliente(@RequestBody Map<String, String> body)
    {
        Long studioId = getCurrentTenantId();
        if ( studioId == null )
        {
            return ResponseEntity.badRequest().body("Tenant non identificato.");
        }
        String emailOrPiva = body != null ? body.get("search") : null;
        if ( emailOrPiva == null || emailOrPiva.trim().isEmpty() )
        {
            return ResponseEntity.badRequest().body("Inserisci una Partita IVA o Email valida.");
        }

        try
        {
            boolean inviato = studioDelegate.invitaCliente(studioId, emailOrPiva, "STUDIO_" + studioId);
            if ( !inviato )
            {
                return ResponseEntity.status(404).body("Nessuna azienda cliente trovata in SmartDoc con questa Partita IVA / Email.");
            }
            return ResponseEntity.ok(Map.of("message", "Richiesta di delega inviata con successo all'azienda cliente."));
        }
        catch ( IllegalArgumentException e )
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch ( Exception e )
        {
            log.error("Errore durante l'invio della richiesta di delega", e);
            return ResponseEntity.internalServerError().body("Errore nell'invio della delega.");
        }
    }

    @GetMapping("/deleghe/ricevute")
    public ResponseEntity<?> getDelegheRicevute()
    {
        Long clienteId = getCurrentTenantId();
        if ( clienteId == null )
        {
            return ResponseEntity.badRequest().body("Tenant non identificato.");
        }
        try
        {
            List<Map<String, Object>> list = studioDelegate.getDelegheRicevute(clienteId);
            return ResponseEntity.ok(list);
        }
        catch ( Exception e )
        {
            log.error("Errore nel recupero delle deleghe ricevute", e);
            return ResponseEntity.internalServerError().body("Errore nel recupero delle deleghe.");
        }
    }

    @PostMapping("/deleghe/{id}/accetta")
    public ResponseEntity<?> accettaDelega(@PathVariable Long id)
    {
        Long clienteId = getCurrentTenantId();
        if ( clienteId == null )
        {
            return ResponseEntity.badRequest().body("Tenant non identificato.");
        }
        try
        {
            studioDelegate.accettaDelega(id, clienteId);
            return ResponseEntity.ok(Map.of("message", "Delega accettata con successo."));
        }
        catch ( Exception e )
        {
            log.error("Errore nell'accettazione della delega {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nell'accettazione della delega.");
        }
    }

    @PostMapping("/deleghe/{id}/revoca")
    public ResponseEntity<?> revocaDelega(@PathVariable Long id)
    {
        Long tenantId = getCurrentTenantId();
        if ( tenantId == null )
        {
            return ResponseEntity.badRequest().body("Tenant non identificato.");
        }
        try
        {
            studioDelegate.revocaDelega(id, tenantId);
            return ResponseEntity.ok(Map.of("message", "Delega revocata."));
        }
        catch ( Exception e )
        {
            log.error("Errore nella revoca della delega {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nella revoca della delega.");
        }
    }

    @PostMapping("/impersonate/{idCliente}")
    public ResponseEntity<?> impersonateCliente(@PathVariable Long idCliente,
                                                @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress)
    {
        Long studioId = getCurrentTenantId();
        if ( studioId == null )
        {
            return ResponseEntity.badRequest().body("Tenant studio non identificato.");
        }
        try
        {
            Map<String, Object> targetTenant = studioDelegate.impersonateCliente(studioId, idCliente, "STUDIO_OPERATOR", ipAddress);
            return ResponseEntity.ok(targetTenant);
        }
        catch ( SecurityException e )
        {
            return ResponseEntity.status(403).body(e.getMessage());
        }
        catch ( Exception e )
        {
            log.error("Errore durante l'impersonificazione dell'azienda cliente {}", idCliente, e);
            return ResponseEntity.internalServerError().body("Errore durante l'accesso al tenant cliente.");
        }
    }

    @GetMapping("/audit-log")
    public ResponseEntity<?> getAuditLog()
    {
        Long clienteId = getCurrentTenantId();
        if ( clienteId == null )
        {
            return ResponseEntity.badRequest().body("Tenant non identificato.");
        }
        try
        {
            List<Map<String, Object>> list = studioDelegate.getAuditLogs(clienteId);
            return ResponseEntity.ok(list);
        }
        catch ( Exception e )
        {
            log.error("Errore nel recupero dell'audit log per il cliente {}", clienteId, e);
            return ResponseEntity.internalServerError().body("Errore nel recupero del registro accessi.");
        }
    }

    @PostMapping("/export/batch")
    public void exportBatchZip(@RequestBody Map<String, List<Long>> body,
                               @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress,
                               HttpServletResponse response)
    {
        Long studioId = getCurrentTenantId();
        if ( studioId == null )
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        List<Long> clientIds = body != null ? body.get("clientIds") : null;
        if ( clientIds == null || clientIds.isEmpty() )
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try
        {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"Export_Studio_Fatture_" + System.currentTimeMillis() + ".zip\"");
            studioDelegate.generateBatchZip(studioId, clientIds, "STUDIO_OPERATOR", ipAddress, response.getOutputStream());
            response.getOutputStream().flush();
        }
        catch ( Exception e )
        {
            log.error("Errore durante la generazione dell'archivio batch ZIP", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/export/client/{idCliente}")
    public void exportClientZip(@PathVariable Long idCliente,
                                @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress,
                                HttpServletResponse response)
    {
        exportBatchZip(Map.of("clientIds", List.of(idCliente)), ipAddress, response);
    }
}
