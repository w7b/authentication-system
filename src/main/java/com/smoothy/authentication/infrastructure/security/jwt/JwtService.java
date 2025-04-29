package com.smoothy.authentication.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class JwtService {

    @Autowired
    private KeyReader keyReader;
    @Autowired
    private JwtDecoder jwtDecoder;


    public String generateToken(String sub, String name) {
        Algorithm algorithm = Algorithm.RSA256(
                (RSAPublicKey) keyReader.loadPublicKey(),
                keyReader.loadPrivateKey()
        );

        return JWT.create()
                .withSubject(sub)
                .withClaim("name", name)
                .withIssuedAt(new Date())
                .withExpiresAt(getExpirationTime())
                .sign(algorithm);
    }

    public boolean validadeToken(String access_token) {
        try{
            Algorithm algorithm = Algorithm.RSA256(
                    (RSAPublicKey) keyReader.loadPublicKey(),
                    keyReader.loadPrivateKey()
            );

            JWT.require(algorithm)
                    .build()
                    .verify(access_token);

            return true;
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Invalid or expired token");
        }
    }

    public DecodedJWT decodeToken(String access_token) {
        Algorithm algorithm = Algorithm.RSA256(
                (RSAPublicKey) keyReader.loadPublicKey(),
                keyReader.loadPrivateKey()
        );

        return JWT.require(algorithm).build().verify(access_token);
    }

    public String extractSubject(String access_token) {
        return decodeToken(access_token).getSubject();
    }

    public String extracName(String access_token) {
        return decodeToken(access_token).getClaim("name").asString();
    }

    private Date getExpirationTime() {
        return Date.from(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.UTC));
    }


}
