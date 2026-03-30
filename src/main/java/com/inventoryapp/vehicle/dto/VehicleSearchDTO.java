package com.inventoryapp.vehicle.dto;

import com.inventoryapp.vehicle.entity.Vehicle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Filter DTO for searching vehicles with a request body.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Vehicle Search Filter Request Body")
public class VehicleSearchDTO {

    @Schema(description = "Vehicle model (partial match)", example = "Corolla")
    private String model;

    @Schema(description = "Vehicle status (AVAILABLE, SOLD, UNDER_MAINTENANCE, RESERVED)", example = "AVAILABLE")
    private Vehicle.VehicleStatus status;

    @Schema(description = "Minimum price", example = "15000")
    private BigDecimal priceMin;

    @Schema(description = "Maximum price", example = "50000")
    private BigDecimal priceMax;

    @Schema(description = "Dealer ID filter", example = "1")
    private Long dealerId;

    @Schema(description = "Dealer subscription filter (BASIC, PREMIUM, ENTERPRISE)", example = "PREMIUM")
    private String subscription;
}
