package dev.mmaksymko.users.dto;

import dev.mmaksymko.users.models.UserRole;
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
    private LocalDateTime registeredAt;
    private String pfpUrl;

    public UserResponse(UserResponse userResponse){
        this.id = userResponse.id;
        this.firstName = userResponse.firstName;
        this.lastName = userResponse.lastName;
        this.email = userResponse.email;
        this.role = userResponse.role;
        this.registeredAt = userResponse.registeredAt;
        this.pfpUrl = userResponse.getPfpUrl();
    }
}