package com.inventoryapp.order.entity;

import com.inventoryapp.common.domain.BaseEntity;
import com.inventoryapp.dealer.entity.Dealer;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity for multi-tenant vehicle ordering.
 */
@Entity(name = "VehicleOrder")
@Table(name = "vehicle_orders", indexes = {
    @Index(name = "idx_order_dealer", columnList = "dealer_id"),
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_tenant", columnList = "tenant_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VehicleOrderItem> items = new ArrayList<>();

    @PrePersist
    public void onPersist() {
        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
    }

    public void addItem(VehicleOrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
