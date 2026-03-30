package com.inventoryapp.auth.security.jwt;

import com.inventoryapp.common.enums.UserRole;

/**
 * Interface for JWT token operations.
 * Provides abstraction for token generation, validation, and claim extraction.
 */
public interface JwtTokenProvider {

    /**
     * Generate JWT token for user with email, userId, tenantId, and role claims
     * 
     * @param email    User's email address
     * @param userId   User's ID
     * @param tenantId User's tenant ID
     * @param role     User's role
     * @return JWT token string
     */
    String generateToken(String email, Long userId, Long tenantId, UserRole role);

    /**
     * Extract user ID from JWT token
     * 
     * @param token JWT token
     * @return User ID
     */
    Long extractUserId(String token);

    /**
     * Validate JWT token
     * 
     * @param token JWT token to validate
     * @return true if token is valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Extract email from JWT token
     * 
     * @param token JWT token
     * @return Email address (acts as username)
     */
    String extractUsername(String token);

    /**
     * Extract tenant ID from JWT token
     * 
     * @param token JWT token
     * @return Tenant ID
     */
    Long extractTenantId(String token);

    /**
     * Extract user role from JWT token
     * 
     * @param token JWT token
     * @return User role
     */
    UserRole extractRole(String token);

    /**
     * Check if token is expired
     * 
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    boolean isTokenExpired(String token);
}
