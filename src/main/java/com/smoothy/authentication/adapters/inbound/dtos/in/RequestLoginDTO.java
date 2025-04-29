package com.smoothy.authentication.adapters.inbound.dtos.in;

public record RequestLoginDTO
        (
                String login,
                String password
        ) {
}
