package com.inventoryapp.inventory.service;

import com.inventoryapp.common.exception.ForbiddenAccessException;
import com.inventoryapp.common.exception.ResourceNotFoundException;
import com.inventoryapp.inventory.entity.Inventory;
import com.inventoryapp.inventory.repository.InventoryRepository;
import com.inventoryapp.dealer.entity.Dealer;
import com.inventoryapp.dealer.repository.DealerRepository;
import com.inventoryapp.vehicle.entity.Vehicle;
import com.inventoryapp.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Service for multi-tenant inventory management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final DealerRepository dealerRepository;
    private final VehicleRepository vehicleRepository;

    /**
     * Get inventory for a tenant (full list, no pagination).
     */
    @Transactional(readOnly = true)
    public List<Inventory> getInventory(Long tenantId) {
        return inventoryRepository.findAllByTenantId(tenantId);
    }

    /**
     * Get inventory for a specific dealer (full list, no pagination).
     */
    @Transactional(readOnly = true)
    public List<Inventory> getDealerInventory(@NonNull Long dealerId, Long tenantId) {
        return inventoryRepository.findByDealerIdAndTenantId(dealerId, tenantId);
    }

    /**
     * Update stock level for a vehicle at a dealer.
     * Creates new inventory record if not exists.
     */
    @Transactional
    public Inventory updateStock(@NonNull Long dealerId, @NonNull Long vehicleId, Integer quantity, Long tenantId) {
        log.info("Updating stock for vehicle {} at dealer {} in tenant {}", vehicleId, dealerId, tenantId);

        // Verify dealer and vehicle exist in this tenant
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found"));
        if (tenantId != null && tenantId != 0L && !dealer.getTenantId().equals(tenantId)) {
            throw new ForbiddenAccessException("Cross-tenant access denied to dealer");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        if (tenantId != null && tenantId != 0L && !vehicle.getTenantId().equals(tenantId)) {
            throw new ForbiddenAccessException("Cross-tenant access denied to vehicle");
        }

        Inventory inventory = inventoryRepository.findByVehicleIdAndDealerIdAndTenantId(vehicleId, dealerId, tenantId)
                .orElseGet(() -> {
                    Inventory newInv = Inventory.builder()
                            .dealer(dealer)
                            .vehicle(vehicle)
                            .quantity(0)
                            .build();
                    newInv.setTenantId(tenantId);
                    return newInv;
                });

        inventory.setQuantity(quantity);
        return inventoryRepository.save(inventory);
    }
}
