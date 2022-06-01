package jp.vmware.sol.api.composite.product;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Api("製品情報のための REST API")
public interface ProductCompositeService {

    /**
     * 利用例:
     * curl -X POST $HOST:$PORT/product-composite \
     *   -H "Content-Type: application/json" --data \
     *   '{"productId":123, "name":"product 123", "weight":123}'
     * @param body
     */
    @ApiOperation(
            value = "${api.product-composite.create-composite-product.description}",
            notes = "${api.product-composite.create-composite-product.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, 不正なフォーマット"),
            @ApiResponse(code = 422, message = "Unprocessable entity, 指定したパラメータで処理を継続できません")
    })
    @PostMapping(
            value = "/product-composite",
            consumes = "application/json"
    )
    Mono<Void> createCompositeProduct(@RequestBody ProductAggregate body);

    /**
     * 利用例: curl $HOST:$PORT/product-composite/1
     * @param productId
     * @return 製品情報。見つからない場合は null
     */
    @ApiOperation(
            value = "${api.product-composite.get-composite-product.description}",
            notes = "${api.product-composite.get-composite-product.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, 不正なフォーマット"),
            @ApiResponse(code = 404, message = "Not Found, 指定した id に対応する製品が見つかりません"),
            @ApiResponse(code = 422, message = "Unprocessable entity, 指定したパラメータで処理を継続できません")
    })
    @GetMapping(
            value = "/product-composite/{productId}",
            produces = "application/json"
    )
    Mono<ProductAggregate> getCompositeProduct(
            @PathVariable int productId,
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent);

    /**
     * 利用例:
     * curl -X DELETE $HOST:$PORT/product-composite/1

     * @param productId
     */
    @ApiOperation(
            value = "${api.product-composite.delete-composite-product.description}",
            notes = "${api.product-composite.delete-composite-product.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, 不正なフォーマット"),
            @ApiResponse(code = 422, message = "Unprocessable entity, 指定したパラメータで処理を継続できません")
    })
    @DeleteMapping(
            value = "/product-composite/{productId}"
    )
    Mono<Void> deleteCompositeProduct(@PathVariable int productId);
}
