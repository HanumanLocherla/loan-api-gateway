package com.hanuman.gateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record JwtProperties(String issuer,
                            String audience,
                            long accessTokenTtl,
                            long refreshTokenTtl,
                            String privateKey,
                            String publicKey) {

}
