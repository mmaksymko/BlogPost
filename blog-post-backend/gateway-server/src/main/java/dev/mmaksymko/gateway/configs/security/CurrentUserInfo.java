package dev.mmaksymko.gateway.configs.security;

import dev.mmaksymko.gateway.models.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class CurrentUserInfo {
    private Long id=null;
    private UserRole role = null;

    public void unset(){
        this.role=null;
        this.id=null;
    }
}
