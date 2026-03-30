package com.inventoryapp.common.ratelimiter;
 
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
import java.io.IOException;
 
/**
 * Filter for applying rate limits to all incoming requests.
 * Uses RateLimiterService to check Redis for request counts.
 * Returns 429 Too Many Requests if the limit is exceeded.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimiterFilter extends OncePerRequestFilter {
    
    private final RateLimiterService rateLimiterService;
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        // Skip rate-limiting for Swagger UI and API Docs
        String path = request.getRequestURI();
        if (path.contains("swagger") || path.contains("api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Identify the rate-limiting key (Tenant + User IP/Name)
        Long tenantId = null;
        String userName = "anonymous";
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof com.inventoryapp.auth.security.AuthUser authUser) {
            tenantId = authUser.getTenantId();
            userName = auth.getName(); // or authUser.getId().toString() if preferred, but email/name is standard
        }
        
        String tenantKey = tenantId != null ? tenantId.toString() : "anonymous";
        String key = "tenant:" + tenantKey + ":ip:" + request.getRemoteAddr();
        
        // Check if user is authenticated for per-user rate limiting
        if (!"anonymous".equals(userName) && !"anonymousUser".equals(userName)) {
            key = "user:" + userName;
        }
        
        if (!rateLimiterService.isAllowed(key)) {
            log.warn("Rate limit exceeded for path: {} from: {}. Key: {}", path, request.getRemoteAddr(), key);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Too many requests. Please try again later.\"}");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
