package dev.mmaksymko.comments.services.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmaksymko.comments.configs.KafkaAvailabilityManager;
import dev.mmaksymko.comments.dto.CommentResponse;
import dev.mmaksymko.comments.dto.kafka.EventType;
import dev.mmaksymko.comments.dto.kafka.CommentEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentProducer {
    private static final String TOPIC = "comment-event";
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaAvailabilityManager kafkaAvailabilityManager;

    @Async
    public void sendMessage(String topic, CommentEvent event) {
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

    public void sendCreatedEvent(CommentResponse post) {
        CommentEvent event = new CommentEvent(post, EventType.CREATED);
        sendMessage(TOPIC, event);
    }

    public void sendUpdatedEvent(CommentResponse post) {
        CommentEvent event = new CommentEvent(post, EventType.UPDATED);
        sendMessage(TOPIC, event);
    }

    public void sendDeletedEvent(CommentResponse post) {
        CommentEvent event = new CommentEvent(post, EventType.DELETED);
        sendMessage(TOPIC, event);
    }
}
