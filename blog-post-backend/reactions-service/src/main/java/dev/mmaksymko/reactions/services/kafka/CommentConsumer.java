package dev.mmaksymko.reactions.services.kafka;

import dev.mmaksymko.reactions.dto.kafka.CommentEvent;
import dev.mmaksymko.reactions.dto.kafka.EventType;
import dev.mmaksymko.reactions.services.redis.RedisCommentService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentConsumer {
    private final RedisCommentService commentService;

    @KafkaListener(id = "reactions-comment-event-listener", topics = "comment-event", autoStartup = "false")
    public void postEventListener(@Payload CommentEvent commentEvent) {
        switch(commentEvent.getEventType()) {
            case EventType.CREATED -> commentService.saveComment(commentEvent);
            case EventType.UPDATED -> commentService.updateComment(commentEvent);
            case EventType.DELETED -> commentService.deleteComment(commentEvent.getCommentId());
            default -> throw new IllegalArgumentException("Unexpected event type: " + commentEvent.getEventType());
        }
    }
}
