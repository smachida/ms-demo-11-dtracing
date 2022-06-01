package jp.vmware.sol.microservices.core.review.services;

import jp.vmware.sol.api.core.review.Review;
import jp.vmware.sol.api.core.review.ReviewService;
import jp.vmware.sol.microservices.core.review.persistence.ReviewEntity;
import jp.vmware.sol.microservices.core.review.persistence.ReviewRepository;
import jp.vmware.sol.util.exceptions.InvalidInputException;
import jp.vmware.sol.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ReviewServiceImpl implements ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final ReviewRepository repository;
    private final ReviewMapper mapper;
    private final Scheduler scheduler;

    @Autowired
    public ReviewServiceImpl(Scheduler scheduler, ReviewRepository repository, ReviewMapper mapper, ServiceUtil serviceUtil) {
        this.scheduler = scheduler;
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Review createReview(Review review) {
        try {
            ReviewEntity entity = mapper.apiToEntity(review);
            ReviewEntity newEntity = repository.save(entity);

            LOG.debug("createReview: created a review entity: {}/{}", review.getProductId(), review.getReviewId());
            return mapper.entityTiApi(newEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidInputException("Duplicate key, Product Id: " + review.getProductId() +
                    ", Review Id: " + review.getReviewId());
        }
    }

    @Override
    public Flux<Review> getReviews(int productId) {
        if (productId < 1)
            throw new InvalidInputException("Invalid productId: " + productId);

        return asyncFlux(getByProductId(productId)).log();
    }

    protected List<Review> getByProductId(int productId) {
        List<ReviewEntity> entityList = repository.findByProductId(productId);
        List<Review> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getReviews: response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteReviews(int productId) {
        LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }

    private <T> Flux<T> asyncFlux(Iterable<T> iterable) {
        return Flux.fromIterable(iterable).publishOn(scheduler);
    }
}
