package com.inventoryapp.common.exception;

/**
 * Exception thrown when tenant identifier is missing or invalid.
 * Results in HTTP 400 (Bad Request) response.
 */
public class TenantNotFoundException extends RuntimeException {
    
    public TenantNotFoundException(String message) {
        super(message);
    }

    public TenantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
