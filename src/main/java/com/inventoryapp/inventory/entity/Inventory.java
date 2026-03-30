package com.inventoryapp.inventory.entity;

import com.inventoryapp.common.domain.BaseEntity;
import com.inventoryapp.dealer.entity.Dealer;
import com.inventoryapp.vehicle.entity.Vehicle;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Inventory entity linking Dealer and Vehicle for multi-tenant stock management.
 */
@Entity
@Table(name = "inventory", indexes = {
    @Index(name = "idx_inventory_vehicle", columnList = "vehicle_id"),
    @Index(name = "idx_inventory_dealer", columnList = "dealer_id"),
    @Index(name = "idx_inventory_tenant", columnList = "tenant_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "dealer"})
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dealer_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Dealer dealer;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
