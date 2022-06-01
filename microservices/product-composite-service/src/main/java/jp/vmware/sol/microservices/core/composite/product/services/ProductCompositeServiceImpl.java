package jp.vmware.sol.microservices.core.composite.product.services;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
//import io.github.resilience4j.reactor.retry.RetryExceptionWrapper;
import jp.vmware.sol.api.composite.product.*;
import jp.vmware.sol.api.core.product.Product;
import jp.vmware.sol.api.core.recommendation.Recommendation;
import jp.vmware.sol.api.core.review.Review;
import jp.vmware.sol.util.exceptions.NotFoundException;
import jp.vmware.sol.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);

    private final SecurityContext nullSC = new SecurityContextImpl();
    private ServiceUtil serviceUtil;
    private ProductCompositeIntegration integration;

    @Autowired
    public ProductCompositeServiceImpl(ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public Mono<Void> createCompositeProduct(ProductAggregate body) {
        return ReactiveSecurityContextHolder.getContext().doOnSuccess(
                sc -> internalCreateCompositeProduct(sc, body)).then();
    }

    public void internalCreateCompositeProduct(SecurityContext sc, ProductAggregate body) {
        try {
            logAuthorizationInfo(sc);

            // トランザクション処理なし(部分更新の可能性あり)
            LOG.debug("createCompositeProduct: create a new composite entity for productId: {}", body.getProductId());
            Product newProduct = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
            integration.createProduct(newProduct);

            if (body.getRecommendations() != null) {
                body.getRecommendations().forEach(recommendation -> {
                    Recommendation newRecommendation =
                            new Recommendation(
                                    body.getProductId(),
                                    recommendation.getRecommendationId(),
                                    recommendation.getAuthor(),
                                    recommendation.getRate(),
                                    recommendation.getContent(),
                                    null);
                    integration.createRecommendation(newRecommendation);
                });
            }

            if (body.getReviews() != null) {
                body.getReviews().forEach(review -> {
                    Review newReview =
                            new Review(
                                    body.getProductId(),
                                    review.getReviewId(),
                                    review.getAuthor(),
                                    review.getSubject(),
                                    review.getContent(),
                                    null);
                    integration.createReview(newReview);
                });
            }

            LOG.debug("createCompositeProduct: composite entities created for productId: {}", body.getProductId());

        } catch (RuntimeException ex) {
            LOG.warn("createCompositeProduct failed", ex);
            throw ex;
        }
    }

    @Override
    public Mono<ProductAggregate> getCompositeProduct(int productId, int delay, int faultPercent) {
        // Product, Recommendation, 及び Review サービス呼び出しの並行実行
        return Mono.zip(
                // lambda
                values -> createProductAggregate(
                        (SecurityContext) values[0],
                        (Product) values[1],
                        (List<Recommendation>)values[2],
                        (List<Review>)values[3],
                        serviceUtil.getServiceAddress()),
                ReactiveSecurityContextHolder.getContext().defaultIfEmpty(nullSC),
                integration.getProduct(productId, delay, faultPercent)
                .onErrorMap(TimeoutException.class, timeoutException -> timeoutException.getCause())
                //        .onErrorMap(RetryExceptionWrapper.class, retryException -> retryException.getCause())
                .onErrorReturn(CallNotPermittedException.class, getProductFallbackValue(productId)),
                integration.getRecommendations(productId).collectList(),
                integration.getReviews(productId).collectList()
        ).doOnError(ex -> LOG.warn("getCompositeProduct failed: {}", ex.toString())).log();
    }

    @Override
    public Mono<Void> deleteCompositeProduct(int productId) {
        return ReactiveSecurityContextHolder.getContext().doOnSuccess(
                sc -> internalDeleteCompositeProduct(sc, productId)).then();
    }

    private void internalDeleteCompositeProduct(SecurityContext sc, int productId) {

        try {
            logAuthorizationInfo(sc);

            // トランザクション処理なし(部分更新の可能性あり)
            LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);
            integration.deleteProduct(productId);
            integration.deleteRecommendations(productId);
            integration.deleteReviews(productId);
            LOG.debug("deleteCompositeProduct: aggregate entities deleted for productId: {}", productId);
        } catch (RuntimeException e) {
            LOG.warn("deleteCompositeProduct failed: {}", e.toString());
            throw e;
        }
    }


    private Product getProductFallbackValue(int productId) {

        LOG.warn("Creating a fallback product for productId = {}", productId);

        if (productId == 14) {
            String msg = "Product Id: " + productId + " not found in fallback cache!";
            LOG.warn(msg);
            throw new NotFoundException(msg);
        }

        return new Product(productId, "Fallback product" + productId, productId, serviceUtil.getServiceAddress());
    }


    private ProductAggregate createProductAggregate(
            SecurityContext sc,
            Product product,
            List<Recommendation> recommendations,
            List<Review> reviews,
            String serviceAddress) {

        logAuthorizationInfo(sc);

        // 1. Setup product info
        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        // 2. Copy summary recommendation info, if available
        List<RecommendationSummary> recommendationSummaries = (recommendations == null) ? null :
                recommendations.stream()
                        .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
                        .collect(Collectors.toList());

        // 3. Copy summary review info, if available
        List<ReviewSummary> reviewSummaries = (reviews == null)  ? null :
                reviews.stream()
                        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                        .collect(Collectors.toList());

        // 4. Create info regarding the involved microservices addresses
        String productAddress = product.getServiceAddress();
        String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
        String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).getServiceAddress() : "";
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);

    }

    private void logAuthorizationInfo(SecurityContext sc) {
        if (sc != null && sc.getAuthentication() != null && sc.getAuthentication() instanceof JwtAuthenticationToken) {
            Jwt jwtToken = ((JwtAuthenticationToken)sc.getAuthentication()).getToken();
            logAuthorizationInfo(jwtToken);
        } else {
            LOG.warn("No JWT based Authentication supplied, running tests are we?");
        }
    }

    private void logAuthorizationInfo(Jwt jwt) {
        if (jwt == null) {
            LOG.warn("No JWT supplied, running tests are we?");
        } else {
            if (LOG.isDebugEnabled()) {
                URL issuer = jwt.getIssuer();
                List<String> audience = jwt.getAudience();
                Object subject = jwt.getClaims().get("sub");
                Object scopes = jwt.getClaims().get("scope");
                Object expires = jwt.getClaims().get("exp");

                LOG.debug("Authorization info: Subject: {}, scopes: {}, expires {}: issuer: {}, audience: {}", subject, scopes, expires, issuer, audience);
            }
        }
    }

}
