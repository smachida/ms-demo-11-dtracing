#!/bin/bash
docker build -t ms-demo-11-dtracing-product-service .
docker images | grep product-service
