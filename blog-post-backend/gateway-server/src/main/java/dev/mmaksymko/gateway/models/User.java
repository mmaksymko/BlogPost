package dev.mmaksymko.gateway.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    @Id
    private String email;
    private UserRole role;
    private LocalDateTime registeredAt;
    private String pfpUrl;

    public User(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.registeredAt = user.getRegisteredAt();
        this.pfpUrl = user.getPfpUrl();
    }
}
