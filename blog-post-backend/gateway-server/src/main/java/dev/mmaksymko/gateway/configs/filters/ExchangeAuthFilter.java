package dev.mmaksymko.gateway.configs.filters;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Configuration
public class ExchangeAuthFilter extends AuthFilter implements ExchangeFilterFunction {
    @Override
    public @NonNull Mono<ClientResponse> filter(@NonNull ClientRequest request, ExchangeFunction next) {
        return addBearerToken(request)
                .flatMap(next::exchange)
                .switchIfEmpty(next.exchange(request)); // If no access token, proceed without modifying the request
    }

    private Mono<ClientRequest> addBearerToken(ClientRequest request) {
        return super.getToken()
                .map(idToken -> ClientRequest.from(request).headers(httpHeaders -> httpHeaders.setBearerAuth(idToken)).build());
    }
}
