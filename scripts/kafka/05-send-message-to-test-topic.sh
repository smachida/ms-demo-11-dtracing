#!/bin/bash
cd ../..
docker-compose -f docker-compose-kafka.yaml exec kafka /opt/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic
