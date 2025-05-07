# JPO-UTILS Release Notes

## Version 2.1.0
----------------------------------------
### **Summary**
In this release, default values for many environment variables are now defined directly in the docker-compose.yml file. A complete monitoring stack is included, featuring Grafana, 
Prometheus, Node Exporter, and MongoDB dashboards to enhance observability. MongoDB TTL indexes have been updated for better data management, and new Conflict Monitor event state 
progression topics were added to improve event tracking. Kafka support has been expanded with customizable topics and connectors, deduplicated processed BSM topics, configurable 
max-tasks settings, and optional Confluent Cloud integration. Jikkou utilities have been updated to bundle scripts within Docker images, simplify deployments, and support missing 
event topics. Additionally, MongoDB data can now be initialized from a relative data path, some collection names have been updated, and the Jikkou topic creation script has been 
modified to utilize the custom topic section to address requests for custom topic retention times.

Enhancements in this release:
- [CDOT PR 13](https://github.com/CDOT-CV/jpo-utils/pull/13): Set up CI with Azure Pipelines
- [CDOT PR 14](https://github.com/CDOT-CV/jpo-utils/pull/14): Testing Devops Pipelines
- [CDOT PR 15](https://github.com/CDOT-CV/jpo-utils/pull/15): Adding Cm Event State Progression Event
- [CDOT PR 16](https://github.com/CDOT-CV/jpo-utils/pull/16): Update Jikkou Max tasks option
- [CDOT PR 17](https://github.com/CDOT-CV/jpo-utils/pull/17): Mongo ttl updates
- [CDOT PR 18](https://github.com/CDOT-CV/jpo-utils/pull/18): Monitoring Stack & Default Values in Docker Compose
- [CDOT PR 19](https://github.com/CDOT-CV/jpo-utils/pull/19): Custom Kafka topics / Connectors
- [CDOT PR 20](https://github.com/CDOT-CV/jpo-utils/pull/20): CI Updates
- [CDOT PR 21](https://github.com/CDOT-CV/jpo-utils/pull/21): Supporting MongoDB Sample Data Initialization
- [CDOT PR 22](https://github.com/CDOT-CV/jpo-utils/pull/22): Update Intersection API Topic and Collection Names
- [CDOT PR 23](https://github.com/CDOT-CV/jpo-utils/pull/23): Custom Kafka Retention ms
- [USDOT PR 34](https://github.com/usdot-jpo-ode/jpo-utils/pull/34): Feature/deduplicated processed bsm
- [USDOT PR 35](https://github.com/usdot-jpo-ode/jpo-utils/pull/35): Jikkou Image Updates
- [USDOT PR 36](https://github.com/usdot-jpo-ode/jpo-utils/pull/36): Confluent Cloud Topic Creation
- [USDOT PR 37](https://github.com/usdot-jpo-ode/jpo-utils/pull/37): Adding Missing Progression Event Topics


## Version 2.0.0
----------------------------------------
### **Summary**
The first release of the jpo-utils package. This package is focused on hosting utility applications that other parts of the Conflict Monitor depend on. Many of these components are provided by 3rd party groups such as Kafka and MongoDB. This first official release formalizes the migration of multiple components from other repositories to the jpo-utils repository. The migrated components are as follows
- MongoDB - Transferred from the jpo-conflictmonitor
- Kafka - Transferred from the jpo-ode
- Kafka Connect - Transferred from the jpo-conflictmonitor repository

This release also makes additional changes to the submodule components as follows
- Refactored docker-compose files to use compose profiles for modular component approach
- Adds in Jikkou for managing Kafka topic creation and Kafka connect behaviors
- Updates mongoDB indexes and collection list to support new collections

Enhancements in this release:
- USDOT PR 2: Kafka Connect Module Addition
- USDOT PR 3: Kafka topics management
- USDOT PR 5: Add TMC filtered TIM topic
- USDOT PR 6: Merge Release/2024 q3 to master branch
- USDOT PR 7: Sync Develop with Master branch for Q3 release 2024
- USDOT PR 8: Kafka Connect Jikkou Refactor
- USDOT PR 9: Environmental Variable Fixes
- USDOT PR 10: Kafka topic addition
- USDOT PR 11: updates to mongo and kafka connect
- USDOT PR 12: update keyfile generation to use env vars
- USDOT PR 13: Added Spat and ASN Tim Deduplication
- USDOT PR 14: Adding DB entries for Progression Events
- USDOT PR 15: Cimms event aggregation topics
- USDOT PR 16: Updating Conflict Monitor Mongo Connectors
- USDOT PR 17: chore: make kafka_init.sh executable
- USDOT PR 18: Adding JPO-Deduplicator to jpo-utils
- CDOT PR 3: Updated Deduplicator for Compatibility with ode 4.0
- CDOT PR 4: Enable BSM deduplication by default
- USDOT PR 21: Adding missing cm topic
- USDOT PR 22: Adding Default Credentials
- USDOT PR 23: Adding missing ode topics
- USDOT PR 24: Index updates
- CDOT PR 6: Adding MEC Deposit Resources
- USDOT PR 25: Updating version for kafka ui to latest release
- CDOT PR 7: Tim compatibility and CI updates
- CDOT PR 8: Jpo deduplicator removal
- USDOT PR 27: Updates to jikkou image build (for dockerhub)


### **NOTES**
- The jpo-deduplicator was added to the jpo-utils during the development of this release but has been since transferred to it's own repository: [jpo-deduplicator](https://github.com/usdot-jpo-ode/jpo-deduplicator)
