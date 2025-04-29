package com.smoothy.authentication.infrastructure.security;

import com.smoothy.authentication.adapters.outbound.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityRepository extends JpaRepository<UserEntity, String> {

//    UserDetails findByLogin(String login);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByLogin(String login);

}
