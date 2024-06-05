package dev.mmaksymko.gateway.repositories;

import dev.mmaksymko.gateway.models.User;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
@AllArgsConstructor
public class UserRepository {
    private final ReactiveRedisTemplate<String, User> redisTemplate;

    public Mono<User> findByEmail(String email) {
        return redisTemplate
                .opsForValue()
                .get(email);
    }

    public Mono<User> save(User user) {
        return redisTemplate
                .opsForValue()
                .set(user.getEmail(), user)
                .flatMap(success -> {
                    if (!success) {
                        return Mono.error(new RuntimeException("Failed to save user to Redis"));
                    }
                    return Mono.just(user);
                })
                .onErrorComplete();
    }

    public Mono<Void> deleteByEmail(String email) {
        return redisTemplate
                .delete(email)
                .then();
    }

}