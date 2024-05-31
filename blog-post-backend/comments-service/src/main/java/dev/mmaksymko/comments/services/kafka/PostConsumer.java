package dev.mmaksymko.comments.services.kafka;

import dev.mmaksymko.comments.dto.kafka.PostEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PostConsumer {
    @KafkaListener(id = "comments-post-event-listener", topics = "post-event")
    public void postEventListener(@Payload PostEvent postEvent) {
        System.out.println(postEvent);
    }
}
