package dev.mmaksymko.email.dto;

import java.time.LocalDateTime;

public record User(
    Long id,
    String firstName,
    String lastName,
    String email,
    UserRole role,
    LocalDateTime registeredAt,
    String pfpUrl
) {}
