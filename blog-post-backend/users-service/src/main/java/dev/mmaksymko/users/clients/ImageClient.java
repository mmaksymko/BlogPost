package dev.mmaksymko.users.clients;

import dev.mmaksymko.users.configs.web.OpenFeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "images-service", configuration = OpenFeignClientConfiguration.class)
public interface ImageClient {
    @PostMapping("/images/{bucket}/")
    String uploadImage(@RequestPart("file") MultipartFile file, @PathVariable String bucket);
}
