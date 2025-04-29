package com.smoothy.authentication.adapters.outbound.repositories;

import com.smoothy.authentication.adapters.outbound.entities.OAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthRepository extends JpaRepository<OAuthEntity, String> {
    Optional<OAuthEntity> findByEmail(String email);
    Optional<OAuthEntity> findByAccountIdAndProvider(String accountId, String provider);


}
