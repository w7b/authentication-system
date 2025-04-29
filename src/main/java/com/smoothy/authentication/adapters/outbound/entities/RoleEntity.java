package com.smoothy.authentication.adapters.outbound.entities;

import com.smoothy.authentication.adapters.outbound.entities.configs.RoleAssigner;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "roles_tb")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleAssigner role;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;


    public RoleEntity(RoleAssigner role, UUID accountId) {
        this.role = role;
        this.accountId = accountId;
    }

    public RoleAssigner getRole() {
        return role;
    }
    public void setRole(RoleAssigner role) {
        this.role = role;
    }

    public UUID getAccountId() {
        return accountId;
    }
    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }
}
