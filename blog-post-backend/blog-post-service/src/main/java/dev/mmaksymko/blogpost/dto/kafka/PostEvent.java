package dev.mmaksymko.blogpost.dto.kafka;


import dev.mmaksymko.blogpost.dto.PostResponse;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostEvent extends PostResponse {
    private final EventType eventType;

    public PostEvent(PostResponse postResponse, EventType eventType) {
        super(postResponse.getId(), postResponse.getTitle(), postResponse.getContent(), postResponse.getAuthorId(), postResponse.getPostedAt());
        this.eventType = eventType;
    }
}