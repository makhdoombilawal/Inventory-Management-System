package com.inventoryapp.order.controller;

import com.inventoryapp.auth.security.AuthUser;
import com.inventoryapp.common.response.ApiResponse;
import com.inventoryapp.common.response.ResponseUtil;
import com.inventoryapp.order.dto.OrderRequestDTO;
import com.inventoryapp.order.dto.OrderResponseDTO;
import com.inventoryapp.order.dto.OrderStatusUpdateDTO;
import com.inventoryapp.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for multi-tenant vehicle order management.
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Place and track vehicle orders")
@SecurityRequirement(name = "bearer-jwt")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Create a new order.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GLOBAL_ADMIN', 'DEALER')")
    @Operation(summary = "Create order", description = "Place a new vehicle order for a dealer.")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @Valid @NonNull @RequestBody OrderRequestDTO request) {
        Long tenantId = getTenantIdFromContext();
        return ResponseUtil.created("Order placed successfully", orderService.createOrder(request, tenantId));
    }

    /**
     * Get order details.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get order", description = "Retrieve order details by ID.")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrder(@PathVariable @NonNull Long id) {
        Long tenantId = getTenantIdFromContext();
        return ResponseUtil.success("Order details retrieved", orderService.getOrder(id, tenantId));
    }

    /**
     * Get all orders for the current tenant.
     */
    @GetMapping
    @Operation(summary = "Get tenant orders", description = "Retrieve all orders for current tenant (full list).")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrders(
            @RequestParam(required = false) java.util.Map<String, String> allParams) {
        Long tenantId = getTenantIdFromContext();
        return ResponseUtil.success("Orders retrieved", orderService.getOrders(tenantId));
    }

    /**
     * Update order status.
     */
    @PutMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'GLOBAL_ADMIN', 'DEALER')")
    @Operation(summary = "Update order status", description = "Standardize status update (PUT via JSON body).")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateStatus(
            @Valid @RequestBody OrderStatusUpdateDTO request) {
        Long tenantId = getTenantIdFromContext();
        return ResponseUtil.success("Order status updated", 
                orderService.updateStatus(java.util.Objects.requireNonNull(request.getId()), java.util.Objects.requireNonNull(request.getStatus()), tenantId));
    }

    private Long getTenantIdFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthUser)) {
            return null; // Or throw a specific exception
        }
        AuthUser authUser = (AuthUser) auth.getPrincipal();
        return authUser.getTenantId();
    }
}
