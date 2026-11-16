package com.hanuman.gateway.service;

import com.hanuman.gateway.service.client.PostsApiClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PostsServiceImpl {

    private final PostsApiClient jsonApiClient;

    PostsServiceImpl(PostsApiClient jsonApiClient) {
        this.jsonApiClient = jsonApiClient;
    }

    public Flux<String> getPostsData() {
        return jsonApiClient.getAllPostData();
    }

    public Mono<String> getPost(String pathVariable) {
        return jsonApiClient.getPostData(pathVariable);
    }
}
