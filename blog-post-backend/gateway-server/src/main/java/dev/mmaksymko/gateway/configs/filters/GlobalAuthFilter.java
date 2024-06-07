package dev.mmaksymko.gateway.configs.filters;

import dev.mmaksymko.gateway.configs.security.CurrentUserInfo;
import dev.mmaksymko.gateway.configs.web.JwtManager;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class GlobalAuthFilter extends AuthFilter implements GlobalFilter {
    public GlobalAuthFilter(CurrentUserInfo user, JwtManager jwtManager) {
        super(jwtManager, user);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            return addBearerToken(exchange.getRequest())
                    .flatMap(request -> chain.filter(exchange.mutate().request(request).build())).switchIfEmpty(chain.filter(exchange)); // If no access token, proceed without modifying the request
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Mono<ServerHttpRequest> addBearerToken(ServerHttpRequest request) {
        return super.getToken()
                .map(idToken -> request.mutate()
                        .headers(httpHeaders -> {
                            httpHeaders.setBearerAuth(idToken);
                        })
                        .build());
    }

}