package com.hanuman.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/gateway/api/v1/**").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/api/v1/auth/login",
                                "/api/v1/auth/refresh").permitAll()
                        .pathMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .pathMatchers("/api/v1/kyc/**").hasAnyRole("KYC_OFFICER", "LOAN_OFFICER", "ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/v1/loans/**")
                        .hasAnyRole("CUSTOMER", "LOAN_OFFICER", "ADMIN")
                        .pathMatchers("/api/v1/loans/**")
                        .hasAnyRole("CUSTOMER", "LOAN_OFFICER",
                                "CREDIT_MANAGER", "ADMIN")
                        .anyExchange().authenticated()
                ).oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                new JwtRoleConverter())))
                .build();
    }
}
