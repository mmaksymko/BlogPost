package dev.mmaksymko.gateway.configs;

import dev.mmaksymko.gateway.handlers.OAuth2LoginSuccessHandler;
import dev.mmaksymko.gateway.handlers.OAuth2LogoutSuccessHandler;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;

import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LogoutSuccessHandler OAuth2LogoutSuccessHandler;
    private final FrontEndProperties frontEndProperties;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .headers(headers -> headers.contentTypeOptions(contentTypeOptions -> {}))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**", "/error", "/login/**", "/webjars/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2Login(oauth2 -> oauth2.authenticationSuccessHandler(oAuth2LoginSuccessHandler))
                .logout(logout -> logout.logoutSuccessHandler(OAuth2LogoutSuccessHandler))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontEndProperties.url(), frontEndProperties.url() + "/oauth2/redirect", frontEndProperties.url() + "/**"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return urlBasedCorsConfigurationSource;
    }
}
