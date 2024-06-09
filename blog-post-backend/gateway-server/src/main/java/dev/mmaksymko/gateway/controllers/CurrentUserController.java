package dev.mmaksymko.gateway.controllers;

import dev.mmaksymko.gateway.dto.UserResponse;
import dev.mmaksymko.gateway.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("current-user/")
public class CurrentUserController {
    private final UserService userService;

    public CurrentUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Mono<UserResponse> getCurrentUser() {
        return userService.getCurrentUser();
    }
}
