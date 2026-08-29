package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.login.LoginDelegate;
import it.tinna.smartdoc.server.payload.JwtResponse;
import it.tinna.smartdoc.server.payload.LoginRequest;
import it.tinna.smartdoc.server.security.JwtService;
import it.tinna.smartdoc.shared.dto.login.UtenteDto;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private LoginDelegate loginDelegate;
    
    @Autowired
    private it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate configurazioneDelegate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("serviceJdbcTemplate")
    private org.springframework.jdbc.core.JdbcTemplate serviceJdbcTemplate;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Set the context to the selected municipality's database
            DatabaseContextHolder.setClientDatabase(loginRequest.getEnte());
            
            UtenteDto user = loginDelegate.getUserByUsername(loginRequest.getUsername());

            if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body(java.util.Collections.singletonMap("erroreUtenteNonTrovato", true));
            }

            // Generate Token
            java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
            extraClaims.put("dbName", loginRequest.getEnte());

            // Fetch label and tipo_account from d_e_enti in service DB
            try {
                java.util.Map<String, Object> entiMap = serviceJdbcTemplate.queryForMap(
                    "SELECT label, tipo_account FROM d_e_enti WHERE nome_db = ? AND fl_deleted = 0 LIMIT 1", loginRequest.getEnte());
                String enteLabel = (String) entiMap.get("label");
                Number tipoAccountNum = (Number) entiMap.get("tipo_account");
                int tipoAccount = tipoAccountNum != null ? tipoAccountNum.intValue() : 1;

                extraClaims.put("enteLabel", org.apache.commons.lang3.StringUtils.defaultIfEmpty(enteLabel, loginRequest.getEnte()));
                extraClaims.put("tipoAccount", tipoAccount);
                extraClaims.put("tipo_account", tipoAccount);
            } catch (Exception e) {
                extraClaims.put("enteLabel", loginRequest.getEnte());
                extraClaims.put("tipoAccount", 1);
                extraClaims.put("tipo_account", 1);
            }
            
            // Fetch Global Config
            try {
                // Populate session-like config into JWT claims
                // Note: Keys should match what frontend expects or what legacy stored in session
                String abilitaDivisioni = configurazioneDelegate.getByKey(it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_DOMAIN_GLOBAL, it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_KEY_ABILITADIVISIONI);
                extraClaims.put(it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_KEY_ABILITADIVISIONI, org.apache.commons.lang3.StringUtils.defaultIfEmpty(abilitaDivisioni, "0"));

                String abilitaProgetti = configurazioneDelegate.getByKey(it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_DOMAIN_GLOBAL, it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_KEY_ABILITAPROGETTI);
                extraClaims.put(it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_KEY_ABILITAPROGETTI, org.apache.commons.lang3.StringUtils.defaultIfEmpty(abilitaProgetti, "0"));

                // Fetch Articoli Config for convenience (optional, user asked for "config parameter")
                // Better to fetch ARTICOLI domain entirely? User said: "if configuration parameter... interface receives info immediately"
                // Putting Articoli config in JWT might bloat it, but "read at login and stored in token" was the request.
                // Let's add ARTICOLI domain map as well? Or just the critical global ones?
                // The prompt example was "Ceramica" which is TIPOSTORE (Global).
                // So Global configs are priority.
                
                // Let's also add 'ARTICOLI' specific configs commonly used in UI logic
                java.util.Map<String, it.tinna.smartdoc.shared.dto.configurazione.ConfigurazioneDto> articoliConfig = configurazioneDelegate.getByDomain(it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIGURAZIONE_DOMINIO_ARTICOLI);
                 for (java.util.Map.Entry<String, it.tinna.smartdoc.shared.dto.configurazione.ConfigurazioneDto> entry : articoliConfig.entrySet()) {
                    extraClaims.put("ARTICOLI_" + entry.getKey(), entry.getValue().getValore());
                }

            } catch (Exception e) {
                // Log error but proceed with login?
                e.printStackTrace();
            }

            String jwt = jwtService.generateToken(extraClaims, user);

            return ResponseEntity.ok(new JwtResponse(jwt, user));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("Database error: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody it.tinna.smartdoc.server.payload.ChangePasswordRequest request) {
        try {
            // Context should be already set by the filter, but if it came with ente in request, set it to be sure
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(request.getEnte())) {
                DatabaseContextHolder.setClientDatabase(request.getEnte());
            }

            // Get current user from security context or token?
            // AuthController is public, but change-password should be protected
            // Assuming current password check is enough to verify intent
            
            // For now, let's assume we need the user to be identified.
            // Since we don't have an easy way to get the authenticated user ID here without looking at the token,
            // we can ask the frontend to send the username or ID, or better, look at the SecurityContext.
            
            Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username;
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            UtenteDto user = loginDelegate.getUserByUsername(username);
            if (user == null || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body("Password attuale errata");
            }

            loginDelegate.updatePassword(user.getId(), request.getNewPassword());
            return ResponseEntity.ok("Password aggiornata con successo");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Errore durante l'aggiornamento della password: " + e.getMessage());
        }
    }

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("sharedJdbcTemplate")
    private org.springframework.jdbc.core.JdbcTemplate sharedJdbcTemplate;

    @PostMapping("/completa-registrazione")
    public ResponseEntity<?> completaRegistrazione(@RequestBody java.util.Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");
        String db = request.get("db");

        if (org.apache.commons.lang3.StringUtils.isBlank(token) || org.apache.commons.lang3.StringUtils.isBlank(newPassword)) {
            return ResponseEntity.badRequest().body("Token e nuova password sono obbligatori");
        }

        // La ricerca dell'utente per token avviene PRIMA di sapere a quale utente (e quindi tenant)
        // appartenga: sul DB condiviso questa tabella è protetta da Row Level Security basata su
        // app.current_tenant, che normalmente viene impostato dal JwtAuthenticationFilter leggendo
        // il token JWT. Qui non c'è ancora nessun JWT, quindi va impostato manualmente a partire
        // dal parametro "db" incluso nel link di attivazione, altrimenti la SELECT non troverebbe
        // mai alcuna riga (nessun tenant = RLS blocca tutto) e il token risulterebbe sempre "non valido".
        if (org.apache.commons.lang3.StringUtils.isNotBlank(db)) {
            DatabaseContextHolder.setClientDatabase(db.trim());
        }

        try {
            String tokenPattern = "TOKEN:" + token.trim();
            java.util.List<java.util.Map<String, Object>> users = sharedJdbcTemplate.execute((org.springframework.jdbc.core.ConnectionCallback<java.util.List<java.util.Map<String, Object>>>) (java.sql.Connection conn) -> {
                String sql = "SELECT k_d_e_utenti, username, tenant_id FROM d_e_utenti WHERE password = ? AND fl_deleted = 0";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, tokenPattern);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
                        if (rs.next()) {
                            java.util.Map<String, Object> map = new java.util.HashMap<>();
                            map.put("k_d_e_utenti", rs.getLong("k_d_e_utenti"));
                            map.put("username", rs.getString("username"));
                            map.put("tenant_id", rs.getObject("tenant_id"));
                            list.add(map);
                        }
                        return list;
                    }
                }
            });

            if (users == null || users.isEmpty()) {
                return ResponseEntity.badRequest().body("Token di attivazione non valido o già utilizzato.");
            }

            java.util.Map<String, Object> userRow = users.get(0);
            Number userId = (Number) userRow.get("k_d_e_utenti");
            String encodedPassword = passwordEncoder.encode(newPassword.trim());

            // Nessun "SET" manuale qui: il tenant è già impostato via DatabaseContextHolder in cima
            // al metodo (verificato implicitamente dal fatto che la SELECT sopra ha trovato la riga
            // sotto RLS), quindi TenantAwareDataSource applica automaticamente il proprio
            // "SET LOCAL app.current_tenant" anche su questa nuova connessione/transazione.
            sharedJdbcTemplate.execute((org.springframework.jdbc.core.ConnectionCallback<Object>) (java.sql.Connection conn) -> {
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                        "UPDATE d_e_utenti SET password = ? WHERE k_d_e_utenti = ?")) {
                    ps.setString(1, encodedPassword);
                    ps.setLong(2, userId.longValue());
                    ps.executeUpdate();
                }
                return null;
            });

            return ResponseEntity.ok(java.util.Collections.singletonMap("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Errore durante il completamento della registrazione: " + e.getMessage());
        } finally {
            DatabaseContextHolder.clearClientDatabase();
        }
    }

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("mailSenderServiceGeneric")
    private it.tinna.smartdoc.service.mail.MailSenderService mailSenderServiceGeneric;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("tenantAwareDataSource")
    private it.tinna.smartdoc.server.database.TenantAwareDataSource tenantAwareDataSource;

    @PostMapping("/registrazione-prova")
    public ResponseEntity<?> registrazioneProva(@RequestBody java.util.Map<String, Object> request) {
        String ragioneSociale = (String) request.get("ragioneSociale");
        String partitaIva = (String) request.get("partitaIva");
        String email = (String) request.get("email");
        Object planObj = request.get("tipoAccount");
        Integer tipoAccount = planObj != null ? Integer.parseInt(planObj.toString()) : 3;

        if (org.apache.commons.lang3.StringUtils.isBlank(ragioneSociale) || 
            org.apache.commons.lang3.StringUtils.isBlank(partitaIva) || 
            org.apache.commons.lang3.StringUtils.isBlank(email)) {
            return ResponseEntity.badRequest().body("Ragione Sociale, Partita IVA ed Email sono obbligatorie");
        }

        String pivaClean = partitaIva.trim().replaceAll("[^a-zA-Z0-9]", "");
        String emailClean = email.trim();

        try {
            Integer countPartitaIva = serviceJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM d_e_enti WHERE fl_deleted = 0 AND partita_iva = ?",
                    Integer.class, pivaClean
            );
            Integer countEmail = serviceJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM d_e_enti WHERE fl_deleted = 0 AND lower(email_errori_sdi) = lower(?)",
                    Integer.class, emailClean
            );

            if (countPartitaIva != null && countPartitaIva > 0 && countEmail != null && countEmail > 0) {
                return ResponseEntity.badRequest().body("Questa Partita IVA e questa Email risultano già registrate nel sistema.");
            } else if (countPartitaIva != null && countPartitaIva > 0) {
                return ResponseEntity.badRequest().body("Questa Partita IVA risulta già registrata nel sistema.");
            } else if (countEmail != null && countEmail > 0) {
                return ResponseEntity.badRequest().body("Questa Email risulta già associata a un altro account.");
            }

            String dbName = "sd_" + pivaClean.toLowerCase();
            String label = ragioneSociale.trim();

            int flProva = (tipoAccount == 5) ? 0 : 1;

            org.springframework.jdbc.support.KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
            serviceJdbcTemplate.update(connection -> {
                java.sql.PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO d_e_enti (label, nome_db, partita_iva, tipo_account, fl_prova, tipo_rinnovo, fl_fattura_elettronica, dt_attivazione, email_errori_sdi, fl_deleted, new_version) VALUES (?, ?, ?, ?, ?, 'ANNUAL', 1, CURRENT_DATE, ?, 0, 1)",
                        java.sql.Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, label);
                ps.setString(2, dbName);
                ps.setString(3, pivaClean);
                ps.setInt(4, tipoAccount);
                ps.setInt(5, flProva);
                ps.setString(6, emailClean);
                return ps;
            }, keyHolder);

            Number newTenantKey = (Number) keyHolder.getKeys().get("k_d_e_enti");
            if (newTenantKey == null) {
                newTenantKey = (Number) keyHolder.getKeys().get("id");
            }
            long tenantId = newTenantKey.longValue();

            // Aggiorna subito la cache dbKey->tenantId di TenantAwareDataSource: quella cache non
            // scade mai, quindi se questo dbName era gia' stato risolto in passato (es. un tentativo
            // precedente fallito con la stessa Partita IVA, poi ripulito) l'ID vecchio resterebbe in
            // cache per sempre, causando un mismatch con app.current_tenant e una violazione RLS
            // sull'insert successivo in d_e_utenti.
            tenantAwareDataSource.primeTenantId(dbName, tenantId);

            String activationToken = java.util.UUID.randomUUID().toString();
            String initialTokenPassword = "TOKEN:" + activationToken;

            // Tenant impostato via DatabaseContextHolder (SET LOCAL automatico di TenantAwareDataSource,
            // transaction-scoped) invece di un "SET" manuale non-LOCAL, che con il connection pooling
            // potrebbe restare attivo sulla connessione fisica oltre questa richiesta.
            DatabaseContextHolder.setClientDatabase(dbName);
            try {
                sharedJdbcTemplate.execute((org.springframework.jdbc.core.ConnectionCallback<Object>) (java.sql.Connection conn) -> {
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO d_e_utenti (username, password, email, nome, cognome, k_d_e_gruppi, tenant_id, fl_deleted) VALUES (?, ?, ?, ?, 'Amministratore', 1, ?, 0)")) {
                        ps.setString(1, emailClean);
                        ps.setString(2, initialTokenPassword);
                        ps.setString(3, emailClean);
                        ps.setString(4, label);
                        ps.setLong(5, tenantId);
                        ps.executeUpdate();
                    }
                    return null;
                });
            } finally {
                DatabaseContextHolder.clearClientDatabase();
            }
            String activationUrl = "https://app.smart-doc.it/completa-registrazione?token=" + activationToken
                    + "&db=" + java.net.URLEncoder.encode(dbName, java.nio.charset.StandardCharsets.UTF_8);
            boolean isStudio = (tipoAccount == 5);
            String subject = isStudio ? "Attiva il tuo Account Studio Contabile Partner su SmartDoc!" : "Attiva i tuoi 3 Mesi Gratis su SmartDoc!";
            String bodyHtml = buildActivationHtmlEmail(label, activationUrl, isStudio);

            mailSenderServiceGeneric.send(subject, bodyHtml, null, null, new String[]{emailClean});

            return ResponseEntity.ok(java.util.Collections.singletonMap("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Errore durante la registrazione della prova gratuita: " + e.getMessage());
        }
    }
    private String buildActivationHtmlEmail(String companyName, String activationUrl, boolean isStudio) {
        String emailSubject = isStudio ? "Attiva il tuo Account Studio Contabile Partner su SmartDoc!" : "Attiva i tuoi 3 Mesi Gratis su SmartDoc!";
        String badgeText = isStudio ? "💼 STUDIO PARTNER GRATIS" : "🎁 3 MESI GRATIS";
        String titleText = isStudio ? "Benvenuto nel Programma Studio Partner! 💼" : "Benvenuto in SmartDoc! 🚀";
        String mainText = isStudio
            ? "Gentile <strong>" + companyName + "</strong>,<br><br>Grazie per esserti registrato come <strong>Studio Contabile Partner</strong> su SmartDoc! Il tuo account multi-azienda <strong>gratuito per sempre</strong> è stato creato con successo."
            : "Gentile <strong>" + companyName + "</strong>,<br><br>Grazie per aver scelto SmartDoc! I tuoi <strong>90 giorni di prova gratuita</strong> a costo zero sono stati attivati con successo.";

        String featureTitle = isStudio ? "Cosa include il tuo Account Studio Partner:" : "Cosa include la tua prova gratuita:";
        String featuresList = isStudio
            ? "<li>Dashboard Multi-Azienda centralizzata</li>" +
              "<li>Download Batch XML e CSV Prima Nota in 1-Click</li>" +
              "<li>Accredito ed utilizzo 100% Gratuito per lo Studio</li>"
            : "<li>Fatturazione Elettronica SDI illimitata</li>" +
              "<li>Utenti illimitati (accesso gratuito per il tuo commercialista)</li>" +
              "<li>Zero costi iniziali e nessuna carta richiesta</li>";

        String buttonText = isStudio ? "Attiva Account Studio e Scegli Password →" : "Attiva Account e Scegli Password →";

        return "<!DOCTYPE html>"
             + "<html>"
             + "<head>"
             + "  <meta charset=\"UTF-8\">"
             + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
             + "  <title>" + emailSubject + "</title>"
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
             + "                      <span style=\"background-color:" + (isStudio ? "#10b981" : "#f59e0b") + "; color:" + (isStudio ? "#ffffff" : "#0f172a") + "; font-size:11px; font-weight:800; text-transform:uppercase; padding:6px 14px; border-radius:20px; letter-spacing:0.5px;\">" + badgeText + "</span>"
             + "                    </td>"
             + "                  </tr>"
             + "                </table>"
             + "              </td>"
             + "            </tr>"
             + "            <tr>"
             + "              <td style=\"padding:36px; text-align:left;\">"
             + "                <h2 style=\"margin:0 0 12px 0; font-size:22px; font-weight:800; color:#0f172a;\">" + titleText + "</h2>"
             + "                <p style=\"margin:0 0 20px 0; font-size:15px; line-height:1.6; color:#475569;\">"
             + "                  " + mainText
             + "                </p>"
             + "                <p style=\"margin:0 0 28px 0; font-size:15px; line-height:1.6; color:#475569;\">"
             + "                  Per accedere al tuo portale studio ed impostare la tua password personale, clicca sul pulsante qui sotto:"
             + "                </p>"
             + "                <div style=\"text-align:center; margin:32px 0;\">"
             + "                  <a href=\"" + activationUrl + "\" target=\"_blank\" style=\"display:inline-block; background:" + (isStudio ? "linear-gradient(135deg, #059669 0%, #047857 100%)" : "linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)") + "; color:#ffffff; font-size:16px; font-weight:800; text-decoration:none; padding:16px 36px; border-radius:12px; box-shadow:0 6px 20px rgba(5,150,105,0.35);\">"
             + "                    " + buttonText
             + "                  </a>"
             + "                </div>"
             + "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"background-color:#f8fafc; border:1px solid #e2e8f0; border-radius:12px; margin:28px 0 20px 0;\">"
             + "                  <tr>"
             + "                    <td style=\"padding:20px 24px;\">"
             + "                      <div style=\"font-size:12px; font-weight:800; text-transform:uppercase; color:" + (isStudio ? "#059669" : "#2563eb") + "; letter-spacing:0.5px; margin-bottom:8px;\">" + featureTitle + "</div>"
             + "                      <ul style=\"margin:0; padding-left:18px; font-size:13px; color:#334155; line-height:1.7;\">"
             + "                        " + featuresList
             + "                      </ul>"
             + "                    </td>"
             + "                  </tr>"
             + "                </table>"
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

