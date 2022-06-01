package jp.vmware.sol.microservices.core.recommendation.services;

import jp.vmware.sol.api.core.recommendation.Recommendation;
import jp.vmware.sol.api.core.recommendation.RecommendationService;
import jp.vmware.sol.api.core.review.Review;
import jp.vmware.sol.microservices.core.recommendation.persistence.RecommendationEntity;
import jp.vmware.sol.microservices.core.recommendation.persistence.RecommendationRepository;
import jp.vmware.sol.util.exceptions.InvalidInputException;
import jp.vmware.sol.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class RecommendationServiceImpl implements RecommendationService {
    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final RecommendationRepository repository;
    private final RecommendationMapper mapper;

    @Autowired
    public RecommendationServiceImpl(RecommendationRepository repository, RecommendationMapper mapper,ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Recommendation createRecommendation(Recommendation recommendation) {
        if (recommendation.getProductId() < 1)
            throw new InvalidInputException("Invalid productId: " + recommendation.getProductId());

        RecommendationEntity entity = mapper.apiToEntity(recommendation);
        Mono<Recommendation> newEntity =
                repository
                .save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Product Id: " +
                                recommendation.getProductId() + ", Recommendation Id: " + recommendation.getRecommendationId())
                )
                .map(e -> mapper.entityToApi(e));

        return newEntity.block();
    }

    @Override
    public Flux<Recommendation> getRecommendations(int productId) {
        if (productId < 1)
            throw new InvalidInputException("Invalid productId: " + productId);

        // Project Reactor: fluent API
        return repository.findByProductId(productId)
                .log()
                .map(e -> mapper.entityToApi(e))
                .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    @Override
    public void deleteRecommendations(int productId) {
        if (productId < 1)
            throw new InvalidInputException("Invalid productId: " + productId);

        LOG.debug("deleteRecommendations tries to delete recommendations for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId)).block();
    }
}
