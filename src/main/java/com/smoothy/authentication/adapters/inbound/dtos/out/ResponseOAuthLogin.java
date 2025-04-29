package com.smoothy.authentication.adapters.inbound.dtos.out;

public record ResponseOAuthLogin(
        String access_token,
        ResponseOAuthUser user
) {
}
