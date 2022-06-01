mongoimport -u mongo -p mongo --db recommendation-db --collection recommendations --file /docker-entrypoint-initdb.d/recommendations.json --jsonArray
