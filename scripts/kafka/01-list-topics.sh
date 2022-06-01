#!/bin/bash
cd ../..
docker-compose -f docker-compose-kafka.yaml exec kafka /opt/kafka/bin/kafka-topics.sh --zookeeper zookeeper --list
