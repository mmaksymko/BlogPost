package dev.mmaksymko.reactions.services;

import dev.mmaksymko.reactions.configs.security.Claims;
import dev.mmaksymko.reactions.dto.ReactionTypeRequest;
import dev.mmaksymko.reactions.dto.ReactionTypeResponse;
import dev.mmaksymko.reactions.mappers.ReactionTypeMapper;
import dev.mmaksymko.reactions.models.ReactionType;
import dev.mmaksymko.reactions.repositories.jpa.ReactionTypeRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ReactionTypeService {
    private final ReactionTypeRepository reactionTypeRepository;
    private final ReactionTypeMapper reactionTypeMapper;
    private final Claims claims;

    public Page<ReactionTypeResponse> getPageOfReactionTypes(Pageable pageable) {
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        return reactionTypeRepository.findAll(pageable)
                .map(reactionTypeMapper::toResponse);
    }


    @Cacheable("reactionTypes")
    public Map<String, ReactionType> getAllReactionTypes() {
        return reactionTypeRepository.findAll().stream()
                .collect(Collectors.toMap(ReactionType::getName, Function.identity()));
    }

    public ReactionTypeResponse getReactionTypeById(Long id) {
        return reactionTypeRepository.findById(id)
                .map(reactionTypeMapper::toResponse)
                .orElseThrow();
    }

    public ReactionTypeResponse getReactionTypeByName(String name) {
        return reactionTypeRepository.findByName(name)
                .map(reactionTypeMapper::toResponse)
                .orElseThrow();
    }

    @CacheEvict(value = "reactionTypes", allEntries = true)
    @Transactional
    @Modifying
    @Retry(name = "retry-reaction")
    @RateLimiter(name = "rate-limit-reaction")
    public ReactionTypeResponse addReactionType(ReactionTypeRequest request) {
        ReactionType reactionType = reactionTypeMapper.toEntity(request);

        ReactionType saved = reactionTypeRepository.save(reactionType);

        return reactionTypeMapper.toResponse(saved);
    }

    @CacheEvict(value = "reactionTypes", allEntries = true)
    @Transactional
    @Modifying
    @Retry(name = "retry-reaction")
    @RateLimiter(name = "rate-limit-reaction")
    public ReactionTypeResponse updateReactionType(Long id, ReactionTypeRequest request) {
        ReactionType retrieved = reactionTypeRepository.findById(id).orElseThrow();

        ReactionType updated = reactionTypeMapper.toEntity(id, request);

        ReactionType saved = reactionTypeRepository.save(updated);

        return reactionTypeMapper.toResponse(saved);
    }

    @CacheEvict(value = "reactionTypes", allEntries = true)
    @Transactional
    @Modifying
    public void deleteReactionTypeById(Long id) {
        reactionTypeRepository.deleteById(id);
    }
}
