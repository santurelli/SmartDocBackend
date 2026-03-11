package it.tinna.smartdoc.server.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import it.tinna.smartdoc.server.database.DatabaseContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${smartdoc.external.api.key:FASTORDER-SECRET-KEY-2026}")
    private String validApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        
        // Solo per gli endpoint esterni
        if (requestUri.startsWith("/api/external/")) {
            String apiKey = request.getHeader("X-API-KEY");

            if (validApiKey.equals(apiKey)) {
                // Imposta il contesto del database se passato come parametro
                String dbKey = request.getParameter("dbKey");
                if (dbKey != null) {
                    log.info("Setting Database Context (via API Key) to: {}", dbKey);
                    DatabaseContextHolder.setClientDatabase(dbKey);
                }

                // Autenticazione fittizia per bypassare i controlli Spring Security successivi
                UserDetails userDetails = new UserDetailsImpl(
                        0,
                        "external-api",
                        "",
                        java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_EXTERNAL_SERVICE"))
                );

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid API Key");
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
}
