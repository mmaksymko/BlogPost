package dev.mmaksymko.gateway.services;

import dev.mmaksymko.gateway.clients.UserClient;
import dev.mmaksymko.gateway.models.User;
import dev.mmaksymko.gateway.repositories.UserRepository;
import dev.mmaksymko.gateway.services.kafka.UserProducer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@CircuitBreaker(name = "circuit-breaker-auth")
public class UserService {
    private final UserRepository userRepository;
    private final UserProducer userProducer;
    private final UserClient userClient;

    public UserService(UserRepository userRepository, UserProducer userProducer, UserClient userClient) {
        this.userProducer = userProducer;
        this.userClient = userClient;
        this.userRepository = userRepository;
    }

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
        return userClient
                .createUser(user)
                .flatMap(userRepository::save)
                .flatMap(u -> Mono
                        .just(u)
                        .doOnNext(bool -> userProducer.sendCreatedEvent(user).subscribeOn(Schedulers.boundedElastic()).subscribe())
                        .thenReturn(u)
                );
    }

    public Mono<User> updateUser(User user) {
        return createUser(user);
    }

    public Mono<Void> deleteUser(String email) {
        return userRepository.deleteByEmail(email);
    }

}
