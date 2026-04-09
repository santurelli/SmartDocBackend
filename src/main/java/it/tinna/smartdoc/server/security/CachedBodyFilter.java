package it.tinna.smartdoc.server.security;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter that wraps the request in a CachedBodyHttpServletRequest for external API calls,
 * allowing the body to be read multiple times (useful for HMAC validation).
 * This filter MUST run before ApiKeyFilter.
 */
@Component
public class CachedBodyFilter extends OncePerRequestFilter {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CachedBodyFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        
        if (requestUri.startsWith("/api/external/")) {
            log.info("Wrapping request for URI: {} to allow multiple body reads", requestUri);
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
            filterChain.doFilter(cachedRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
