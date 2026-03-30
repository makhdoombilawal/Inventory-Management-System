package com.inventoryapp.admin.controller;

import com.inventoryapp.admin.dto.SubscriptionCountDTO;
import com.inventoryapp.admin.service.AdminService;
import com.inventoryapp.auth.dto.AdminUserCreationDTO;
import com.inventoryapp.auth.dto.UserResponseDTO;
import com.inventoryapp.auth.service.AuthService;
import com.inventoryapp.common.response.ApiResponse;
import com.inventoryapp.common.response.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin controller for global admin operations.
 * 
 * All endpoints require GLOBAL_ADMIN role.
 * Handles:
 * - Admin user creation (ADMIN and GLOBAL_ADMIN roles)
 * - Global system statistics
 * - Cross-tenant operations
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Management", description = "Global admin operations")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('GLOBAL_ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;

    /**
     * Create admin or global admin user - GLOBAL_ADMIN ONLY.
     * 
     * Allows creation of:
     * - ADMIN users (tenant-scoped)
     * - GLOBAL_ADMIN users (system-wide)
     * 
     * @param adminUserDTO Admin user creation details
     * @return Created user details
     */
    @PostMapping("/users")
    @Operation(summary = "Create admin user", description = "Create ADMIN or GLOBAL_ADMIN user. Requires GLOBAL_ADMIN role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Admin user created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request - validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires GLOBAL_ADMIN role")
    })
    public ResponseEntity<ApiResponse<UserResponseDTO>> createAdminUser(
            @Valid @RequestBody AdminUserCreationDTO adminUserDTO) {

        log.info("Admin user creation request for: {} with role: {}",
                adminUserDTO.getEmail(), adminUserDTO.getRole());

        UserResponseDTO response = authService.createAdminUser(adminUserDTO);

        return ResponseUtil.created("Admin user created successfully", response);
    }

    /**
     * Get count of dealers by subscription type - GLOBAL scope.
     * No tenant filtering applied - shows data for entire system.
     */
    @GetMapping("/dealers/countBySubscription")
    @Operation(summary = "Count dealers by subscription (GLOBAL)", description = "Get global count of all dealers grouped by subscription type. GLOBAL_ADMIN only.")
    public ResponseEntity<ApiResponse<Map<String, SubscriptionCountDTO>>> countBySubscription() {
        Map<String, SubscriptionCountDTO> result = adminService.countBySubscriptionGlobal();
        log.info("Admin requested global subscription counts");
        return ResponseUtil.success("Subscription counts retrieved successfully", result);
    }

    /**
     * Get total dealers count - GLOBAL scope.
     */
    @GetMapping("/dealers/total")
    @Operation(summary = "Total dealers count (GLOBAL)", description = "Get total count of all dealers in system. GLOBAL_ADMIN only.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTotalDealers() {
        long total = adminService.getTotalDealersCount();
        long active = adminService.getActiveDealersCount();

        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        response.put("active", active);
        response.put("inactive", total - active);

        log.info("Admin requested total dealers count: {}", total);
        return ResponseUtil.success("Dealer counts retrieved successfully", response);
    }

    /**
     * Health check endpoint for admin service.
     */
    @GetMapping("/health")
    @Operation(summary = "Admin health check", description = "Verify admin access and system health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Admin API");
        response.put("scope", "GLOBAL");

        return ResponseUtil.success("Admin service is running", response);
    }
}
