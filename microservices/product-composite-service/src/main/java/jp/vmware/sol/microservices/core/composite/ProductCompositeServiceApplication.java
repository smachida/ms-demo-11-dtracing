package jp.vmware.sol.microservices.core.composite;

import jp.vmware.sol.microservices.core.composite.product.services.ProductCompositeIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@EnableSwagger2WebFlux
@SpringBootApplication
@ComponentScan("jp.vmware.sol")
public class ProductCompositeServiceApplication {

    @Value("${api.common.version:}")           String apiVersion;
    @Value("${api.common.title:}")             String apiTitle;
    @Value("${api.common.description:}")       String apiDescription;
    @Value("${api.common.termsOfServiceUrl:}") String apiTermsOfServiceUrl;
    @Value("${api.common.license:}")           String apiLicense;
    @Value("${api.common.licenseUrl:}")        String apiLicenseUrl;
    @Value("${api.common.contact.name:}")      String apiContactName;
    @Value("${api.common.contact.url:}")       String apiContactUrl;
    @Value("${api.common.contact.email:}")     String apiContactEmail;


    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Docket apiDocumentation() {
        return new Docket(SWAGGER_2)
                .select()
                .apis(basePackage("jp.vmware.sol.microservices.core.composite"))
                .paths(PathSelectors.any())
                .build()
                .globalResponseMessage(GET, emptyList())
                .apiInfo(new ApiInfo(
                   apiTitle,
                   apiDescription,
                   apiVersion,
                   apiTermsOfServiceUrl,
                   new Contact(apiContactName, apiContactUrl, apiContactEmail),
                        apiLicense,
                        apiLicenseUrl,
                        emptyList()

                ));
    }

//    @Autowired
//    HealthAggregator healthAggregator;

    @Autowired
    ProductCompositeIntegration integration;

    @Bean
    ReactiveHealthContributor coreServices() {
        Map<String, ReactiveHealthIndicator> contributorMap = new HashMap<>();
        contributorMap.put("product", new ReactiveHealthIndicator() {
            @Override
            public Mono<Health> health() {
                return integration.getProductHealth();
            }
        });
        contributorMap.put("recommendation", new ReactiveHealthIndicator() {
            @Override
            public Mono<Health> health() {
                return integration.getRecommendationHealth();
            }
        });
        contributorMap.put("review", new ReactiveHealthIndicator() {
            @Override
            public Mono<Health> health() {
                return integration.getReviewHealth();
            }
        });
        return CompositeReactiveHealthContributor.fromMap(contributorMap);
    }

    /*
    @Bean
    ReactiveHealthIndicator coreServices() {
        ReactiveHealthIndicatorRegistry registry =
                new DefaultReactiveHealthIndicatorRegistry(new LinkedHashMap<>());
        registry.register("product", () -> integration.getProductHealth());
        registry.register("recommendation", () -> integration.getRecommendationHealth());
        registry.register("review", () -> integration.getReviewHealth());

        return new CompositeReactiveHealthIndicator(healthAggregator, registry);
    }
     */

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        final WebClient.Builder builder = WebClient.builder();
        return builder;
    }

    public static void main(String[] args) {
        SpringApplication.run(ProductCompositeServiceApplication.class, args);
    }
}
