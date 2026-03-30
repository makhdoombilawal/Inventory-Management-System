package com.inventoryapp.admin.service;

import com.inventoryapp.admin.dto.SubscriptionCountDTO;
import com.inventoryapp.dealer.entity.Dealer;
import com.inventoryapp.dealer.repository.DealerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin service for global admin operations
 * Operations here are GLOBAL scope, not tenant-scoped
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class AdminService {
    
    private final DealerRepository dealerRepository;
    
    public AdminService(DealerRepository dealerRepository) {
        this.dealerRepository = dealerRepository;
    }
    
    /**
     * Count dealers by subscription type - GLOBAL SCOPE
     * This is an admin operation that counts across all tenants
     */
    public Map<String, SubscriptionCountDTO> countBySubscriptionGlobal() {
        Map<String, SubscriptionCountDTO> result = new HashMap<>();
        
        // Count for each subscription type across all tenants
        long basicCount = countBasicSubscriptions();
        long premiumCount = countPremiumSubscriptions();
        long enterpriseCount = countEnterpriseSubscriptions();
        
        result.put("BASIC", SubscriptionCountDTO.builder()
                .subscriptionType(Dealer.SubscriptionType.BASIC)
                .count(basicCount)
                .build());
        
        result.put("PREMIUM", SubscriptionCountDTO.builder()
                .subscriptionType(Dealer.SubscriptionType.PREMIUM)
                .count(premiumCount)
                .build());
        
        result.put("ENTERPRISE", SubscriptionCountDTO.builder()
                .subscriptionType(Dealer.SubscriptionType.ENTERPRISE)
                .count(enterpriseCount)
                .build());
        
        log.info("Global subscription count: BASIC={}, PREMIUM={}, ENTERPRISE={}", 
                basicCount, premiumCount, enterpriseCount);
        
        return result;
    }
    
    /**
     * Count basic subscription dealers
     */
    private long countBasicSubscriptions() {
        return dealerRepository.countBySubscriptionTypeAndActiveTrue(Dealer.SubscriptionType.BASIC);
    }
    
    /**
     * Count premium subscription dealers
     */
    private long countPremiumSubscriptions() {
        return dealerRepository.countBySubscriptionTypeAndActiveTrue(Dealer.SubscriptionType.PREMIUM);
    }
    
    /**
     * Count enterprise subscription dealers
     */
    private long countEnterpriseSubscriptions() {
        return dealerRepository.countBySubscriptionTypeAndActiveTrue(Dealer.SubscriptionType.ENTERPRISE);
    }
    
    /**
     * Get total number of dealers - GLOBAL SCOPE
     */
    public long getTotalDealersCount() {
        return dealerRepository.count();
    }
    
    /**
     * Get total number of active dealers - GLOBAL SCOPE
     */
    public long getActiveDealersCount() {
        return dealerRepository.countByActiveTrue();
    }
    
    /**
     * Count dealers by subscription for a SPECIFIC tenant
     */
    public Map<String, Long> countBySubscriptionForTenant(Long tenantId) {
        Map<String, Long> counts = new HashMap<>();
        for (Dealer.SubscriptionType type : Dealer.SubscriptionType.values()) {
            counts.put(type.name(), dealerRepository.countByTenantIdAndSubscriptionType(tenantId, type));
        }
        return counts;
    }
}
