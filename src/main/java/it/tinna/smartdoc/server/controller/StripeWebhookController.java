package it.tinna.smartdoc.server.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.service.mail.MailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/stripe")
@Slf4j
public class StripeWebhookController {

    @Value("${stripe.webhook.secret:}")
    private String endpointSecret;

    @Autowired
    @Qualifier("serviceJdbcTemplate")
    private JdbcTemplate serviceJdbcTemplate;

    @Autowired
    @Qualifier("sharedJdbcTemplate")
    private JdbcTemplate sharedJdbcTemplate;

    @Autowired
    @Qualifier("mailSenderServiceGeneric")
    private MailSenderService mailSenderServiceGeneric;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {

        log.info("Ricevuto Webhook da Stripe");
        Event event;

        try {
            if (endpointSecret != null && !endpointSecret.trim().isEmpty() && !endpointSecret.startsWith("whsec_xxx") && sigHeader != null) {
                event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            } else {
                log.warn("Stripe webhook secret non configurato o firma assente. Parsing del payload senza verifica firma.");
                event = Event.GSON.fromJson(payload, Event.class);
            }
        } catch (SignatureVerificationException e) {
            log.error("Firma Webhook Stripe non valida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            log.error("Errore nel parsing del Webhook Stripe: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
        }

        if (event == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty event");
        }

        log.info("Stripe Event Type: {}", event.getType());

        if ("checkout.session.completed".equals(event.getType()) || "invoice.payment_succeeded".equals(event.getType())) {
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);

            if (stripeObject instanceof Session) {
                Session session = (Session) stripeObject;
                processCheckoutSession(session);
            } else {
                log.warn("Payload non deserializzato come Session per event type: {}", event.getType());
            }
        } else if ("customer.subscription.deleted".equals(event.getType()) || "invoice.payment_failed".equals(event.getType())) {
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);
            
            String email = null;
            if (stripeObject instanceof com.stripe.model.Invoice) {
                email = ((com.stripe.model.Invoice) stripeObject).getCustomerEmail();
            } else if (stripeObject instanceof com.stripe.model.Subscription) {
                email = ((com.stripe.model.Subscription) stripeObject).getCustomer();
            }

            if (email != null && !email.trim().isEmpty()) {
                expireAccountPlan(email);
            }
        }

