package com.inventoryapp.dealer.dto;

import com.inventoryapp.dealer.entity.Dealer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;

/**
 * DTO for dealer update request (all fields optional)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerUpdateDTO {
    
    @jakarta.validation.constraints.NotNull(message = "Dealer ID is required for update")
    private Long id;
    
    private String name;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private Dealer.SubscriptionType subscriptionType;
    
    private String contactPerson;
    
    private String phoneNumber;
    
    private String address;
}
