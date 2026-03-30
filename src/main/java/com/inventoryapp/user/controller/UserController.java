package com.inventoryapp.user.controller;

import com.inventoryapp.auth.dto.RegisterRequestDTO;
import com.inventoryapp.auth.dto.UserResponseDTO;
import com.inventoryapp.auth.security.AuthUser;
import com.inventoryapp.auth.service.AuthService;
import com.inventoryapp.common.response.ApiResponse;
import com.inventoryapp.common.response.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * User controller for user registration and profile management.
 * 
 * Registration endpoint creates USER role only.
 * Login has been moved to AuthController (/api/auth/login).
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User registration and profile management")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final AuthService authService;
    
    /**
     * Register new user - PUBLIC ENDPOINT.
     * Only creates users with USER role.
     * Admin users must be created through /api/admin/users endpoint.
     * 
     * @param registerRequest Registration details (username, password, tenantId)
     * @return Created user details
     */
    @PostMapping("/register")
    @Operation(
        summary = "Register new user",
        description = "Create new user account with USER role. Requires email, password, and tenantId in body."
    )
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO registerRequest) {
        
        log.info("Registration request received for user: {}", registerRequest.getEmail());
        
        UserResponseDTO response = authService.register(registerRequest);
        
        return ResponseUtil.created("User registered successfully", response);
    }
    
    /**
     * Get user profile - REQUIRES AUTHENTICATION.
     * Returns authenticated user's profile information.
     */
    @GetMapping("/profile")
    @Operation(
        summary = "Get user profile",
        description = "Retrieve authenticated user's profile information"
    )
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser) auth.getPrincipal();
        String email = authUser.getEmail();
        Long tenantId = authUser.getTenantId();
        UserResponseDTO profile = authService.getProfile(email, tenantId);
        return ResponseUtil.success("Profile retrieved successfully", profile);
    }
}
