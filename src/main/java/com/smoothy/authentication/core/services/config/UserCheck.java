package com.smoothy.authentication.core.services.config;

import com.smoothy.authentication.adapters.inbound.dtos.in.RequestRegisterDTO;
import com.smoothy.authentication.adapters.inbound.dtos.in.UserUpdateRequestDTO;
import com.smoothy.authentication.adapters.mapper.UserMapper;
import com.smoothy.authentication.adapters.outbound.entities.UserEntity;
import com.smoothy.authentication.adapters.outbound.repositories.UserRepository;
import com.smoothy.authentication.infrastructure.Exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@Slf4j
public class UserCheck {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LogManager.getLogger();


    public UserCheck(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    public void registrationChecks(RequestRegisterDTO request) {
        if (request.login() == null  || request.login().length() < 3 || request.login().length() > 32 ) {
            throw new ValidationException("Username must be between 3 and 32 characters", HttpStatus.BAD_REQUEST);
        }

        if (request.password() == null || request.password().length() < 8 || request.password().length() > 64) {
            throw new ValidationException("Password must be between 8 and 64 characters", HttpStatus.BAD_REQUEST);
        }
        if (request.email() == null || !request.email().contains("@")) {
            throw new ValidationException("Invalid email format", HttpStatus.BAD_REQUEST);
        }
        if (request.phoneNumber() == null || request.phoneNumber() < 13) {
            throw new ValidationException("Phone number must have at least 13 digits", HttpStatus.BAD_REQUEST);
        }


         if (userRepository.findByLogin(request.login()).isPresent()) {
             logger.info("#LOG-1 UserValidations > registrationChecks - login.isPresent() check attempt");
             throw new ValidationException("Username already exists", HttpStatus.CONFLICT);
         }
         if (userRepository.findByEmail(request.email()).isPresent()) {
             logger.info("#LOG-2 UserValidations > registrationChecks - email.isPresent() check attempt");
             throw new ValidationException("Email already exists", HttpStatus.CONFLICT);
         }
         if (userRepository.findByPhoneNumber(request.phoneNumber()).isPresent()) {
             logger.info("#LOG-3 UserValidations > registrationChecks - phoneNumber.isPresent() check attempt");
             throw new ValidationException("phone number already exists", HttpStatus.CONFLICT);
         }
    }

    public void updateChecks(UserUpdateRequestDTO request) {
        if (request.getLogin() != null && !request.getLogin().trim().isEmpty()) {
            Optional<UserEntity> existingUserWithLogin = userRepository.findByLogin(request.getLogin());

            if (existingUserWithLogin.isPresent() && !existingUserWithLogin.get().getUuid().equals(request.getUuid())) {
                logger.info("#LOG-1 UserValidations > updateChecks - login already exists for DIFFERENT user");
                throw new ValidationException("Username already exists", HttpStatus.CONFLICT);
            }
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            Optional<UserEntity> existingUserWithEmail = userRepository.findByEmail(request.getEmail());

            if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getUuid().equals(request.getUuid())) {
                logger.info("#LOG-2 UserValidations > updateChecks - email already exists for DIFFERENT user");
                throw new ValidationException("Email already exists", HttpStatus.CONFLICT);
            }
        }
        if (request.getPhoneNumber() != null) {
            Optional<UserEntity> existingUserWithPhone = userRepository.findByPhoneNumber(request.getPhoneNumber());
            if (existingUserWithPhone.isPresent() && !existingUserWithPhone.get().getUuid().equals(request.getUuid())) {
                logger.info("#LOG-3 UserValidations > updateChecks - phone number already exists for DIFFERENT user");
                throw new ValidationException("phone number already exists", HttpStatus.CONFLICT);
            }
        }

    }

    public void checkPasswordCorrect(String password, String encodedPassword) {
        logger.info("Senha antiga recebida na request (request.getPassword()): {}", password); // Logue a senha que veio na request
        logger.info("Hash da senha armazenada no banco (user.getPassword()): {}", encodedPassword); // Logue o hash do banco

        if (!passwordEncoder.matches(password, encodedPassword)) {
            logger.info("Caiu aqui!");
            throw new ValidationException("Invalid password", HttpStatus.UNAUTHORIZED);
        }
    }
}
