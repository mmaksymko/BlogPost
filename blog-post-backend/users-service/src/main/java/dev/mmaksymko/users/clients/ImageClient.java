package dev.mmaksymko.users.clients;

import dev.mmaksymko.users.configs.web.OpenFeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "images-service", configuration = OpenFeignClientConfiguration.class)
public interface ImageClient {
    @PostMapping(value="/images/{bucket}/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadImage(@PathVariable String bucket, @RequestPart("file") MultipartFile file);
}
