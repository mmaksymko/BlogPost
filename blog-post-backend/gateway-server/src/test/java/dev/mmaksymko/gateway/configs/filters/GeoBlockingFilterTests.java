package dev.mmaksymko.gateway.configs.filters;

import dev.mmaksymko.gateway.configs.GeoLite2Properties;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GeoBlockingFilterTests {
    private GeoBlockingFilter geoBlockingFilter;

    @Mock
    private GeoLite2Properties geoLite2Properties;
    @Mock
    private ServerWebExchange exchange;
    @Mock
    private GatewayFilterChain chain;
    @Mock
    private ServerHttpRequest request;
    @Mock
    private ServerHttpResponse response;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);

        when(exchange.getRequest()).thenReturn(request);
        when(geoLite2Properties.localHosts()).thenReturn(List.of("127.0.0.1", "0:0:0:0:0:0:0:1"));
        when(geoLite2Properties.blockedCountries()).thenReturn(List.of("RU", "BY", "CN", "IL", "VA"));
        when(geoLite2Properties.countryLocation()).thenReturn(new ClassPathResource("GeoLite2-Country.mmdb"));
    }

    @Nested
    class FilterTests {
        @BeforeEach
        public void setup() {
            when(exchange.getRequest()).thenReturn(request);
            when(exchange.getResponse()).thenReturn(response);

            when(response.setComplete()).thenReturn(Mono.empty());

            geoBlockingFilter = spy(new GeoBlockingFilter(geoLite2Properties));
            doReturn("").when(geoBlockingFilter).getClientId(any(ServerWebExchange.class));
        }

        @Test
        @DisplayName("Should block request when isBlocked returns true")
        public void shouldBlockRequestWhenIsBlockedReturnsTrue() {
            doReturn(true).when(geoBlockingFilter).isBlocked(anyString());

            StepVerifier.create(geoBlockingFilter.filter(exchange, chain))
                    .expectComplete()
                    .verify();

            verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("Should not block request when isBlocked returns false")
        public void shouldNotBlockRequestWhenIsBlockedReturnsFalse() {
            doReturn(false).when(geoBlockingFilter).isBlocked(anyString());
            when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

            StepVerifier.create(geoBlockingFilter.filter(exchange, chain))
                    .expectComplete()
                    .verify();

            verify(response, never()).setStatusCode(HttpStatus.FORBIDDEN);
        }
    }

    @Nested
    class TestBlockedCountries {
        @BeforeEach
        public void setup() throws IOException {
            when(chain.filter(exchange)).thenReturn(Mono.empty());

            geoBlockingFilter = new GeoBlockingFilter(geoLite2Properties);
            geoBlockingFilter.init();
        }

        @ParameterizedTest
        @DisplayName("Should block request from russia")
        @CsvFileSource(resources = "/ips/russia.csv")
        public void shouldBlockRequestFromRussia(String ip) {
            final boolean isBlocked = geoBlockingFilter.isBlocked(ip);

            assertTrue(isBlocked);
        }

        @ParameterizedTest
        @DisplayName("Should block request from belarus")
        @CsvFileSource(resources = "/ips/belarus.csv")
        public void shouldBlockRequestFromBelarus(String ip) {
            final boolean isBlocked = geoBlockingFilter.isBlocked(ip);

            assertTrue(isBlocked);
        }

        @ParameterizedTest
        @DisplayName("Should block request from china")
        @CsvFileSource(resources = "/ips/china.csv")
        public void shouldBlockRequestFromChina(String ip) {
            final boolean isBlocked = geoBlockingFilter.isBlocked(ip);

            assertTrue(isBlocked);
        }

        @ParameterizedTest
        @DisplayName("Should block request from vatican city")
        @CsvFileSource(resources = "/ips/vatican_city.csv")
        public void shouldBlockRequestFromVaticanCity(String ip) {
            final boolean isBlocked = geoBlockingFilter.isBlocked(ip);

            assertTrue(isBlocked);
        }

        @ParameterizedTest
        @DisplayName("Should allow requests from non-blocked ips")
        @CsvFileSource(resources = "/ips/allowed.csv")
        public void shouldAllowRequestFromAllowedIps(String ip) {
            final boolean isBlocked = geoBlockingFilter.isBlocked(ip);

            assertFalse(isBlocked);
        }
    }
}