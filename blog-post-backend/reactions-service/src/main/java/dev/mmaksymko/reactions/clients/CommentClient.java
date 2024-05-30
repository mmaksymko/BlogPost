package dev.mmaksymko.reactions.clients;

import dev.mmaksymko.reactions.configs.OpenFeignClientConfiguration;
import dev.mmaksymko.reactions.models.Comment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "comments-service", configuration = OpenFeignClientConfiguration.class)
public interface CommentClient {
    @GetMapping("{id}/base/")
    Comment getComment(@PathVariable Long id);
}

