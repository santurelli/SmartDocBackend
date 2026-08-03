package it.tinna.smartdoc.server.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userEmail;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.trace("No Bearer token found in request to {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            try {
                jwt = authHeader.substring(7);
                userEmail = jwtService.extractUsername(jwt);
                log.debug("JWT Filter: extracted user {} from token", userEmail);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                log.warn("JWT Filter: Token expired for request to {}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token expired");
                return;
            } catch (Exception e) {
                log.error("JWT Filter: Invalid token for request to {}", request.getRequestURI(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.isTokenValid(jwt, userEmail)) {
                    log.debug("JWT Filter: Token is valid for user {}", userEmail);
                    // Extract dbName from token
                    String dbName = jwtService.extractClaim(jwt, claims -> claims.get("dbName", String.class));
                    Integer userId = jwtService.extractClaim(jwt, claims -> claims.get("id", Integer.class));
                    String role = jwtService.extractClaim(jwt, claims -> claims.get("role", String.class));
                    Integer tipoAccount = jwtService.extractClaim(jwt, claims -> claims.get("tipoAccount", Integer.class));
                    if (role == null) {
                        role = "ROLE_USER";
                    }

                    if (dbName != null) {
                        log.info("Setting Database Context to: {}", dbName);
                        DatabaseContextHolder.setClientDatabase(dbName);
                    } else {
                        log.warn("No dbName found in token!");
                    }

                    UserContextHolder.setTipoAccount(tipoAccount);

                    // Create UserDetailsImpl
                    UserDetails userDetails = new UserDetailsImpl(
                            userId,
                            userEmail,
                            "",
                            java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(role))
                    );

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            DatabaseContextHolder.clearClientDatabase();
            UserContextHolder.clear();
        }
    }
}

