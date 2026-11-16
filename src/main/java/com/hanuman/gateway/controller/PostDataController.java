package com.hanuman.gateway.controller;

import com.hanuman.gateway.service.PostsServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/gateway/api/v1")
public class PostDataController {

    private final PostsServiceImpl jsonService;

    PostDataController(PostsServiceImpl jsonService) {
        this.jsonService = jsonService;
    }

    @GetMapping(value = "/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<String> getPostData() {
        return jsonService.getPostsData();
    }


}
