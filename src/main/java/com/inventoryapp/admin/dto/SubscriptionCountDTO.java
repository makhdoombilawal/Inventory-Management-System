package com.inventoryapp.admin.dto;

import com.inventoryapp.dealer.entity.Dealer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for subscription count statistics
 * Used in global admin operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionCountDTO implements java.io.Serializable {
    
    private Dealer.SubscriptionType subscriptionType;
    
    private Long count;
}
