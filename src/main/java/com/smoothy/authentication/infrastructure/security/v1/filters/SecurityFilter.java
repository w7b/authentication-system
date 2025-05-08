package com.smoothy.authentication.infrastructure.security.v1.filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.smoothy.authentication.infrastructure.security.v1.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = null;

        Cookie[] cookies = request.getCookies();
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("AUTH_TOKEN")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try{
                Authentication authentication = jwtService.getAuthentication(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Usuário '" + authentication.getName() + "' autenticado via JWT (Cookie) com roles: " + authentication.getAuthorities());
                }
            } catch ( JWTVerificationException e ) {
                logger.info("Validacao do token do cookie 'auth' falhou: {}"+ e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

//    private String recoverToken(HttpServletRequest request) {
//        var authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
//        return authHeader.replace("Bearer ", "");
//    }
}
