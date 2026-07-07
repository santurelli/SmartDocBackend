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
}

