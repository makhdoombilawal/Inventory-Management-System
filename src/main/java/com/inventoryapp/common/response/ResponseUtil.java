package com.inventoryapp.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for creating standardized API responses.
 * 
 * Provides helper methods for:
 * - Success responses
 * - Error responses
 * - Validation error responses
 */
public class ResponseUtil {
    
    private ResponseUtil() {
        // Utility class, prevent instantiation
    }
    
    /**
     * Create a success response with data
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }
    
    /**
     * Create a success response without data
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message) {
        return ResponseEntity.ok(ApiResponse.success(message));
    }
    
    /**
     * Create a success response with custom status
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data, @NonNull HttpStatus status) {
        return ResponseEntity.status(status).body(ApiResponse.success(message, data));
    }
    
    /**
     * Create an error response
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, @NonNull HttpStatus status) {
        return ResponseEntity.status(status).body(ApiResponse.error(message));
    }
    
    /**
     * Create an error response with error details
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, Object errors, @NonNull HttpStatus status) {
        return ResponseEntity.status(status).body(ApiResponse.error(message, errors));
    }
    
    /**
     * Create a validation error response from field errors
     */
    public static <T> ResponseEntity<ApiResponse<T>> validationError(String message, List<FieldError> fieldErrors) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : fieldErrors) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message, errors));
    }
    
    /**
     * Create a bad request response
     */
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Create an unauthorized response
     */
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return error(message, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Create a forbidden response
     */
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return error(message, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Create a not found response
     */
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Create an internal server error response
     */
    public static <T> ResponseEntity<ApiResponse<T>> internalServerError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Create a created response (201)
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return success(message, data, HttpStatus.CREATED);
    }
}
