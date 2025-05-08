package com.smoothy.authentication.adapters.inbound.dtos.out;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String login,
        String email,
        Long phoneNumber
) {
    public UserResponseDto(UUID id, String login, String email, Long phoneNumber) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
