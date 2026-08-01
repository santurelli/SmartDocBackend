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

            // Fetch label from d_e_enti in service DB
            try {
                String enteLabel = serviceJdbcTemplate.queryForObject(
                    "SELECT label FROM d_e_enti WHERE nome_db = ?", String.class, loginRequest.getEnte());
                extraClaims.put("enteLabel", org.apache.commons.lang3.StringUtils.defaultIfEmpty(enteLabel, loginRequest.getEnte()));
            } catch (Exception e) {
                extraClaims.put("enteLabel", loginRequest.getEnte());
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

        if (org.apache.commons.lang3.StringUtils.isBlank(token) || org.apache.commons.lang3.StringUtils.isBlank(newPassword)) {
            return ResponseEntity.badRequest().body("Token e nuova password sono obbligatori");
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
            Object tenantIdObj = userRow.get("tenant_id");
            String encodedPassword = passwordEncoder.encode(newPassword.trim());

            sharedJdbcTemplate.execute((org.springframework.jdbc.core.ConnectionCallback<Object>) (java.sql.Connection conn) -> {
                if (tenantIdObj != null) {
                    try (java.sql.Statement stmt = conn.createStatement()) {
                        stmt.execute("SET app.current_tenant = '" + tenantIdObj + "'");
                    }
                }
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
        }
    }

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("mailSenderServiceGeneric")
    private it.tinna.smartdoc.service.mail.MailSenderService mailSenderServiceGeneric;

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
            Integer countExisting = serviceJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM d_e_enti WHERE fl_deleted = 0 AND (partita_iva = ? OR lower(email_errori_sdi) = lower(?))",
                    Integer.class, pivaClean, emailClean
            );

            if (countExisting != null && countExisting > 0) {
                return ResponseEntity.badRequest().body("Questa Partita IVA o Email risulta già registrata nel sistema.");
            }

            String dbName = "sd_" + pivaClean.toLowerCase();
            String label = ragioneSociale.trim();

            org.springframework.jdbc.support.KeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
            serviceJdbcTemplate.update(connection -> {
                java.sql.PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO d_e_enti (label, nome_db, partita_iva, tipo_account, fl_fattura_elettronica, dt_attivazione, email_errori_sdi, fl_deleted) VALUES (?, ?, ?, ?, 1, CURRENT_DATE, ?, 0)",
                        java.sql.Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, label);
                ps.setString(2, dbName);
                ps.setString(3, pivaClean);
                ps.setInt(4, tipoAccount);
                ps.setString(5, emailClean);
                return ps;
            }, keyHolder);

            Number newTenantKey = (Number) keyHolder.getKeys().get("k_d_e_enti");
            if (newTenantKey == null) {
                newTenantKey = (Number) keyHolder.getKeys().get("id");
            }
            long tenantId = newTenantKey.longValue();

            String activationToken = java.util.UUID.randomUUID().toString();
            String initialTokenPassword = "TOKEN:" + activationToken;

            sharedJdbcTemplate.execute((org.springframework.jdbc.core.ConnectionCallback<Object>) (java.sql.Connection conn) -> {
                try (java.sql.Statement stmt = conn.createStatement()) {
                    stmt.execute("SET app.current_tenant = '" + tenantId + "'");
                }
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO d_e_utenti (username, password, email, nome, cognome, k_d_e_gruppi, tenant_id, fl_deleted, new_version) VALUES (?, ?, ?, ?, 'Amministratore', 1, ?, 0, 1)")) {
                    ps.setString(1, emailClean);
                    ps.setString(2, initialTokenPassword);
                    ps.setString(3, emailClean);
                    ps.setString(4, label);
                    ps.setLong(5, tenantId);
                    ps.executeUpdate();
                }
                return null;
            });

            String activationUrl = "https://app.smart-doc.it/completa-registrazione?token=" + activationToken;
            String subject = "Attiva i tuoi 3 Mesi Gratis su SmartDoc!";
            String body = "Gentile " + label + ",\n\n"
                    + "Grazie per aver scelto la Prova Gratuita di 3 Mesi di SmartDoc!\n\n"
                    + "I tuoi 90 giorni di prova sono stati attivati a costo zero. Per completare la registrazione e scegliere la tua password di accesso, clicca sul link seguente:\n\n"
                    + activationUrl + "\n\n"
                    + "Cordiali saluti,\n"
                    + "Il Team di SmartDoc\n"
                    + "https://www.smart-doc.it";

            mailSenderServiceGeneric.send(subject, body, null, null, new String[]{emailClean});

            return ResponseEntity.ok(java.util.Collections.singletonMap("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Errore durante la registrazione della prova gratuita: " + e.getMessage());
        }
    }
}

