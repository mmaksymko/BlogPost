package dev.mmaksymko.gateway.dto;

import dev.mmaksymko.gateway.models.UserRole;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
    private String pfpUrl;
}