package dev.mmaksymko.blogpost.services.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmaksymko.blogpost.configs.KafkaAvailabilityManager;
import dev.mmaksymko.blogpost.dto.PostResponse;
import dev.mmaksymko.blogpost.dto.kafka.EventType;
import dev.mmaksymko.blogpost.dto.kafka.PostEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostProducer {
    private static final String TOPIC = "post-event";
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaAvailabilityManager kafkaAvailabilityManager;

    @Async
    public void sendMessage(String topic, PostEvent event) {
        if (!kafkaAvailabilityManager.isAvailable()) {
            return;
        }
        try {
            String eventAsString = objectMapper.writeValueAsString(event);
            System.out.println("Sending message: " + eventAsString);
            kafkaTemplate.send(topic, eventAsString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCreatedEvent(PostResponse post) {
        PostEvent event = new PostEvent(post, EventType.CREATED);
        sendMessage(TOPIC, event);
    }

    public void sendUpdatedEvent(PostResponse post) {
        PostEvent event = new PostEvent(post, EventType.UPDATED);
        sendMessage(TOPIC, event);
    }

    public void sendDeletedEvent(PostResponse post) {
        PostEvent event = new PostEvent(post, EventType.DELETED);
        sendMessage(TOPIC, event);
    }
}
