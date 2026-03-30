package com.inventoryapp.auth.dto;

import com.inventoryapp.common.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for admin user creation by global admin.
 * Includes choice of role (ADMIN, GLOBAL_ADMIN).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserCreationDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Role is required")
    private UserRole role;

    @NotNull(message = "Tenant ID is required")
    private Long tenantId;
}
