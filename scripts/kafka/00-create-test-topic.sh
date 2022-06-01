#!/bin/bash
cd ../..
docker-compose -f docker-compose-kafka.yaml exec kafka /opt/kafka/bin/kafka-topics.sh --zookeeper zookeeper --create --topic test-topic --partitions 1 --replication-factor 1
