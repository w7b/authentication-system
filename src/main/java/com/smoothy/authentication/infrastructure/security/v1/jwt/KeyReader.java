package com.smoothy.authentication.infrastructure.security.v1.jwt;

import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class KeyReader {

    protected RSAPrivateKey loadPrivateKey() {
        try {
            InputStream inputStream = KeyReader.class.getClassLoader().getResourceAsStream("keys/private_key.pem");
            if (inputStream == null) {
                throw new RuntimeException("Arquivo private_key.pem não encontrado");
            }

            String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            key = key
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", ""); // remove quebras de linha, espaços, tabs, etc.

            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return (RSAPrivateKey) keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar a chave privada: " + e.getMessage(), e);
        }
    }

    protected RSAPublicKey loadPublicKey() {
        try {
            InputStream inputStream = KeyReader.class.getClassLoader().getResourceAsStream("keys/public_key.pem");
            if (inputStream == null) {
                throw new RuntimeException("Arquivo public_key.pem não encontrado");
            }

            String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            key = key
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", ""); // remove quebras de linha, espaços, tabs, etc.

            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded); // <- aqui estava o erro de sintaxe
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return (RSAPublicKey) keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar a chave pública: " + e.getMessage(), e);
        }
    }



}