        return ResponseEntity.ok("Received");
    }

    private void expireAccountPlan(String email) {
        try {
            int updatedRows = serviceJdbcTemplate.update(
                    "UPDATE d_e_enti SET tipo_account = 0 WHERE fl_deleted = 0 AND (lower(email_errori_sdi) = lower(?) OR lower(label) = lower(?))",
                    email.trim(), email.trim()
            );
            log.info("Impostato tipo_account = 0 (SCADUTO) per cliente {} (righe aggiornate: {}).", email, updatedRows);
        } catch (Exception e) {
            log.error("Errore durante la disattivazione del piano per email: " + email, e);
        }
    }

    private void processCheckoutSession(Session session) {
        log.info("Processo Checkout Session Stripe ID: {}", session.getId());

        String customerEmail = session.getCustomerDetails() != null ? session.getCustomerDetails().getEmail() : session.getCustomerEmail();
        String companyName = session.getCustomerDetails() != null ? session.getCustomerDetails().getName() : null;
        Map<String, String> metadata = session.getMetadata();
        
        Integer targetTipoAccount = null;

        // 1. Cerca tipoAccount da Metadata
        if (metadata != null && metadata.containsKey("tipoAccount")) {
            try {
                targetTipoAccount = Integer.parseInt(metadata.get("tipoAccount"));
            } catch (NumberFormatException e) {
                log.warn("Valore tipoAccount non numerico nei metadati: {}", metadata.get("tipoAccount"));
            }
        }

        if (targetTipoAccount == null) {
            targetTipoAccount = 3; // Default a Professional se non specificato
        }

        String partitaIva = metadata != null ? metadata.get("partitaIva") : null;
        if (partitaIva == null && metadata != null) {
            partitaIva = metadata.get("piva");
        }

        String tipoRinnovo = "ANNUAL";
        if (metadata != null && metadata.containsKey("tipoRinnovo")) {
            tipoRinnovo = metadata.get("tipoRinnovo");
        } else if (metadata != null && metadata.containsKey("interval")) {
            tipoRinnovo = "month".equalsIgnoreCase(metadata.get("interval")) ? "MONTHLY" : "ANNUAL";
        }

        updateOrCreateAccountPlan(partitaIva, customerEmail, companyName, targetTipoAccount, tipoRinnovo);
    }

    private void updateOrCreateAccountPlan(String partitaIva, String email, String companyName, Integer tipoAccount, String tipoRinnovo) {
        try {
            int updatedRows = 0;
            if (partitaIva != null && !partitaIva.trim().isEmpty()) {
                updatedRows = serviceJdbcTemplate.update(
                        "UPDATE d_e_enti SET tipo_account = ?, fl_prova = 0, dt_attivazione = CURRENT_DATE, tipo_rinnovo = ? WHERE fl_deleted = 0 AND partita_iva = ?",
                        tipoAccount, tipoRinnovo, partitaIva.trim()
                );
            }

            if (updatedRows == 0 && email != null && !email.trim().isEmpty()) {
                updatedRows = serviceJdbcTemplate.update(
                        "UPDATE d_e_enti SET tipo_account = ?, fl_prova = 0, dt_attivazione = CURRENT_DATE, tipo_rinnovo = ? WHERE fl_deleted = 0 AND (lower(email_errori_sdi) = lower(?) OR lower(label) = lower(?))",
                        tipoAccount, tipoRinnovo, email.trim(), email.trim()
                );
            }

            // 3. Se ancora non trovato, cerca l'utente in d_e_utenti (Shared DB) per recuperare il tenant_id
            if (updatedRows == 0 && email != null && !email.trim().isEmpty()) {
                try {
                    Long tenantIdFound = sharedJdbcTemplate.queryForObject(
                            "SELECT tenant_id FROM d_e_utenti WHERE fl_deleted = 0 AND (lower(email) = lower(?) OR lower(username) = lower(?)) LIMIT 1",
                            Long.class, email.trim(), email.trim()
                    );
                    if (tenantIdFound != null) {
                        updatedRows = serviceJdbcTemplate.update(
                                "UPDATE d_e_enti SET tipo_account = ?, fl_prova = 0, dt_attivazione = CURRENT_DATE, tipo_rinnovo = ? WHERE k_d_e_enti = ?",
                                tipoAccount, tipoRinnovo, tenantIdFound
                        );
                    }
                } catch (org.springframework.dao.EmptyResultDataAccessException e) {
                    // Nessun utente trovato in d_e_utenti
                }
            }

            if (updatedRows > 0) {
                log.info("Aggiornato con successo tipo_account a {} per cliente esistente (PIVA: {}, Email: {}).", tipoAccount, partitaIva, email);
            } else {
                log.info("Nessun cliente esistente trovato. Inizio onboarding automatico NUOVO CLIENTE (PIVA: {}, Email: {})...", partitaIva, email);
                provisionNewTenant(partitaIva, email, companyName, tipoAccount);
            }
        } catch (Exception e) {
            log.error("Errore nella gestione del cliente per PIVA: " + partitaIva, e);
        }
    }

    private void provisionNewTenant(String partitaIva, String email, String companyName, Integer tipoAccount) {
        try {
            String label = (companyName != null && !companyName.trim().isEmpty()) ? companyName.trim() : email;
            String pivaClean = (partitaIva != null && !partitaIva.trim().isEmpty()) ? partitaIva.trim() : "PIVA_" + System.currentTimeMillis();
            String dbName = "sd_" + pivaClean.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

            // 1. Inserisci riga in d_e_enti (Service DB)
            KeyHolder keyHolder = new GeneratedKeyHolder();
            serviceJdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO d_e_enti (label, nome_db, partita_iva, tipo_account, fl_fattura_elettronica, dt_attivazione, email_errori_sdi, fl_deleted) VALUES (?, ?, ?, ?, 1, CURRENT_DATE, ?, 0)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, label);
                ps.setString(2, dbName);
                ps.setString(3, pivaClean);
                ps.setInt(4, tipoAccount);
                ps.setString(5, email);
                return ps;
            }, keyHolder);

            Number newTenantKey = (Number) keyHolder.getKeys().get("k_d_e_enti");
            if (newTenantKey == null) {
                newTenantKey = (Number) keyHolder.getKeys().get("id");
            }
            long tenantId = newTenantKey.longValue();
            log.info("Creato nuovo tenant in d_e_enti con k_d_e_enti/tenant_id = {}", tenantId);

            // 2. Genera Token Attivazione
            String activationToken = UUID.randomUUID().toString();
            String initialTokenPassword = "TOKEN:" + activationToken;

            // 3. Inserisci utente admin in d_e_utenti (Shared DB)
            // Il tenant va impostato tramite DatabaseContextHolder (non con un "SET" manuale sulla
            // connessione): TenantAwareDataSource applica un "SET LOCAL app.current_tenant", che è
            // transaction-scoped e si annulla da solo al commit. Un "SET" senza LOCAL è invece
            // session-scoped e, con il connection pooling, potrebbe restare attivo sulla connessione
            // fisica anche dopo che è tornata al pool, "trapelando" su una richiesta successiva
            // non correlata che non imposta esplicitamente un tenant.
            DatabaseContextHolder.setClientDatabase(dbName);
            try {
                sharedJdbcTemplate.execute((org.springframework.jdbc.core.ConnectionCallback<Object>) (Connection conn) -> {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO d_e_utenti (username, password, email, nome, cognome, k_d_e_gruppi, tenant_id, fl_deleted) VALUES (?, ?, ?, ?, 'Amministratore', 1, ?, 0)")) {
                        ps.setString(1, email);
                        ps.setString(2, initialTokenPassword);
                        ps.setString(3, email);
                        ps.setString(4, label);
                        ps.setLong(5, tenantId);
                        ps.executeUpdate();
                    }
                    return null;
                });
            } finally {
                DatabaseContextHolder.clearClientDatabase();
            }
            log.info("Creato utente admin per nuovo cliente {} (tenant_id = {}) con token di attivazione", email, tenantId);

            // 4. Invia email di benvenuto con link attivazione (Opzione B)
            sendActivationEmail(email, label, activationToken, dbName);

        } catch (Exception e) {
            log.error("Errore durante l'onboarding automatico del nuovo tenant per email: " + email, e);
        }
    }

    private void sendActivationEmail(String recipientEmail, String companyName, String activationToken, String dbName) {
        try {
            String activationUrl = "https://app.smart-doc.it/completa-registrazione?token=" + activationToken
                    + "&db=" + java.net.URLEncoder.encode(dbName, java.nio.charset.StandardCharsets.UTF_8);
            String subject = "Benvenuto in SmartDoc! Completa l'attivazione del tuo account";
            String bodyHtml = buildActivationHtmlEmail(companyName, activationUrl);

            mailSenderServiceGeneric.send(subject, bodyHtml, null, null, new String[]{recipientEmail});
            log.info("Email di attivazione inviata con successo a {}", recipientEmail);
        } catch (Exception e) {
            log.error("Errore durante l'invio dell'email di attivazione a " + recipientEmail, e);
        }
    }

    private String buildActivationHtmlEmail(String companyName, String activationUrl) {
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
             + "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">"
             + "                  <tr>"
             + "                    <td>"
             + "                      <span style=\"font-size:26px; font-weight:900; color:#ffffff; letter-spacing:-0.5px;\">Smart<span style=\"color:#3b82f6;\">Doc</span></span>"
             + "                    </td>"
             + "                    <td align=\"right\">"
             + "                      <span style=\"background-color:#f59e0b; color:#0f172a; font-size:11px; font-weight:800; text-transform:uppercase; padding:6px 14px; border-radius:20px; letter-spacing:0.5px;\">🎁 3 MESI GRATIS</span>"
             + "                    </td>"
             + "                  </tr>"
             + "                </table>"
             + "              </td>"
             + "            </tr>"
             + "            <tr>"
             + "              <td style=\"padding:36px; text-align:left;\">"
             + "                <h2 style=\"margin:0 0 12px 0; font-size:22px; font-weight:800; color:#0f172a;\">Benvenuto in SmartDoc! 🚀</h2>"
             + "                <p style=\"margin:0 0 20px 0; font-size:15px; line-height:1.6; color:#475569;\">"
             + "                  Gentile <strong>" + companyName + "</strong>,<br><br>"
             + "                  Grazie per aver scelto SmartDoc! Il tuo account è stato attivato con successo."
             + "                </p>"
             + "                <p style=\"margin:0 0 28px 0; font-size:15px; line-height:1.6; color:#475569;\">"
             + "                  Per accedere al tuo gestionale ed impostare la tua password personale, clicca sul pulsante qui sotto:"
             + "                </p>"
             + "                <div style=\"text-align:center; margin:32px 0;\">"
             + "                  <a href=\"" + activationUrl + "\" target=\"_blank\" style=\"display:inline-block; background:linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%); color:#ffffff; font-size:16px; font-weight:800; text-decoration:none; padding:16px 36px; border-radius:12px; box-shadow:0 6px 20px rgba(37,99,235,0.35);\">"
             + "                    Attiva Account e Scegli Password →"
             + "                  </a>"
             + "                </div>"
             + "                <p style=\"margin:20px 0 0 0; font-size:12px; color:#94a3b8; line-height:1.5;\">"
             + "                  Se il pulsante non funziona, copia ed incolla questo link nel tuo browser:<br>"
             + "                  <a href=\"" + activationUrl + "\" style=\"color:#2563eb; text-decoration:underline; word-break:break-all;\">" + activationUrl + "</a>"
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
