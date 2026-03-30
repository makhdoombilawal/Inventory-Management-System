package com.inventoryapp.auth.security.jwt;

import com.inventoryapp.common.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT token generation and validation utility.
 * Implements JwtTokenProvider interface for token operations.
 */
@Component
public class JwtUtil implements JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Generate JWT token for user with email, userId, tenantId, and role claims
     */
    @Override
    public String generateToken(String email, Long userId, Long tenantId, UserRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("tenantId", tenantId);
        claims.put("role", role.name());

        return createToken(claims, email);
    }

    /**
     * Create JWT token with claims
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate JWT token signature and expiration
     */
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extract username from JWT token
     */
    @Override
    public String extractUsername(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extract tenantId from JWT token
     */
    @Override
    public Long extractTenantId(String token) {
        Object tenantId = getAllClaimsFromToken(token).get("tenantId");
        if (tenantId == null) return null;
        if (tenantId instanceof Number) {
            return ((Number) tenantId).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(tenantId));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Extract userId from JWT token
     */
    @Override
    public Long extractUserId(String token) {
        Object userId = getAllClaimsFromToken(token).get("userId");
        if (userId == null) return null;
        if (userId instanceof Number) {
            return ((Number) userId).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(userId));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Extract role from JWT token
     */
    @Override
    public UserRole extractRole(String token) {
        String roleStr = (String) getAllClaimsFromToken(token).get("role");
        return UserRole.valueOf(roleStr);
    }

    /**
     * Check if token is expired
     */
    @Override
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaimFromToken(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Get specific claim from token
     */
    private <T> T getClaimFromToken(String token, java.util.function.Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Get all claims from token
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Legacy methods for backward compatibility
    public String getUsernameFromToken(String token) {
        return extractUsername(token);
    }

    public Long getTenantIdFromToken(String token) {
        return extractTenantId(token);
    }

    public Long getUserIdFromToken(String token) {
        return extractUserId(token);
    }

    public UserRole getRoleFromToken(String token) {
        return extractRole(token);
    }
}
