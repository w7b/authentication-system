package com.smoothy.authentication.infrastructure.security.v1.oauth2.services;

import com.smoothy.authentication.core.services.OAuthService;
import com.smoothy.authentication.infrastructure.security.v1.jwt.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
public class CustomerOAuthService implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final OAuthService authService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    public CustomerOAuthService(JwtService jwtService, OAuthService authService, OAuth2AuthorizedClientService authorizedClient) {
        this.jwtService = jwtService;
        this.authService = authService;
        this.authorizedClientService = authorizedClient;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {


        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        registrationId,
                        authentication.getName()
                );

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        String jwt = jwtService.generateToken(oAuth2User.getName());

//        ResponseOAuthUser userResponse = authService.responseOAuthUser(authentication, jwt);
//        ResponseOAuthLogin payload = new ResponseOAuthLogin(jwt, userResponse);

        response.setStatus(HttpServletResponse.SC_OK);
//        new ObjectMapper().writeValue(response.getWriter(), payload);
    }
}
