package jp.vmware.sol.springcloud.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class HealthCheckConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckConfiguration.class);

    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;

    @Autowired
    public HealthCheckConfiguration(
            WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Bean
    ReactiveHealthContributor healthCheckMicroservices() {
        Map<String, ReactiveHealthIndicator> contributorMap = new HashMap<>();

        contributorMap.put("auth-server", () -> getHealth("http://auth-server"));
        contributorMap.put("product", () -> getHealth("http://product"));
        contributorMap.put("recommendation", () -> getHealth("http://recommendation"));
        contributorMap.put("review", () -> getHealth("http://review"));
        contributorMap.put("product-composite", () -> getHealth("http://product-composite"));

        return CompositeReactiveHealthContributor.fromMap(contributorMap);
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return getWebClient()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                .log();
    }

    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = webClientBuilder.build();
        }
        return webClient;
    }
}