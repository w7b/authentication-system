package com.smoothy.authentication.adapters.inbound.dtos.out;

import com.smoothy.authentication.adapters.inbound.dtos.in.RequestRegisterDTO;

public record ResponseRegisterDTO(
        String access_token,
        RequestRegisterDTO register
        ) {}
