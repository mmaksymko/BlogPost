package dev.mmaksymko.reactions.dto.kafka;

import dev.mmaksymko.reactions.models.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class CommentEvent extends Comment {
    private EventType eventType;
}
