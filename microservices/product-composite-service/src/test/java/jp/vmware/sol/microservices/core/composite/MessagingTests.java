package jp.vmware.sol.microservices.core.composite;

import jp.vmware.sol.api.composite.product.ProductAggregate;
import jp.vmware.sol.api.composite.product.RecommendationSummary;
import jp.vmware.sol.api.composite.product.ReviewSummary;
import jp.vmware.sol.api.core.product.Product;
import jp.vmware.sol.api.core.recommendation.Recommendation;
import jp.vmware.sol.api.core.review.Review;
import jp.vmware.sol.api.event.Event;
import jp.vmware.sol.microservices.core.composite.product.services.ProductCompositeIntegration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.BlockingQueue;

import static java.util.Collections.singletonList;
import static jp.vmware.sol.api.event.Event.Type.CREATE;
import static jp.vmware.sol.api.event.Event.Type.DELETE;
import static jp.vmware.sol.microservices.core.composite.IsSameEvent.sameEventExceptCreatedAt;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;
import static org.springframework.http.HttpStatus.OK;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT,
        classes = {ProductCompositeServiceApplication.class, TestSecurityConfig.class },
        properties = {"spring.main.allow-bean-definition-overriding=true", "eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
public class MessagingTests {
    private static final int PRODUCT_ID_OK = 1;
    private static final int PRODUCT_ID_NOT_FOUND = 2;
    private static final int PRODUCT_ID_INVALID = 3;

    @Autowired
    private WebTestClient client;

    @Autowired
    private ProductCompositeIntegration.MessageSources channels;

    @Autowired
    private MessageCollector collector;

    BlockingQueue<Message<?>> queueProducts = null;
    BlockingQueue<Message<?>> queueRecommendations = null;
    BlockingQueue<Message<?>> queueReviews = null;

    @Before
    public void setup() {
        queueProducts = getQueue(channels.outputProducts());
        queueRecommendations = getQueue(channels.outputRecommendation());
        queueReviews = getQueue(channels.outputReviews());
    }

    @Test
    public void createCompositeProduct1() {
        ProductAggregate composite = new ProductAggregate(1, "name", 1, null, null, null);
        postAndVerifyProduct(composite, OK);

        assertEquals(1, queueProducts.size());

        Event<Integer, Product> expectedEvent =
                new Event(CREATE, composite.getProductId(),
                        new Product(
                                composite.getProductId(),
                                composite.getName(),
                                composite.getWeight(), null));
        assertThat(queueProducts, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

        assertEquals(0, queueRecommendations.size());
        assertEquals(0, queueReviews.size());
    }

    @Test
    public void createCompositeProduct2() {
        ProductAggregate composite = new ProductAggregate(1, "name", 1,
                singletonList(new RecommendationSummary(1, "a", 1, "c")),
                singletonList(new ReviewSummary(1, "a", "s" , "c")), null);
        postAndVerifyProduct(composite, OK);

        assertEquals(1, queueProducts.size());

        Event<Integer, Product> expectedProductEvent =
                new Event(CREATE, composite.getProductId(),
                        new Product(
                                composite.getProductId(),
                                composite.getName(),
                                composite.getWeight(), null));
        assertThat(queueProducts, receivesPayloadThat(sameEventExceptCreatedAt(expectedProductEvent)));

        assertEquals(1, queueRecommendations.size());
        RecommendationSummary recommendationSummary = composite.getRecommendations().get(0);
        Event<Integer, Product> expectedRecommendationEvent =
                new Event(CREATE, composite.getProductId(),
                        new Recommendation(
                                composite.getProductId(),
                                recommendationSummary.getRecommendationId(),
                                recommendationSummary.getAuthor(),
                                recommendationSummary.getRate(),
                                recommendationSummary.getContent(), null));
        assertThat(queueRecommendations, receivesPayloadThat(sameEventExceptCreatedAt(expectedRecommendationEvent)));

        assertEquals(1, queueReviews.size());
        ReviewSummary reviewSummary = composite.getReviews().get(0);
        Event<Integer, Product> expectedReviewEvent =
                new Event(CREATE, composite.getProductId(),
                        new Review(composite.getProductId(),
                                reviewSummary.getReviewId(),
                                reviewSummary.getAuthor(),
                                reviewSummary.getSubject(),
                                reviewSummary.getContent(), null));
        assertThat(queueReviews, receivesPayloadThat(sameEventExceptCreatedAt(expectedReviewEvent)));
    }

    @Test
    public void deleteCompositeProduct() {
        deleteAndVerifyProduct(1, OK);

        assertEquals(1, queueProducts.size());

        Event<Integer, Product> expectedEvent = new Event(DELETE, 1, null);
        assertThat(queueProducts, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

        assertEquals(1, queueRecommendations.size());
        Event<Integer, Product> expectedRecommendationEvent = new Event(DELETE, 1, null);
        assertThat(queueRecommendations, receivesPayloadThat(sameEventExceptCreatedAt(expectedRecommendationEvent)));

        assertEquals(1, queueReviews.size());
        Event<Integer, Product> expectedReviewEvent = new Event(DELETE, 1, null);
        assertThat(queueReviews, receivesPayloadThat(sameEventExceptCreatedAt(expectedReviewEvent)));
    }

    // Utility methods

    private BlockingQueue<Message<?>> getQueue(MessageChannel messageChannel) {
        return collector.forChannel(messageChannel);
    }

    private void postAndVerifyProduct(ProductAggregate compositeProduct, HttpStatus expectedStatus) {
        client.post()
                .uri("/product-composite")
                .body(just(compositeProduct), ProductAggregate.class)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    private void deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
        client.delete()
                .uri("product-composite/" + productId)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }
}
