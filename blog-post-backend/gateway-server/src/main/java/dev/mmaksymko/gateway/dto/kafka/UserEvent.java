package dev.mmaksymko.gateway.dto.kafka;

import dev.mmaksymko.gateway.models.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class UserEvent extends User {
    private final EventType eventType;

    public UserEvent(User user, EventType eventType) {
        super(user);
        this.eventType = eventType;
    }
}