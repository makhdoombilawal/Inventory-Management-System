package com.inventoryapp.auth.service;

import com.inventoryapp.auth.dto.LoginRequestDTO;
import com.inventoryapp.auth.dto.LoginResponseDTO;
import com.inventoryapp.auth.dto.RegisterRequestDTO;
import com.inventoryapp.auth.dto.AdminUserCreationDTO;
import com.inventoryapp.auth.dto.UserResponseDTO;
import com.inventoryapp.auth.entity.Role;
import com.inventoryapp.auth.entity.User;
import com.inventoryapp.auth.repository.RoleRepository;
import com.inventoryapp.auth.repository.UserRepository;
import com.inventoryapp.auth.security.jwt.JwtTokenProvider;
import com.inventoryapp.common.enums.UserRole;
import com.inventoryapp.common.exception.InvalidRequestException;
import com.inventoryapp.common.exception.ResourceNotFoundException;
import com.inventoryapp.common.exception.UnauthorizedAccessException;
import com.inventoryapp.common.tenant.entity.Tenant;
import com.inventoryapp.common.tenant.repository.TenantRepository;
import com.inventoryapp.dealer.entity.Dealer;
import com.inventoryapp.dealer.repository.DealerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication service handling login, registration, and user management.
 * 
 * Responsibilities:
 * - User authentication (login)
 * - User registration (USER role only)
 * - Admin user creation (ADMIN and GLOBAL_ADMIN roles)
 * - JWT token generation
 * - Password encryption
 * - Role validation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    /**
     * Authenticate user and generate JWT token.
     * 
     * @param loginRequest Login credentials (email, password)
     * @param tenantId Tenant ID from X-Tenant-Id header
     * @return Login response with JWT token and user details
     * @throws UnauthorizedAccessException if credentials are invalid
     */
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO loginRequest, Long tenantId) {
        log.info("Login attempt for user: {} in tenant: {}", loginRequest.getEmail(), tenantId);
        
        try {
            User user;
            if (tenantId == null) {
                // Global search if no tenant ID provided
                user = userRepository.findByEmail(loginRequest.getEmail())
                        .orElseThrow(() -> {
                            log.warn("Login failed: User {} not found globally", loginRequest.getEmail());
                            return new UnauthorizedAccessException("Invalid credentials");
                        });
            } else {
                // Search specifically in the provided tenant
                user = userRepository.findByEmailAndTenantId(loginRequest.getEmail(), tenantId)
                        .orElseThrow(() -> {
                            log.warn("Login failed: User {} not found in tenant {}", loginRequest.getEmail(), tenantId);
                            return new UnauthorizedAccessException("Invalid credentials");
                        });
            }
            
            // Check if user is active
            if (!user.getActive()) {
                throw new UnauthorizedAccessException("User account is inactive");
            }
            
            // Verify password (PasswordConverter handles decryption)
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                log.warn("Login failed: Password mismatch for user {} in tenant {}", loginRequest.getEmail(), tenantId);
                throw new UnauthorizedAccessException("Invalid credentials");
            }
            
            // Generate JWT token
            String token = jwtTokenProvider.generateToken(
                    user.getEmail(),
                    user.getId(),
                    user.getTenantId(),
                    user.getRole().getName()
            );
            
            log.info("Login successful for user: {} with role: {}", user.getEmail(), user.getRole().getName());
            
            return LoginResponseDTO.builder()
                    .token(token)
                    .email(user.getEmail())
                    .role(user.getRole().getName())
                    .tenantId(user.getTenantId())
                    .active(user.getActive())
                    .expiresIn(jwtExpiration)
                    .build();
                    
        } catch (UnauthorizedAccessException e) {
            log.warn("Login failed for user: {} - {}", loginRequest.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Login error for user: {}", loginRequest.getEmail(), e);
            throw new UnauthorizedAccessException("Authentication failed");
        }
    }
    
    /**
     * Register a new user with USER role only.
     * Public registration does NOT allow ADMIN or GLOBAL_ADMIN roles.
     * 
     * @param registerRequest Registration details (email, password)
     * @param tenantId Tenant ID from X-Tenant-Id header
     * @return User response DTO
     * @throws InvalidRequestException if email already exists or validation fails
     */
    @Transactional
    public UserResponseDTO register(RegisterRequestDTO registerRequest) {
        Long tenantId = registerRequest.getTenantId();
        
        // ENTERPRISE SAFETY: Block automated registration to the global admin tenant (0)
        if (tenantId != null && tenantId == 0L) {
            log.warn("Blocked registration attempt to global admin tenant: {}", registerRequest.getEmail());
            throw new InvalidRequestException("Registration not allowed for global admin tenant");
        }
        
        log.info("Registration attempt for user: {} in tenant: {}", 
                registerRequest.getEmail(), tenantId);
        
        // AUTO-GENERATE TENANT if not provided
        if (tenantId == null) {
            log.info("No tenant ID provided, creating new tenant for user: {}", registerRequest.getEmail());
            Tenant newTenant = Tenant.builder()
                    .name(registerRequest.getEmail().split("@")[0] + " Organization")
                    .status(Tenant.TenantStatus.ACTIVE)
                    .build();
            Tenant savedTenant = tenantRepository.save(java.util.Objects.requireNonNull(newTenant));
            tenantId = savedTenant.getId();
            log.info("Auto-generated tenant ID: {} for user: {}", tenantId, registerRequest.getEmail());
        }
        
        // Ensure the tenant (Dealer record) exists in the database
        ensureTenantExists(tenantId, registerRequest.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmailAndTenantId(
                registerRequest.getEmail(), 
                tenantId)) {
            throw new InvalidRequestException("Email already exists in this tenant");
        }
        
        // Fetch DEALER role entity
        Role userRole = roleRepository.findByName(UserRole.DEALER)
                .orElseThrow(() -> new ResourceNotFoundException("DEALER role not found"));
        
        // Create user with obtained role entity
        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(userRole)  // Set role entity
                .active(true)
                .build();
        
        // Set tenantId manually since it's inherited from BaseEntity
        user.setTenantId(tenantId);
        
        User savedUser = userRepository.save(user);
        
        log.info("User registered successfully: {} with role: USER", savedUser.getEmail());
        
        return mapToUserResponseDTO(savedUser);
    }
    
    /**
     * Create admin or global admin user.
     * Only accessible by GLOBAL_ADMIN role.
     * Validates that role is either ADMIN or GLOBAL_ADMIN.
     * 
     * @param adminUserDTO Admin user creation details (email, password, role)
     * @param tenantId Tenant ID from X-Tenant-Id header
     * @return User response DTO
     * @throws InvalidRequestException if validation fails
     */
    @Transactional
    public UserResponseDTO createAdminUser(AdminUserCreationDTO adminUserDTO) {
        Long tenantId = adminUserDTO.getTenantId();
        log.info("Admin user creation attempt: {} with role: {} in tenant: {}", 
                adminUserDTO.getEmail(), adminUserDTO.getRole(), tenantId);
        
        // Ensure the tenant (Dealer record) exists in the database
        ensureTenantExists(tenantId, adminUserDTO.getEmail());
        
        // Validate role is ADMIN or GLOBAL_ADMIN
        if (adminUserDTO.getRole() != UserRole.ADMIN && 
            adminUserDTO.getRole() != UserRole.GLOBAL_ADMIN) {
            throw new InvalidRequestException("Invalid role. Only ADMIN or GLOBAL_ADMIN allowed");
        }
        
        // Check if user already exists
        if (userRepository.existsByEmailAndTenantId(
                adminUserDTO.getEmail(), 
                tenantId)) {
            throw new InvalidRequestException("Email already exists in this tenant");
        }
        
        // Fetch specified role entity
        Role targetRole = roleRepository.findByName(adminUserDTO.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + adminUserDTO.getRole()));
        
        // Create admin user
        User user = User.builder()
                .email(adminUserDTO.getEmail())
                .password(passwordEncoder.encode(adminUserDTO.getPassword()))
                .role(targetRole)
                .active(true)
                .build();
        
        // Set tenantId manually since it's inherited from BaseEntity
        user.setTenantId(tenantId);
        
        User savedUser = userRepository.save(user);
        
        log.info("Admin user created successfully: {} with role: {}", 
                savedUser.getEmail(), savedUser.getRole());
        
        return mapToUserResponseDTO(savedUser);
    }
    
    /**
     * Enterprise: Ensures a tenant has at least one organizational record (Dealer).
     * Creates a default Dealer record if none exists.
     */
    private void ensureTenantExists(Long tenantId, String email) {
        if (tenantId == null || tenantId == 0L) {
            return; // Global admin tenant is managed during bootstrap
        }
 
        if (!dealerRepository.existsByTenantId(tenantId)) {
            log.info("Configuring new tenant: {} with owner: {}", tenantId, email);
            
            Dealer defaultDealer = Dealer.builder()
                    .name(tenantId + " Organization")
                    .email(email)
                    .subscriptionType(Dealer.SubscriptionType.BASIC)
                    .active(true)
                    .contactPerson("Pending Setup")
                    .build();
            
            defaultDealer.setTenantId(tenantId);
            dealerRepository.save(defaultDealer);
            
            log.info("Created default organizational record for tenant: {}", tenantId);
        }
    }
    
    /**
     * Get profile for the currently authenticated user.
     *
     * @param email    Email extracted from JWT (principal)
     * @param tenantId Tenant ID extracted from JWT (details)
     * @return User profile response DTO
     * @throws ResourceNotFoundException if user no longer exists
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getProfile(String email, Long tenantId) {
        User user = userRepository.findByEmailAndTenantId(email, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserResponseDTO(user);
    }

    /**
     * Map User entity to UserResponseDTO
     */
    private UserResponseDTO mapToUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .tenantId(user.getTenantId())
                .active(user.getActive())
                .build();
    }
}
