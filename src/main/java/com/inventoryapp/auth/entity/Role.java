package com.inventoryapp.auth.entity;

import com.inventoryapp.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

/**
 * Role entity for 3NF authorization management.
 * Possible values: USER, ADMIN, GLOBAL_ADMIN, DEALER
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private UserRole name;

    @Column(name = "description")
    private String description;
}
