package com.inventoryapp.order.service;

import com.inventoryapp.common.exception.ForbiddenAccessException;
import com.inventoryapp.common.exception.ResourceNotFoundException;
import com.inventoryapp.dealer.entity.Dealer;
import com.inventoryapp.dealer.repository.DealerRepository;
import com.inventoryapp.order.dto.OrderItemRequestDTO;
import com.inventoryapp.order.dto.OrderItemResponseDTO;
import com.inventoryapp.order.dto.OrderRequestDTO;
import com.inventoryapp.order.dto.OrderResponseDTO;
import com.inventoryapp.order.entity.VehicleOrder;
import com.inventoryapp.order.entity.VehicleOrderItem;
import com.inventoryapp.order.entity.OrderStatus;
import com.inventoryapp.order.repository.OrderRepository;
import com.inventoryapp.vehicle.entity.Vehicle;
import com.inventoryapp.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for multi-tenant vehicle order management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final DealerRepository dealerRepository;
    private final VehicleRepository vehicleRepository;

    /**
     * Create a new order for a tenant.
     */
    @Transactional
    public OrderResponseDTO createOrder(@NonNull OrderRequestDTO request, Long tenantId) {
        log.info("Creating order for dealer {} in tenant {}", request.getDealerId(), tenantId);

        Dealer dealer = dealerRepository.findById(Objects.requireNonNull(request.getDealerId()))
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found"));
        if (tenantId != null && tenantId != 0L && !dealer.getTenantId().equals(tenantId)) {
            throw new ForbiddenAccessException("Cross-tenant access denied to dealer");
        }

        VehicleOrder order = VehicleOrder.builder()
                .dealer(dealer)
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();
        order.setTenantId(tenantId);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            Vehicle vehicle = vehicleRepository.findById(Objects.requireNonNull(itemRequest.getVehicleId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + itemRequest.getVehicleId()));
            if (tenantId != null && tenantId != 0L && !vehicle.getTenantId().equals(tenantId)) {
                throw new ForbiddenAccessException("Cross-tenant access denied to vehicle: " + itemRequest.getVehicleId());
            }

            VehicleOrderItem item = VehicleOrderItem.builder()
                    .vehicle(vehicle)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(vehicle.getPrice())
                    .build();
            item.setTenantId(tenantId);
            item.calculateSubTotal();

            order.addItem(item);
            total = total.add(item.getSubTotal());
        }

        order.setTotalAmount(total);
        VehicleOrder savedOrder = orderRepository.save(order);

        return mapToResponseDTO(savedOrder);
    }

    /**
     * Get order details by ID within a tenant.
     */
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrder(@NonNull Long id, Long tenantId) {
        VehicleOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (tenantId != null && tenantId != 0L && !order.getTenantId().equals(tenantId)) {
            throw new ForbiddenAccessException("Cross-tenant access denied to order");
        }
        return mapToResponseDTO(order);
    }

    /**
     * Get all orders for a tenant (full list, no pagination).
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrders(Long tenantId) {
        List<VehicleOrder> orders;
        if (tenantId != null && tenantId == 0L) {
            orders = orderRepository.findAll(); // Global Admin view
        } else {
            orders = orderRepository.findAllByTenantId(tenantId);
        }
        return orders.stream()
                .map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    /**
     * Update order status.
     */
    @Transactional
    public OrderResponseDTO updateStatus(@NonNull Long id, @NonNull OrderStatus status, Long tenantId) {
        VehicleOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (tenantId != null && tenantId != 0L && !order.getTenantId().equals(tenantId)) {
            throw new ForbiddenAccessException("Cross-tenant access denied to order");
        }
        
        order.setStatus(status);
        return mapToResponseDTO(orderRepository.save(order));
    }

    private OrderResponseDTO mapToResponseDTO(VehicleOrder order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .dealerId(order.getDealer().getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .tenantId(order.getTenantId())
                .items(order.getItems().stream().map(this::mapToItemDTO).collect(Collectors.toList()))
                .build();
    }

    private OrderItemResponseDTO mapToItemDTO(VehicleOrderItem item) {
        return OrderItemResponseDTO.builder()
                .id(item.getId())
                .vehicleId(item.getVehicle().getId())
                .vehicleModel(item.getVehicle().getModel())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subTotal(item.getSubTotal())
                .build();
    }
}
