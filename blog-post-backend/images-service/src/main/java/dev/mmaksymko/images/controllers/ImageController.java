package dev.mmaksymko.images.controllers;

import dev.mmaksymko.images.services.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/images/")
@AllArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/{bucket}/{name}/")
    public ResponseEntity<byte[]> getImage(@PathVariable String bucket, @PathVariable String name) {
        byte[] imageBytes = imageService.getImage(bucket, name);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name);

        return ResponseEntity.ok()
                .headers(headers)
                .body(imageBytes);
    }

    @PostMapping("/{bucket}/")
    public String uploadImage(@RequestPart("file") MultipartFile file, @PathVariable String bucket) {
        return imageService.uploadImage(bucket, file);
    }

    @DeleteMapping("/{bucket}/{name}/")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public void deleteImage(@PathVariable String bucket, @PathVariable String name) {
        imageService.deleteImage(bucket, name);
    }
}
