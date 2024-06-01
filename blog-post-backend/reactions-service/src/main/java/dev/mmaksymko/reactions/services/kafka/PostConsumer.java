package dev.mmaksymko.reactions.services.kafka;

import dev.mmaksymko.reactions.dto.kafka.EventType;
import dev.mmaksymko.reactions.dto.kafka.PostEvent;
import dev.mmaksymko.reactions.services.redis.RedisPostService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostConsumer {
    private final RedisPostService postService;

    @KafkaListener(id = "reactions-post-event-listener", topics = "post-event", autoStartup = "false")
    public void postEventListener(@Payload PostEvent postEvent) {
        switch(postEvent.getEventType()) {
            case EventType.CREATED -> postService.savePost(postEvent);
            case EventType.UPDATED -> postService.updatePost(postEvent);
            case EventType.DELETED -> postService.deletePost(postEvent.getId());
            default -> throw new IllegalArgumentException("Unexpected event type: " + postEvent.getEventType());
        }
    }
}