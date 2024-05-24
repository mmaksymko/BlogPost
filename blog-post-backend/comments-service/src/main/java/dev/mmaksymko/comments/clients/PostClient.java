package dev.mmaksymko.comments.clients;

import dev.mmaksymko.comments.configs.OpenFeignClientConfiguration;
import dev.mmaksymko.comments.models.Post;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "blog-post-service", configuration = OpenFeignClientConfiguration.class)
public interface PostClient {
    @GetMapping("/posts/{id}/")
    Post getPost(@PathVariable Long id);
}
