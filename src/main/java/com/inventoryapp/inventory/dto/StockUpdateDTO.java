package com.inventoryapp.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating stock level via request body.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Stock Update Request Body")
public class StockUpdateDTO {

    @NotNull(message = "Dealer ID is required")
    @Schema(description = "Dealer ID", example = "1")
    private Long dealerId;

    @NotNull(message = "Vehicle ID is required")
    @Schema(description = "Vehicle ID", example = "1")
    private Long vehicleId;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Schema(description = "New stock quantity", example = "10")
    private Integer quantity;
}
