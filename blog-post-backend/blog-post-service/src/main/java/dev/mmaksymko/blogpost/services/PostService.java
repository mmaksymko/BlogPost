package dev.mmaksymko.blogpost.services;

import dev.mmaksymko.blogpost.configs.exceptions.ForbiddenException;
import dev.mmaksymko.blogpost.configs.security.Claims;
import dev.mmaksymko.blogpost.dto.PostRequest;
import dev.mmaksymko.blogpost.dto.PostResponse;
import dev.mmaksymko.blogpost.mappers.PostMapper;
import dev.mmaksymko.blogpost.models.Post;
import dev.mmaksymko.blogpost.repositories.PostRepository;
import dev.mmaksymko.blogpost.services.kafka.PostProducer;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostProducer postProducer;
    private final Claims claims;

    public Page<PostResponse> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(postMapper::toResponse);
    }

    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow();

        return postMapper.toResponse(post);
    }

    public Page<PostResponse> getPosts(Pageable pageable, Long authorId) {
        if (authorId == null) {
            return getPosts(pageable);
        }
        return postRepository.findAllByAuthorId(authorId, pageable).map(postMapper::toResponse);
    }

    @Transactional
    @Modifying
    @RateLimiter(name = "rate-limit-post")
    @Retry(name = "retry-post")
    public PostResponse addPost(PostRequest requestPost) {
        Post post = postMapper.toEntity(requestPost, getUserId());

        Post savedPost = postRepository.save(post);

        PostResponse postResponse = postMapper.toResponse(savedPost);

        postProducer.sendCreatedEvent(postResponse);

        return postResponse;
    }

    @Transactional
    @Modifying
    @RateLimiter(name = "rate-limit-post")
    @Retry(name = "retry-post")
    public PostResponse updatePost(Long id, PostRequest requestPost) {
        Post retrievedPost = postRepository.findById(id).orElseThrow();

        if (!isUserAllowedToModify(retrievedPost.getAuthorId())) {
            throw new ForbiddenException("You don't have permission to modify this user");
        }

        retrievedPost.setTitle(requestPost.title());
        retrievedPost.setContent(requestPost.content());
        retrievedPost.setHeaderImageURL(requestPost.headerImageURL());

        Post savedPost = postRepository.save(retrievedPost);

        PostResponse postResponse = postMapper.toResponse(savedPost);

        postProducer.sendUpdatedEvent(postResponse);

        return postResponse;
    }

    @Transactional
    @Modifying
    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow();

        if (!isUserAllowedToModify(post.getAuthorId())) {
            throw new ForbiddenException("You don't have permission to modify this user");
        }

        postRepository.deleteById(id);

        PostResponse postResponse = PostResponse.builder().id(id).build();

        postProducer.sendDeletedEvent(postResponse);
    }

    private boolean isUserAllowedToModify(Long userId) {
        return Optional.ofNullable(claims.getClaim("role"))
                .map(Object::toString)
                .map(role -> role.equals("ADMIN") || role.equals("SUPER_ADMIN") || userId.equals(getUserId()))
                .orElse(false);
    }

    private Long getUserId() {
        return Optional.ofNullable(claims.getClaim("id"))
                .map(Object::toString)
                .map(Long::parseLong)
                .orElse(null);
    }
}
