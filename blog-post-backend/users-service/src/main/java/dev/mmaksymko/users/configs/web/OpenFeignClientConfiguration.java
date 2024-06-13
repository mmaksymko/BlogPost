package dev.mmaksymko.users.configs.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmaksymko.users.configs.exceptions.ExternalApiErrorDecoder;
import dev.mmaksymko.users.configs.security.JwtConfig;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;

@AllArgsConstructor
public class OpenFeignClientConfiguration {
    private final ObjectMapper objectMapper;
    private final JwtConfig jwtConfig;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ExternalApiErrorDecoder(objectMapper);
    }
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String token = jwtConfig.getTokenValue();
            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }
}