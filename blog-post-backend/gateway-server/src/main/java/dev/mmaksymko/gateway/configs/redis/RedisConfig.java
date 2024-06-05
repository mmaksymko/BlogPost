package dev.mmaksymko.gateway.configs.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmaksymko.gateway.models.User;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;

    @Bean
    public RedisURI redisURI() {
        return RedisURI
                .Builder
                .redis(redisProperties.host(), redisProperties.port())
                .withPassword(redisProperties.password())
                .withDatabase(redisProperties.database())
                .build();
    }

    @Bean
    public RedisConfiguration redisConfiguration(RedisURI redisURI) {
        return LettuceConnectionFactory.createRedisConfiguration(redisURI);
    }

    @Bean
    public LettuceConnectionFactory reactiveRedisConnectionFactory(RedisConfiguration redisConfiguration) {
        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean
    public ReactiveRedisTemplate<String, User> reactiveRedisTemplate(ObjectMapper mapper, ReactiveRedisConnectionFactory factory) {
        PrefixedStringRedisSerializer keySerializer = new PrefixedStringRedisSerializer("User:");
        Jackson2JsonRedisSerializer<User> valueSerializer = new Jackson2JsonRedisSerializer<>(mapper, User.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, User> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, User> context =
                builder.value(valueSerializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
