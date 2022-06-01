package jp.vmware.sol.microservices.core.recommendation.services;

import jp.vmware.sol.api.core.recommendation.Recommendation;
import jp.vmware.sol.api.core.recommendation.RecommendationService;
import jp.vmware.sol.api.event.Event;
import jp.vmware.sol.util.exceptions.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class MessageProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final RecommendationService recommendationService;

    @Autowired
    public MessageProcessor(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    // 非同期ストリーミングイベントの処理
    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Recommendation> event) {
        LOG.info("Process message {} created at {}...",
                event.getData(), event.getEventCreatedAt());

        switch (event.getEventType()) {
            case CREATE:
                Recommendation recommendation = event.getData();
                LOG.info("Create recommendation with id: {}/{}",
                        recommendation.getProductId(), recommendation.getRecommendationId());
                recommendationService.createRecommendation(recommendation);
                break;
            case DELETE:
                int productId = event.getKey();
                LOG.info("Delete  recommendations with ProductId: {}", productId);
                recommendationService.deleteRecommendations(productId);
                break;
            default:
                String errorMessgage = "Incorrect event type: " + event.getEventType() +
                        ", expected a CREATE or DELETE event";
                LOG.warn(errorMessgage);
                throw new EventProcessingException(errorMessgage);
        }

        LOG.info("Message processing done! {}", event.getData());
    }
}
