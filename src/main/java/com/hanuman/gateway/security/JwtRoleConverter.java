package com.hanuman.gateway.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public class JwtRoleConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        var authorities = new ArrayList<SimpleGrantedAuthority>();
        var roles = jwt.getClaimAsStringList("roles");
        if (roles != null) {
            roles.forEach(role -> authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + role)));
        }
        var scopes = jwt.getClaimAsStringList("scope");
        if (scopes != null) {
            scopes.forEach(scope -> authorities.add(
                    new SimpleGrantedAuthority("SCOPE_" + scope)));
        }
        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }
}
