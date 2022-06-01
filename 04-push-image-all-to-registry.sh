#!/bin/bash

HARBOR_HOST=172.16.140.11

echo "pushing the images to the registry: $HARBOR_HOST"
docker login $HARBOR_HOST

docker tag ms-demo-11-dtracing-product-service $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-product-service
docker push $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-product-service
docker tag ms-demo-11-dtracing-recommendation-service $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-recommendation-service
docker push $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-recommendation-service
docker tag ms-demo-11-dtracing-review-service $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-review-service
docker push $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-review-service
docker tag ms-demo-11-dtracing-product-composite-service $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-product-composite-service
docker push $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-product-composite-service
docker tag ms-demo-11-dtracing-eureka-server $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-eureka-server
docker push $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-eureka-server
docker tag ms-demo-11-dtracing-gateway $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-gateway
docker push $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-gateway
docker tag ms-demo-11-dtracing-authorization-server $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-authorization-server
docker push $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-authorization-server
docker tag ms-demo-11-dtracing-config-server $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-config-server
docker push $HARBOR_HOST/ms-demo/ms-demo-11-dtracing-config-server
