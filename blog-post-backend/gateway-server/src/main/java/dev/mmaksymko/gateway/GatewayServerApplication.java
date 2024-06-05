package dev.mmaksymko.gateway;

import dev.mmaksymko.gateway.configs.FrontEndProperties;
import dev.mmaksymko.gateway.configs.GeoLite2Properties;
import dev.mmaksymko.gateway.configs.redis.RedisProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
@EnableConfigurationProperties({GeoLite2Properties.class, FrontEndProperties.class, RedisProperties.class})
public class GatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServerApplication.class, args);
	}

}
