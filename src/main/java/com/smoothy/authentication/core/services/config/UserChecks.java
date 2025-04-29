package com.smoothy.authentication.core.services.config;

import com.smoothy.authentication.adapters.inbound.dtos.in.RequestRegisterDTO;
import com.smoothy.authentication.infrastructure.Exceptions.ValidationException;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserChecks {

    public void registrationChecks(RequestRegisterDTO request) {
        if (request.login() == null || request.login().length() < 3 || request.login().length() > 32) {
            throw new ValidationException("Username must be between 3 and 32 characters");
        }

        if (request.password() == null || request.password().length() < 8 || request.password().length() > 64) {
            throw new ValidationException("Password must be between 8 and 64 characters");
        }
        if (request.email() == null || !request.email().contains("@")) {
            throw new ValidationException("Invalid email format");
        }
        if (request.phoneNumber() == null || request.phoneNumber() < 13) {
            throw new ValidationException("Phone number must have at least 13 digits");
        }
    }
}
