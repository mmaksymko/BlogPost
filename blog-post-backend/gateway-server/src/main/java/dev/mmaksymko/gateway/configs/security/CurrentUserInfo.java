package dev.mmaksymko.gateway.configs.security;

import dev.mmaksymko.gateway.models.UserRole;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class CurrentUserInfo {
    private Long id;
    private UserRole role;

    @PostConstruct
    public void init(){
        unset();
    }

    public void unset(){
        this.role=UserRole.UNATHORIZED;
        this.id=-1L;
    }
}
