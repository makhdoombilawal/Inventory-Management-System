package com.inventoryapp.common.config;

import com.inventoryapp.auth.entity.Role;
import com.inventoryapp.auth.entity.User;
import com.inventoryapp.common.enums.UserRole;
import com.inventoryapp.auth.repository.RoleRepository;
import com.inventoryapp.auth.repository.UserRepository;
import com.inventoryapp.common.tenant.entity.Tenant;
import com.inventoryapp.common.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;
import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data initializer to seed initial Roles, Tenants, and Admin user.
 * Runs once on application startup.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting data initialization...");

        // 1. Seed Roles
        seedRole(UserRole.USER, "Standard system user");
        seedRole(UserRole.ADMIN, "Tenant administrator");
        seedRole(UserRole.GLOBAL_ADMIN, "Global system administrator");
        seedRole(UserRole.DEALER, "Dealer representative");

        // 1.5. Cleanup old system tenant if exists to resolve duplicates
        cleanupOldSystemTenant();

        // 2. Seed System Tenant (ID 0L)
        seedTenant(0L, "System Tenant");

        // 3. Seed Global Admin User
        seedGlobalAdmin();

        log.info("Data initialization completed.");
    }

    private void seedRole(UserRole userRole, String description) {
        if (roleRepository.findByName(userRole).isEmpty()) {
            Role role = Objects.requireNonNull(Role.builder()
                    .name(userRole)
                    .description(description)
                    .build());
            roleRepository.save(role);
            log.info("Seeded role: {}", userRole);
        }
    }

    private void seedTenant(@NonNull Long id, @NonNull String name) {
        if (tenantRepository.findById(id).isPresent()) {
            return;
        }
        
        log.info("Seeding new tenant: {} (ID: {})", name, id);
        if (id == 0L) {
            try {
                jdbcTemplate.execute("SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO'");
            } catch (Exception e) {
                // Ignore for non-MySQL databases like H2
            }
            jdbcTemplate.update("INSERT INTO tenants (id, name, status, created_at) VALUES (0, ?, 'ACTIVE', CURRENT_TIMESTAMP)", name);
        } else {
            tenantRepository.save(Objects.requireNonNull(Tenant.builder()
                    .name(name)
                    .status(Tenant.TenantStatus.ACTIVE)
                    .build()));
        }
    }

    private void seedGlobalAdmin() {
        String adminEmail = "admin@system.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            Role globalAdminRole = roleRepository.findByName(UserRole.GLOBAL_ADMIN)
                    .orElseThrow(() -> new RuntimeException("GLOBAL_ADMIN role not found"));

            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(globalAdminRole)
                    .active(true)
                    .build();
            admin.setTenantId(0L); // System tenant
            
            userRepository.save(admin);
            log.info("Seeded global admin user: {}", adminEmail);
        }
    }

    private void cleanupOldSystemTenant() {
        Optional<User> oldSystemAdmin = userRepository.findByEmailAndTenantId("admin@system.com", 0L);
        if (oldSystemAdmin.isPresent()) {
            log.info("Cleaning up old '0' tenant user");
            userRepository.delete(oldSystemAdmin.get());
        }
        if (tenantRepository.findById(0L).isPresent()) {
            log.info("Cleaning up old '0' tenant record");
            tenantRepository.deleteById(0L);
        }
        
        // Force flush to execute the DELETE before the upcoming INSERT
        // Hibernate queues INSERTs before DELETEs by default which causes a UK conflict
        userRepository.flush();
        tenantRepository.flush();
    }
}
