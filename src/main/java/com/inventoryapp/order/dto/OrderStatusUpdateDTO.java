package com.inventoryapp.order.dto;

import com.inventoryapp.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating order status via request body.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order Status Update Request Body")
public class OrderStatusUpdateDTO {

    @NotNull(message = "Order ID is required")
    private Long id;

    @NotNull(message = "Order status is required")
    @Schema(description = "Payment and fulfillment status", example = "PAID")
    private OrderStatus status;
}
