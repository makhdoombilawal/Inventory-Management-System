package com.inventoryapp.auth.repository;

import com.inventoryapp.auth.entity.User;
import com.inventoryapp.common.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * 
 * Provides methods for:
 * - Finding users by username and tenant
 * - Checking user existence
 * - Querying active users
 * - Role-based queries
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email and tenant ID.
     * Critical for authentication and tenant isolation.
     */
    Optional<User> findByEmailAndTenantId(String email, Long tenantId);

    /**
     * Check if user exists by email and tenant ID
     */
    boolean existsByEmailAndTenantId(String email, Long tenantId);

    /**
     * Find all active users for a tenant
     */
    List<User> findByTenantIdAndActiveTrue(Long tenantId);

    /**
     * Find all users by tenant ID
     */
    List<User> findByTenantId(Long tenantId);

    /**
     * Find users by role and tenant
     */
    List<User> findByRoleAndTenantId(UserRole role, Long tenantId);

    /**
     * Find all active users by role
     */
    List<User> findByRoleAndActiveTrue(UserRole role);

    /**
     * Count users by tenant
     */
    long countByTenantId(Long tenantId);

    /**
     * Count active users by tenant
     */
    long countByTenantIdAndActiveTrue(Long tenantId);

    /**
     * Find user by email (for global admin operations)
     * Use with caution - bypasses tenant isolation
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if GLOBAL_ADMIN exists
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.role.name = com.inventoryapp.common.enums.UserRole.GLOBAL_ADMIN AND u.active = true")
    boolean existsGlobalAdmin();
}
