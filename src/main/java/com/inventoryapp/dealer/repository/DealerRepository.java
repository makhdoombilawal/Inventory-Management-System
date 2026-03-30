package com.inventoryapp.dealer.repository;

import com.inventoryapp.dealer.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Dealer entity with tenant-scoped queries
 */
@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {
    
    /**
     * Find all active dealers for a tenant (no pagination)
     */
    List<Dealer> findByTenantIdAndActiveTrue(Long tenantId);
    
    /**
     * Find dealers by subscription type (no pagination)
     */
    List<Dealer> findByTenantIdAndSubscriptionTypeAndActiveTrue(
            Long tenantId,
            Dealer.SubscriptionType subscriptionType);
    
    /**
     * Find dealer by name and tenant
     */
    Optional<Dealer> findByNameAndTenantIdAndActiveTrue(String name, Long tenantId);
    
    /**
     * Count dealers by subscription type for a tenant
     */
    long countByTenantIdAndSubscriptionType(Long tenantId, Dealer.SubscriptionType subscriptionType);
    
    /**
     * Count all active dealers for a tenant
     */
    long countByTenantIdAndActiveTrue(Long tenantId);
 
    /**
     * Check if ANY dealer exists for the given tenant ID
     */
    boolean existsByTenantId(Long tenantId);
    


    /**
     * GLOBAL SCOPE: Count dealers by subscription across ALL tenants
     */
    long countBySubscriptionTypeAndActiveTrue(Dealer.SubscriptionType subscriptionType);

    /**
     * GLOBAL SCOPE: Count active dealers across ALL tenants
     */
    long countByActiveTrue();
}
