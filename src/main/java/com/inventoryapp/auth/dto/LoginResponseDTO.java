package com.inventoryapp.auth.dto;

import com.inventoryapp.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login response.
 * 
 * Contains:
 * - token: JWT access token
 * - username: Authenticated user's username
 * - role: User's role (USER, ADMIN, GLOBAL_ADMIN)
 * - tenantId: User's tenant ID
 * - expiresIn: Token expiration time in milliseconds
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    
    private String token;
    private String email;
    private UserRole role;
    private Long tenantId;
    private Boolean active;
    private Long expiresIn;
}
