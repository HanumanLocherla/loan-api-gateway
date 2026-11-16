package com.hanuman.gateway.service;

import com.hanuman.gateway.dto.LoginRequest;
import com.hanuman.gateway.dto.LoginResponse;
import com.hanuman.gateway.properties.JwtProperties;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    // Demo credentials only. Replace with database-backed authentication.
    private final String demoUsername = "admin";
    private final String demoPasswordHash =
            passwordEncoder.encode("password");

    public AuthService(
            JwtEncoder jwtEncoder,
            JwtProperties jwtProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }


    public Mono<LoginResponse> login(LoginRequest request) {
        if (!demoUsername.equals(request.username())
                || !passwordEncoder.matches(
                request.password(), demoPasswordHash)) {
            throw new BadCredentialsException("Invalid username or password");
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtProperties.accessTokenTtl(), ChronoUnit.SECONDS);

        List<String> roles = List.of("ROLE_ADMIN", "ROLE_KYC_READ");

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .subject(request.username())
                .audience(List.of(jwtProperties.audience()))
                .issuedAt(now)
                .expiresAt(expiresAt)
                .id(UUID.randomUUID().toString())
                .claim("roles", roles)
                .claim("token_type", "access")
                .build();

        JwsHeader headers = JwsHeader
                .with(SignatureAlgorithm.RS256)
                .build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(headers, claims)
        ).getTokenValue();

        long tokenExpiresInSecond = jwtProperties.accessTokenTtl() * 60;

        return Mono.just(new LoginResponse(
                token,
                "Bearer",
                tokenExpiresInSecond));
    }
}
