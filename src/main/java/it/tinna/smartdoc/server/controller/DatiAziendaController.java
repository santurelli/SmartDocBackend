package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.tinna.smartdoc.server.delegate.datiazienda.DatiAziendaDelegate;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;

@RestController
@RequestMapping("/api/dati-azienda")
public class DatiAziendaController {

    private static final Logger _log = LoggerFactory.getLogger(DatiAziendaController.class);

    @Autowired
    private DatiAziendaDelegate datiAziendaDelegate;

    @GetMapping
    public ResponseEntity<Map<String, Object>> get() {
        try {
            return ResponseEntity.ok(datiAziendaDelegate.get());
        } catch (SQLException e) {
            _log.error("Error fetching company data", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Void> save(
            @RequestParam(required = false) Boolean deleteLogo,
            @RequestParam String denominazione,
            @RequestParam Integer idRegimeFiscale,
            @RequestParam String codiceFiscale,
            @RequestParam String partitaIva,
            @RequestParam String indirizzo,
            @RequestParam String citta,
            @RequestParam String cap,
            @RequestParam String provincia,
            @RequestParam String telefono,
            @RequestParam String fax,
            @RequestParam String pec,
            @RequestParam String email,
            @RequestParam String sitoWeb,
            @RequestParam(required = false) MultipartFile file) {

        try {
            DatiAziendaDto dto = new DatiAziendaDto();
            dto.setDenominazione(denominazione);
            dto.setIdRegimeFiscale(idRegimeFiscale);
            dto.setPartitaIva(partitaIva);
            dto.setCodiceFiscale(codiceFiscale);
            dto.setIndirizzo(indirizzo);
            dto.setCitta(citta);
            dto.setCap(cap);
            dto.setProvincia(provincia);
            dto.setTelefono(telefono);
            dto.setFax(fax);
            dto.setPec(pec);
            dto.setEmail(email);
            dto.setSitoWeb(sitoWeb);
            dto.setDeleteLogo(deleteLogo);

            datiAziendaDelegate.save(dto, file);
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            _log.error("Error saving company data", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

