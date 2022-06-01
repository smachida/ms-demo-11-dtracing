#!/bin/bash
ACCESS_TOKEN=$(curl -k https://writer:secret@localhost:8443/oauth/token -d grant_type=password -d username=vmware -d password=password -s | jq -r .access_token)
echo $ACCESS_TOKEN
