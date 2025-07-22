# Storage Management

The default configuration of the CV-Manager system is very data intensive and stores multiple forms of CV-Data redundantly in the database. While this provides some benefit for compliance and debugging purposes, it is also often a strain on limited cloud and storage budgets. Below are some tips and tricks for reducing the storage footprint of CV-Data

## Stop syncing unused collections
By Default JPO-utils syncs a number of topics created by the ode, geojson-converter, Conflict Monitor and CV-Manager. However, many of these collections are unused by the CV-Manager system and therefore not required to be saved to the database. In particular, data formats such as the OdeSpatJson format which have lots of traffic can quickly become a storage burden on the system. 

1) To stop a topic from syncing to mongoDB users can modify the `kafka-connectors-values.yaml` file and remove entries. Make sure to remove the entire entry for the given topic. For example, to stop the OdeSpatJson topic from syncing remove the following:
```
- topicName: topic.OdeSpatJson
        collectionName: OdeSpatJson
        generateTimestamp: true
```

Please note, that none of the Ode collections are required for running the GeoJson Converter, Conflict Monitor, or CV-Manager systems. The OdeSpatJson and OdeMapJson collections in particular should be deleted first as they generally contains significant data.

2) Once a topic is deleted from the `kafka-connectors-values.yaml` file. Users can bring up the remaining connectors by rebuilding the kafka-setup container (see step 3). For existing deployments that already have the connectors created. The removed connectors will need to be removed manually. This can be done by manually deleting connectors using curl. 
```
curl -X DELETE "http://localhost:8083/connectors/sink.OdeSpatJson" >/dev/null 2>&1 
```
Alternatively, users may delete all existing topics are recreate them. This may be faster if updating a large number of connectors. The [delete_connectors.sh](../kafka-connect/delete_connectors.sh) script will automatically delete all existing connectors.
```
cd kafka-connect
./delete_connectors.sh
```

3) Once all the unused connectors have been deleted, reapply the connectors scripts to the cluster by rebuilding the kafka-connect-setup container. Make sure kafka_connect, kafka_connect_standalone, or kafka_connect_setup is specified in the COMPOSE_PROFILES variable of your .env file before rebuilding to ensure the setup container is rerun
```
docker-compose up --build -d
```




## Change MongoDB Data Retention Time
By default all collections in the default mongoDB are only retained for a set time window. Depending on deployment requirements, a shorter retention window may be desirable to reduce storage costs and improve database query speeds. Data retention is managed by adjusting TTL's on collections in mongoDB. Below are instructions to adjust the data retention of an existing mongoDB deployment.

1) There are currently two environment variables that configure the data retention duration in mongoDB. The `MONGO_DATA_RETENTION_SECONDS` variable configures the number of seconds that data generated within the system should be retained for. Similarly, he `MONGO_ASN_RETENTION_SECONDS` variable configures the number of seconds that raw ASN.1 data should be retained for. By default Mongo Data is retained for 60 days, while ASN.1 data is configured for 24 hours. To change the data retention window, modify the .env file in the jpo-utils repo, or parent module and provide new values for these variables
```
MONGO_DATA_RETENTION_SECONDS=604800 # 1 week
MONGO_ASN_RETENTION_SECONDS=3600 # 1 Hour
```

2) Once the variables are changed, rebuild the docker-setup container and let it run its setup procedure. Make sure that one of the following is specified in the `COMPOSE_PROFILES` variable to ensure the mongo setup container runs mongo, mongo_full, mongo_setup. Then run the following to reboot

```
docker-compose up --build -d
```

3) Once the indexes have been updated, verify that the change worked properly by checking the TTL index for the adjusted collections. This can be done by logging into the database using

```
mongosh -u <username> -p <password> --authenticationDatabase admin
use CV
db.ProcessedSpat.getIndexes();
```

Alternatively, this may be checked in MongoDB compass


