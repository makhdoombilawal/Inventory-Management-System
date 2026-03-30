package com.inventoryapp.auth.security.filter;

import com.inventoryapp.auth.security.AuthUser;
import com.inventoryapp.auth.security.jwt.JwtTokenProvider;
import com.inventoryapp.common.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter.
 *
 * This filter:
 * 1. Extracts JWT token from Authorization header (Bearer scheme)
 * 2. Validates token signature and expiration
 * 3. Extracts userId, email, tenantId, and role from token
 * 4. Constructs an AuthUser principal and sets it into the SecurityContext
 *
 * ENTERPRISE RULE: tenantId is ONLY derived from the JWT. Never from headers.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.extractUsername(token);
                Long userId = jwtTokenProvider.extractUserId(token);
                Long tenantId = jwtTokenProvider.extractTenantId(token);
                UserRole role = jwtTokenProvider.extractRole(token);

                // Build the AuthUser principal from JWT claims
                AuthUser authUser = new AuthUser(userId, username, role.name(), tenantId);

                // Create Spring Security authentication token with AuthUser as the principal
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        authUser,
                        null,
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.name()))
                    );

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("JWT auth OK: user=" + username + " tenant=" + tenantId + " role=" + role);
            }
        } catch (Exception e) {
            logger.error("JWT authentication failed: " + e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header (Bearer scheme)
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Skip JWT filter for public endpoints
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/api/auth/health") ||
               path.startsWith("/api/users/register") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/actuator/health");
    }
}
