package com.smoothy.authentication.core.services;

import com.smoothy.authentication.adapters.inbound.dtos.in.RequestRegisterDTO;
import com.smoothy.authentication.adapters.inbound.dtos.out.ResponseRegisterDTO;
import com.smoothy.authentication.adapters.mapper.UserMapper;
import com.smoothy.authentication.adapters.outbound.entities.RoleEntity;
import com.smoothy.authentication.adapters.outbound.entities.UserEntity;
import com.smoothy.authentication.adapters.outbound.entities.configs.RoleAssigner;
import com.smoothy.authentication.adapters.outbound.repositories.RoleRepository;
import com.smoothy.authentication.adapters.outbound.repositories.UserRepository;
import com.smoothy.authentication.core.services.config.UserChecks;
import com.smoothy.authentication.infrastructure.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserChecks userChecks;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder encoder, JwtService jwtService, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserChecks userChecks) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userChecks = userChecks;
    }

    public ResponseRegisterDTO register(RequestRegisterDTO register) {

        UserEntity user = new UserEntity(
                register.login(),
                passwordEncoder.encode(register.password()),
                register.email(),
                register.phoneNumber()
        );

        userChecks.registrationChecks(register);
        userMapper.EntityToResponse(userRepository.save(user));

        RoleEntity role = new RoleEntity(RoleAssigner.ROLE_USER, user.getUuid());
        roleRepository.save(role);

        String access_token = jwtService.generateToken(user.getEmail(), user.getLogin());

        return new ResponseRegisterDTO(access_token, new RequestRegisterDTO(
                register.login(),
                passwordEncoder.encode(register.password()),
                register.email(),
                register.phoneNumber()));
    }

}
