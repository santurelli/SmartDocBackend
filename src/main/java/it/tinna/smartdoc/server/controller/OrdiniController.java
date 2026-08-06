package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.tinna.smartdoc.server.delegate.documenti.OrdiniDelegate;
import it.tinna.smartdoc.server.security.UserContextHolder;
import it.tinna.smartdoc.shared.dto.documenti.OrdineDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;

@RestController
@RequestMapping("/api/ordini")
public class OrdiniController
{

    private static final Logger  log = LoggerFactory.getLogger(OrdiniController.class);

    private static final int     TIPO_ACCOUNT_ENTERPRISE = 4;

    @Autowired
    private OrdiniDelegate       ordiniDelegate;

    private boolean isPianoEnterprise()
    {
        Integer tipoAccount = UserContextHolder.getTipoAccount();
        return tipoAccount != null && tipoAccount >= TIPO_ACCOUNT_ENTERPRISE;
    }

    @GetMapping
    public ResponseEntity<?> getList(@RequestParam(required = false) String dtFrom,
                                     @RequestParam(required = false) String dtTo,
                                     @RequestParam(required = false) String soggetto,
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
            DatatablesResponseDto<OrdineDto> list = ordiniDelegate.getList(dtFrom, dtTo, soggetto, length, start, orderColumn, orderDir);
            return ResponseEntity.ok(list);
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero dell'elenco ordini", e);
            return ResponseEntity.internalServerError().body("Errore nel recupero degli ordini.");
        }
    }

    @GetMapping("/aperti")
    public ResponseEntity<?> getOrdiniAperti(@RequestParam long idFornitore)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            List<OrdineDto> list = ordiniDelegate.getOrdiniAperti(idFornitore);
            return ResponseEntity.ok(list);
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero degli ordini aperti per il fornitore {}", idFornitore, e);
            return ResponseEntity.internalServerError().body("Errore nel recupero degli ordini aperti.");
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
            OrdineDto dto = ordiniDelegate.getById(id);
            return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero dell'ordine {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nel recupero dell'ordine.");
        }
    }

    @PostMapping
    public ResponseEntity<?> insert(@RequestBody OrdineDto dto)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            Integer id = ordiniDelegate.insert(dto);
            return ResponseEntity.ok(id);
        }
        catch ( SQLException e )
        {
            log.error("Errore nel salvataggio dell'ordine", e);
            return ResponseEntity.internalServerError().body("Errore nel salvataggio dell'ordine.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id,
                                    @RequestBody OrdineDto dto)
    {
        if ( !isPianoEnterprise() )
        {
            return ResponseEntity.status(403).build();
        }
        try
        {
            dto.setId(id);
            ordiniDelegate.update(dto);
            return ResponseEntity.ok(id);
        }
        catch ( SQLException e )
        {
            log.error("Errore nell'aggiornamento dell'ordine {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nell'aggiornamento dell'ordine.");
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
            OrdineDto dto = new OrdineDto();
            dto.setId(id);
            dto.setUserLastUpdate(userId);
            ordiniDelegate.delete(dto);
            return ResponseEntity.ok().build();
        }
        catch ( SQLException e )
        {
            log.error("Errore nella cancellazione dell'ordine {}", id, e);
            return ResponseEntity.internalServerError().body("Errore nella cancellazione dell'ordine.");
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
            return ResponseEntity.ok(ordiniDelegate.isExistentNumero(numero, particella, data, id));
        }
        catch ( SQLException e )
        {
            log.error("Errore nella verifica del numero ordine", e);
            return ResponseEntity.internalServerError().body("Errore nella verifica del numero ordine.");
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
            return ResponseEntity.ok(ordiniDelegate.getNextNum(data));
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero del prossimo numero ordine", e);
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
            return ResponseEntity.ok(ordiniDelegate.getCombosMap());
        }
        catch ( SQLException e )
        {
            log.error("Errore nel recupero delle combo per gli ordini", e);
            return ResponseEntity.internalServerError().body("Errore nel recupero delle combo.");
        }
    }

}
