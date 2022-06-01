package jp.vmware.sol.microservices.core.product.services;

import jp.vmware.sol.api.core.product.Product;
import jp.vmware.sol.api.core.product.ProductService;
import jp.vmware.sol.microservices.core.product.persistance.ProductEntity;
import jp.vmware.sol.microservices.core.product.persistance.ProductRepository;
import jp.vmware.sol.util.exceptions.InvalidInputException;
import jp.vmware.sol.util.exceptions.NotFoundException;
import jp.vmware.sol.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Random;

import static reactor.core.publisher.Mono.error;

@RestController
public class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Autowired
    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }


    @Override
    public Product createProduct(Product product) {
        if (product.getProductId() < 1)
            throw new InvalidInputException("Invalid productId: " + product.getProductId());

        ProductEntity entity = mapper.apiToEntity(product);
        Mono<Product> newEntity =
                repository
                .save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Product Id: " + product.getProductId())
                )
                .map(e -> mapper.entityToApi(e));

        return newEntity.block();
    }

    @Override
    public Mono<Product> getProduct(int productId, int delay, int faultPercent) {
        LOG.debug("/product return the found product for productId={}", productId);
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        if (delay > 0) {
            simulateDelay(delay);
        }

        if (faultPercent > 0) {
            throwErrorIfBadLuck(faultPercent);
        }

        // Project Reactor: fluent API
        return repository.findByProductId(productId)
                .switchIfEmpty(error (new NotFoundException("No product found for Product Id: " + productId)))
                .log()
                .map(e -> mapper.entityToApi(e))
                .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;}
                );
    }

    @Override
    public void deleteProduct(int productId) {
        if (productId < 1)
            throw new InvalidInputException("Invalid productId: " + productId);

        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        repository.findByProductId(productId)
                .log()
                .map(e -> repository.delete(e))
                .flatMap(e -> e).block();
    }

    private void simulateDelay(int delay) {
        LOG.debug("Sleeping for {} seconds...", delay);

        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {}

        LOG.debug("Woke up.");
    }

    private void throwErrorIfBadLuck(int faultPercent) {
        int randomThreshold = getRandomNumber(1, 100);
        if (faultPercent < randomThreshold) {
            LOG.debug("[Simulation] No error occurred, {} < {}", faultPercent, randomThreshold);
        } else {
            LOG.debug("[Simulation] An error occurred, {} >= {}", faultPercent, randomThreshold);
            throw new RuntimeException("[Simulation] Something went wrong...");
        }
    }

    private final Random randomNumberGenerator = new Random();
    private int getRandomNumber(int min, int max) {

        if (max < min) {
            throw new RuntimeException("invalid values: min=" + min + ", max=" + max);
        }

        return randomNumberGenerator.nextInt((max - min) + 1) + min;
    }

}
