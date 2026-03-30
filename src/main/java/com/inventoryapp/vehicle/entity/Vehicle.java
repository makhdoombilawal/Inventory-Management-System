package com.inventoryapp.vehicle.entity;

import com.inventoryapp.common.domain.BaseEntity;
import com.inventoryapp.dealer.entity.Dealer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Vehicle entity representing vehicles in inventory
 * Extends BaseEntity for automatic tenantId and timestamps
 */
@Entity
@Table(name = "vehicles", indexes = {
        @Index(name = "idx_vehicle_tenant_status", columnList = "tenant_id,status"),
        @Index(name = "idx_vehicle_dealer_tenant", columnList = "dealer_id,tenant_id"),
        @Index(name = "idx_vehicle_model_tenant", columnList = "model,tenant_id"),
        @Index(name = "idx_vehicle_status", columnList = "status"),
        @Index(name = "idx_vehicle_vin", columnList = "vin")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Vehicle extends BaseEntity {
    
    @Column(nullable = false, length = 100)
    private String model;
    
    @Column(length = 50)
    private String manufacturer;
    
    @Column(length = 10)
    private String year;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VehicleStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;
    
    @Column(length = 50)
    private String vin;
    
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;
    
    public enum VehicleStatus {
        AVAILABLE, SOLD, UNDER_MAINTENANCE, RESERVED
    }
}
