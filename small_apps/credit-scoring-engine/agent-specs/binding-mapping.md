# Service Binding Mapping for Credit Scoring Engine

**Ticket:** OSM-21 - Map Service Bindings to K8s Config for App credit-scoring-engine  
**Application:** credit-scoring-engine  
**Date:** August 6, 2025  
**Analyst:** Devin AI  

## Overview

This document maps the 7 Cloud Foundry service bindings defined in `manifest.yml` to their corresponding Kubernetes ConfigMaps and Secrets. Each service binding is translated into appropriate K8s resources following the naming conventions and security best practices.

## Service Binding Mapping Table

| CF Service | K8s Resource Type | Secret/ConfigMap Name | Contents | Notes |
|------------|-------------------|----------------------|----------|-------|
| credit-postgres-primary | Secret + ConfigMap | pe-eng-credit-scoring-engine-postgres-primary-secret<br/>pe-eng-credit-scoring-engine-postgres-primary-config | **Secret:** username, password<br/>**ConfigMap:** host, port, database, connection-params | Primary PostgreSQL database for credit data |
| credit-postgres-replica | Secret + ConfigMap | pe-eng-credit-scoring-engine-postgres-replica-secret<br/>pe-eng-credit-scoring-engine-postgres-replica-config | **Secret:** username, password<br/>**ConfigMap:** host, port, database, connection-params | Read-only PostgreSQL replica for reporting |
| credit-redis-cluster | Secret + ConfigMap | pe-eng-credit-scoring-engine-redis-cluster-secret<br/>pe-eng-credit-scoring-engine-redis-cluster-config | **Secret:** password, auth-token<br/>**ConfigMap:** host, port, cluster-nodes, timeout | Redis cluster for caching and session management |
| model-storage-s3 | Secret + ConfigMap | pe-eng-credit-scoring-engine-model-storage-s3-secret<br/>pe-eng-credit-scoring-engine-model-storage-s3-config | **Secret:** access-key-id, secret-access-key<br/>**ConfigMap:** endpoint, bucket, region, path-prefix | S3-compatible storage for ML model artifacts |
| credit-bureau-proxy | Secret + ConfigMap | pe-eng-credit-scoring-engine-credit-bureau-proxy-secret<br/>pe-eng-credit-scoring-engine-credit-bureau-proxy-config | **Secret:** api-key, client-secret<br/>**ConfigMap:** base-url, timeout, retry-config | Proxy service for credit bureau API access |
| encryption-service | Secret + ConfigMap | pe-eng-credit-scoring-engine-encryption-service-secret<br/>pe-eng-credit-scoring-engine-encryption-service-config | **Secret:** encryption-key, signing-key<br/>**ConfigMap:** algorithm, key-rotation-interval | Data encryption/decryption service |
| audit-trail-kafka | Secret + ConfigMap | pe-eng-credit-scoring-engine-audit-trail-kafka-secret<br/>pe-eng-credit-scoring-engine-audit-trail-kafka-config | **Secret:** username, password, ssl-cert<br/>**ConfigMap:** brokers, topic, consumer-group | Kafka cluster for compliance audit logging |

## Design Decisions

### Naming Convention
- **Pattern:** `pe-eng-credit-scoring-engine-<service>-<type>`
- **Team:** `pe-eng` (Platform Engineering)
- **App:** `credit-scoring-engine`
- **Environment:** `dev` (for sample files)

### Resource Separation Strategy
- **Secrets:** Store sensitive data (credentials, keys, certificates)
- **ConfigMaps:** Store non-sensitive configuration (endpoints, timeouts, parameters)
- **Granularity:** One Secret and one ConfigMap per service for clear separation

### Security Considerations
- All credential values use `BASE64_ENCODED_*_PLACEHOLDER` format
- Secrets are separated from configuration to enable different access controls
- Ready for integration with external secret management systems

### Label Strategy
All resources include mandatory labels:
- `app.kubernetes.io/name: credit-scoring-engine`
- `app.kubernetes.io/version: "3.1.0"`
- `app.kubernetes.io/part-of: retail-banking`
- `environment: dev`
- `managed-by: helm`

## Configuration Parameters by Service

### PostgreSQL Services (Primary & Replica)
- **Connection:** host, port, database name
- **Credentials:** username, password
- **Parameters:** SSL mode, connection pooling, application name

### Redis Cluster
- **Connection:** cluster nodes, port
- **Credentials:** password, auth token
- **Parameters:** timeout, retry configuration

### S3 Model Storage
- **Connection:** endpoint URL, region
- **Credentials:** access key ID, secret access key
- **Parameters:** bucket name, path prefix

### Credit Bureau Proxy
- **Connection:** base URL, timeout settings
- **Credentials:** API key, client secret
- **Parameters:** retry configuration, rate limiting

### Encryption Service
- **Connection:** service endpoint
- **Credentials:** encryption key, signing key
- **Parameters:** algorithm, key rotation interval

### Kafka Audit Trail
- **Connection:** broker list, topic configuration
- **Credentials:** username, password, SSL certificates
- **Parameters:** consumer group, retention policy

## Environment Variable Mapping

The following environment variables from `manifest.yml` will be sourced from these K8s resources:

### Database Configuration
- `SPRING_DATASOURCE_URL` → postgres-primary-config
- `SPRING_DATASOURCE_USERNAME` → postgres-primary-secret
- `SPRING_DATASOURCE_PASSWORD` → postgres-primary-secret

### Redis Configuration
- `SPRING_REDIS_HOST` → redis-cluster-config
- `SPRING_REDIS_PASSWORD` → redis-cluster-secret

### External API Configuration
- Credit bureau URLs → credit-bureau-proxy-config
- API credentials → credit-bureau-proxy-secret

### Model Storage Configuration
- `CUSTOM_MODEL_PATH` → model-storage-s3-config
- S3 credentials → model-storage-s3-secret

## Implementation Notes

1. **Current Status:** All 7 services are currently unused in the application code but represent the target production architecture
2. **Migration Path:** These configurations enable gradual implementation of external service integrations
3. **Compliance:** Supports FCRA/ECOA compliance requirements through proper audit trail configuration
4. **Scalability:** Designed to support the 4-instance deployment configuration from Cloud Foundry

## Next Steps

1. Deploy sample configurations to development environment
2. Implement service integration code to consume these configurations
3. Validate connectivity and configuration injection
4. Extend to staging and production environments with environment-specific values
