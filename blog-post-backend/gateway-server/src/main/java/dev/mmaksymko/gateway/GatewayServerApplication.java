package dev.mmaksymko.gateway;

import dev.mmaksymko.gateway.configs.FrontEndProperties;
import dev.mmaksymko.gateway.configs.GeoLite2Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({GeoLite2Properties.class, FrontEndProperties.class})
public class GatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServerApplication.class, args);
	}

}
