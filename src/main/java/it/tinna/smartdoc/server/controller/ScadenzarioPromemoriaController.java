package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.tinna.smartdoc.server.delegate.scadenzario.ScadenzarioPromemoriaDelegate;
import it.tinna.smartdoc.shared.dto.scadenzario.ScadenzarioInvioDto;
import it.tinna.smartdoc.shared.dto.scadenzario.ScadenzarioRegolaDto;

@RestController
@RequestMapping("/api/scadenzario/promemoria")
public class ScadenzarioPromemoriaController
{

    private static final Logger                log = LoggerFactory.getLogger(ScadenzarioPromemoriaController.class);

    private final ScadenzarioPromemoriaDelegate scadenzarioPromemoriaDelegate;

    public ScadenzarioPromemoriaController(ScadenzarioPromemoriaDelegate scadenzarioPromemoriaDelegate)
    {
        this.scadenzarioPromemoriaDelegate = scadenzarioPromemoriaDelegate;
    }

    @GetMapping("/invii")
    public ResponseEntity<?> getInviiByCliente(@RequestParam long idCliente)
    {
        try
        {
            List<ScadenzarioInvioDto> list = scadenzarioPromemoriaDelegate.getInviiByCliente(idCliente);
            return ResponseEntity.ok(list);
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero delle comunicazioni inviate per il cliente {}", idCliente, e);
            return ResponseEntity.internalServerError().body("Errore nel recupero delle comunicazioni.");
        }
    }

    @PostMapping("/esegui-ora")
    public ResponseEntity<?> eseguiOra()
    {
        try
        {
            org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof it.tinna.smartdoc.server.security.UserDetailsImpl))
            {
                return ResponseEntity.status(403).body("Accesso negato.");
            }

            it.tinna.smartdoc.server.security.UserDetailsImpl userDetails = (it.tinna.smartdoc.server.security.UserDetailsImpl) authentication.getPrincipal();
            String username = userDetails.getUsername() != null ? userDetails.getUsername().toLowerCase() : "";

            if (!username.contains("support"))
            {
                log.warn("Tentativo non autorizzato di esecuzione manuale del batch promemoria da parte dell'utente: {}", username);
                return ResponseEntity.status(403).body("Operazione consentita solo all'utente di supporto.");
            }

            scadenzarioPromemoriaDelegate.processaPromemoria();
            return ResponseEntity.ok().build();
        }
        catch ( Exception e )
        {
            log.error("Errore nell'esecuzione manuale del batch promemoria", e);
            return ResponseEntity.internalServerError().body("Errore durante l'esecuzione dei promemoria.");
        }
    }

    @GetMapping("/invii/fattura")
    public ResponseEntity<?> getInviiByFattura(@RequestParam long idFattura)
    {
        try
        {
            List<ScadenzarioInvioDto> list = scadenzarioPromemoriaDelegate.getInviiByFattura(idFattura);
            return ResponseEntity.ok(list);
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero delle comunicazioni inviate per la fattura {}", idFattura, e);
            return ResponseEntity.internalServerError().body("Errore nel recupero delle comunicazioni.");
        }
    }

    @GetMapping("/invii/fattura-fornitore")
    public ResponseEntity<?> getInviiByFatturaFornitore(@RequestParam long idFatturaFornitore)
    {
        try
        {
            List<ScadenzarioInvioDto> list = scadenzarioPromemoriaDelegate.getInviiByFatturaFornitore(idFatturaFornitore);
            return ResponseEntity.ok(list);
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero delle comunicazioni inviate per la fattura fornitore {}", idFatturaFornitore, e);
            return ResponseEntity.internalServerError().body("Errore nel recupero delle comunicazioni.");
        }
    }

    @GetMapping
    public ResponseEntity<?> getList(@RequestParam(required = false) String tipo)
    {
        try
        {
            List<ScadenzarioRegolaDto> list = scadenzarioPromemoriaDelegate.getList(tipo);
            return ResponseEntity.ok(list);
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero delle regole di scadenzario", e);
            return ResponseEntity.internalServerError().body("Errore nel recupero delle regole.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable long id)
    {
        try
        {
            ScadenzarioRegolaDto dto = scadenzarioPromemoriaDelegate.getById(id);
            return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero della regola {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nel recupero della regola.");
        }
    }

    @PostMapping
    public ResponseEntity<?> insert(@RequestBody ScadenzarioRegolaDto dto)
    {
        try
        {
            long id = scadenzarioPromemoriaDelegate.insert(dto);
            return ResponseEntity.ok(id);
        }
        catch ( SQLException e )
        {
            log.error("Errore nell'inserimento della regola di scadenzario", e);
            return ResponseEntity.internalServerError().body("Errore nel salvataggio della regola.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id,
                                    @RequestBody ScadenzarioRegolaDto dto)
    {
        try
        {
            dto.setId(id);
            scadenzarioPromemoriaDelegate.update(dto);
            return ResponseEntity.ok(id);
        }
        catch ( SQLException e )
        {
            log.error("Errore nell'aggiornamento della regola {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nell'aggiornamento della regola.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id)
    {
        try
        {
            scadenzarioPromemoriaDelegate.delete(id);
            return ResponseEntity.ok().build();
        }
        catch ( SQLException e )
        {
            log.error("Errore nella cancellazione della regola {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nella cancellazione della regola.");
        }
    }

}
