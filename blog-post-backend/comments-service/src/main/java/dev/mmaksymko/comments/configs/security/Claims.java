package dev.mmaksymko.comments.configs.security;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class Claims {
    private Map<String, Object> claims = new HashMap<>();

    public void setClaims(Map<String, Object> claims) {
        this.claims = claims;
    }

    public Object getClaim(String key) {
        return claims.getOrDefault(key, null);
    }
}
