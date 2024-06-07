package dev.mmaksymko.gateway.configs.filters;

import dev.mmaksymko.gateway.configs.security.CurrentUserInfo;
import dev.mmaksymko.gateway.configs.web.JwtManager;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public abstract class AuthFilter {
    private final JwtManager jwtManager;
    protected final CurrentUserInfo user;

    public AuthFilter(JwtManager jwtManager, CurrentUserInfo user) {
        this.jwtManager = jwtManager;
        this.user = user;
    }

    protected Mono<String> getToken() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(authentication -> authentication instanceof OAuth2AuthenticationToken)
                .cast(OAuth2AuthenticationToken.class)
                .flatMap(this::getIdToken);
    }

    protected Mono<String> getIdToken(OAuth2AuthenticationToken oauthToken) {
        return Mono.justOrEmpty(((DefaultOidcUser) oauthToken.getPrincipal()).getIdToken())
                .map(token -> {
                        Map<String, Object> initialMap = new HashMap<>(Map.of("id", user.getId(), "role", user.getRole().name()));
                        initialMap.putAll(token.getClaims());
                        return jwtManager.generateToken(initialMap);
                });
    }
}
