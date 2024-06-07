package dev.mmaksymko.gateway.handlers;

import dev.mmaksymko.gateway.configs.security.CurrentUserInfo;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;

@Configuration
@AllArgsConstructor
public class OAuth2LogoutSuccessHandler implements ServerLogoutSuccessHandler {
    private final CurrentUserInfo currentUserId;
    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        currentUserId.unset();
        webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.OK);
        return webFilterExchange.getExchange().getResponse().setComplete();
    }
}