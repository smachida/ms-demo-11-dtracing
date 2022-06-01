#!/bin/bash
docker build -t ms-demo-11-dtracing-gateway .
docker images | grep gateway
