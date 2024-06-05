package dev.mmaksymko.users.dto.kafka;

import dev.mmaksymko.users.dto.UserResponse;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class UserEvent extends UserResponse {
    private final EventType eventType;

    public UserEvent(UserResponse userResponse, EventType eventType) {
        super(userResponse);
        this.eventType = eventType;
    }
}