# jpo-utils

**US Department of Transportation (USDOT) Intelligent Transportation Systems (ITS) Joint Program Office (JPO) Utilities**

The JPO ITS utilities repository serves as a central location for deploying open-source utilities used by other JPO-ITS repositories.

<a name="toc"></a>

**Table of Contents**

- [jpo-utils](#jpo-utils)
  - [1. Configuration](#1-configuration)
    - [System Requirements](#system-requirements)
    - [Tips and Advice](#tips-and-advice)
  - [2. MongoDB](#2-mongodb)
    - [Quick Run](#quick-run)
  - [3. Kafka](#3-kafka)
    - [Configure Topic Creation](#configure-topic-creation)
    - [Quick Run](#quick-run-1)
  - [4. MongoDB Kafka Connect](#4-mongodb-kafka-connect)
    - [Configuration](#configuration)
    - [Configure Kafka Connector Creation](#configure-kafka-connector-creation)
    - [Quick Run](#quick-run-2)
  - [5. jpo-deduplicator](#5-jpo-deduplicator)
    - [Deduplication Config](#deduplication-config)
    - [Generate a Github Token](#generate-a-github-token)
    - [Quick Run](#quick-run-3)
  - [Security Notice](#security-notice)


<a name="base-configuration"></a>

## 1. Configuration

### System Requirements

-  Minimum RAM: 16 GB
-  Supported operating systems:
   -  Ubuntu 22.04 Linux (Recommended)
   -  Windows 10/11 Professional (Professional version required for Docker virtualization)
   -  OSX 10 Mojave
      -  NOTE: Not all images have ARM64 builds (they can still be ran through a compatibility layer)
-  Docker-compose V2 - version 3.4 or newer

The jpo-utils repository is intended to be ran with docker-compose v2 as it uses functionality added in the v2 release.

### Tips and Advice

Read the following guides to familiarize yourself with the jpo-utils Docker configuration.

- [Docker README](docker.md)
- [Docker Compose Profiles](https://docs.docker.com/compose/profiles/)

**Important!**
You must rename `sample.env` to `.env` for Docker to automatically read the file. Do not push this file to source control.

<a name="mongodb"></a>

## 2. MongoDB

A MongoDB instance that is initialized as a standalone replica-set and has configured users is configured in the [docker-compose-mongo](docker-compose-mongo.yml) file. To use a different `setup_mongo.sh` or `create_indexes.js` script, pass in the relative path of the new script by overriding the `KAFKA_INIT_SCRIPT_RELATIVE_PATH` or `MONGO_CREATE_INDEXES_SCRIPT_RELATIVE_PATH` environmental variables. These scripts facilitate the initialization of the MongoDB Database along with the created indexes.

Where the `COMPOSE_PROFILES` variable in you're `.env` file are as follows:

- `mongo_full` - deploys all resources in the [docker-compose-mongo.yml](docker-compose-mongo.yml) file
  - `mongo` - only deploys the `mongo` and `mongo-setup` services
  - `mongo_express` - only deploys the `mongo-express` service

### Quick Run

1. Create a copy of `sample.env` and rename it to `.env`.
2. Update the variable `DOCKER_HOST_IP` to the local IP address of the system running docker which can be found by running the `ifconfig` command
   1. Hint: look for "inet addr:" within "eth0" or "en0" for OSX
3. Set the password for `MONGO_ADMIN_DB_PASS` and `MONGO_READ_WRITE_PASS` environmental variables to a secure password.
4. Set the `COMPOSE_PROFILES` variable to: `mongo_full`
5. Run the following command: `docker-compose up -d`
6. Go to `localhost:8082` in your browser and verify that `mongo-express` can see the created database

[Back to top](#toc)

<a name="kafka"></a>

## 3. Kafka

The [Bitnami Kafka](https://hub.docker.com/r/bitnami/kafka) is being used as a hybrid controller and broker in the  [docker-compose-kafka](docker-compose-kafka.yml) file. To use a different `kafka_init.sh` script, pass in the relative path of the new script by overriding the `KAFKA_INIT_SCRIPT_RELATIVE_PATH` environmental variable. This can help in initializing new topics at startup.

An optional `kafka-init`, `schema-registry`, and `kafka-ui` instance can be deployed by configuring the `COMPOSE_PROFILES` as follows:

- `kafka_full` - deploys all resources in the [docker-compose-kafka.yml](docker-compose-kafka.yml) file
  - `kafka` - only deploys the `kafka` services
  - `kafka_setup` - deploys a `kafka-setup` service that creates topics in the `kafka` service.  
  - `kafka_schema_registry` - deploys a `kafka-schema-registry` service that can be used to manage schemas for kafka topics
  - `kafka_ui` - deploys a [web interface](https://github.com/kafbat/kafka-ui) to interact with the kafka cluster

### Configure Topic Creation

The Kafka topics created by the `kafka-setup` service are configured in the [kafka-topics-values.yaml](jikkou/kafka-topics-values.yaml) file.  The topics in that file are organized by the application, and sorted into "Stream Topics" (those with `cleanup.policy` = `delete`) and "Table Topics" (with `cleanup.policy` = `compact`).  

The following enviroment variables can be used to configure Kafka Topic creation.  

| Environment Variable | Description |
|---|---|
| `KAFKA_TOPIC_CREATE_ODE` | Whether to create topics for the ODE |
| `KAFKA_TOPIC_CREATE_GEOJSONCONVERTER` | Whether to create topics for the GeoJSON Converter |
| `KAFKA_TOPIC_CREATE_CONFLICTMONITOR` | Whether to create topics for the Conflict Monitor |
| `KAFKA_TOPIC_CREATE_DEDUPLICATOR` | Whether to create topics for the Deduplicator |
| `KAFKA_TOPIC_PARTITIONS` | Number of partitions |
| `KAFKA_TOPIC_REPLICAS` | Number of replicas |
| `KAFKA_TOPIC_MIN_INSYNC_REPLICAS` | Minumum number of in-sync replicas (for use with ack=all) |
| `KAFKA_TOPIC_RETENTION_MS` | Retention time for stream topics, milliseconds |
| `KAFKA_TOPIC_DELETE_RETENTION_MS` | Tombstone retention time for compacted topics, milliseconds |
| `KAFKA_TOPIC_CONFIG_RELATIVE_PATH` | Relative path to the Kafka topic yaml configuration script, upper level directories are supported |

### Quick Run

1. Create a copy of `sample.env` and rename it to `.env`.
2. Update the variable `DOCKER_HOST_IP` to the local IP address of the system running docker which can be found by running the `ifconfig` command
   1. Hint: look for "inet addr:" within "eth0" or "en0" for OSX
3. Set the `COMPOSE_PROFILES` variable to: `kafka_full`
4. Run the following command: `docker-compose up -d`
5. Go to `localhost:8001` in your browser and verify that `kafka-ui` can see the created kafka cluster and initialized topics

[Back to top](#toc)


<a name="mongodb-kafka-connect"></a>

## 4. MongoDB Kafka Connect
The mongo-connector service connects to specified Kafka topics and deposits these messages to separate collections in the MongoDB Database. The codebase that provides this functionality comes from Confluent using their community licensed [cp-kafka-connect image](https://hub.docker.com/r/confluentinc/cp-kafka-connect). Documentation for this image can be found [here](https://docs.confluent.io/platform/current/connect/index.html#what-is-kafka-connect).

### Configuration

Kafka connectors are managed by the 

Set the `COMPOSE_PROFILES` environmental variable as follows:

- `kafka_connect` will only spin up the `kafka-connect` and `kafka-init` services in [docker-compose-connect](docker-compose-connect.yml)
  - NOTE: This implies that you will be using a separate Kafka and MongoDB cluster
- `kafka_connect_standalone` will run the following:
  1. `kafka-connect` service from [docker-compose-connect](docker-compose-connect.yml)
  2. `kafka-init` service from [docker-compose-connect](docker-compose-connect.yml)
  3. `kafka` service from [docker-compose-kafka](docker-compose-kafka.yml)
  4. `mongo` and `mongo-setup` services from [docker-compose-mongo](docker-compose-mongo.yml)

### Configure Kafka Connector Creation

The Kafka connectors created by the `kafka-connect-setup` service are configured in the [kafka-connectors-values.yaml](jikkou/kafka-connectors-values.yaml) file.  The connectors in that file are organized by the application, and given parameters to define the Kafka -> MongoDB sync connector:

| Connector Variable  | Required | Condition | Description|
|---|---|---|---|
| `topicName` | Yes | Always | The name of the Kafka topic to sync from |
| `collectionName` | Yes | Always | The name of the MongoDB collection to write to |
| `generateTimestamp` | No | Optional | Enable or disable adding a timestamp to each message (true/false) |
| `connectorName` | No | Optional | Override the name of the connector from the `collectionName` to this field instead |
| `useTimestamp` | No | Optional | Converts the `timestampField` field at the top level of the value to a BSON date |
| `timestampField` | No | Required if `useTimestamp` is `true` | The name of the timestamp field at the top level of the message |
| `useKey` | No | Optional | Override the document `_id` field in MongoDB to use a specified `keyField` from the message |
| `keyField` | No | Required if `useKey` is `true` | The name of the key field |

The following environment variables can be used to configure Kafka Connectors:

| Environment Variable | Description |
|---|---|
| `CONNECT_URL` | Kafka connect API URL |
| `CONNECT_LOG_LEVEL` | Kafka connect log level (`OFF`, `ERROR`, `WARN`, `INFO`) |
| `CONNECT_TASKS_MAX` | Number of concurrent tasks to configure on kafka connectors |
| `CONNECT_CREATE_ODE` | Whether to create kafka connectors for the ODE |
| `CONNECT_CREATE_GEOJSONCONVERTER` | Whether to create topics for the GeojsonConverter |
| `CONNECT_CREATE_CONFLICTMONITOR` | Whether to create kafka connectors for the Conflict Monitor |
| `CONNECT_CREATE_DEDUPLICATOR` | Whether to create topics for the Deduplicator |
| `CONNECT_CONFIG_RELATIVE_PATH` | Relative path to the Kafka connector yaml configuration script, upper level directories are supported |

### Quick Run

1. Create a copy of `sample.env` and rename it to `.env`.
2. Update the variable `DOCKER_HOST_IP` to the local IP address of the system running docker
3. Set the password for `MONGO_ADMIN_DB_PASS` and `MONGO_READ_WRITE_PASS` environmental variables to a secure password.
4. Set the `COMPOSE_PROFILES` variable to: `kafka_connect_standalone,mongo_express,kafka_ui,kafka_setup`
5. Navigate back to the root directory and run the following command: `docker compose up -d`
6. Produce a sample message to one of the sink topics by using `kafka_ui` by:
   1. Go to `localhost:8001`
   2. Click local -> Topics
   3. Select `topic.OdeBsmJson`
   4. Select `Produce Message`
   5. Leave the defaults except set the `Value` field to `{"foo":"bar"}`
   6. Click `Produce  Message`
7. View the synced message in `mongo-express` by:
   1. Go to `localhost:8082`
   2. Click `ode` -- Or click whatever value you set the `MONGO_DB_NAME` to
   3. Click `OdeBsmJson`, and now you should see your message!
8. Feel free to test this with other topics or by producing to these topics using the [ODE](https://github.com/usdot-jpo-ode/jpo-ode)


<a name="deduplicator"></a>

## 5. jpo-deduplicator
The JPO-Deduplicator is a Kafka Java spring-boot application designed to reduce the number of messages stored and processed in the ODE system. This is done by reading in messages from an input topic (such as topic.ProcessedMap) and outputting a subset of those messages on a related output topic (topic.DeduplicatedProcessedMap). Functionally, this is done by removing deduplicate messages from the input topic and only passing on unique messages. In addition, each topic will pass on at least 1 message per hour even if the message is a duplicate. This behavior helps ensure messages are still flowing through the system. The following topics currently support deduplication.

- topic.ProcessedMap -> topic.DeduplicatedProcessedMap
- topic.ProcessedMapWKT -> topic.DeduplicatedProcessedMapWKT
- topic.OdeMapJson -> topic.DeduplicatedOdeMapJson
- topic.OdeTimJson -> topic.DeduplicatedOdeTimJson
- topic.OdeRawEncodedTIMJson -> topic.DeduplicatedOdeRawEncodedTIMJson
- topic.OdeBsmJson -> topic.DeduplicatedOdeBsmJson
- topic.ProcessedSpat -> topic.DeduplicatedProcessedSpat

### Deduplication Config

When running the jpo-deduplication as a submodule in jpo-utils, the deduplicator will automatically turn on deduplication for a topic when that topic is created. For example if the KAFKA_TOPIC_CREATE_GEOJSONCONVERTER environment variable is set to true, the deduplicator will start performing deduplication for ProcessedMap, ProcessedMapWKT, and ProcessedSpat data. 

To manually configure deduplication for a topic, the following environment variables can also be used.

| Environment Variable | Description |
|---|---|
| `ENABLE_PROCESSED_MAP_DEDUPLICATION` | `true` / `false` - Enable ProcessedMap message Deduplication |
| `ENABLE_PROCESSED_MAP_WKT_DEDUPLICATION` | `true` / `false` - Enable ProcessedMap WKT message Deduplication |
| `ENABLE_ODE_MAP_DEDUPLICATION` | `true` / `false` - Enable ODE MAP message Deduplication |
| `ENABLE_ODE_TIM_DEDUPLICATION` | `true` / `false` - Enable ODE TIM message Deduplication |
| `ENABLE_ODE_RAW_ENCODED_TIM_DEDUPLICATION` | `true` / `false` - Enable ODE Raw Encoded TIM Deduplication |
| `ENABLE_PROCESSED_SPAT_DEDUPLICATION` | `true` / `false` - Enable ProcessedSpat Deduplication |
| `ENABLE_ODE_BSM_DEDUPLICATION` | `true` / `false` - Enable ODE BSM Deduplication |

### Generate a Github Token

A GitHub token is required to pull artifacts from GitHub repositories. This is required to obtain the jpo-deduplicator jars and must be done before attempting to build this repository.

1. Log into GitHub.
2. Navigate to Settings -> Developer settings -> Personal access tokens.
3. Click "New personal access token (classic)".
   1. As of now, GitHub does not support `Fine-grained tokens` for obtaining packages.
4. Provide a name and expiration for the token.
5. Select the `read:packages` scope.
6. Click "Generate token" and copy the token.
7. Copy the token name and token value into your `.env` file.

For local development the following steps are also required
8. Create a copy of [settings.xml](jpo-deduplicator/jpo-deduplicator/settings.xml) and save it to `~/.m2/settings.xml`
9. Update the variables in your `~/.m2/settings.xml` with the token value and target jpo-ode organization.

### Quick Run
1. Create a copy of `sample.env` and rename it to `.env`.
2. Update the variable `MAVEN_GITHUB_TOKEN` to a github token used for downloading jar file dependencies. For full instructions on how to generate a token please see here: 
3. Set the password for `MONGO_ADMIN_DB_PASS` and `MONGO_READ_WRITE_PASS` environmental variables to a secure password.
4. Set the `COMPOSE_PROFILES` variable to: `kafka,kafka_ui,kafka_setup, jpo-deduplicator`
5. Navigate back to the root directory and run the following command: `docker compose up -d`
6. Produce a sample message to one of the sink topics by using `kafka_ui` by:
   1. Go to `localhost:8001`
   2. Click local -> Topics
   3. Select `topic.OdeMapJson`
   4. Select `Produce Message`
   5. Copy in sample JSON for a Map Message
   6. Click `Produce  Message` multiple times
7. View the synced message in `kafka_ui` by:
   1. Go to `localhost:8001`
   2. Click local -> Topics
   3. Select `topic.DeduplicatedOdeMapJson`
   4. You should now see only one copy of the map message sent. 

[Back to top](#toc)

## Security Notice

While default passwords are provided for development convenience, it is **strongly recommended** to:

1. Change all passwords before deploying to any environment
2. Never use default passwords in production
3. Use secure password generation and management practices
4. Consider using Docker secrets or environment management tools for production deployments
