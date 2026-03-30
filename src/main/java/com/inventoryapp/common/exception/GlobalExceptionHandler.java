package com.inventoryapp.common.exception;

import com.inventoryapp.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler that intercepts exceptions thrown by controllers
 * and converts them into standardized ApiResponse format.
 * 
 * Provides centralized exception handling and consistent error response format.
 * All exceptions return ApiResponse with:
 * - status: "error"
 * - message: Error description
 * - errors: Additional error details (optional)
 * - timestamp: When error occurred
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(TenantNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleTenantNotFoundException(
                        TenantNotFoundException ex, HttpServletRequest request) {

                log.error("Tenant not found: {} at {}", ex.getMessage(), request.getRequestURI());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
                        ResourceNotFoundException ex, HttpServletRequest request) {

                log.error("Resource not found: {} at {}", ex.getMessage(), request.getRequestURI());
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(UnauthorizedAccessException.class)
        public ResponseEntity<ApiResponse<Object>> handleUnauthorizedAccessException(
                        UnauthorizedAccessException ex, HttpServletRequest request) {

                log.error("Unauthorized access: {} at {}", ex.getMessage(), request.getRequestURI());
                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(ForbiddenAccessException.class)
        public ResponseEntity<ApiResponse<Object>> handleForbiddenAccessException(
                        ForbiddenAccessException ex, HttpServletRequest request) {

                log.error("Forbidden access: {} at {}", ex.getMessage(), request.getRequestURI());
                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(InvalidRequestException.class)
        public ResponseEntity<ApiResponse<Object>> handleInvalidRequestException(
                        InvalidRequestException ex, HttpServletRequest request) {

                log.error("Invalid request: {} at {}", ex.getMessage(), request.getRequestURI());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Object>> handleValidationException(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                String errorMessage = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(FieldError::getDefaultMessage)
                                .collect(Collectors.joining("; "));

                log.error("Validation error: {} at {}", errorMessage, request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Validation failed", errors));
        }

        @ExceptionHandler(RateLimitExceededException.class)
        public ResponseEntity<ApiResponse<Object>> handleRateLimitExceeded(
                        RateLimitExceededException ex, HttpServletRequest request) {

                log.warn("Rate limit exceeded: {} at {}", ex.getMessage(), request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.TOO_MANY_REQUESTS)
                                .body(ApiResponse.error("Too many requests. Please try again later."));
        }

        @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
                        org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {

                log.error("Access denied: {} at {}", ex.getMessage(), request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.error(
                                                "Access denied. You do not have permission to access this resource."));
        }

        @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
        public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
                        org.springframework.security.core.AuthenticationException ex, HttpServletRequest request) {

                log.error("Authentication failed: {} at {}", ex.getMessage(), request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.error("Authentication failed. Please provide valid credentials."));
        }

        @ExceptionHandler(org.springframework.web.bind.MissingRequestHeaderException.class)
        public ResponseEntity<ApiResponse<Object>> handleMissingHeader(
                        org.springframework.web.bind.MissingRequestHeaderException ex, HttpServletRequest request) {

                log.error("Missing required header: {} at {}", ex.getHeaderName(), request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Missing required header: " + ex.getHeaderName()));
        }

        @ExceptionHandler(org.springframework.data.mapping.PropertyReferenceException.class)
        public ResponseEntity<ApiResponse<Object>> handlePropertyReferenceException(
                        org.springframework.data.mapping.PropertyReferenceException ex, HttpServletRequest request) {

                log.error("Invalid sort property: {} at {}", ex.getMessage(), request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Invalid sort field: " + ex.getPropertyName()));
        }

        @ExceptionHandler(org.springframework.dao.InvalidDataAccessApiUsageException.class)
        public ResponseEntity<ApiResponse<Object>> handleInvalidDataPathUsageException(
                        org.springframework.dao.InvalidDataAccessApiUsageException ex, HttpServletRequest request) {

                log.error("Invalid data access API usage at {}: {}", request.getRequestURI(), ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(
                                                "Invalid request parameter. Check your sorting or filtering criteria."));
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        log.error("Message not readable at {}: {}", request.getRequestURI(), ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid request body format or value."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
                        IllegalArgumentException ex, HttpServletRequest request) {

                log.error("Invalid argument at {}: {}", request.getRequestURI(), ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Invalid request parameter or argument format."));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleGenericException(
                        Exception ex, HttpServletRequest request) {

                log.error("Unexpected error at {}: ", request.getRequestURI(), ex);

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("An unexpected error occurred. Please try again later."));
        }
}
