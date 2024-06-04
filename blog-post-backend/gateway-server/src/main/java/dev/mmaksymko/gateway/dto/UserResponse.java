package dev.mmaksymko.gateway.dto;

import dev.mmaksymko.gateway.models.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
    private LocalDateTime registeredAt;
    private String pfpUrl;
}