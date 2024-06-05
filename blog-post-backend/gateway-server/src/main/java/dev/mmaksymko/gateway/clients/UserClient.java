package dev.mmaksymko.gateway.clients;

import dev.mmaksymko.gateway.models.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface UserClient {
    @GetExchange("users/{id}/")
    Mono<User> getUser(@PathVariable Long id);
    @GetExchange("users/email/{email}/")
    Mono<User> getUser(@PathVariable String email);
    @PostExchange("users/")
    Mono<User> createUser(@RequestBody User user);
}