package com.smoothy.authentication.infrastructure.security.v1.oauth2.repository;

public interface iOAuthRepository {
    public String getAvatar_url();
    public String getAccountId();
    public String getEmail();
    public String getName();
    public String getLocale();
}
