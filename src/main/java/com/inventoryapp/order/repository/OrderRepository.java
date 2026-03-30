package com.inventoryapp.order.repository;

import com.inventoryapp.order.entity.VehicleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<VehicleOrder, Long> {



    /**
     * Find all orders for a tenant.
     */
    List<VehicleOrder> findAllByTenantId(Long tenantId);

    /**
     * Find all orders for a dealer within a tenant.
     */
    List<VehicleOrder> findByDealerIdAndTenantId(Long dealerId, Long tenantId);

    /**
     * Find order by order number and tenant.
     */
    Optional<VehicleOrder> findByOrderNumberAndTenantId(String orderNumber, Long tenantId);
}
