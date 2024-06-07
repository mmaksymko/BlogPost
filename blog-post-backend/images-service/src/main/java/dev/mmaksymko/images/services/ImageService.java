package dev.mmaksymko.images.services;

import dev.mmaksymko.images.configs.minio.MinioProperties;
import dev.mmaksymko.images.configs.exceptions.BucketCreationException;
import dev.mmaksymko.images.configs.security.Claims;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.minio.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class ImageService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final Claims claims;

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
            return returnURI(bucketName, response.object());
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
                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs
                                .builder()
                                .bucket(bucketName)
                                .config(getPublicPolicy(bucketName))
                                .build()
                );
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

    private String returnURI(String bucketName, String objectName) {
        String host = getHostFromUrl(minioProperties.url());
        return String.format("%s/%s/%s", host, bucketName, objectName);
    }

    private String getHostFromUrl(String url) {
        Pattern pattern = Pattern.compile("http://minio:(\\d+)/");
        Matcher matcher = pattern.matcher(url);

        return matcher.find()
                ? String.format("http://localhost:%s/", matcher.group(1))
                : url;
    }

    private String getPublicPolicy(String bucketName) {
        return String.format("""
        {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Sid": "PublicRead",
                    "Effect": "Allow",
                    "Principal": "*",
                    "Action": "s3:GetObject",
                    "Resource": "arn:aws:s3:::%s/*"
                }
            ]
        }
        """, bucketName);
    }
}
