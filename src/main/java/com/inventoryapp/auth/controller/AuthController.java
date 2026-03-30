package com.inventoryapp.auth.controller;

import com.inventoryapp.auth.dto.LoginRequestDTO;
import com.inventoryapp.auth.dto.LoginResponseDTO;
import com.inventoryapp.auth.service.AuthService;
import com.inventoryapp.common.response.ApiResponse;
import com.inventoryapp.common.response.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller handling login operations.
 * 
 * Endpoints:
 * - POST /api/auth/login: Authenticate user and return JWT token
 * 
 * This controller is public (no authentication required).
 * Rate limiting is applied to prevent brute force attacks.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Login endpoint - authenticates user and returns JWT token.
     * 
     * @param loginRequest Login credentials (email, password)
     * @param tenantId Tenant ID from X-Tenant-Id header
     * @return JWT token and user details
     */
    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticate user with email and password. Returns JWT token on success."
    )
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        
        log.info("Login request received for user: {}", loginRequest.getEmail());
        
        LoginResponseDTO response = authService.login(loginRequest, null);
        
        return ResponseUtil.success("Login successful", response);
    }
    
    /**
     * Health check endpoint for auth service
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if authentication service is running")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseUtil.success("Authentication service is running", "OK");
    }
}
