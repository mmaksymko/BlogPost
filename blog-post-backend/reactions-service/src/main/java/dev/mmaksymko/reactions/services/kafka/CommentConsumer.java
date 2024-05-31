package dev.mmaksymko.reactions.services.kafka;

import dev.mmaksymko.reactions.dto.kafka.CommentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class CommentConsumer {
    @KafkaListener(id = "reactions-comment-event-listener", topics = "comment-event")
    public void postEventListener(@Payload CommentEvent postEvent) {
        System.out.println(postEvent);
    }
}
