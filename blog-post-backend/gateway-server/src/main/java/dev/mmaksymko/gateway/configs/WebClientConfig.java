package dev.mmaksymko.gateway.configs;

import dev.mmaksymko.gateway.clients.UserClient;
import lombok.AllArgsConstructor;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@AllArgsConstructor
public class WebClientConfig {
    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;
    private final ExternalApiErrorDecoder errorDecoder;

    @Bean
    public WebClient webClient() {
        return WebClient
                .builder()
                .filter(lbFunction)
                .filter(errorDecoder)
                .build();
    }

    @Bean
    UserClient postClient(WebClient webClient) {
        WebClient client = webClient
                .mutate()
                .baseUrl("http://users-service/")
                .build();

        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory
                        .builderFor(WebClientAdapter.create(client))
                        .build();
        return httpServiceProxyFactory.createClient(UserClient.class);
    }
}