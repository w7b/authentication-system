package com.smoothy.authentication.adapters.inbound.dtos.out;

public record ResponseOAuthUser(
        String avatar_url,
        String accountId,
        String email,
        String name,
        String locale,
        String provider
) {


}
