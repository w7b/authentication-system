package com.smoothy.authentication.infrastructure.security.filters;

import com.smoothy.authentication.infrastructure.security.oauth2.services.CustomAuthorizationRequestResolver;
import com.smoothy.authentication.infrastructure.security.oauth2.services.CustomerOAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    SecurityFilter filtersSecurity;
    private final CustomerOAuthService customerOAuthService;

    public SecurityConfig(CustomerOAuthService customerOAuthService) {
        this.customerOAuthService = customerOAuthService;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository repo) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/login").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/token-info").permitAll()

                        //OAuth2 Filters
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers("/login/oauth2/code/google**").permitAll()
                        .requestMatchers("/login/oauth2/code/github**").permitAll()

                        //Roles Settings
                        .requestMatchers(HttpMethod.POST, "/").permitAll()
                        .requestMatchers("/dashboard").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth -> oauth
                        .successHandler(customerOAuthService)
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(
                                        new CustomAuthorizationRequestResolver(repo, "/oauth2/authorization")
                                )
                        )
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtAuthenticationConverter()))
                )

//                .addFilterBefore(filtersSecurity, UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}
