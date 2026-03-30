package com.inventoryapp.common.tenant;

import com.inventoryapp.auth.security.AuthUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import java.io.IOException;

/**
 * Enterprise Tenant Filter - enforces multi-tenancy and validates extraction.
 * 
 * Responsibilities:
 * 1. Extract tenantId directly from the SecurityContext's AuthUser principal
 * 2. Store tenantId in TenantContext for query scoping
 * 3. Ensure context cleanup
 * 4. NEVER fallback to HTTP headers.
 */
@Slf4j
@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        Long tenantId = null;

        // 1. Auto-extraction from SecurityContext (set by JwtAuthenticationFilter)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUser authUser) {
            tenantId = authUser.getTenantId();
        }

        // 2. Validation for protected endpoints
        if (!shouldNotFilter(request)) {
            if (tenantId == null) {
                log.warn("Unauthorized request: Missing tenant identity from JWT for {}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
                response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
                response.getWriter().write("{\"status\": \"error\", \"message\": \"Authentication required: missing tenant context in JWT\"}");
                return;
            }
        }

        try {
            if (tenantId != null) {
                log.debug("Setting tenant focus: {}", tenantId);
                TenantContext.setTenantId(tenantId);
            }

            filterChain.doFilter(request, response);
            
        } finally {
            // CRITICAL: Always clear the tenant context
            TenantContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || 
               path.startsWith("/health") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/api/auth/login") ||
               path.startsWith("/api/auth/health") ||
               path.startsWith("/api/users/register");
    }
}
