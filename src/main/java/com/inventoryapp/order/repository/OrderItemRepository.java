package com.inventoryapp.order.repository;

import com.inventoryapp.order.entity.VehicleOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<VehicleOrderItem, Long> {

    /**
     * Find order items by order ID and tenant.
     */
    List<VehicleOrderItem> findByOrderIdAndTenantId(Long orderId, Long tenantId);
}
