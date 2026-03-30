package com.inventoryapp.dealer.entity;

import com.inventoryapp.common.converter.EmailConverter;
import com.inventoryapp.common.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * Dealer entity representing vehicle dealers
 * Email is encrypted for security
 * Extends BaseEntity for automatic tenantId and timestamps
 */
@Entity
@Table(name = "dealers",
    indexes = {
        @Index(name = "idx_dealer_tenant", columnList = "tenant_id"),
        @Index(name = "idx_dealer_subscription", columnList = "subscription_type"),
        @Index(name = "idx_dealer_active", columnList = "active")
    })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Dealer extends BaseEntity {
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Convert(converter = EmailConverter.class)
    @Column(name = "email", nullable = false, columnDefinition = "LONGTEXT")
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false, length = 50)
    private SubscriptionType subscriptionType;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    @Column(nullable = false, length = 255)
    private String contactPerson;
    
    @Column(length = 20)
    private String phoneNumber;
    
    @Column(columnDefinition = "LONGTEXT")
    private String address;
    
    public enum SubscriptionType {
        BASIC, PREMIUM, ENTERPRISE
    }
}
