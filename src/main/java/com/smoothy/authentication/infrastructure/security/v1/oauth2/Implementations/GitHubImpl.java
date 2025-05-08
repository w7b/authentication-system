package com.smoothy.authentication.infrastructure.security.v1.oauth2.Implementations;

import com.smoothy.authentication.infrastructure.security.v1.oauth2.repository.iOAuthRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GitHubImpl implements iOAuthRepository {
    private final OAuth2User user;

    public GitHubImpl(OAuth2User user) {
        this.user = user;
    }

    @Override
    public String getAvatar_url() {
        return (String) user.getAttribute("avatar_url");
    }

    @Override
    public String getAccountId() {
        Object id = user.getAttribute("id");
        return id != null ? String.valueOf(id) : null;

    }

    @Override
    public String getEmail() {
        return (String)  user.getAttribute("email");
    }

    @Override
    public String getName() {
        return (String)  user.getAttribute("name");
    }

    @Override
    public String getLocale() {
        return (String)  user.getAttribute("location");
    }

}
