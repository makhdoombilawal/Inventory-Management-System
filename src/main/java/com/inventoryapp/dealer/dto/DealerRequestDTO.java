package com.inventoryapp.dealer.dto;

import com.inventoryapp.dealer.entity.Dealer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for dealer creation request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerRequestDTO {
    
    private Long id;
    
    @NotBlank(message = "Dealer name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotNull(message = "Subscription type is required")
    private Dealer.SubscriptionType subscriptionType;
    
    @NotBlank(message = "Contact person is required")
    private String contactPerson;
    
    private String phoneNumber;
    
    private String address;
}
