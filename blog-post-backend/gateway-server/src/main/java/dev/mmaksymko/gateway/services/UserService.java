package dev.mmaksymko.gateway.services;

import dev.mmaksymko.gateway.clients.UserClient;
import dev.mmaksymko.gateway.configs.security.CurrentUserInfo;
import dev.mmaksymko.gateway.dto.UserResponse;
import dev.mmaksymko.gateway.models.User;
import dev.mmaksymko.gateway.models.UserRole;
import dev.mmaksymko.gateway.repositories.UserRepository;
import dev.mmaksymko.gateway.services.kafka.UserProducer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@CircuitBreaker(name = "circuit-breaker-auth")
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserProducer userProducer;
    private final UserClient userClient;
    private final CurrentUserInfo currentUser;

    public Mono<UserResponse> getCurrentUser() {
        Long id = currentUser.getId();
        UserRole role = currentUser.getRole();

        Mono<String> firstName = getParameter("given_name");
        Mono<String> lastName = getParameter("family_name");
        Mono<String> email = getParameter("email");
        Mono<String> pfpUrl = getParameter("picture");

        return Mono.zip(firstName, lastName, email, pfpUrl)
                .map(tuple -> UserResponse.builder()
                        .id(id)
                        .role(role)
                        .firstName(tuple.getT1())
                        .lastName(tuple.getT2())
                        .email(tuple.getT3())
                        .pfpUrl(tuple.getT4())
                        .build());
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

    public Mono<String> getParameter(String name){
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) securityContext.getAuthentication();
                    return (String) authentication.getPrincipal().getAttributes().get(name);
                });
    }
}
