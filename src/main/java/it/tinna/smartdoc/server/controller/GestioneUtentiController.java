package it.tinna.smartdoc.server.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.datiazienda.DatiAziendaDelegate;
import it.tinna.smartdoc.server.delegate.utenti.UtentiDelegate;
import it.tinna.smartdoc.service.mail.MailSenderService;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
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

    @Autowired
    private DatiAziendaDelegate datiAziendaDelegate;

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
                String companyName = null;
                try {
                    DatiAziendaDto datiAzienda = datiAziendaDelegate.getDatiAzienda();
                    if (datiAzienda != null) {
                        companyName = datiAzienda.getDenominazione();
                    }
                } catch (Exception ex) {
                    log.warn("Impossibile recuperare la denominazione azienda per l'email di benvenuto", ex);
                }
                String dbKey = DatabaseContextHolder.getClientDatabase();
                String loginUrl = buildLoginUrl(dbKey, companyName);

                String subject = "Benvenuto in SmartDoc - Credenziali di accesso";
                String body = buildWelcomeEmail(dto.getNome(), dto.getUsername(), randomPassword, companyName, loginUrl);
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

    private String buildLoginUrl(String dbKey, String companyName) {
        String baseUrl = "https://app.smart-doc.it/login";
        if (dbKey == null || dbKey.isEmpty() || companyName == null || companyName.isEmpty()) {
            return baseUrl;
        }
        try {
            String entedb = URLEncoder.encode(dbKey, StandardCharsets.UTF_8.toString());
            String entelabel = URLEncoder.encode(companyName, StandardCharsets.UTF_8.toString());
            return baseUrl + "?entedb=" + entedb + "&entelabel=" + entelabel;
        } catch (UnsupportedEncodingException e) {
            return baseUrl;
        }
    }

    private String buildWelcomeEmail(String nome, String username, String randomPassword, String companyName, String loginUrl) {
        String companyLine = (companyName != null && !companyName.isEmpty())
                ? "Sei stato invitato ad accedere a SmartDoc per l'azienda <strong>" + companyName + "</strong>."
                : "Sei stato invitato ad accedere a SmartDoc.";
        return "<!DOCTYPE html>"
             + "<html>"
             + "<head>"
             + "  <meta charset=\"UTF-8\">"
             + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
             + "  <title>Benvenuto in SmartDoc!</title>"
             + "</head>"
             + "<body style=\"margin:0; padding:0; background-color:#f1f5f9; font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; -webkit-font-smoothing:antialiased;\">"
             + "  <center>"
             + "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"background-color:#f1f5f9; padding:40px 16px;\">"
             + "      <tr>"
             + "        <td align=\"center\">"
             + "          <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"max-width:600px; background-color:#ffffff; border-radius:16px; overflow:hidden; box-shadow:0 10px 30px rgba(15,23,42,0.08); border:1px solid #e2e8f0;\">"
             + "            <tr>"
             + "              <td style=\"background-color:#0f172a; padding:32px 36px; text-align:left;\">"
             + "                <span style=\"font-size:26px; font-weight:900; color:#ffffff; letter-spacing:-0.5px;\">Smart<span style=\"color:#3b82f6;\">Doc</span></span>"
             + "              </td>"
             + "            </tr>"
             + "            <tr>"
             + "              <td style=\"padding:36px; text-align:left;\">"
             + "                <h2 style=\"margin:0 0 12px 0; font-size:22px; font-weight:800; color:#0f172a;\">Benvenuto in SmartDoc! 👋</h2>"
             + "                <p style=\"margin:0 0 20px 0; font-size:15px; line-height:1.6; color:#475569;\">"
             + "                  Ciao <strong>" + nome + "</strong>,<br><br>"
             + "                  " + companyLine
             + "                </p>"
             + "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"background-color:#f8fafc; border:1px solid #e2e8f0; border-radius:12px; margin:0 0 28px 0;\">"
             + "                  <tr>"
             + "                    <td style=\"padding:20px 24px;\">"
             + "                      <p style=\"margin:0 0 10px 0; font-size:14px; color:#475569;\">Username: <strong style=\"color:#0f172a;\">" + username + "</strong></p>"
             + "                      <p style=\"margin:0; font-size:14px; color:#475569;\">Password temporanea: <strong style=\"color:#0f172a;\">" + randomPassword + "</strong></p>"
             + "                    </td>"
             + "                  </tr>"
             + "                </table>"
             + "                <p style=\"margin:0 0 20px 0; font-size:13px; line-height:1.6; color:#94a3b8;\">"
             + "                  Ti consigliamo di cambiare la password al primo accesso."
             + "                </p>"
             + "                <div style=\"text-align:center; margin:32px 0;\">"
             + "                  <a href=\"" + loginUrl + "\" target=\"_blank\" style=\"display:inline-block; background:linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%); color:#ffffff; font-size:16px; font-weight:800; text-decoration:none; padding:16px 36px; border-radius:12px; box-shadow:0 6px 20px rgba(37,99,235,0.35);\">"
             + "                    Accedi a SmartDoc →"
             + "                  </a>"
             + "                </div>"
             + "                <p style=\"margin:20px 0 0 0; font-size:12px; color:#94a3b8; line-height:1.5;\">"
             + "                  Se il pulsante non funziona, copia ed incolla questo link nel tuo browser:<br>"
             + "                  <a href=\"" + loginUrl + "\" style=\"color:#2563eb; text-decoration:underline; word-break:break-all;\">" + loginUrl + "</a>"
             + "                </p>"
             + "              </td>"
             + "            </tr>"
             + "            <tr>"
             + "              <td style=\"background-color:#f8fafc; padding:24px 36px; border-top:1px solid #e2e8f0; text-align:center; font-size:12px; color:#64748b;\">"
             + "                SmartDoc &middot; Gestionale di Fatturazione Elettronica Cloud<br>"
             + "                <a href=\"https://www.smart-doc.it\" style=\"color:#2563eb; text-decoration:none; font-weight:600;\">www.smart-doc.it</a> &middot; Supporto: <a href=\"mailto:info@smart-doc.it\" style=\"color:#2563eb; text-decoration:none;\">info@smart-doc.it</a>"
             + "              </td>"
             + "            </tr>"
             + "          </table>"
             + "        </td>"
             + "      </tr>"
             + "    </table>"
             + "  </center>"
             + "</body>"
             + "</html>";
    }
}
