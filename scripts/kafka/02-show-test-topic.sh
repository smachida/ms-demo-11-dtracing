#!/bin/bash
# topic name: products, recommendations or reviews
cd ../..
docker-compose -f docker-compose-kafka.yaml exec kafka /opt/kafka/bin/kafka-topics.sh --describe --zookeeper zookeeper --topic test-topic
