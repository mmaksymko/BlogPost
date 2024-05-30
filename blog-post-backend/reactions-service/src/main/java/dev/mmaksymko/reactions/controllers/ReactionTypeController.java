package dev.mmaksymko.reactions.controllers;

import dev.mmaksymko.reactions.dto.ReactionTypeRequest;
import dev.mmaksymko.reactions.dto.ReactionTypeResponse;
import dev.mmaksymko.reactions.services.ReactionTypeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reaction-types/")
@AllArgsConstructor
public class ReactionTypeController {
    private final ReactionTypeService reactionTypeService;

    @GetMapping
    public Page<ReactionTypeResponse> getReactionTypes(
            @RequestParam(required = false) Pageable pageable
    ) {
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        return reactionTypeService.getPageOfReactionTypes(pageable);
    }

    @GetMapping("{id}/")
    public ReactionTypeResponse getReactionType(@PathVariable Long id) {
        return reactionTypeService.getReactionTypeById(id);
    }

    @GetMapping("name/{name}/")
    public ReactionTypeResponse getReactionTypeByName(@PathVariable String name) {
        return reactionTypeService.getReactionTypeByName(name);
    }

    @PostMapping
    public ReactionTypeResponse addReactionType(@RequestBody ReactionTypeRequest reactionType) {
        return reactionTypeService.addReactionType(reactionType);
    }

    @PutMapping("{id}/")
    public ReactionTypeResponse updateReactionType(
            @PathVariable Long id,
            @RequestBody ReactionTypeRequest reactionType
    ) {
        return reactionTypeService.updateReactionType(id, reactionType);
    }

    @DeleteMapping("{id}/")
    public void deleteReactionType(@PathVariable Long id) {
        reactionTypeService.deleteReactionTypeById(id);
    }
}
