package com.inventoryapp.inventory.repository;

import com.inventoryapp.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {



    @Query("SELECT i FROM Inventory i WHERE i.tenantId = :tenantId")
    List<Inventory> findAllByTenantId(@Param("tenantId") Long tenantId);



    @Query("SELECT i FROM Inventory i WHERE i.dealer.id = :dealerId AND i.tenantId = :tenantId")
    List<Inventory> findByDealerIdAndTenantId(@Param("dealerId") Long dealerId, @Param("tenantId") Long tenantId);

    @Query("SELECT i FROM Inventory i WHERE i.vehicle.id = :vehicleId AND i.dealer.id = :dealerId AND i.tenantId = :tenantId")
    Optional<Inventory> findByVehicleIdAndDealerIdAndTenantId(@Param("vehicleId") Long vehicleId, @Param("dealerId") Long dealerId, @Param("tenantId") Long tenantId);
}
