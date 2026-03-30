package com.inventoryapp.vehicle.dto;

import com.inventoryapp.vehicle.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO for vehicle creation request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDTO {
    
    private Long id;
    
    @NotNull(message = "Dealer ID is required")
    private Long dealerId;
    
    @NotBlank(message = "Model is required")
    private String model;
    
    private String manufacturer;
    
    private String year;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Status is required")
    private Vehicle.VehicleStatus status;
    
    private String vin;
    
    private String description;
}
