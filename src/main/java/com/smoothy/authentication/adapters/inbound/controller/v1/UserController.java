package com.smoothy.authentication.adapters.inbound.controller.v1;


import com.smoothy.authentication.adapters.inbound.dtos.in.RequestLoginDTO;
import com.smoothy.authentication.adapters.inbound.dtos.in.RequestRegisterDTO;
import com.smoothy.authentication.adapters.inbound.dtos.in.UserUpdateRequestDTO;
import com.smoothy.authentication.adapters.inbound.dtos.out.UserResponseDto;
import com.smoothy.authentication.adapters.mapper.UserMapper;
import com.smoothy.authentication.adapters.outbound.entities.UserEntity;
import com.smoothy.authentication.adapters.outbound.repositories.UserRepository;
import com.smoothy.authentication.core.services.UserService;
import com.smoothy.authentication.infrastructure.security.v1.jwt.JwtService;
import com.smoothy.authentication.infrastructure.security.services.CustomerUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final Logger logger = LogManager.getLogger();

    @PostMapping("register")
    public ResponseEntity<UserResponseDto> registeredUser(@RequestBody RequestRegisterDTO request, HttpServletResponse response) {

        UserEntity registeredUser = userService.registerUser(request);

        String token = jwtService.generateToken(registeredUser);

        Cookie jwtCookie = jwtService.generateCookie(token);
        response.addCookie(jwtCookie);

        logger.info("User registered: {}", response.toString());

        UserResponseDto responseBody = userMapper.fromEntityToResponseDTO(registeredUser);

        return ResponseEntity.ok(responseBody);
    }

@PostMapping("login")
public ResponseEntity<?> authenticateUser(@RequestBody RequestLoginDTO request, HttpServletResponse response) {
    try {

        UserEntity authenticatedUser = userService.authenticateUser(request);

        String token = jwtService.generateToken(authenticatedUser);
        Cookie jwtCookie = jwtService.generateCookie(token);
        response.addCookie(jwtCookie);

        UserResponseDto responseDto = new UserResponseDto(
                authenticatedUser.getUuid(),
                authenticatedUser.getLogin(),
                authenticatedUser.getEmail(),
                authenticatedUser.getPhoneNumber()
        );

        return ResponseEntity.ok(responseDto);

    } catch (ValidationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
    }

}

    @PatchMapping("user/update/{uuid}")
    public ResponseEntity<UserResponseDto> updateAuthenticatedUser(
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            @RequestBody @Valid UserUpdateRequestDTO updateRequestDTO,
            @PathVariable UUID uuid
    ) {
        UUID id = userDetails.getUuid();

        UserResponseDto updatedUser = userService.updateUser(uuid , updateRequestDTO);

        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping("logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        userService.logoutUser(session);
        logger.info("User logged out");

        return ResponseEntity.ok().build();
    }

    @GetMapping("user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomerUserDetails userDetails) {
        if (userDetails == null) {
            logger.error("Endpoint /user chamado sem AuthenticationPrincipal injetado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required.");
        }

        return userService.selectCurrentUser(userDetails);
    }


}
