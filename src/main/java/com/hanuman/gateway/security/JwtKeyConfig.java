package com.hanuman.gateway.security;

import com.hanuman.gateway.properties.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtKeyConfig {

    @Bean
    public RSAPrivateKey jwtPrivateKey(
            JwtProperties properties,
            ResourceLoader resourceLoader) throws Exception {

        Resource resource =
                resourceLoader.getResource(properties.privateKey());

        String pem = readPem(resource);

        String keyContent = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(keyContent);

        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(keyBytes);

        return (RSAPrivateKey) KeyFactory
                .getInstance("RSA")
                .generatePrivate(keySpec);
    }

    @Bean
    public RSAPublicKey jwtPublicKey(
            JwtProperties properties,
            ResourceLoader resourceLoader) throws Exception {

        Resource resource =
                resourceLoader.getResource(properties.publicKey());

        String pem = readPem(resource);

        String keyContent = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(keyContent);

        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(keyBytes);

        return (RSAPublicKey) KeyFactory
                .getInstance("RSA")
                .generatePublic(keySpec);
    }

    private String readPem(Resource resource) throws Exception {

        try (var inputStream = resource.getInputStream()) {
            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }

}
