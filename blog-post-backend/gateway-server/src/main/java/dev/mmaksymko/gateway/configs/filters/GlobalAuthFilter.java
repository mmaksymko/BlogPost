package dev.mmaksymko.gateway.configs.filters;

import dev.mmaksymko.gateway.configs.web.JwtManager;
import dev.mmaksymko.gateway.services.CurrentUserService;
import dev.mmaksymko.gateway.services.UserService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class GlobalAuthFilter extends AuthFilter implements GlobalFilter {
    public GlobalAuthFilter(JwtManager jwtManager, ObjectProvider<CurrentUserService> userServiceProvider) {
        super(jwtManager, userServiceProvider);
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