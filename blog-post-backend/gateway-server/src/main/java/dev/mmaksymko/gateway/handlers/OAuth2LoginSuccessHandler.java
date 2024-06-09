package dev.mmaksymko.gateway.handlers;

import dev.mmaksymko.gateway.configs.FrontEndProperties;
import dev.mmaksymko.gateway.configs.security.CurrentUserInfo;
import dev.mmaksymko.gateway.models.User;
import dev.mmaksymko.gateway.models.UserRole;
import dev.mmaksymko.gateway.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class OAuth2LoginSuccessHandler implements ServerAuthenticationSuccessHandler {
    private final UserService userService;
    private final CurrentUserInfo currentUserId;
    private final FrontEndProperties frontEndProperties;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();
        String email = attributes.getOrDefault("email", "").toString();

        return userService.getUser(email)
                .doOnNext(user -> handleUserAuthentication(user, attributes, oAuth2AuthenticationToken).subscribe())
                .switchIfEmpty(Mono.defer(() -> userService
                            .createUser(getUserFromAttributes(attributes))
                            .doOnNext(user -> handleUserAuthentication(user, attributes, oAuth2AuthenticationToken).subscribe())
                ))
                .then(Mono.fromRunnable(() -> {
                    webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.PERMANENT_REDIRECT);
                    webFilterExchange.getExchange().getResponse().getHeaders().setLocation(URI.create(frontEndProperties.url() + "/oauth2/redirect"));
                }))
                .then(webFilterExchange.getExchange().getResponse().setComplete());
    }

    public Mono<Void> handleUserAuthentication(User user, Map<String, Object> attributes, OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        return Mono
                .just(new DefaultOAuth2User(List.of(new SimpleGrantedAuthority(user.getRole().name())), attributes, "email"))
                .doOnNext(__ -> currentUserId.setId(user.getId()))
                .doOnNext(__ -> currentUserId.setRole(user.getRole()))
                .map(newUser -> new OAuth2AuthenticationToken(newUser,
                List.of(new SimpleGrantedAuthority(user.getRole().name()),
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()))
                .map(securityAuth -> {
                    SecurityContext securityContext = new SecurityContextImpl();
                    securityContext.setAuthentication(securityAuth);
                    return securityContext;
                })
                .map(securityContext -> Mono.deferContextual(Mono::just)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))))
                .then();
    }

    public User getUserFromAttributes(Map<String, Object> attributes){
        Object pfpUrlObj = attributes.getOrDefault("picture", null);
        String pfpUrl = pfpUrlObj == null ? null : pfpUrlObj.toString().replace("s96-c", "s512-c");
        return User
                .builder()
                .email(attributes.get("email").toString())
                .firstName(attributes.getOrDefault("given_name", "_").toString())
                .lastName(attributes.getOrDefault("family_name", "_").toString())
                .pfpUrl(pfpUrl)
                .role(UserRole.USER)
                .build();
    }
}
