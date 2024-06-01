package dev.mmaksymko.reactions.repositories.jpa;

import dev.mmaksymko.reactions.models.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionTypeRepository extends JpaRepository<ReactionType, Long> {
    Optional<ReactionType> findByName(String name);
}

