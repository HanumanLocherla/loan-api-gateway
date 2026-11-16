package com.hanuman.gateway.service.client;

import com.hanuman.gateway.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class UsersClient {

    @Value("${api.baseUrl}")
    private String baseUrl;

    private final WebClient webClient;

    UsersClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<List<UserDto>> findAllUsers() {
        return webClient.method(HttpMethod.GET)
                .uri(baseUrl + "/users")
                .headers(headers ->
                        headers.add("Authorization", ""))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(UserDto.class)
                .collectList();
    }

}

