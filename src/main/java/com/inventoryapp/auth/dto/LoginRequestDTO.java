package com.inventoryapp.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login request.
 * 
 * Contains:
 * - username: User's username
 * - password: User's password (will be validated against encrypted password)
 * - tenantId: Tenant identifier for multi-tenancy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @NotBlank(message = "Email or Username is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
