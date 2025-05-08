package com.smoothy.authentication.infrastructure.security.services;

import com.smoothy.authentication.adapters.outbound.entities.RoleEntity;
import com.smoothy.authentication.adapters.outbound.entities.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomerUserDetails implements UserDetails {

    private final UserEntity user;

    public UUID getUuid() { return user.getUuid(); }
    public CustomerUserDetails(UserEntity user) { this.user = user; }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (RoleEntity roleEntity : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(roleEntity.getRole().name()));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

}
