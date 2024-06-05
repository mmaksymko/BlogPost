package dev.mmaksymko.gateway.configs.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class AuthFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(authentication -> authentication instanceof OAuth2AuthenticationToken)
                .cast(OAuth2AuthenticationToken.class)
                .flatMap(this::getIdToken)
                .flatMap(idToken -> {
                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .headers(httpHeaders -> httpHeaders.setBearerAuth(idToken))
                            .build();

                    return chain.filter(exchange.mutate().request(request).build());
                })
                .switchIfEmpty(chain.filter(exchange)); // If no access token, proceed without modifying the request
    }

    private Mono<String> getIdToken(OAuth2AuthenticationToken oauthToken) {
        return Mono.justOrEmpty(((DefaultOidcUser) oauthToken.getPrincipal()).getIdToken())
                .map(AbstractOAuth2Token::getTokenValue);
    }
}
