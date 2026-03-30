package com.inventoryapp.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotNull(message = "Dealer ID is required")
    private Long dealerId;

    @NotNull(message = "Order items are required")
    private List<OrderItemRequestDTO> items;
}
