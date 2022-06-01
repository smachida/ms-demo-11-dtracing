#!/bin/bash
docker build -t ms-demo-11-dtracing-product-composite-service .
docker images | grep product-composite-service
