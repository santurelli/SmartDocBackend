package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.tinna.smartdoc.server.delegate.documenti.BollaCaricoDelegate;
import it.tinna.smartdoc.server.security.UserContextHolder;
import it.tinna.smartdoc.shared.dto.documenti.BollaCaricoDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;

@RestController
@RequestMapping("/api/bollecarico")
public class BollaCaricoController
{

    private static final Logger    log = LoggerFactory.getLogger(BollaCaricoController.class);

    private static final int       TIPO_ACCOUNT_ENTERPRISE = 4;

    @Autowired
    private BollaCaricoDelegate    bollaCaricoDelegate;

    private boolean isPianoEnterprise()
    {
        Integer tipoAccount = UserContextHolder.getTipoAccount();
        return tipoAccount != null && tipoAccount >= TIPO_ACCOUNT_ENTERPRISE;
    }

    @GetMapping
    public ResponseEntity<?> getList(@RequestParam(required = false) String dtFrom,
                                     @RequestParam(required = false) String dtTo,
                                     @RequestParam(required = false) String soggetto,
                                     @RequestParam(required = false) String numDocFornitore,
                                     @RequestParam(required = false, defaultValue = "0") int start,
                                     @RequestParam(required = false, defaultValue = "10") int length,
                                     @RequestParam(required = false) String orderColumn,
                                     @RequestParam(required = false, defaultValue = "desc") String orderDir)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            DatatablesResponseDto<BollaCaricoDto> list = bollaCaricoDelegate.getList(dtFrom, dtTo, soggetto, numDocFornitore, length, start, orderColumn, orderDir);
            return ResponseEntity.ok(list);
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero dell'elenco bolle di carico", e);
            return ResponseEntity.internalServerError().body("Errore nel recupero delle bolle di carico.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable long id)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            BollaCaricoDto dto = bollaCaricoDelegate.getById(id);
            return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero della bolla di carico {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nel recupero della bolla di carico.");
        }
    }

    @PostMapping
    public ResponseEntity<?> insert(@RequestBody BollaCaricoDto dto)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            Integer id = bollaCaricoDelegate.insert(dto);
            return ResponseEntity.ok(id);
        }
        catch ( Exception e )
        {
            log.error("Errore nel salvataggio della bolla di carico", e);
            return ResponseEntity.internalServerError().body("Errore nel salvataggio della bolla di carico.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id,
                                    @RequestBody BollaCaricoDto dto)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            dto.setId(id);
            bollaCaricoDelegate.update(dto);
            return ResponseEntity.ok(id);
        }
        catch ( Exception e )
        {
            log.error("Errore nell'aggiornamento della bolla di carico {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nell'aggiornamento della bolla di carico.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id,
                                    @RequestParam(required = false) Long userId)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            BollaCaricoDto dto = new BollaCaricoDto();
            dto.setId(id);
            dto.setUserLastUpdate(userId);
            bollaCaricoDelegate.delete(dto);
            return ResponseEntity.ok().build();
        }
        catch ( SQLException e )
        {
            log.error("Errore nella cancellazione della bolla di carico {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nella cancellazione della bolla di carico.");
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<?> isExistentNumero(@RequestParam Integer numero,
                                              @RequestParam(required = false) String particella,
                                              @RequestParam(required = false) String data,
                                              @RequestParam(required = false) Integer id)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            return ResponseEntity.ok(bollaCaricoDelegate.isExistentNumero(numero, particella, data, id));
        }
        catch ( SQLException e )
        {
            log.error("Errore nella verifica del numero bolla di carico", e);
            return ResponseEntity.internalServerError().body("Errore nella verifica del numero.");
        }
    }

    @GetMapping("/nextNum")
    public ResponseEntity<?> getNextNum(@RequestParam String data)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            return ResponseEntity.ok(bollaCaricoDelegate.getNextNum(data));
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero del prossimo numero bolla di carico", e);
            return ResponseEntity.internalServerError().body("Errore nel recupero del prossimo numero.");
        }
    }

    @GetMapping("/combos")
    public ResponseEntity<?> getCombosMap()
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            return ResponseEntity.ok(bollaCaricoDelegate.getCombosMap());
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero delle combo per le bolle di carico", e);
            return ResponseEntity.internalServerError().body("Errore nel recupero delle combo.");
        }
    }

}
