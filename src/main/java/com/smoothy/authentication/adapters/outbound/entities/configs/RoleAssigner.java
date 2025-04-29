package com.smoothy.authentication.adapters.outbound.entities.configs;

import java.util.Collections;
import java.util.List;

public enum RoleAssigner {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String role;

    RoleAssigner(String role){
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public List<String> asList() {
        return Collections.singletonList(role);
    }

    // Se quiser, pode criar algo para vários role, se necessário
    public static List<String> defaultRoles() {
        return List.of(ROLE_USER.role);
    }
}


