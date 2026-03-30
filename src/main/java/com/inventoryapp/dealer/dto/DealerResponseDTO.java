package com.inventoryapp.dealer.dto;

import com.inventoryapp.dealer.entity.Dealer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for dealer response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerResponseDTO {
    
    private Long id;
    
    private String name;
    
    private String email;
    
    private Dealer.SubscriptionType subscriptionType;
    
    private Boolean active;
    
    private String contactPerson;
    
    private String phoneNumber;
    
    private String address;
    
    private Long tenantId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
