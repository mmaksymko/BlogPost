package dev.mmaksymko.reactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableAsync
@EnableScheduling
@EnableJpaRepositories(basePackages = "dev.mmaksymko.reactions.repositories.jpa")
@EnableRedisRepositories(basePackages = "dev.mmaksymko.reactions.repositories.redis")
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class ReactionsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactionsServiceApplication.class, args);
	}

}
