#!/bin/bash
docker build -t ms-demo-11-dtracing-recommendation-service .
docker images | grep recommendation-service
