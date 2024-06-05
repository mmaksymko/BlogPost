package dev.mmaksymko.users.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_user")
@SecondaryTable(name = "user_pfp", pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_id"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write="?::USER_ROLE_DOMAIN")
    @Column(columnDefinition = "USER_ROLE_DOMAIN", name = "role", nullable = false)
    private UserRole role;

    @CreationTimestamp
    @Column(name = "registered_at", updatable = false)
    private LocalDateTime registeredAt;

    @Column(name = "pfp_url", table = "user_pfp")
    private String pfpUrl;
}
