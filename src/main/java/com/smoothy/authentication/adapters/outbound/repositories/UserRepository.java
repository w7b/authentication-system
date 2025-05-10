package com.smoothy.authentication.adapters.outbound.repositories;

import com.smoothy.authentication.adapters.outbound.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email); //for validations
    Optional<UserEntity> findByLogin(String login); //for find users
    Optional<UserEntity> findByPhoneNumber(Long phoneNumber); //for validations
    Optional<UserEntity> findByUuid(UUID uuid); //for updateUser validation
}
