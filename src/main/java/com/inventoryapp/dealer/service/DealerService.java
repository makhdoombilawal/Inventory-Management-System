package com.inventoryapp.dealer.service;

import com.inventoryapp.common.exception.ForbiddenAccessException;
import com.inventoryapp.common.exception.ResourceNotFoundException;
import com.inventoryapp.dealer.dto.DealerRequestDTO;
import com.inventoryapp.dealer.dto.DealerResponseDTO;
import com.inventoryapp.dealer.dto.DealerUpdateDTO;
import com.inventoryapp.dealer.entity.Dealer;
import com.inventoryapp.dealer.repository.DealerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Dealer service for CRUD operations with tenant validation and email encryption
 */
@Service
@Slf4j
@Transactional
public class DealerService {
    
    private final DealerRepository dealerRepository;
    
    public DealerService(DealerRepository dealerRepository) {
        this.dealerRepository = dealerRepository;
    }
    
    /**
     * Create new dealer
     */
    public DealerResponseDTO create(@NonNull DealerRequestDTO request, Long tenantId) {
        try {
            Dealer dealer = Dealer.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .subscriptionType(request.getSubscriptionType())
                    .active(true)
                    .contactPerson(request.getContactPerson())
                    .phoneNumber(request.getPhoneNumber())
                    .address(request.getAddress())
                    .build();
            
            // Set tenantId manually since it's inherited from BaseEntity
            dealer.setTenantId(tenantId);
            
            Dealer saved = dealerRepository.save(dealer);
            log.info("Dealer created: {} for tenant: {}", request.getName(), tenantId);
            
            return mapToResponseDTO(saved);
        } catch (Exception e) {
            log.error("Failed to create dealer", e);
            throw new RuntimeException("Failed to create dealer", e);
        }
    }
    
    /**
     * Get dealer by ID with tenant validation
     */
    public DealerResponseDTO getById(@NonNull Long id, Long tenantId) {
        Dealer dealer = dealerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found"));
        
        validateTenantAccess(tenantId, dealer.getTenantId());
        return mapToResponseDTO(dealer);
    }
    
    /**
     * Get all dealers for tenant (full list, no pagination).
     */
    @Cacheable(value = "dealers_all", key = "#tenantId")
    public List<DealerResponseDTO> getAllByTenant(Long tenantId) {
        return dealerRepository.findByTenantIdAndActiveTrue(tenantId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }
    
    /**
     * Get dealers by subscription type (full list, no pagination).
     */
    public List<DealerResponseDTO> getBySubscriptionType(Long tenantId,
                                                         Dealer.SubscriptionType subscriptionType) {
        return dealerRepository.findByTenantIdAndSubscriptionTypeAndActiveTrue(tenantId, subscriptionType)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }
    
    /**
     * Update dealer
     */
    public DealerResponseDTO update(@NonNull Long dealerId, DealerUpdateDTO request, Long tenantId) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer not found"));
        
        validateTenantAccess(tenantId, dealer.getTenantId());
        
        try {
            if (request.getName() != null) dealer.setName(request.getName());
            if (request.getEmail() != null) {
                dealer.setEmail(request.getEmail());
            }
            if (request.getSubscriptionType() != null) {
                dealer.setSubscriptionType(request.getSubscriptionType());
            }
            if (request.getContactPerson() != null) dealer.setContactPerson(request.getContactPerson());
            if (request.getPhoneNumber() != null) dealer.setPhoneNumber(request.getPhoneNumber());
            if (request.getAddress() != null) dealer.setAddress(request.getAddress());
            
            Dealer updated = dealerRepository.save(dealer);
            log.info("Dealer updated: {} for tenant: {}", dealerId, tenantId);
            
            return mapToResponseDTO(updated);
        } catch (Exception e) {
            log.error("Failed to update dealer", e);
            throw new RuntimeException("Failed to update dealer", e);
        }
    }
    
    /**
     * Delete dealer (soft delete by setting active to false)
     */
    public void delete(@NonNull Long dealerId, Long tenantId) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer not found"));
        
        validateTenantAccess(tenantId, dealer.getTenantId());
        
        dealer.setActive(false);
        dealerRepository.save(dealer);
        log.info("Dealer deleted: {} for tenant: {}", dealerId, tenantId);
    }
    
    /**
     * Validate tenant access to resource
     */
    private void validateTenantAccess(Long requestTenantId, Long resourceTenantId) {
        if (requestTenantId != null && requestTenantId == 0L) {
            return; // Global Admin override
        }
        if (requestTenantId == null || !requestTenantId.equals(resourceTenantId)) {
            throw new ForbiddenAccessException("Cross-tenant access denied");
        }
    }
    
    /**
     * Map Dealer entity to response DTO
     */
    private DealerResponseDTO mapToResponseDTO(Dealer dealer) {
        return DealerResponseDTO.builder()
                .id(dealer.getId())
                .name(dealer.getName())
                .email(dealer.getEmail())
                .subscriptionType(dealer.getSubscriptionType())
                .active(dealer.getActive())
                .contactPerson(dealer.getContactPerson())
                .phoneNumber(dealer.getPhoneNumber())
                .address(dealer.getAddress())
                .tenantId(dealer.getTenantId())
                .createdAt(dealer.getCreatedAt())
                .updatedAt(dealer.getUpdatedAt())
                .build();
    }
}
