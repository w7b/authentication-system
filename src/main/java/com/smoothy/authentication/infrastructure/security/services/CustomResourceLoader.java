package com.smoothy.authentication.infrastructure.security.services;

import com.smoothy.authentication.infrastructure.security.jwt.KeyReader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CustomResourceLoader extends DefaultResourceLoader {
    @Override
    public Resource getResource(String location) {
        try{
            if (location.startsWith("public_key.pem")) {
                return getResource("classpath:/keys/" + location.substring("public_key.pem".length()));

            }
            InputStream inputStream = KeyReader.class.getClassLoader().getResourceAsStream("classpath:keys/public_key.pem");
            assert inputStream != null;
            String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            key = key
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            return super.getResource(location) ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
