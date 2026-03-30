package com.inventoryapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Application Class for Multi-Tenant Inventory Management System
 * 
 * Architecture: Modular Monolith with Clean Architecture principles
 * 
 * Key Features:
 * - Multi-tenancy with header-based tenant isolation (X-Tenant-Id)
 * - Role-based access control (USER, GLOBAL_ADMIN)
 * - RESTful APIs for Dealer and Vehicle management
 * - Production-grade error handling and logging
 */
@SpringBootApplication
@EnableJpaAuditing
public class InventoryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryManagementApplication.class, args);
    }
}
