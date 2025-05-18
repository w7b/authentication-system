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
import com.smoothy.authentication.infrastructure.security.services.CustomerUserDetails;
import com.smoothy.authentication.infrastructure.security.v1.jwt.JwtService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public void logoutUser(HttpSession session) {

        if(session != null) {
            session.invalidate();
            logger.info("User Service > logoutUser | #1 LOG OUT. {}", session.getId());
        }
    }

    public ResponseEntity<?> selectCurrentUser(CustomerUserDetails userDetails) {
        String username = attemptGetUsername(userDetails, 2);

        if (username == null) {
            logger.warn("Falha ao obter username após tentativas.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unable to extract username.");
        }

        try {
            UserEntity user = userRepository.findByLogin(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado no DB"));

            UserResponseDto infoResponse = new UserResponseDto(
                    user.getUuid(),
                    user.getLogin(),
                    user.getEmail(),
                    user.getPhoneNumber()
            );

            return ResponseEntity.ok(infoResponse);

        } catch (UsernameNotFoundException e) {
            logger.warn("Usuário autenticado não encontrado no banco: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found in database.");
        } catch (ValidationException e) {
            logger.trace("ValidationException ao obter usuário: {}", e.getMessage());
            HttpStatus status = e.getCause() != null ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.valueOf(e.getCause().getMessage());
            return ResponseEntity.status(status).body("Error getting current user info: " + e.getMessage());
        }
    }

    private String attemptGetUsername(UserDetails userDetails, int attempts) {
        for (int i = 0; i < attempts; i++) {
            String username = userDetails.getUsername();
            if (username != null) {
                return username;
            }
        }
        return null;
    }

}





