package dev.mmaksymko.gateway.configs.filters;

import dev.mmaksymko.gateway.configs.web.JwtManager;
import dev.mmaksymko.gateway.services.CurrentUserService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public abstract class AuthFilter {
    private final JwtManager jwtManager;
    private final ObjectProvider<CurrentUserService> userServiceProvider;

    public AuthFilter(JwtManager jwtManager, ObjectProvider<CurrentUserService> userServiceProvider) {
        this.jwtManager = jwtManager;
        this.userServiceProvider = userServiceProvider;
    }

    protected Mono<String> getToken() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(authentication -> authentication instanceof OAuth2AuthenticationToken)
                .cast(OAuth2AuthenticationToken.class)
                .flatMap(this::getIdToken);
    }

    protected Mono<String> getIdToken(OAuth2AuthenticationToken oauthToken) {
        CurrentUserService userService = userServiceProvider.getIfAvailable();
        if (userService == null) {
            return Mono.empty();
        }
        return userService.getCurrentUser()
                .flatMap(user -> Mono.justOrEmpty(((DefaultOidcUser) oauthToken.getPrincipal()).getIdToken())
                        .map(token -> {
                            Map<String, Object> initialMap = new HashMap<>(Map.of("id", user.getId(), "role", user.getRole().name()));
                            initialMap.putAll(token.getClaims());
                            return jwtManager.generateToken(initialMap);
                        }));
    }
}