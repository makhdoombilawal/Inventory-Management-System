package com.inventoryapp.vehicle.dto;

import com.inventoryapp.vehicle.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for vehicle response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDTO {
    
    private Long id;
    
    private String model;
    
    private String manufacturer;
    
    private String year;
    
    private BigDecimal price;
    
    private Vehicle.VehicleStatus status;
    
    private String vin;
    
    private String description;
    
    private Boolean available;
    
    private Long dealerId;
    
    private Long tenantId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
