package jp.vmware.sol.api.core.review;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ReviewService {

    @PostMapping(
            value = "/review",
            consumes = "application/json",
            produces = "application/json"
    )
    Review createReview(@RequestBody Review review);

    @GetMapping(
            value = "/review",
            produces = "application/json"
    )
    Flux<Review> getReviews(
            @RequestParam(value = "productId", required = true) int productId);

    @DeleteMapping(value = "/review")
    void deleteReviews(@RequestParam(value = "productId", required = true) int productId);
}
