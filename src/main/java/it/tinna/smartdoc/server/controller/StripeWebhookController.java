package it.tinna.smartdoc.server.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
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

        if ("checkout.session.completed".equals(event.getType())) {
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);

            if (stripeObject instanceof Session) {
                Session session = (Session) stripeObject;
                processCheckoutSession(session);
            } else {
                log.warn("Payload non deserializzato come Session per event type: {}", event.getType());
            }
        }

        return ResponseEntity.ok("Received");
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

        // 2. Se non presente in metadata, cerca per Partita IVA o Email
        String partitaIva = metadata != null ? metadata.get("partitaIva") : null;
        if (partitaIva == null && metadata != null) {
            partitaIva = metadata.get("piva");
        }

        updateOrCreateAccountPlan(partitaIva, customerEmail, companyName, targetTipoAccount);
    }

    private void updateOrCreateAccountPlan(String partitaIva, String email, String companyName, Integer tipoAccount) {
        try {
            int updatedRows = 0;
            if (partitaIva != null && !partitaIva.trim().isEmpty()) {
                updatedRows = serviceJdbcTemplate.update(
                        "UPDATE d_e_enti SET tipo_account = ?, dt_attivazione = CURRENT_DATE WHERE fl_deleted = 0 AND partita_iva = ?",
                        tipoAccount, partitaIva.trim()
                );
            }

            if (updatedRows == 0 && email != null && !email.trim().isEmpty()) {
                updatedRows = serviceJdbcTemplate.update(
                        "UPDATE d_e_enti SET tipo_account = ?, dt_attivazione = CURRENT_DATE WHERE fl_deleted = 0 AND (lower(email_errori_sdi) = lower(?) OR lower(label) = lower(?))",
                        tipoAccount, email.trim(), email.trim()
                );
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
            sharedJdbcTemplate.execute((org.springframework.jdbc.core.ConnectionCallback<Object>) (Connection conn) -> {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SET app.current_tenant = '" + tenantId + "'");
                }
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO d_e_utenti (username, password, email, nome, cognome, k_d_e_gruppi, tenant_id, fl_deleted, new_version) VALUES (?, ?, ?, ?, 'Amministratore', 1, ?, 0, 1)")) {
                    ps.setString(1, email);
                    ps.setString(2, initialTokenPassword);
                    ps.setString(3, email);
                    ps.setString(4, label);
                    ps.setLong(5, tenantId);
                    ps.executeUpdate();
                }
                return null;
            });
            log.info("Creato utente admin per nuovo cliente {} (tenant_id = {}) con token di attivazione", email, tenantId);

            // 4. Invia email di benvenuto con link attivazione (Opzione B)
            sendActivationEmail(email, label, activationToken);

        } catch (Exception e) {
            log.error("Errore durante l'onboarding automatico del nuovo tenant per email: " + email, e);
        }
    }

    private void sendActivationEmail(String recipientEmail, String companyName, String activationToken) {
        try {
            String activationUrl = "https://app.smart-doc.it/completa-registrazione?token=" + activationToken;
            String subject = "Benvenuto in SmartDoc! Completa l'attivazione del tuo account";
            String body = "Gentile " + companyName + ",\n\n"
                    + "Grazie per aver acquistato SmartDoc!\n\n"
                    + "Il tuo account è stato creato con successo. Per scegliere la tua password personale ed iniziare ad utilizzare il gestionale, clicca sul link seguente:\n\n"
                    + activationUrl + "\n\n"
                    + "Se non riesci a cliccare sul link, copialo ed incollalo nel tuo browser.\n\n"
                    + "Cordiali saluti,\n"
                    + "Il Team di SmartDoc\n"
                    + "https://www.smart-doc.it";

            mailSenderServiceGeneric.send(subject, body, null, null, new String[]{recipientEmail});
            log.info("Email di attivazione inviata con successo a {}", recipientEmail);
        } catch (Exception e) {
            log.error("Errore durante l'invio dell'email di attivazione a " + recipientEmail, e);
        }
    }
}
