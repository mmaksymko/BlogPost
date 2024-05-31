package dev.mmaksymko.reactions.services.kafka;

import dev.mmaksymko.reactions.dto.kafka.PostEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PostConsumer {
    @KafkaListener(id = "reactions-post-event-listener", topics = "post-event")
    public void postEventListener(@Payload PostEvent postEvent) {
        System.out.println(postEvent);
    }
}