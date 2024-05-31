package dev.mmaksymko.comments.dto.kafka;

import dev.mmaksymko.comments.dto.BaseCommentResponse;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class CommentEvent extends BaseCommentResponse {
    private final EventType eventType;

    public CommentEvent(BaseCommentResponse commentResponse, EventType eventType) {
        super(commentResponse);
        this.eventType = eventType;
    }
}