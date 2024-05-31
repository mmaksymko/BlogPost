package dev.mmaksymko.comments.dto.kafka;

import dev.mmaksymko.comments.models.Post;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class PostEvent extends Post {
    private EventType eventType;
}
