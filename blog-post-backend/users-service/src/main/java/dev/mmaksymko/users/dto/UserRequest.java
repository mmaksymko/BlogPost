package dev.mmaksymko.users.dto;

import dev.mmaksymko.users.models.UserRole;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record UserRequest(
    @NonNull Long id,
    @NonNull String firstName,
    @NonNull String lastName,
    @NonNull String email,
    @NonNull UserRole role,
    @NonNull LocalDateTime registeredAt,
    String pfpUrl
) {}
