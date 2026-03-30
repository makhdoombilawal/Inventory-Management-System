package com.inventoryapp.vehicle.service;

import com.inventoryapp.common.exception.ForbiddenAccessException;
import com.inventoryapp.vehicle.dto.VehicleRequestDTO;
import com.inventoryapp.vehicle.dto.VehicleResponseDTO;
import com.inventoryapp.vehicle.dto.VehicleUpdateDTO;
import com.inventoryapp.vehicle.entity.Vehicle;
import com.inventoryapp.vehicle.repository.VehicleRepository;
import com.inventoryapp.dealer.entity.Dealer;
import com.inventoryapp.dealer.repository.DealerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vehicle service for CRUD operations with tenant validation and complex filtering
 */
@Service
@Slf4j
@Transactional
public class VehicleService {
    
    private final VehicleRepository vehicleRepository;
    private final DealerRepository dealerRepository;
    
    public VehicleService(VehicleRepository vehicleRepository, DealerRepository dealerRepository) {
        this.vehicleRepository = vehicleRepository;
        this.dealerRepository = dealerRepository;
    }
    
    /**
     * Create new vehicle
     */
    public VehicleResponseDTO create(@NonNull VehicleRequestDTO request, Long tenantId) {
        Vehicle vehicle = Vehicle.builder()
                .model(request.getModel())
                .manufacturer(request.getManufacturer())
                .year(request.getYear())
                .price(request.getPrice())
                .status(request.getStatus())
                .vin(request.getVin())
                .description(request.getDescription())
                .available(true)
                .build();
        
        Dealer dealer = dealerRepository.findById(java.util.Objects.requireNonNull(request.getDealerId()))
                .orElseThrow(() -> new IllegalArgumentException("Dealer not found"));
        vehicle.setDealer(dealer);
        
        // Set tenantId manually since it's inherited from BaseEntity
        vehicle.setTenantId(tenantId);
        
        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle created: {} for tenant: {}", request.getModel(), tenantId);
        
        return mapToResponseDTO(saved);
    }
    
    /**
     * Get vehicle by ID with tenant validation
     */
    public VehicleResponseDTO getById(@NonNull Long id, Long tenantId) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        
        validateTenantAccess(tenantId, vehicle.getTenantId());
        return mapToResponseDTO(vehicle);
    }
    
    /**
     * Get all vehicles for tenant (full list, no pagination)
     */
    @Cacheable(value = "vehicles_all", key = "#tenantId")
    public List<VehicleResponseDTO> getAllByTenant(Long tenantId) {
        return vehicleRepository.findByTenantIdAndAvailableTrue(tenantId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }
    
    /**
     * Find vehicles with complex filters: model, status, price range, subscription (full list)
     */
    public List<VehicleResponseDTO> findByFilters(Long tenantId,
                                                    String model,
                                                    Vehicle.VehicleStatus status,
                                                    BigDecimal priceMin,
                                                    BigDecimal priceMax,
                                                    String subscription) {
        
        // If subscription is PREMIUM, filter only premium vehicles
        if ("PREMIUM".equalsIgnoreCase(subscription)) {
            BigDecimal minPrice = priceMin != null ? priceMin : BigDecimal.ZERO;
            return vehicleRepository.findPremiumVehicles(tenantId, minPrice)
                    .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
        }
        
        // Otherwise, use general filters
        return vehicleRepository.findVehiclesByFilters(tenantId, model, status, priceMin, priceMax)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }
    
    /**
     * Get vehicles by status (full list, no pagination)
     */
    public List<VehicleResponseDTO> getByStatus(Long tenantId, Vehicle.VehicleStatus status) {
        return vehicleRepository.findByTenantIdAndStatusAndAvailableTrue(tenantId, status)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }
    
    /**
     * Get vehicles by dealer (full list, no pagination)
     */
    public List<VehicleResponseDTO> getByDealer(@NonNull Long dealerId, Long tenantId) {
        return vehicleRepository.findByDealerIdAndTenantIdAndAvailableTrue(dealerId, tenantId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }
    
    /**
     * Update vehicle
     */
    public VehicleResponseDTO update(@NonNull Long id, @NonNull VehicleUpdateDTO request, Long tenantId) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        
        validateTenantAccess(tenantId, vehicle.getTenantId());
        
        if (request.getModel() != null) vehicle.setModel(request.getModel());
        if (request.getManufacturer() != null) vehicle.setManufacturer(request.getManufacturer());
        if (request.getYear() != null) vehicle.setYear(request.getYear());
        if (request.getPrice() != null) vehicle.setPrice(request.getPrice());
        if (request.getStatus() != null) vehicle.setStatus(request.getStatus());
        if (request.getDescription() != null) vehicle.setDescription(request.getDescription());
        
        Vehicle updated = vehicleRepository.save(vehicle);
        log.info("Vehicle updated: {} for tenant: {}", id, tenantId);
        
        return mapToResponseDTO(updated);
    }
    
    /**
     * Delete vehicle (soft delete by setting available to false)
     */
    public void delete(@NonNull Long id, Long tenantId) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        
        validateTenantAccess(tenantId, vehicle.getTenantId());
        
        vehicle.setAvailable(false);
        vehicleRepository.save(vehicle);
        log.info("Vehicle deleted: {} for tenant: {}", id, tenantId);
    }
    
    /**
     * Validate tenant access to resource
     */
    private void validateTenantAccess(Long requestTenantId, Long resourceTenantId) {
        if (requestTenantId != null && requestTenantId == 0L) {
            return; // Global Admin override
        }
        if (requestTenantId == null || !requestTenantId.equals(resourceTenantId)) {
            throw new ForbiddenAccessException("Cross-tenant access denied");
        }
    }
    
    /**
     * Map Vehicle entity to response DTO
     */
    private VehicleResponseDTO mapToResponseDTO(Vehicle vehicle) {
        return VehicleResponseDTO.builder()
                .id(vehicle.getId())
                .model(vehicle.getModel())
                .manufacturer(vehicle.getManufacturer())
                .year(vehicle.getYear())
                .price(vehicle.getPrice())
                .status(vehicle.getStatus())
                .vin(vehicle.getVin())
                .description(vehicle.getDescription())
                .available(vehicle.getAvailable())
                .dealerId(vehicle.getDealer() != null ? vehicle.getDealer().getId() : null)
                .tenantId(vehicle.getTenantId())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
