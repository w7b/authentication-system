package com.smoothy.authentication.core.services;

import com.smoothy.authentication.adapters.inbound.dtos.in.RequestLoginDTO;
import com.smoothy.authentication.adapters.inbound.dtos.in.RequestRegisterDTO;
import com.smoothy.authentication.adapters.inbound.dtos.in.UserUpdateRequestDTO;
import com.smoothy.authentication.adapters.inbound.dtos.out.UserResponseDto;
import com.smoothy.authentication.adapters.mapper.UserMapper;
import com.smoothy.authentication.adapters.outbound.entities.RoleEntity;
import com.smoothy.authentication.adapters.outbound.entities.UserEntity;
import com.smoothy.authentication.adapters.outbound.entities.configs.RoleAssigner;
import com.smoothy.authentication.adapters.outbound.repositories.RoleRepository;
import com.smoothy.authentication.adapters.outbound.repositories.UserRepository;
import com.smoothy.authentication.core.services.config.UserCheck;
import com.smoothy.authentication.infrastructure.Exceptions.ValidationException;
import com.smoothy.authentication.infrastructure.security.v1.jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserCheck userValidator;
    private final JwtService jwtService;

    private static final Logger logger = LogManager.getLogger();


    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserCheck userValidator, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userValidator = userValidator;
        this.jwtService = jwtService;
    }




    public UserEntity registerUser(RequestRegisterDTO register) {

        if (userRepository.findByLogin(register.login()).isPresent()) {
            throw new ValidationException("User aldery exists", HttpStatus.BAD_REQUEST);
        }

        userValidator.registrationChecks(register);

        UserEntity newUser = userMapper.fromEntityToRequest(register);
        newUser.setPassword(passwordEncoder.encode(register.password()));
        UserEntity savedUser = userRepository.saveAndFlush(newUser);

        //Role
        RoleEntity role = new RoleEntity(RoleAssigner.ROLE_USER, savedUser.getUuid());
        roleRepository.saveAndFlush(role);

        return savedUser;
    }


    public UserEntity authenticateUser(RequestLoginDTO request) {

        UserEntity user = userRepository.findByLogin(request.login())
                .orElseThrow(() -> new ValidationException("Unknown user", HttpStatus.NOT_FOUND));

        userValidator.checkPasswordCorrect(request.password(), user.getPassword());

        return user;
    }

    @Transactional
    public UserResponseDto updateUser (UUID uuid, UserUpdateRequestDTO request) {

        UserEntity user = userRepository.findByUuid(uuid).orElseThrow(() ->
                new ValidationException("Unknown user", HttpStatus.NOT_FOUND)
        );

        userValidator.updateChecks(request);

        userMapper.userUpdate(request, user);

        if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty() ) {
             if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                logger.info("User Service > updateUser | #1 LOG ERROR: PASSWORDS EQUALS.");
                throw new ValidationException("Incorrect old password", HttpStatus.UNAUTHORIZED);
             }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        UserEntity savedUser = userRepository.save(user);

        return userMapper.fromEntityToResponseDTO(savedUser);
    }

}

//Classes da minha service estao passando UserEntity como tipo dela