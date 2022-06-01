#!/bin/bash
cd ../..
docker-compose -f docker-compose-kafka.yaml exec kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic $1 --from-beginning --timeout-ms 1000
