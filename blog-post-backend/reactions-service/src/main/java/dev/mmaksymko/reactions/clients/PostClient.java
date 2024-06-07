package dev.mmaksymko.reactions.clients;

import dev.mmaksymko.reactions.configs.web.OpenFeignClientConfiguration;
import dev.mmaksymko.reactions.models.Post;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "blog-post-service", configuration = OpenFeignClientConfiguration.class)
public interface PostClient {
    @GetMapping("/posts/{id}/")
    Post getPost(@PathVariable Long id);
}
