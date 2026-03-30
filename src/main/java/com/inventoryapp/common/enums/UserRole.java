package com.inventoryapp.common.enums;

/**
 * User roles for authorization
 * USER - Standard user with basic access
 * ADMIN - Tenant admin with elevated privileges
 * GLOBAL_ADMIN - System-wide administrator
 */
public enum UserRole {
    USER,
    ADMIN,
    GLOBAL_ADMIN,
    DEALER
}
