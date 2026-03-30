package com.inventoryapp.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base entity class for all domain entities in the system.
 * 
 * Provides common fields:
 * - Long auto-increment primary key
 * - Tenant identifier (String) for multi-tenancy support
 * - Audit timestamps (createdAt, updatedAt)
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * Tenant identifier for multi-tenancy support.
     * Maps to a BIGINT identifier.
     */
    @Column(name = "tenant_id", nullable = true, updatable = false)
    private Long tenantId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * PrePersist callback to automatically set tenant ID from context.
     */
    @PrePersist
    protected void onCreate() {
        if (this.tenantId == null) {
            this.tenantId = com.inventoryapp.common.tenant.TenantContext.getTenantId();
        }
    }
}
