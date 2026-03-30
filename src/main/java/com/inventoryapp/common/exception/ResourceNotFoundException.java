package com.inventoryapp.common.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * Results in HTTP 404 (Not Found) response.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Convenience constructor for resource not found by ID.
     */
    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format("%s not found with id: %s", resourceName, resourceId));
    }
}
