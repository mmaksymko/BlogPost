package dev.mmaksymko.reactions.configs;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class OpenFeignClientConfiguration {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new ExternalApiErrorDecoder();
    }
}