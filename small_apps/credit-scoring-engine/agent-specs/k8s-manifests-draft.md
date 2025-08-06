# Kubernetes Manifests for Credit Scoring Engine

**Ticket:** OSM-22 - Create Kubernetes manifests for credit-scoring-engine application  
**Application:** credit-scoring-engine  
**Date:** August 6, 2025  
**Analyst:** Devin AI  

## Overview

This document describes the Kubernetes manifests created for migrating the credit-scoring-engine Spring Boot application from Cloud Foundry to Kubernetes. The manifests include deployment, service, and ingress configurations that maintain the same functionality and resource allocation as the existing CF deployment while following organizational Kubernetes standards.

## Manifest Files

### 1. Deployment (`k8s/deployment.yaml`)

The deployment manifest configures the credit-scoring-engine application with the following key specifications:

#### Resource Configuration
- **Replicas:** 2 (scaled down from CF's 4 instances for initial deployment)
- **Memory:** 3Gi limit, 2.5Gi request (matching CF's 3072M allocation)
- **CPU:** 2000m limit, 500m request (following organizational standards)
- **JVM Settings:** `-Xmx2560m -XX:+UseG1GC -XX:+UseStringDeduplication`

#### Security Context
Following organizational security standards from Rule 02:
- `runAsNonRoot: true`
- `seccompProfile.type: RuntimeDefault`
- `readOnlyRootFilesystem: true`
- `capabilities.drop: ["ALL"]`

#### Health Probes
- **Liveness Probe:** Uses custom `/api/v1/credit/health/detailed` endpoint
  - Initial delay: 60 seconds
  - Period: 30 seconds
  - Timeout: 10 seconds
  - Failure threshold: 3
- **Readiness Probe:** Uses Spring Boot actuator `/actuator/health` endpoint
  - Initial delay: 30 seconds
  - Period: 10 seconds
  - Timeout: 5 seconds
  - Failure threshold: 3

#### Environment Variables
The deployment includes all 40+ environment variables from the CF manifest:

**Core Application Settings:**
- `SPRING_PROFILES_ACTIVE: production,scoring`
- `JBP_CONFIG_OPEN_JDK_JRE: [jre: {version: 17.+}]`
- `JVM_OPTS: -Xmx2560m -XX:+UseG1GC -XX:+UseStringDeduplication`

**Credit Bureau APIs:**
- `EXPERIAN_API_URL: https://api.experian.com/credit`
- `EQUIFAX_API_URL: https://api.equifax.com/ews`
- `TRANSUNION_API_URL: https://api.transunion.com/credit`
- `CREDIT_API_TIMEOUT: 15000`

**ML Model Configuration:**
- `FICO_MODEL_VERSION: 9.0`
- `VANTAGE_MODEL_VERSION: 4.0`
- `CUSTOM_MODEL_PATH: /models/proprietary-score-v2.3.pkl`
- `ML_FEATURE_COUNT: 247`
- `MODEL_REFRESH_INTERVAL: 24h`
- `A_B_TEST_ENABLED: true`

**Compliance Settings:**
- `FCRA_COMPLIANCE_MODE: true`
- `ECOA_COMPLIANCE_MODE: true`
- `ADVERSE_ACTION_NOTIFICATIONS: true`

#### Service Binding Integration
The deployment references all 7 ConfigMaps and Secrets created in OSM-21:
- `pe-eng-credit-scoring-engine-postgres-primary-config/secret`
- `pe-eng-credit-scoring-engine-postgres-replica-config/secret`
- `pe-eng-credit-scoring-engine-redis-cluster-config/secret`
- `pe-eng-credit-scoring-engine-model-storage-s3-config/secret`
- `pe-eng-credit-scoring-engine-credit-bureau-proxy-config/secret`
- `pe-eng-credit-scoring-engine-encryption-service-config/secret`
- `pe-eng-credit-scoring-engine-audit-trail-kafka-config/secret`

#### Volume Mounts
- **ML Models:** `/models` (read-only, for ML model artifacts)
- **Temporary Storage:** `/tmp` (writable for JVM temporary files)
- **Application Logs:** `/tmp/app-logs` (writable for application logging)

#### Monitoring Integration
- Prometheus scraping enabled with annotations:
  - `prometheus.io/scrape: "true"`
  - `prometheus.io/port: "8080"`
  - `prometheus.io/path: "/actuator/prometheus"`

### 2. Service (`k8s/service.yaml`)

The service manifest creates a ClusterIP service for internal cluster communication:

#### Configuration
- **Type:** ClusterIP (internal access only)
- **Port:** 8080 (matching Spring Boot default)
- **Target Port:** 8080
- **Protocol:** TCP
- **Selector:** Matches deployment labels

#### Labels
Following organizational naming conventions from Rule 04:
- `app.kubernetes.io/name: credit-scoring-engine`
- `app.kubernetes.io/version: "3.1.0"`
- `app.kubernetes.io/part-of: retail-banking`
- `environment: dev`
- `managed-by: helm`

### 3. Ingress (`k8s/ingress.yaml`)

The ingress manifest exposes the application externally using the same routes as the CF deployment:

#### Routes
Based on CF manifest routes:
- `credit-scoring.internal.banking.com` (internal banking access)
- `credit-api-v3.banking.com` (external API access)

#### Configuration
- **Ingress Class:** nginx
- **TLS:** Enabled with SSL redirect
- **Path Type:** Prefix
- **Backend:** Routes to the credit-scoring-engine service on port 8080

#### Security
- SSL redirect enabled
- TLS certificate reference: `credit-scoring-engine-tls`

## Design Decisions

### Image Strategy
- **Placeholder:** `[REGISTRY]/credit-scoring-engine:[TAG]`
- **Rationale:** Allows for flexible registry and tag configuration during deployment
- **Base Image:** Expected to use `eclipse-temurin:17-jre-alpine` as documented in container strategy

### Scaling Strategy
- **Initial Replicas:** 2 (reduced from CF's 4 for initial deployment)
- **Horizontal Scaling:** Ready for HPA configuration
- **Resource Allocation:** Maintains CF memory allocation for ML workloads

### Security Implementation
- **Non-Root Execution:** Follows organizational security standards
- **Read-Only Root Filesystem:** Enhances container security
- **Capability Dropping:** Minimal Linux capabilities
- **Secret Separation:** Sensitive data isolated in Kubernetes Secrets

### Health Monitoring
- **Custom Health Endpoint:** Leverages application's business logic health check
- **Actuator Integration:** Uses Spring Boot's built-in health monitoring
- **Prometheus Metrics:** Ready for observability stack integration

## Migration Considerations

### From Cloud Foundry
1. **Memory Allocation:** Maintains 3GB memory allocation for ML model processing
2. **Environment Variables:** All CF environment variables preserved
3. **Health Checks:** Custom health endpoint maintained
4. **Service Bindings:** Mapped to Kubernetes ConfigMaps/Secrets
5. **Routes:** External routes preserved through ingress configuration

### Deployment Prerequisites
1. **ConfigMaps/Secrets:** All service binding resources must be deployed first
2. **TLS Certificate:** `credit-scoring-engine-tls` secret must be created
3. **Ingress Controller:** nginx ingress controller must be available
4. **Container Registry:** Image must be built and pushed to specified registry

### Operational Readiness
1. **Monitoring:** Prometheus scraping configured
2. **Logging:** Application logs directed to `/tmp/app-logs`
3. **Compliance:** FCRA/ECOA compliance settings maintained
4. **ML Models:** Volume mount ready for model artifact deployment

## Testing Strategy

### Local Testing
1. **Health Endpoints:** Verify `/api/v1/credit/health/detailed` responds correctly
2. **Actuator Endpoints:** Confirm `/actuator/health` is accessible
3. **Environment Variables:** Validate all configuration is properly injected
4. **Resource Limits:** Monitor memory usage under load

### Integration Testing
1. **Service Discovery:** Verify service-to-service communication
2. **Ingress Routing:** Test external access through both hostnames
3. **Database Connectivity:** Validate PostgreSQL connections
4. **Cache Access:** Confirm Redis cluster connectivity
5. **External APIs:** Test credit bureau proxy connections

### Performance Testing
1. **Memory Usage:** Validate 3GB allocation is sufficient
2. **CPU Performance:** Monitor under ML model processing load
3. **Response Times:** Compare with CF deployment performance
4. **Scaling Behavior:** Test horizontal pod autoscaling

## Next Steps

1. **Image Build:** Create container image using Dockerfile and container strategy
2. **Secret Creation:** Deploy TLS certificates and any missing secrets
3. **Deployment:** Apply manifests to development environment
4. **Validation:** Execute testing strategy and validate functionality
5. **Scaling:** Increase replicas to 4 to match CF deployment
6. **Production:** Extend manifests for staging and production environments

## Compliance Notes

This deployment maintains all regulatory compliance requirements:
- **FCRA Compliance:** Maintained through environment variables and audit trail
- **ECOA Compliance:** Preserved from CF configuration
- **Audit Trail:** Kafka integration for compliance logging
- **Data Encryption:** Encryption service integration maintained
- **Access Controls:** Kubernetes RBAC ready for implementation

The manifests follow all organizational standards and are ready for deployment to the development environment as part of the Cloud Foundry to Kubernetes migration initiative.
