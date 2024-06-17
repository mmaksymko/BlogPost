package dev.mmaksymko.gateway.services;

import dev.mmaksymko.gateway.clients.UserClient;
import dev.mmaksymko.gateway.dto.UserResponse;
import dev.mmaksymko.gateway.models.User;
import dev.mmaksymko.gateway.repositories.UserRepository;
import dev.mmaksymko.gateway.services.kafka.UserProducer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Service
@CircuitBreaker(name = "circuit-breaker-auth")
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserProducer userProducer;
    private final UserClient userClient;

    public Mono<User> getUser(Long userId) {
        return userClient.getUser(userId);
    }

    public Mono<User> getUser(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(userClient.getUser(email)
                .flatMap(userRepository::save))
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<User> createUser(User user) {
        return saveUser(user)
                .flatMap(u -> Mono
                        .just(u)
                        .doOnNext(bool -> userProducer.sendCreatedEvent(user).subscribeOn(Schedulers.boundedElastic()).subscribe())
                        .thenReturn(u)
                );
    }

    public Mono<User> updateUser(User user) {
         return saveUser(user);
    }

    private Mono<User> saveUser(User user) {
        return userClient
                .createUser(user)
                .flatMap(userRepository::save);
    }

    public Mono<Void> deleteUser(String email) {
        return userRepository.deleteByEmail(email);
    }

}
