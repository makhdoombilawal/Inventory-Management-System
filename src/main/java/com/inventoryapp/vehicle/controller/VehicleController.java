package com.inventoryapp.vehicle.controller;

import com.inventoryapp.auth.security.AuthUser;
import com.inventoryapp.common.response.ApiResponse;
import com.inventoryapp.common.response.ResponseUtil;
import com.inventoryapp.vehicle.dto.VehicleRequestDTO;
import com.inventoryapp.vehicle.dto.VehicleResponseDTO;
import com.inventoryapp.vehicle.dto.VehicleSearchDTO;
import com.inventoryapp.vehicle.dto.VehicleUpdateDTO;
import com.inventoryapp.vehicle.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * Vehicle controller for CRUD operations with advanced filtering and Swagger documentation
 */
@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicle Management", description = "Vehicle inventory CRUD operations")
@SecurityRequirement(name = "bearer-jwt")
@Slf4j
public class VehicleController {
    
    private final VehicleService vehicleService;
    
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }
    
    /**
     * Create new vehicle
     */
    @PostMapping
    @Operation(summary = "Create vehicle", description = "Add new vehicle to inventory")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Vehicle created successfully")
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> create(@Valid @NonNull @RequestBody VehicleRequestDTO request) {
        Long tenantId = getTenantIdFromContext();
        VehicleResponseDTO response = vehicleService.create(request, tenantId);
        log.info("New vehicle created: {} for tenant: {}", request.getModel(), tenantId);
        return ResponseUtil.created("Vehicle created successfully", response);
    }
    
    /**
     * Get vehicle by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle", description = "Retrieve vehicle by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vehicle found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> getById(@PathVariable @NonNull Long id) {
        Long tenantId = getTenantIdFromContext();
        VehicleResponseDTO response = vehicleService.getById(id, tenantId);
        return ResponseUtil.success("Vehicle found", response);
    }
    
    /**
     * Get all vehicles without filters (GET without path parameters)
     */
    @GetMapping
    @Operation(summary = "Get all vehicles", description = "Retrieve all vehicles from database")
    public ResponseEntity<ApiResponse<List<VehicleResponseDTO>>> getAll() {
        try {
            Long tenantId = getTenantIdFromContext();
            List<VehicleResponseDTO> response = vehicleService.getAllByTenant(tenantId);
            return ResponseUtil.success("Vehicles retrieved successfully", response);
        } catch (NullPointerException e) {
            log.warn("Tenant context not available, returning empty list");
            return ResponseUtil.success("Vehicles retrieved successfully", List.of());
        }
    }
    
    @PostMapping("/search")
    @Operation(summary = "Search vehicles", description = "Search vehicles with advanced filtering using a JSON body (full list)")
    public ResponseEntity<ApiResponse<List<VehicleResponseDTO>>> search(
            @Valid @RequestBody VehicleSearchDTO searchRequest) {
        
        Long tenantId = getTenantIdFromContext();
        
        List<VehicleResponseDTO> response = vehicleService.findByFilters(
                tenantId, 
                searchRequest.getModel(), 
                searchRequest.getStatus(), 
                searchRequest.getPriceMin(), 
                searchRequest.getPriceMax(), 
                searchRequest.getSubscription());
        
        return ResponseUtil.success("Vehicles retrieved successfully", response);
    }
    
    /**
     * Get all vehicles without filters (alias for backward compatibility)
     */
    @GetMapping("/all")
    @Operation(summary = "Get all vehicles", description = "Retrieve all vehicles from database")
    public ResponseEntity<ApiResponse<List<VehicleResponseDTO>>> getAllAlias() {
        return getAll();
    }
    

    
    /**
     * Update vehicle (Full replacement)
     */
    @PutMapping
    @Operation(summary = "Replace vehicle", description = "Fully replace vehicle information (via JSON body).")
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> replace(
            @Valid @NonNull @RequestBody VehicleRequestDTO request) {
        
        Long tenantId = getTenantIdFromContext();
        Long id = java.util.Objects.requireNonNull(request.getId(), "Vehicle ID is required for replacement");
        
        // Convert RequestDTO to UpdateDTO for service compatibility
        VehicleUpdateDTO updateDto = VehicleUpdateDTO.builder()
                .model(request.getModel())
                .manufacturer(request.getManufacturer())
                .year(request.getYear())
                .price(request.getPrice())
                .status(request.getStatus())
                .description(request.getDescription())
                .build();
                
        VehicleResponseDTO response = vehicleService.update(id, Objects.requireNonNull(updateDto), tenantId);
        log.info("Vehicle fully replaced: {} for tenant: {}", id, tenantId);
        return ResponseUtil.success("Vehicle fully replaced", response);
    }
 
    /**
     * Delete vehicle
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vehicle", description = "Delete vehicle from inventory (soft delete)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Vehicle deleted successfully")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable @NonNull Long id) {
        Long tenantId = getTenantIdFromContext();
        vehicleService.delete(id, tenantId);
        log.info("Vehicle deleted: {} for tenant: {}", id, tenantId);
        return ResponseUtil.success("Vehicle deleted successfully");
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
