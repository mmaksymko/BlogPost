package dev.mmaksymko.images.services;

import dev.mmaksymko.images.configs.exceptions.BucketCreationException;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.minio.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ImageService {
    private final MinioClient minioClient;

    public byte[] getImage(String bucketName, String objectName) {
        try {
            GetObjectArgs object = GetObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();

            InputStream stream = minioClient.getObject(object);
            return stream.readAllBytes();
        } catch (Exception e) {
            throw new NoSuchElementException("Error while getting image", e);
        }
    }

    @Retry(name = "retry-image")
    @RateLimiter(name = "rate-limit-image")
    public String uploadImage(String bucketName, MultipartFile image) {
        validateImage(image);
        createBucketIfNotExists(bucketName);
        try {
            InputStream inputStream = image.getInputStream();

            PutObjectArgs object = PutObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(System.currentTimeMillis() + "_" + image.getOriginalFilename())
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType(image.getContentType())
                    .build();

            ObjectWriteResponse response = minioClient.putObject(object);
            return response.object();
        } catch (Exception e) {
            throw new NoSuchElementException("Error while uploading image", e);
        }
    }

    public void deleteImage(String bucketName, String objectName) {
        try {
            RemoveObjectArgs object = RemoveObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();

            minioClient.removeObject(object);
        } catch (Exception e) {
            throw new NoSuchElementException("Error while deleting image", e);
        }
    }

    private void createBucketIfNotExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new BucketCreationException("Error while creating bucket " + e);
        }
    }

    private void validateImage(MultipartFile image) {
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file type. Only image files are accepted.");
        }
    }
}
