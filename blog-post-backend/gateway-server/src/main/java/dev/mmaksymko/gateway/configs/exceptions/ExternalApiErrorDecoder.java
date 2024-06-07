package dev.mmaksymko.gateway.configs.exceptions;

import dev.mmaksymko.gateway.configs.exceptions.ExternalApiClientException;
import dev.mmaksymko.gateway.configs.exceptions.ExternalApiServerException;
import dev.mmaksymko.gateway.dto.ErrorResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Configuration
public class ExternalApiErrorDecoder implements ExchangeFilterFunction {

    @Override
    public @NonNull Mono<ClientResponse> filter(@NonNull ClientRequest request, @NonNull ExchangeFunction next){
        return next.exchange(request)
                .flatMap(response -> {
                    if (response.statusCode().isError()) {
                        return decode(response, request.url().toString());
                    } else {
                        return Mono.just(response);
                    }
                });
    }

    private Mono<ClientResponse> decode(ClientResponse response, String url) {
        HttpStatusCode status = response.statusCode();

        return response.bodyToMono(ErrorResponse.class)
                .map(ErrorResponse::message)
                .flatMap(error -> {
                    String errorMessage = error + " at " + url;
                    if (status.is5xxServerError()) {
                        return Mono.error(new ExternalApiServerException("Server error: " + errorMessage));
                    } else {
                        return Mono.error(new ExternalApiClientException("Client error: " + errorMessage));
                    }
                });
    }
}