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

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Set the context to the selected municipality's database
            DatabaseContextHolder.setClientDatabase(loginRequest.getEnte());
            
            UtenteDto user = loginDelegate.getUserByUsername(loginRequest.getUsername());

            if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body("Invalid username or password");
            }

            // Generate Token
            java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
            extraClaims.put("dbName", loginRequest.getEnte());
            
            // Fetch Global Config
            try {
                // Populate session-like config into JWT claims
                // Note: Keys should match what frontend expects or what legacy stored in session
                String abilitaDivisioni = configurazioneDelegate.getByKey(it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_DOMAIN_GLOBAL, it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_KEY_ABILITADIVISIONI);
                extraClaims.put(it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_KEY_ABILITADIVISIONI, org.apache.commons.lang3.StringUtils.defaultIfEmpty(abilitaDivisioni, "0"));

                String tipoStore = configurazioneDelegate.getByKey(it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_DOMAIN_GLOBAL, it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_KEY_TIPOSTORE);
                extraClaims.put(it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_KEY_TIPOSTORE, org.apache.commons.lang3.StringUtils.defaultIfEmpty(tipoStore, ""));
                
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
}
