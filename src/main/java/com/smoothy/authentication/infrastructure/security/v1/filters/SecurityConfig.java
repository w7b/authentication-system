package com.smoothy.authentication.infrastructure.security.v1.filters;

import com.smoothy.authentication.infrastructure.security.v1.oauth2.services.CustomerOAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
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

                        //OAuth2 Filters
                        .requestMatchers("/login/oauth2/code/google**").permitAll()
                        .requestMatchers("/login/oauth2/code/github**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/").permitAll()
                        .requestMatchers("/api/v1/user/update**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER") // PUT or PATCH
                        .requestMatchers(HttpMethod.GET, "/api/v1/user").permitAll()
                        .requestMatchers("/api/v1/dashboard").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/api/v1/dashboard/{uuid}").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")

                        .anyRequest().authenticated()
                )

                // ------ This will be restructured soon. ------

//                .oauth2Login(oauth -> oauth
//                        .successHandler(customerOAuthService)
//                        .authorizationEndpoint(endpoint -> endpoint
//                                .authorizationRequestResolver(
//                                        new CustomAuthorizationRequestResolver(repo, "/oauth2/authorization")
//                                )
//                        )
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtAuthenticationConverter()))
//                )
//                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .addFilterBefore(filtersSecurity, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/v1/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID").deleteCookies("AUTH_TOKEN")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.OK.value());
                        }))
                .build();

    }
}
