#!/bin/bash
docker build -t ms-demo-11-dtracing-review-service .
docker images | grep review-service
