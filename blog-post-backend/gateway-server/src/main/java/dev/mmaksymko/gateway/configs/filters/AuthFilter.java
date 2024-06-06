package dev.mmaksymko.gateway.configs.filters;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import reactor.core.publisher.Mono;

public abstract class AuthFilter {
    protected Mono<String> getToken() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(authentication -> authentication instanceof OAuth2AuthenticationToken)
                .cast(OAuth2AuthenticationToken.class)
                .flatMap(this::getIdToken);
    }

    protected Mono<String> getIdToken(OAuth2AuthenticationToken oauthToken) {
        return Mono.justOrEmpty(((DefaultOidcUser) oauthToken.getPrincipal()).getIdToken())
                .map(AbstractOAuth2Token::getTokenValue);
    }
}
