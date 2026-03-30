package com.inventoryapp.order.entity;

import com.inventoryapp.common.domain.BaseEntity;
import com.inventoryapp.vehicle.entity.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * OrderItem entity representing individual vehicles in an order.
 */
@Entity(name = "VehicleOrderItem")
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_item_order", columnList = "order_id"),
    @Index(name = "idx_order_item_vehicle", columnList = "vehicle_id"),
    @Index(name = "idx_order_item_tenant", columnList = "tenant_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleOrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private VehicleOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "sub_total", nullable = false)
    private BigDecimal subTotal;

    @PrePersist
    @PreUpdate
    public void calculateSubTotal() {
        if (this.unitPrice != null && this.quantity != null) {
            this.subTotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }
}
