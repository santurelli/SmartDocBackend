package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.utenti.UtentiDelegate;
import it.tinna.smartdoc.service.mail.MailSenderService;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;
import it.tinna.smartdoc.shared.dto.login.GruppoDto;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/utenti")
@Slf4j
public class GestioneUtentiController {

    @Autowired
    private UtentiDelegate utentiDelegate;

    @Autowired
    @Qualifier("mailSenderServiceFastOrder")
    private MailSenderService mailSenderService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UtenteDto>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String limit) {
        try {
            return ResponseEntity.ok(utentiDelegate.getUtenti(search, sort, limit));
        } catch (SQLException e) {
            log.error("Errore recupero utenti", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> insert(@RequestBody UtenteDto dto) {
        try {
            // Generate random password
            String randomPassword = UUID.randomUUID().toString().substring(0, 8);
            dto.setPassword(randomPassword);

            long id = utentiDelegate.insertUtente(dto);
            dto.setId(id);

            // Send Email
            if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
                String subject = "Benvenuto in SmartDoc - Credenziali di accesso";
                String body = "<p>Ciao " + dto.getNome() + ",</p>"
                        + "<p>Il tuo account è stato creato con successo. Ecco le tue credenziali:</p>"
                        + "<ul><li>Username: <b>" + dto.getUsername() + "</b></li>"
                        + "<li>Password temporanea: <b>" + randomPassword + "</b></li></ul>"
                        + "<p>Ti consigliamo di cambiare la password al primo accesso.</p>";
                try {
                    mailSenderService.send(subject, body, null, new String[]{dto.getEmail()});
                } catch (Exception ex) {
                    log.error("Errore invio mail a {}. Eseguo rollback dell'utente.", dto.getEmail(), ex);
                    utentiDelegate.deleteUtente(id);
                    return ResponseEntity.badRequest().body("Impossibile inviare l'email. L'utente non è stato creato.");
                }
            }

            return ResponseEntity.ok(dto);
        } catch (SQLException e) {
            log.error("Errore inserimento utente", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody UtenteDto dto) {
        try {
            dto.setId(id);
            utentiDelegate.updateUtente(dto);
            return ResponseEntity.ok(dto);
        } catch (SQLException e) {
            log.error("Errore aggiornamento utente", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable long id) {
        try {
            utentiDelegate.deleteUtente(id);
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            log.error("Errore cancellazione utente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/gruppi")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<GruppoDto>> getGruppi() {
        try {
            return ResponseEntity.ok(utentiDelegate.getGruppi());
        } catch (SQLException e) {
            log.error("Errore recupero gruppi", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
