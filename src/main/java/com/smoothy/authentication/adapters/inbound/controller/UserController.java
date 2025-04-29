package com.smoothy.authentication.adapters.inbound.controller;


import com.smoothy.authentication.adapters.inbound.dtos.in.RequestRegisterDTO;
import com.smoothy.authentication.adapters.inbound.dtos.out.ResponseRegisterDTO;
import com.smoothy.authentication.core.services.UserService;
import com.smoothy.authentication.infrastructure.security.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("register")
    public ResponseEntity<ResponseRegisterDTO> register(@Valid @RequestBody RequestRegisterDTO request) {
        ResponseRegisterDTO response = userService.register(request);
        return ResponseEntity.ok(response);
    }
}
