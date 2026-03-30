package com.inventoryapp.inventory.controller;

import com.inventoryapp.auth.security.AuthUser;
import com.inventoryapp.common.response.ApiResponse;
import com.inventoryapp.common.response.ResponseUtil;
import com.inventoryapp.inventory.dto.StockUpdateDTO;
import com.inventoryapp.inventory.entity.Inventory;
import com.inventoryapp.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Controller for multi-tenant inventory management.
 */
@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory Management", description = "Monitor and update dealer stock levels")
@SecurityRequirement(name = "bearer-jwt")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Get all inventory for the current tenant.
     */
    @GetMapping
    @Operation(summary = "Get tenant inventory", description = "Retrieve all inventory records for current tenant (full list).")
    public ResponseEntity<ApiResponse<List<Inventory>>> getInventory() {
        Long tenantId = getTenantIdFromContext();
        return ResponseUtil.success("Inventory retrieved", inventoryService.getInventory(tenantId));
    }

    /**
     * Get inventory for a specific dealer.
     */
    @GetMapping("/dealer/{dealerId}")
    @Operation(summary = "Get dealer inventory", description = "Retrieve inventory records for a specific dealer (full list).")
    public ResponseEntity<ApiResponse<List<Inventory>>> getDealerInventory(
            @PathVariable @NonNull Long dealerId) {
        Long tenantId = getTenantIdFromContext();
        return ResponseUtil.success("Dealer inventory retrieved", 
                inventoryService.getDealerInventory(dealerId, tenantId));
    }

    /**
     * Update stock level for a vehicle.
     */
    @PutMapping("/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'GLOBAL_ADMIN', 'DEALER')")
    @Operation(summary = "Update stock level", description = "Set quantity for a specific vehicle at a dealer (via JSON body).")
    public ResponseEntity<ApiResponse<Inventory>> updateStock(@Valid @RequestBody StockUpdateDTO request) {
        Long tenantId = getTenantIdFromContext();
        return ResponseUtil.success("Stock updated", 
                inventoryService.updateStock(
                        java.util.Objects.requireNonNull(request.getDealerId()), 
                        java.util.Objects.requireNonNull(request.getVehicleId()), 
                        request.getQuantity(), 
                        tenantId));
    }

    private Long getTenantIdFromContext() {
        AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authUser.getTenantId();
    }
}
