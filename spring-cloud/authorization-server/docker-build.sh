#!/bin/bash
docker build -t ms-demo-11-dtracing-authorization-server .
docker images | grep authorization-server
