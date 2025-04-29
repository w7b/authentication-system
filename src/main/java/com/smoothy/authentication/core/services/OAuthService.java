package com.smoothy.authentication.core.services;

import com.smoothy.authentication.adapters.inbound.dtos.out.ResponseOAuthUser;
import com.smoothy.authentication.adapters.mapper.OAuthMapper;
import com.smoothy.authentication.adapters.outbound.entities.OAuthEntity;
import com.smoothy.authentication.adapters.outbound.repositories.OAuthRepository;
import com.smoothy.authentication.infrastructure.security.oauth2.Implementations.GitHubImpl;
import com.smoothy.authentication.infrastructure.security.oauth2.Implementations.GoogleImpl;
import com.smoothy.authentication.infrastructure.security.oauth2.repository.iOAuthRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuthService {

    private final OAuthRepository repository;
    private BCryptPasswordEncoder encoder;
    private final OAuthMapper mapper;

    public OAuthService(OAuthRepository repository, OAuthMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    public ResponseOAuthUser responseOAuthUser(Authentication authentication, String accessToken) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        String provider = oauthToken.getAuthorizedClientRegistrationId().toLowerCase();

        iOAuthRepository impl = switch (provider) {
            case "google" -> new GoogleImpl(oAuth2User); // OIDC - sub, email, name, picture
            case "github" -> new GitHubImpl(oAuth2User); // GitHub - id, login, email, avatar_url
            default -> throw new IllegalArgumentException("OAuth2 provider não suportado: " + provider);
        };

        ResponseOAuthUser response = mapper.toResponse(impl, provider);

        repository.findByAccountIdAndProvider(response.accountId(), response.provider())
                .ifPresentOrElse(
                        existing -> updateOAuthEntity(existing, response),
                        () -> repository.save(fromOAuth(response))
                );

        return response;
    }

    private void updateOAuthEntity(OAuthEntity existing, ResponseOAuthUser response) {
        existing.setAvatar_url(response.avatar_url());
        existing.setAccountId(response.accountId());
        existing.setEmail(response.email());
        existing.setName(response.name());
        existing.setLocale(response.locale());
        repository.save(existing);
    }

    public OAuthEntity fromOAuth(ResponseOAuthUser response) {
        OAuthEntity entity = new OAuthEntity();
        entity.setAvatar_url(response.avatar_url());
        entity.setAccountId(response.accountId());
        entity.setEmail(response.email());
        entity.setName(response.name());
        entity.setLocale(response.locale());
        entity.setProvider(response.provider());
        return entity;
    }

}
