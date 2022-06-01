#!/bin/bash

cd microservices

echo "Building product-composite-service image ..."
cd product-composite-service
./docker-build.sh

echo "Building product-service image ..."
cd ../product-service
./docker-build.sh

echo "Building recommendation-service image ..."
cd ../recommendation-service
./docker-build.sh

echo "Building review-service image ..."
cd ../review-service
./docker-build.sh

echo "Building eureka-server image ..."
cd ../../spring-cloud/eureka-server
./docker-build.sh

echo "Building gateway image ..."
cd ../../spring-cloud/gateway
./docker-build.sh

echo "Building authorization-server image ..."
cd ../../spring-cloud/authorization-server
./docker-build.sh

echo "Building config-server image ..."
cd ../../spring-cloud/config-server
./docker-build.sh

echo " ---- image build done. --- "
docker images | grep ms-demo-11-dtracing
