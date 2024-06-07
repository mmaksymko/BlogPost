package dev.mmaksymko.gateway.configs.web;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Configuration
public class JwtManager {
    @Value("${spring.application.name}")
    private String issuer;

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiry = now.plus(1, ChronoUnit.HOURS);

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject("user")
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

}
