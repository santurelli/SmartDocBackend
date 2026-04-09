package it.tinna.smartdoc.server.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.database.FileQueryReader;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("serviceJdbcTemplate")
    private JdbcTemplate serviceJdbcTemplate;

    private static final long MAX_TIMESTAMP_DIFF_SECONDS = 300; // 5 minutes

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        
        // Solo per gli endpoint esterni
        if (requestUri.startsWith("/api/external/")) {
            String signature = request.getHeader("X-HMAC-Signature");
            String timestampStr = request.getHeader("X-HMAC-Timestamp");
            String dbKey = request.getParameter("dbKey");

            if (signature == null || timestampStr == null || dbKey == null) {
                log.warn("Missing mandatory HMAC headers or dbKey parameter");
                sendError(response, "Missing authentication details");
                return;
            }

            try {
                // 1. Verifica Timestamp (Replay Attack protection)
                long timestamp = Long.parseLong(timestampStr);
                long now = System.currentTimeMillis() / 1000;
                if (Math.abs(now - timestamp) > MAX_TIMESTAMP_DIFF_SECONDS) {
                    log.warn("Request timestamp expired: {} (now: {})", timestamp, now);
                    sendError(response, "Request expired");
                    return;
                }

                // 2. Recupero Secret Key da DB di servizio
                String secretKey = getSecretKey(dbKey);
                if (secretKey == null) {
                    log.warn("No Secret Key found for database: {}", dbKey);
                    sendError(response, "Invalid database key");
                    return;
                }

                // 3. Calcolo HMAC locale
                String payload = "";
                if (request instanceof CachedBodyHttpServletRequest cachedRequest) {
                    payload = new String(cachedRequest.getCachedBody(), StandardCharsets.UTF_8);
                } else {
                    log.warn("Request is NOT an instance of CachedBodyHttpServletRequest. Body caching is disabled for URI: {}", requestUri);
                }
                
                String dataToSign = timestampStr + payload;
                log.info("HMAC Data To Sign: [{}], Timestamp: [{}], Payload: [{}]", dataToSign, timestampStr, payload);
                String calculatedSignature = calculateHmac(secretKey, dataToSign);

                if (!calculatedSignature.equalsIgnoreCase(signature)) {
                    log.warn("Invalid HMAC signature for dbKey: {}. Expected: {}, Received: {}", dbKey, calculatedSignature, signature);
                    sendError(response, "Invalid signature");
                    return;
                }

                // 4. Autenticazione e setting contesto
                log.info("HMAC Authentication successful for dbKey: {}", dbKey);
                DatabaseContextHolder.setClientDatabase(dbKey);

                UserDetails userDetails = new UserDetailsImpl(
                        0,
                        "external-api",
                        "",
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_EXTERNAL_SERVICE"))
                );

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (NumberFormatException e) {
                log.error("Invalid timestamp format: {}", timestampStr);
                sendError(response, "Invalid timestamp format");
                return;
            } catch (Exception e) {
                log.error("Error during HMAC validation", e);
                sendError(response, "Authentication error");
                return;
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Pulizia del contesto se impostato da questo filtro
            if (requestUri.startsWith("/api/external/")) {
                DatabaseContextHolder.clearClientDatabase();
            }
        }
    }

    private String getSecretKey(String dbKey) {
        try {
            String sql = FileQueryReader.getQuery("ENTI_HMAC_AUTH_S01");
            return serviceJdbcTemplate.queryForObject(sql, String.class, dbKey);
        } catch (Exception e) {
            return null;
        }
    }

    private String calculateHmac(String secret, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\",\"errorCode\":\"UNAUTHORIZED\"}");
    }
}
