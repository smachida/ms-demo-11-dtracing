#!/bin/bash
docker build -t ms-demo-11-dtracing-config-server .
docker images | grep config-server
