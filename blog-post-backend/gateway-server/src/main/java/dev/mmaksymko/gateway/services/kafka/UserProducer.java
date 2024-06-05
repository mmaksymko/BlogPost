package dev.mmaksymko.gateway.services.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmaksymko.gateway.configs.kafka.KafkaAvailabilityManager;
import dev.mmaksymko.gateway.dto.kafka.EventType;
import dev.mmaksymko.gateway.dto.kafka.UserEvent;
import dev.mmaksymko.gateway.models.User;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserProducer {
    private static final String TOPIC = "user-created-event";
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

    public Mono<Void> sendCreatedEvent(User user) {
        return Mono.fromRunnable( () -> sendMessage(TOPIC, new UserEvent(user, EventType.CREATED)));
    }
}
