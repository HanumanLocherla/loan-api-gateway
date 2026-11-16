package com.hanuman.gateway.controller;

import com.hanuman.gateway.dto.UserDto;
import com.hanuman.gateway.service.UserServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/gateway/api/v1/users")
public class UserController {

    private final UserServiceImpl userService;

    UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public Mono<List<UserDto>> findAllUsers() {
        return userService.getUsersData();
    }

}
