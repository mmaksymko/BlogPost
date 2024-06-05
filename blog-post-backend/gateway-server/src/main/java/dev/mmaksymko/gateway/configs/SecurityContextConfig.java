package dev.mmaksymko.gateway.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;

@Configuration
public class SecurityContextConfig {
    @Bean
    public SecurityContext securityContext() {
        return new SecurityContextImpl();
    }
}
