package dev.mmaksymko.images.configs.minio;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
public record MinioProperties(
    String url,
    String accessKey,
    String secretKey
){}
