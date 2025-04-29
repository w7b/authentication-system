package com.smoothy.authentication.adapters.outbound.repositories;

import com.smoothy.authentication.adapters.outbound.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, String> {
}
