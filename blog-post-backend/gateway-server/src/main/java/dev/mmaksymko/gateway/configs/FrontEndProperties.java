package dev.mmaksymko.gateway.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "frontend")
public record FrontEndProperties(String url){ }
