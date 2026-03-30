package com.inventoryapp.auth.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Custom authentication principal storing user identity securely.
 * This class is embedded into the SecurityContext upon successful JWT validation.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthUser {
    private Long id;
    private String email;
    private String role;
    private Long tenantId;
}
