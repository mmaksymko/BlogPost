package dev.mmaksymko.images;

import dev.mmaksymko.images.configs.MinioProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MinioProperties.class)
public class ImagesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImagesServiceApplication.class, args);
	}

}
