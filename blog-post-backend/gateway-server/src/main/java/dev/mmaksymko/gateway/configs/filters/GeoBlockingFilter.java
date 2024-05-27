package dev.mmaksymko.gateway.configs.filters;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import dev.mmaksymko.gateway.configs.GeoLite2Properties;
import jakarta.annotation.PostConstruct;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class GeoBlockingFilter implements GlobalFilter {
    private final GeoLite2Properties geoLite2Properties;
    private DatabaseReader databaseReader;

    public GeoBlockingFilter(GeoLite2Properties geoLite2Properties) {
        this.geoLite2Properties = geoLite2Properties;
    }

    @PostConstruct
    public void init() throws IOException {
        databaseReader = new DatabaseReader.Builder(geoLite2Properties.countryLocation().getInputStream()).build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = getClientId(exchange);

        if (isBlocked(clientIp)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange).or(Mono.empty());
    }

    boolean isBlocked(String ip) {
        if (geoLite2Properties.localHosts().contains(ip)) {
            return false;
        }
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CountryResponse response = databaseReader.country(ipAddress);
            String country = response.getCountry().getIsoCode();
            return geoLite2Properties.blockedCountries().stream().anyMatch(country::equalsIgnoreCase);
        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    String getClientId(ServerWebExchange exchange){
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("X-Forwarded-For"))
                .orElseGet(() -> Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
    }
}
