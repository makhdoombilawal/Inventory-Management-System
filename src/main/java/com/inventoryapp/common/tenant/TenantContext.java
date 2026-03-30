package com.inventoryapp.common.tenant;

import lombok.extern.slf4j.Slf4j;

/**
 * Thread-local context holder for tenant information.
 * 
 * This class manages tenant isolation by storing the current tenant ID (String)
 * in a ThreadLocal variable, ensuring that each request thread has its own
 * isolated tenant context.
 */
@Slf4j
public class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
        // Private constructor to prevent instantiation
    }

    /**
     * Sets the current tenant ID for the executing thread.
     */
    public static void setTenantId(Long tenantId) {
        log.debug("Setting tenant ID: {}", tenantId);
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Retrieves the current tenant ID for the executing thread.
     */
    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }

    /**
     * Clears the tenant context for the current thread.
     * CRITICAL: Must be called after each request to prevent memory leaks.
     */
    public static void clear() {
        log.debug("Clearing tenant context");
        CURRENT_TENANT.remove();
    }
}
