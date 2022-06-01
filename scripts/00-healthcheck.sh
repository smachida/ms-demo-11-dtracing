#!/bin/bash
curl -s -k https://localhost:8443/actuator/health | jq 
