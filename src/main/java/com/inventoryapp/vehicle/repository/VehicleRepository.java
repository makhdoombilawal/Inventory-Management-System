package com.inventoryapp.vehicle.repository;

import com.inventoryapp.vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Vehicle entity with complex filtering and tenant-scoped queries
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    /**
     * Find all vehicles for tenant (no pagination)
     */
    List<Vehicle> findByTenantIdAndAvailableTrue(Long tenantId);
    
    /**
     * Find vehicles by status for tenant (no pagination)
     */
    List<Vehicle> findByTenantIdAndStatusAndAvailableTrue(
            Long tenantId,
            Vehicle.VehicleStatus status);
    

    
    @Query("SELECT v FROM Vehicle v WHERE v.tenantId = :tenantId AND v.available = true " +
            "AND (:model IS NULL OR LOWER(v.model) LIKE LOWER(CONCAT('%', CAST(:model as string), '%'))) " +
            "AND (:status IS NULL OR v.status = :status) " +
            "AND (:priceMin IS NULL OR v.price >= :priceMin) " +
            "AND (:priceMax IS NULL OR v.price <= :priceMax)")
    List<Vehicle> findVehiclesByFilters(
            @Param("tenantId") Long tenantId,
            @Param("model") String model,
            @Param("status") Vehicle.VehicleStatus status,
            @Param("priceMin") BigDecimal priceMin,
            @Param("priceMax") BigDecimal priceMax);
    
    @Query("SELECT v FROM Vehicle v JOIN v.dealer d " +
            "WHERE v.tenantId = :tenantId AND v.available = true " +
            "AND d.subscriptionType = 'PREMIUM' " +
            "AND v.price >= :minPrice")
    List<Vehicle> findPremiumVehicles(
            @Param("tenantId") Long tenantId,
            @Param("minPrice") BigDecimal minPrice);
    
    /**
     * Find vehicles by dealer (no pagination)
     */
    List<Vehicle> findByDealerIdAndTenantIdAndAvailableTrue(
            Long dealerId,
            Long tenantId);
    
    /**
     * Count vehicles by status for tenant
     */
    long countByTenantIdAndStatus(Long tenantId, Vehicle.VehicleStatus status);
    
    /**
     * Count all available vehicles for tenant
     */
    long countByTenantIdAndAvailableTrue(Long tenantId);
    
    /**
     * Find vehicle by VIN and tenant
     */
    Optional<Vehicle> findByVinAndTenantIdAndAvailableTrue(String vin, Long tenantId);
}
