#!/bin/sh

echo "KAFKA_TOPIC_CREATE_ODE=$KAFKA_TOPIC_CREATE_ODE"
echo "KAFKA_TOPIC_CREATE_GEOJSONCONVERTER=$KAFKA_TOPIC_CREATE_GEOJSONCONVERTER"
echo "KAFKA_TOPIC_CREATE_CONFLICTMONITOR=$KAFKA_TOPIC_CREATE_CONFLICTMONITOR"
echo "KAFKA_TOPIC_CREATE_DEDUPLICATOR=$KAFKA_TOPIC_CREATE_DEDUPLICATOR"

# Validate and log the filled-in template
./jikkou validate \
     --files kafka-topics-template.jinja \
     --values-files kafka-topics-values.yaml 

# Create or update topics
./jikkou apply \
    --files kafka-topics-template.jinja \
    --values-files kafka-topics-values.yaml 

# Set the maximum number of retries
MAX_RETRIES=5
RETRY_COUNT=0

# Retry the health check until it is ready or the retry limit is reached
# This assumes that if the retry limit is reached that kafka connect is not deployed
until ./jikkou health get kafkaconnect | yq -e '.status.name == "UP"' > /dev/null; do
     if [ "$RETRY_COUNT" -ge "$MAX_RETRIES" ]; then
          echo "Assuming Kafka Connect is not deployed, exiting."
          exit 1
     fi
     echo "Waiting 10 sec for Kafka Connect to be ready (Attempt: $((RETRY_COUNT+1))/$MAX_RETRIES)"
     RETRY_COUNT=$((RETRY_COUNT+1))
     sleep 10
done

./jikkou health get kafkaconnect

# Run the validate and apply scripts
./jikkou validate \
      --files kafka-connectors-template.jinja \
      --values-files kafka-connectors-values.yaml 

./jikkou apply \
     --files kafka-connectors-template.jinja \
      --values-files kafka-connectors-values.yaml 