package dev.mmaksymko.comments.configs.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmaksymko.comments.configs.exceptions.ExternalApiErrorDecoder;
import feign.codec.ErrorDecoder;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;

@AllArgsConstructor
public class OpenFeignClientConfiguration {
    private final ObjectMapper objectMapper;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ExternalApiErrorDecoder(objectMapper);
    }
}