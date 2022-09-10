#!/bin/sh
# taken from: https://github.com/damklis/DataEngineeringProject/blob/develop/connect/connectors/register_connectors.sh

echo -e "Waiting for Kafka Connect to start listening on localhost"
while [ $(curl -s -o /dev/null -w %{http_code} http://localhost:8083/connectors) -ne 200 ] ; do 
  echo -e $(date) " Kafka Connect listener HTTP state: " $(curl -s -o /dev/null -w %{http_code} http://localhost:8083/connectors) " (waiting for 200)"
  sleep 10 
done
echo -e $(date) "Kafka Connect is ready! Listener HTTP state: " $(curl -s -o /dev/null -w %{http_code} http://localhost:8083/connectors)

cd /etc/kafka-connect/connectors
for connector in *.json; do
  echo @$connector
  curl -s -X "POST" "http://localhost:8083/connectors" -H "Content-Type: application/json" -d @$connector
  sleep 10
done

sleep infinity