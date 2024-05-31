package dev.mmaksymko.reactions.dto.kafka;


import dev.mmaksymko.reactions.models.Post;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class PostEvent extends Post {
    private EventType eventType;
}
