package dev.mmaksymko.gateway.services;

import dev.mmaksymko.gateway.dto.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@AllArgsConstructor
public class CurrentUserService {
    private final UserService userService;

    public Mono<UserResponse> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(authentication -> authentication instanceof OAuth2AuthenticationToken)
                .cast(OAuth2AuthenticationToken.class)
                .map(authentication -> {
                    DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
                    Map<String, Object> attributes = principal.getAttributes();
                    return attributes.getOrDefault("email", "").toString();
                })
                .flatMap(userService::getUser)
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .role(user.getRole())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .pfpUrl(user.getPfpUrl())
                        .build());
    }
}
