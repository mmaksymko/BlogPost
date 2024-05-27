package dev.mmaksymko.gateway.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.util.List;

@ConfigurationProperties(prefix = "geolite2")
public record GeoLite2Properties(
    Resource countryLocation,
    List<String> blockedCountries,
    List<String> localHosts
) {}
