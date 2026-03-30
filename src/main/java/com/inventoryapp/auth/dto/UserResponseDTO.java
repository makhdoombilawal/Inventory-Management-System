package com.inventoryapp.auth.dto;

import com.inventoryapp.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user response.
 * 
 * Used when returning user information (excludes sensitive data like password).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String email;
    private UserRole role;
    private Long tenantId;
    private Boolean active;
}
