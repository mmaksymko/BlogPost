package dev.mmaksymko.users.services.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmaksymko.users.configs.KafkaAvailabilityManager;
import dev.mmaksymko.users.dto.UserResponse;
import dev.mmaksymko.users.dto.kafka.EventType;
import dev.mmaksymko.users.dto.kafka.UserEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserProducer {
    private static final String TOPIC = "user-event";
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaAvailabilityManager kafkaAvailabilityManager;

    public void sendMessage(String topic, UserEvent event) {
        if (!kafkaAvailabilityManager.isAvailable()) {
            return;
        }
        try {
            String eventAsString = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, eventAsString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void sendUpdatedEvent(UserResponse user) {
        UserEvent event = new UserEvent(user, EventType.UPDATED);
        sendMessage(TOPIC, event);
    }

    @Async
    public void sendDeletedEvent(UserResponse user) {
        UserEvent event = new UserEvent(user, EventType.DELETED);
        sendMessage(TOPIC, event);
    }
}
