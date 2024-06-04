package dev.mmaksymko.gateway.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class OAuth2LogoutSuccessHandler implements ServerLogoutSuccessHandler {
    private final WebClient webClient;
    private final ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            return authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            ).flatMap(authorizedClient -> {
                if (authorizedClient != null) {
                    String accessToken = authorizedClient.getAccessToken().getTokenValue();
                    return revokeGoogleToken(accessToken); // Revoke token before clearing session
                }
                return Mono.empty();
            }).then();
        }
        return Mono.empty();
    }

    private Mono<Void> revokeGoogleToken(String accessToken) {
        String revokeTokenEndpoint = "https://oauth2.googleapis.com/revoke?token=" + accessToken;
        return webClient.post()
                .uri(revokeTokenEndpoint)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(new RuntimeException("Failed to revoke token")))
                .bodyToMono(Void.class);
    }
}