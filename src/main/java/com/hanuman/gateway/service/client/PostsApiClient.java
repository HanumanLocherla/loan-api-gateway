package com.hanuman.gateway.service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PostsApiClient {

    @Value("${api.baseUrl}")
    private String baseUrl;

    private final WebClient reactiveWebClient;

    // BaseUrl i have initialized for now apart from properties
    public PostsApiClient(WebClient.Builder webClientBuilder) {
        this.reactiveWebClient =
                webClientBuilder
                        .baseUrl(baseUrl)
                        .build();
    }

    public Flux<String> getAllPostData() {
        return reactiveWebClient
                .get()
                .uri(baseUrl + "/posts")
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setBearerAuth("");
                })
                .retrieve()
                .bodyToFlux(String.class);
    }

    public Mono<String> getPostData(String pathVariable) {
        return reactiveWebClient.get()
                .uri(baseUrl + "/posts")
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setBearerAuth("");
                })
                .retrieve()
                .bodyToMono(String.class);
    }

}
