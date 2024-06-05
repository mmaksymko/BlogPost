package dev.mmaksymko.gateway.handlers;

import dev.mmaksymko.gateway.models.User;
import dev.mmaksymko.gateway.models.UserRole;
import dev.mmaksymko.gateway.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements ServerAuthenticationSuccessHandler {
    private final UserService userService;
    private final SecurityContext securityContext;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();
        String email = attributes.getOrDefault("email", "").toString();

        return userService.getUser(email)
                .flatMap(user -> handleUserAuthentication(user, attributes, oAuth2AuthenticationToken))
                .switchIfEmpty(Mono.defer(() -> userService
                            .createUser(getUserFromAttributes(attributes))
                            .flatMap(user -> handleUserAuthentication(user, attributes, oAuth2AuthenticationToken))
                )).then();
    }

    public Mono<User> handleUserAuthentication(User user, Map<String, Object> attributes, OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        Map<String, Object> newAttributes = new HashMap<>(attributes);
        newAttributes.put("id", user.getId());

        DefaultOAuth2User newUser = new DefaultOAuth2User(List.of(new SimpleGrantedAuthority(user.getRole().name())),
                newAttributes, "email");

        Authentication securityAuth = new OAuth2AuthenticationToken(newUser,
                List.of(new SimpleGrantedAuthority(user.getRole().name()),
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());

        securityContext.setAuthentication(securityAuth);

        ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext));

        return Mono.just(user);
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
