package dev.mmaksymko.gateway.services.kafka;

import dev.mmaksymko.gateway.dto.kafka.EventType;
import dev.mmaksymko.gateway.dto.kafka.UserEvent;
import dev.mmaksymko.gateway.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserConsumer {
    private final UserService userService;

    @KafkaListener(id = "gateway-user-event-listener", topics = "user-event", autoStartup = "false")
    public void postEventListener(@Payload UserEvent userEvent) {
        switch(userEvent.getEventType()) {
            case EventType.CREATED -> userService.createUser(userEvent).subscribe();
            case EventType.UPDATED -> userService.updateUser(userEvent).subscribe();
            case EventType.DELETED -> userService.deleteUser(userEvent.getEmail()).subscribe();
            default -> throw new IllegalArgumentException("Unexpected event type: " + userEvent.getEventType());
        }
    }
}
