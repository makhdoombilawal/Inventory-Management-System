package com.inventoryapp.common.exception;

/**
 * Exception thrown when a request contains invalid data or parameters.
 * Results in HTTP 400 (Bad Request) response.
 */
public class InvalidRequestException extends RuntimeException {
    
    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
