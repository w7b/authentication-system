package com.smoothy.authentication.infrastructure.security.services;

import com.smoothy.authentication.adapters.outbound.entities.UserEntity;
import com.smoothy.authentication.adapters.outbound.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailService implements UserDetailsService {


    private final UserRepository uRepository;

    public CustomerUserDetailService(UserRepository uRepository) {
        this.uRepository = uRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        UserEntity user = uRepository.findByLogin(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + identifier));

        return new CustomerUserDetails(user);
    }


}
