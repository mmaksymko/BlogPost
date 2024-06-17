package dev.mmaksymko.gateway.controllers;

import dev.mmaksymko.gateway.dto.UserResponse;
import dev.mmaksymko.gateway.services.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("current-user/")
public class CurrentUserController {
    private final CurrentUserService userService;

    public CurrentUserController(CurrentUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Mono<UserResponse> getCurrentUser() {
        return userService.getCurrentUser();
    }
}
