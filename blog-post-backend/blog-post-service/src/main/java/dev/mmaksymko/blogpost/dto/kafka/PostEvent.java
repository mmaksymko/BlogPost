package dev.mmaksymko.blogpost.dto.kafka;


import dev.mmaksymko.blogpost.dto.PostResponse;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class PostEvent extends PostResponse {
    private final EventType eventType;

    public PostEvent(PostResponse postResponse, EventType eventType) {
        super(postResponse);
        this.eventType = eventType;
    }
}