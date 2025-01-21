## JPO-UTILS Release Notes

## Version 2.0.0

### **Summary**
The first release of the jpo-utils package. This package is focused on hosting utility applications that other parts of the Conflict Monitor depend on. Many of these components are provided by 3rd party groups such as Kafka and MongoDB. This first official release formalizes the migration of multiple components from other repositories to the jpo-utils repository. The migrated components are as follows
- MongoDB - Transferred from the jpo-conflictmonitor
- Kafka - Transferred from the jpo-ode
- Deduplicator - Transferred from the jpo-conflictmonitor
- Kafka Connect - Transferred from the jpo-conflictmonitor repository

This release also makes additional changes to the submodule components as follows
- Refactored docker-compose files to use compose profiles for modular component approach
- Adds in Jikkou for managing Kafka topic creation and Kafka connect behaviors
- Updates mongoDB indexes and collection list to support new collections
- Updated jpo-deduplicator for compatibility with jpo-ode version 4.0.0
