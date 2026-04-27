package com.inventoryapp.dealer.controller;

import com.inventoryapp.auth.security.AuthUser;
import com.inventoryapp.common.response.ApiResponse;
import com.inventoryapp.common.response.ResponseUtil;
import com.inventoryapp.dealer.dto.DealerRequestDTO;
import com.inventoryapp.dealer.dto.DealerResponseDTO;
import com.inventoryapp.dealer.dto.DealerUpdateDTO;
import com.inventoryapp.dealer.service.DealerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Enterprise Dealer Controller - handles organizational tenant records.
 */
@RestController
@RequestMapping("/api/dealers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dealer Management", description = "APIs for managing dealers and organizations")
@SecurityRequirement(name = "bearer-jwt")
public class DealerController {
    
    private final DealerService dealerService;
    
    @PostMapping
    @Operation(summary = "Create dealer", description = "Create new dealer account")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Dealer created successfully")
    public ResponseEntity<ApiResponse<DealerResponseDTO>> create(@Valid @RequestBody DealerRequestDTO request) {
        Long tenantId = getTenantIdFromContext();
        log.info("Creating dealer: {} for tenant: {}", request.getName(), tenantId);
        
        DealerResponseDTO response = dealerService.create(request, tenantId);
        return ResponseUtil.created("Dealer created successfully", response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get dealer by ID", description = "Retrieve dealer details by ID (tenant-scoped)")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dealer found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Dealer not found")
    })
    public ResponseEntity<ApiResponse<DealerResponseDTO>> getById(@PathVariable @NonNull Long id) {
        Long tenantId = getTenantIdFromContext();
        DealerResponseDTO response = dealerService.getById(id, tenantId);
        return ResponseUtil.success("Dealer found", response);
    }
    
    @GetMapping
    @Operation(summary = "Get all dealers", description = "Retrieve all dealers (full list)")
    public ResponseEntity<ApiResponse<List<DealerResponseDTO>>> getAll() {
        Long tenantId = getTenantIdFromContext();
        List<DealerResponseDTO> response = dealerService.getAllByTenant(tenantId);
        return ResponseUtil.success("Dealers retrieved successfully", response);
    }
    
    @PutMapping
    @Operation(summary = "Replace dealer", description = "Fully replace dealer information (via JSON body).")
    public ResponseEntity<ApiResponse<DealerResponseDTO>> replace(
            @Valid @RequestBody DealerRequestDTO request) {
        
        Long id = java.util.Objects.requireNonNull(request.getId(), "Dealer ID is required for replacement");
        Long tenantId = getTenantIdFromContext();
        
        // Convert RequestDTO to UpdateDTO for service compatibility
        DealerUpdateDTO updateDto = DealerUpdateDTO.builder()
                .name(request.getName())
                .email(request.getEmail())
                .subscriptionType(request.getSubscriptionType())
                .contactPerson(request.getContactPerson())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();
                
        DealerResponseDTO response = dealerService.update(id, updateDto, tenantId);
        return ResponseUtil.success("Dealer replaced successfully", response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete dealer", description = "Delete dealer (soft delete)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Dealer deleted successfully")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable @NonNull Long id) {
        Long tenantId = getTenantIdFromContext();
        dealerService.delete(id, tenantId);
        log.info("Dealer deleted: {} for tenant: {}", id, tenantId);
        return ResponseUtil.success("Dealer deleted successfully");
    }
    
    /**
     * Extract tenant ID from AuthUser principal in security context (JWT-based)
     */
    private Long getTenantIdFromContext() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof AuthUser) {
                AuthUser authUser = (AuthUser) authentication.getPrincipal();
                return authUser.getTenantId();
            }
            throw new IllegalStateException("Tenant context not available");
        } catch (Exception e) {
            log.error("Failed to get tenant from context: {}", e.getMessage());
            throw new IllegalStateException("Tenant context not available", e);
        }
    }
}
