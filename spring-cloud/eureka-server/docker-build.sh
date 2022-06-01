#!/bin/bash
docker build -t ms-demo-11-dtracing-eureka-server .
docker images | grep eureka-server
