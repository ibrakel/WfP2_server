package com.example.ueberholserver.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;

/**
 * Checks the {@code X-Api-Key} header on every {@code /api/**} request.
 * Returns 401 immediately if the key is missing or does not match.
 *
 * <p>If the configured key is the built-in default ({@code dev-key}), meaning the
 * {@code API_KEY} environment variable was never set, auth is skipped entirely so
 * the Android app can reach the server over local WiFi without any header changes.
 * Set {@code API_KEY} in the environment to enforce the check in production.
 */
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String DEV_KEY = "dev-key";

    private final String expectedKey;
    private final boolean enforced;

    public ApiKeyAuthFilter(String expectedKey) {
        this.expectedKey = expectedKey;
        this.enforced    = !DEV_KEY.equals(expectedKey);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        if (enforced && request.getRequestURI().startsWith("/api/")) {
            String provided = request.getHeader("X-Api-Key");
            if (provided == null || !MessageDigest.isEqual(
                    provided.getBytes(StandardCharsets.UTF_8),
                    expectedKey.getBytes(StandardCharsets.UTF_8))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }
        }

        if (!enforced || request.getRequestURI().startsWith("/api/")) {
            // Mark as authenticated (either open dev mode, or key just validated above)
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            "api-client", null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_API"))));
        }

        chain.doFilter(request, response);
    }
}
