package com.inventoryapp.vehicle.dto;

import com.inventoryapp.vehicle.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * DTO for vehicle update request (all fields optional)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleUpdateDTO {
    
    @jakarta.validation.constraints.NotNull(message = "Vehicle ID is required for update")
    private Long id;
    
    private String model;
    
    private String manufacturer;
    
    private String year;
    
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    private Vehicle.VehicleStatus status;
    
    private String description;
}
