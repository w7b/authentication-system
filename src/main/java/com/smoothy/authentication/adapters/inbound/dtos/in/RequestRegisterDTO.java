package com.smoothy.authentication.adapters.inbound.dtos.in;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequestRegisterDTO(
        @NotBlank @Size(min = 3, max = 32) String login,
        @NotBlank @Size(min = 8, max = 64) String password,
        @NotBlank @Size(min = 8, max = 64) @Email String email,
        Long phoneNumber
        ) {
}
