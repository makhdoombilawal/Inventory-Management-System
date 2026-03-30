package com.inventoryapp.common.exception;

/**
 * Exception thrown when a user attempts to access a resource they are not authorized to access.
 * 
 * Typically occurs when:
 * - Attempting to access resources belonging to a different tenant
 * - Attempting to access resources without proper role permissions
 * 
 * Results in HTTP 403 (Forbidden) response.
 */
public class UnauthorizedAccessException extends RuntimeException {
    
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
