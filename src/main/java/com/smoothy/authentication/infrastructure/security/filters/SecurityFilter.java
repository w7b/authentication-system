package com.smoothy.authentication.infrastructure.security.filters;

import com.smoothy.authentication.adapters.outbound.entities.RoleEntity;
import com.smoothy.authentication.adapters.outbound.entities.UserEntity;
import com.smoothy.authentication.infrastructure.security.SecurityRepository;
import com.smoothy.authentication.infrastructure.security.jwt.JwtService;
import com.smoothy.authentication.infrastructure.security.services.CustomerUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityRepository securityRepository;
    private CustomerUserDetails userDetails;


    private UserEntity usere;
    private List<GrantedAuthority> authorities;
    private RoleEntity role;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var access_token = this.recoverToken(request);

        if (access_token != null) {
            String login = jwtService.extracName(access_token);

            if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserEntity user = securityRepository.findByLogin(login)
                        .orElseThrow(() -> new UsernameNotFoundException(login));
                CustomerUserDetails userDetails = new CustomerUserDetails(user);


                if (jwtService.validadeToken(access_token)) {
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, // principal (UserDetails)
                            null, // credentials (não precisa senha)
                            userDetails.getAuthorities() // usa o que já está implementado!
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.replace("Bearer ", "");
    }
}
