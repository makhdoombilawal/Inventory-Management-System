package com.inventoryapp.auth.entity;

import com.inventoryapp.common.converter.PasswordConverter;
import com.inventoryapp.common.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Collections;

/**
 * User entity representing system users (dealers, tenants, admins).
 * 
 * Extends BaseEntity for:
 * - Long AUTO_INCREMENT primary key
 * - Tenant ID for multi-tenancy support
 * - Audit timestamps (createdAt, updatedAt)
 * 
 * Password is encrypted at database level using PasswordConverter.
 * Username must be unique per tenant.
 */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = { "email", "tenant_id" }), indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_user_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_user_role", columnList = "role_id"),
        @Index(name = "idx_email_tenant", columnList = "email,tenant_id"),
        @Index(name = "idx_user_active", columnList = "active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity implements UserDetails {

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Convert(converter = PasswordConverter.class)
    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active != null && active;
    }
}
