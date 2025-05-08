package com.smoothy.authentication.infrastructure.security.v1.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.smoothy.authentication.adapters.outbound.entities.UserEntity;
import com.smoothy.authentication.adapters.outbound.repositories.UserRepository;
import com.smoothy.authentication.infrastructure.security.services.CustomerUserDetails;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    private final JWTVerifier verifier;
    private final KeyReader keyReader;
    private final JwtDecoder jwtDecoder;
    private final Long expirationTime;
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger();

    public JwtService(KeyReader keyReader, JwtDecoder jwtDecoder, @Value("${jwt.expiration}") Long expirationTime, UserRepository userRepository) {
        this.keyReader = keyReader;
        this.jwtDecoder = jwtDecoder;
        this.expirationTime = expirationTime; // Atribui o tempo de expiração injetado
        this.userRepository = userRepository;

        try {
            Algorithm algorithm = Algorithm.RSA256(
                    (RSAPublicKey) keyReader.loadPublicKey(),
                    (RSAPrivateKey) keyReader.loadPrivateKey()
            );
            this.verifier = JWT.require(algorithm)
                    .build();
        } catch (Exception e) {
            logger.trace(e.getMessage());
            throw new RuntimeException("Falha na configuração do serviço JWT: Chaves RSA ou algoritmo incorretos.", e);
        }
    }


    public String generateToken(UserEntity user) {
        try {
            Algorithm algorithm = Algorithm.RSA256(
                    (RSAPublicKey) keyReader.loadPublicKey(),
                    (RSAPrivateKey) keyReader.loadPrivateKey()
            );

            CustomerUserDetails userDetails = new CustomerUserDetails(user);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return JWT.create()
                    .withSubject(user.getLogin())
                    .withClaim("id", user.getUuid().toString())
                    .withClaim("role", roles)
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + this.expirationTime))
                    .sign(algorithm);

        } catch (Exception e) {
            System.err.println("Error generating JWT token: " + e.getMessage());
            logger.trace(e.getMessage(), e);
            throw new RuntimeException("Error generating JWT token", e); // Lançar exceção apropriada
        }
    }


    public Cookie generateCookie(String token) {
        Cookie jwtCookie = new Cookie("AUTH_TOKEN", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(7 * 24 * 60 * 60); // 7 dias;
        return jwtCookie;
    }


    public Authentication getAuthentication(String token) throws JWTVerificationException {
        DecodedJWT jwt = this.verifier.verify(token);

        String username = jwt.getSubject();
        List<String> roles = jwt.getClaim("role").asList(String.class);

        if (username == null || roles == null) {
            throw new JWTVerificationException("Claims 'sub' ou 'roles' ausentes no token válido.");
        }

        UserEntity userEntity = userRepository.findByLogin(username)
                .orElseThrow(() -> new JWTVerificationException("Usuário do token não encontrado no banco de dados: " + username));

        CustomerUserDetails userDetails = new CustomerUserDetails(userEntity);


        Collection<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authorities
        );
    }

//    public boolean isValid(String token) {
//        if (token == null || token.isEmpty()) {
//            System.err.println("JWT Validation failed: Token is null or empty.");
//            return false;
//        }
//
//        try {
//            Algorithm algorithm = Algorithm.RSA256(
//                    (RSAPublicKey) keyReader.loadPublicKey(),
//                    (RSAPrivateKey) keyReader.loadPrivateKey()
//            );
//
//            JWTVerifier verifier = JWT.require(algorithm).build();
//            DecodedJWT decoded = verifier.verify(token);
//
//            return !decoded.getExpiresAt().before(new Date());
//
//        } catch (JWTVerificationException | NullPointerException e) {
//            logger.info("#LOG1: JWT Validation failed: " + e.getMessage());
//            return false;
//        } catch (Exception e) {
//            System.err.println("Unexpected error during JWT validation: " + e.getMessage());
//            logger.trace(e.getMessage(), e);
//            return false;
//        }
//    }

    public DecodedJWT decodeToken(String access_token) {
        return JWT.decode(access_token);
    }

    public String extractSubject(DecodedJWT validatedToken) {
        return validatedToken.getSubject();
    }

    public String extractClaims(DecodedJWT validatedToken, String claimsName) {
        return validatedToken.getClaim(claimsName).asString();
    }

//    private Date getExpirationTime() {
//        return new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7); // 7 dias
//    }


}
